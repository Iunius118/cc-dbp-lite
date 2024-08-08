package com.github.iunius118.ccdbplite.peripheral.databasestorage;

import com.github.iunius118.ccdbplite.CCDatabasePeripheralLite;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class DatabaseStorageBlockEntity extends BlockEntity {
    public static final String KEY_DATABASE_STORAGE_ID = "dbsid";

    private int storageID = -1;
    private String databasePath;

    public DatabaseStorageBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public DatabaseStorageBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(CCDatabasePeripheralLite.BlockEntityTypes.DATABASE_STORAGE, blockPos, blockState);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);

        if (compoundTag.contains(KEY_DATABASE_STORAGE_ID, Tag.TAG_INT)) {
            storageID = compoundTag.getInt(KEY_DATABASE_STORAGE_ID);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);

        if (storageID != -1) {
            compoundTag.putInt(KEY_DATABASE_STORAGE_ID, storageID);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();

        if (storageID != -1) {
            compoundTag.putInt(KEY_DATABASE_STORAGE_ID, storageID);
        }

        return compoundTag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        // Synchronize block entity tag to client side
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
    }

    public int getStorageID() {
        return storageID;
    }

    public void setStorageID(int newID) {
        if (storageID != newID) {
            storageID = newID;
            // Update block entity tag
            setChanged();
        }
    }

    public String getDatabasePath() {
        return databasePath;
    }

    public void setDatabasePath(String newPath) {
        databasePath = newPath;
    }
}
