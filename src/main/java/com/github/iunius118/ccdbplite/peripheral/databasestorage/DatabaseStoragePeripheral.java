package com.github.iunius118.ccdbplite.peripheral.databasestorage;

import com.github.iunius118.ccdbplite.CCDatabasePeripheralLite;
import com.github.iunius118.ccdbplite.detabase.Database;
import com.github.iunius118.ccdbplite.detabase.LuaPreparedSQLStatement;
import com.github.iunius118.ccdbplite.detabase.LuaSQLStatement;
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

import java.io.File;
import java.nio.file.Path;

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

    @LuaFunction
    public final LuaSQLStatement createStatement() throws LuaException {
        return Database.createStatement(getDatabaseURL());
    }

    @LuaFunction
    public final LuaPreparedSQLStatement prepareStatement(String sql) throws LuaException {
        return Database.prepareStatement(getDatabaseURL(), sql);
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
        return dirPath.resolve("database.db").toUri().getPath();
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
