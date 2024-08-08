package com.github.iunius118.ccdbplite.peripheral.databasestorage;

import com.github.iunius118.ccdbplite.CCDatabasePeripheralLite;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.Direction;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sqlite.JDBC;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseStoragePeripheral implements IPeripheral {
    public static final String TYPE = "dbstorage";
    public static final String SAVE_DIR_PATH = "dbstorage";

    private final DatabaseStorageBlockEntity storage;

    public DatabaseStoragePeripheral(DatabaseStorageBlockEntity storage) {
        this.storage = storage;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Nullable
    @Override
    public Object getTarget() {
        return storage;
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        if (this == other) {
            return true;
        } else {
            return (other instanceof DatabaseStoragePeripheral otherPeripheral) && (storage == otherPeripheral.getTarget());
        }
    }

    @LuaFunction
    public final int getID() {
        return storage.getStorageID();
    }

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @LuaFunction
    public final void execute(String sql) {
        // TODO: Execute SQL and get result

        // Sample code from SQLite JDBC Driver
        try(
                // create a database connection
                Connection connection = DriverManager.getConnection(JDBC.PREFIX + getDatabasePath());
                Statement statement = connection.createStatement();
        ){
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.execute("drop table if exists person");
            statement.execute("create table person (id integer, name string)");
            statement.execute("insert into person values(1, 'leo')");
            statement.execute("insert into person values(2, 'yui')");
            statement.execute("select * from person");
            ResultSet rs = statement.getResultSet();

            while(rs.next()) {
                // read the result set
                System.out.println("name = " + rs.getString("name"));
                System.out.println("id = " + rs.getInt("id"));
            }
        } catch(Exception e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            e.printStackTrace(System.out);
        }
    }

    private String getDatabasePath() throws LuaException {
        String databasePath = storage.getDatabasePath();

        if (databasePath == null) {
            // Create new database path for new database storage peripheral
            if (storage.getStorageID() == -1) {
                setUniqueStorageID();
            }

            final int storageID = storage.getStorageID();
            final String subDirPath = "%s/%d".formatted(SAVE_DIR_PATH, storageID);
            Path dirPath = storage.getLevel().getServer().getWorldPath(new LevelResource("computercraft")).resolve(subDirPath);
            File dir = dirPath.toFile();

            // Create directories for new database if they do not exist
            if (!dir.exists() && !dir.mkdirs()) {
                throw new LuaException("Folders for new database could not be created");
            }

            // Create path for new database
            databasePath = dirPath.resolve("database.db").toUri().getPath();
            storage.setDatabasePath(databasePath);
        }

        return databasePath;
    }

    private void setUniqueStorageID() throws LuaException {
        // Set unique ID to database peripheral when no ID is set
        final int newID = ComputerCraftAPI.createUniqueNumberedSaveDir(storage.getLevel().getServer(), DatabaseStoragePeripheral.SAVE_DIR_PATH);

        if (newID == -1) {
            throw new LuaException("New ID could not be assigned to database storage");
        }

        storage.setStorageID(newID);
    }

    public static class Provider implements ICapabilityProvider {
        private final LazyOptional<IPeripheral> instance;

        public Provider(DatabaseStorageBlockEntity storage) {
            if (storage != null) {
                instance = LazyOptional.of(() -> new DatabaseStoragePeripheral(storage));
            } else {
                instance = LazyOptional.empty();
            }
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return CCDatabasePeripheralLite.Capabilities.CAPABILITY_PERIPHERAL.orEmpty(cap, instance);
        }
    }
}
