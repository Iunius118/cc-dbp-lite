package com.github.iunius118.ccdbplite.peripheral.databasestorage;

import com.github.iunius118.ccdbplite.CCDatabasePeripheralLite;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Objects;

public class DatabaseStorageBlockEntity extends BlockEntity implements Nameable {
    public static final String KEY_DATABASE_STORAGE_ID = "dbsid";
    public static final String KEY_NAME = "name";
    public static final String KEY_CUSTOM_NAME = "CustomName";

    private int storageID = -1;
    private String storageName;

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

        if (compoundTag.contains(KEY_NAME, Tag.TAG_STRING)) {
            storageName = compoundTag.getString(KEY_NAME);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        saveTagsTo(compoundTag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        var compoundTag = new CompoundTag();
        saveTagsTo(compoundTag);
        return compoundTag;
    }

    private void saveTagsTo(CompoundTag compoundTag) {
        if (storageID != -1) {
            compoundTag.putInt(KEY_DATABASE_STORAGE_ID, storageID);
        }

        if (storageName != null) {
            compoundTag.putString(KEY_NAME, storageName);
            // Save custom name for display name of item
            compoundTag.putString(KEY_CUSTOM_NAME, Component.Serializer.toJson(Component.literal(storageName)));
        }
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

    @Override
    public void saveToItem(ItemStack itemStack) {
        super.saveToItem(itemStack);

        if (hasCustomName()) {
            // Set storage name to display name
            itemStack.setHoverName(getDisplayName());
        }
    }

    public int getStorageID() {
        return storageID;
    }

    public void setStorageID(int newID) {
        if (storageID != newID) {
            storageID = newID;
            // Update block entity tags
            setChanged();
        }
    }

    @Override
    public boolean hasCustomName() {
        return storageName != null && !storageName.isEmpty();
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return hasCustomName() ? Component.literal(storageName) : null;
    }

    @Override
    public Component getName() {
        if (this.hasCustomName()) {
            return Component.literal(storageName);
        } else {
            return Component.translatable(getBlockState().getBlock().getDescriptionId());
        }
    }

    @Override
    public Component getDisplayName() {
        return Nameable.super.getDisplayName();
    }

    @Nullable
    public String getStorageName() {
        return storageName;
    }

    public void setStorageName(String newName) {
        if (!Objects.equals(storageName, newName)) {
            storageName = newName;
            // Update block entity tags
            setChanged();
        }
    }
}
