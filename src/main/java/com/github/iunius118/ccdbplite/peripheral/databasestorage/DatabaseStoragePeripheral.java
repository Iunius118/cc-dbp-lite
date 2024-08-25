package com.github.iunius118.ccdbplite.peripheral.databasestorage;

import com.github.iunius118.ccdbplite.CCDatabasePeripheralLite;
import com.github.iunius118.ccdbplite.detabase.Database;
import com.github.iunius118.ccdbplite.detabase.LuaPreparedSQLStatement;
import com.github.iunius118.ccdbplite.detabase.LuaSQLStatement;
import com.github.iunius118.ccdbplite.detabase.LuaSQLStatementBase;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.Direction;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DatabaseStoragePeripheral implements IPeripheral {
    public static final String TYPE = "dbstorage";
    public static final String SAVE_DIR_PATH = "dbstorage";
    public static final String DEFAULT_DATABASE_NAME = "database.db";

    private final DatabaseStorageBlockEntity storage;

    public DatabaseStoragePeripheral(DatabaseStorageBlockEntity storage) {
        this.storage = storage;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void attach(IComputerAccess computer) {
        Map<IComputerAccess, Set<LuaSQLStatementBase>> connections = storage.getDatabaseConnections();

        synchronized (connections) {
            if (!connections.containsKey(computer)) {
                // Add Set of connections from computer
                connections.put(computer, new HashSet<>());
            }
        }
    }

    @Override
    public void detach(IComputerAccess computer) {
        Map<IComputerAccess, Set<LuaSQLStatementBase>> connections = storage.getDatabaseConnections();

        synchronized (connections) {
            Set<LuaSQLStatementBase> statements = connections.getOrDefault(computer, Collections.emptySet());

            // Close all connections from detached computer
            for (LuaSQLStatementBase statement : statements) {
                statement.closeConnection();
            }

            // Remove Set of connections from computer
            connections.remove(computer);
        }
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

    /**
     * Retrieves an integer to identify this database storage peripheral.
     * This identifier will be assigned to this peripheral when the database in this peripheral is first connected.
     *
     * @return {@code number} The identifier; {@code -1} If it has not yet been assigned.
     */
    @LuaFunction
    public final int getID() {
        return storage.getStorageID();
    }

    /**
     * Connects to the database and returns a {@code Statement} table containing functions for manipulating the database.
     *
     * @return {@code table} The table containing functions that wraps {@code Connection} and {@code Statement} of JDBC.
     * @throws LuaException Thrown when SQL driver returns a warning or error, or fails to assign storage ID.
     */
    @LuaFunction
    public final LuaSQLStatement createStatement(IComputerAccess computer) throws LuaException {
        // Create a new statement
        LuaSQLStatement statement = Database.createStatement(computer, getDatabaseURL());
        // Record the statement
        addStatement(computer, statement);
        return statement;
    }

    /**
     * Connects to the database with parameterized SQL statement and returns a {@code PreparedStatement} table containing functions for manipulating the database.
     *
     * @param sql {@code string} An SQL statement that may contain one or more '?' parameter placeholders.
     * @return {@code table} The table containing functions that wraps {@code Connection}, {@code PreparedStatement}, and {@code ParameterMetaData} of JDBC.
     * @throws LuaException Thrown when SQL driver returns a warning or error, or fails to assign storage ID.
     */
    @LuaFunction
    public final LuaPreparedSQLStatement prepareStatement(IComputerAccess computer, String sql) throws LuaException {
        // Create a new prepared statement
        LuaPreparedSQLStatement preparedStatement = Database.prepareStatement(computer, getDatabaseURL(), sql);
        // Record the statement
        addStatement(computer, preparedStatement);
        return preparedStatement;
    }

    /**
     * Releases all connections and resources to the database from this computer immediately.
     *
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final void closeAll(IComputerAccess computer) throws LuaException {
        Map<IComputerAccess, Set<LuaSQLStatementBase>> connections = storage.getDatabaseConnections();

        synchronized (connections) {
            Set<LuaSQLStatementBase> statements = connections.get(computer);
            // Close all connections to the database from the computer
            statements.forEach(LuaSQLStatementBase::closeConnection);
            statements.clear();
        }
    }

    private String getDatabaseURL() throws LuaException {
        if (storage.getStorageID() == -1) {
            // Set unique ID to database peripheral when no ID is set
            setUniqueStorageID();
        }

        final String subDirPath = SAVE_DIR_PATH + "/" + storage.getStorageID();
        Path dirPath = storage.getLevel().getServer().getWorldPath(new LevelResource("computercraft")).resolve(subDirPath);
        File dir = dirPath.toFile();

        // Create directories for database if they do not exist
        if (!dir.exists() && !dir.mkdirs()) {
            throw new LuaException("Folders for new database could not be created");
        }

        // Create and return URL for database
        return dirPath.resolve(DEFAULT_DATABASE_NAME).toUri().getPath();
    }

    private void setUniqueStorageID() throws LuaException {
        // Set unique ID to database peripheral when no ID is set
        final int newID = ComputerCraftAPI.createUniqueNumberedSaveDir(storage.getLevel().getServer(), DatabaseStoragePeripheral.SAVE_DIR_PATH);

        if (newID == -1) {
            throw new LuaException("New ID could not be assigned to database storage");
        }

        storage.setStorageID(newID);
    }

    public void addStatement(IComputerAccess computer, LuaSQLStatementBase statement) {
        Map<IComputerAccess, Set<LuaSQLStatementBase>> connections = storage.getDatabaseConnections();

        synchronized (connections) {
            Set<LuaSQLStatementBase> statements = connections.get(computer);
            // Remove all statements that are closed
            statements.removeIf(LuaSQLStatementBase::isClosed);
            // Add new statement
            statements.add(statement);
        }
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
