package com.github.iunius118.ccdbplite.block;

import com.github.iunius118.ccdbplite.CCDatabasePeripheralLite;
import com.github.iunius118.ccdbplite.peripheral.databasestorage.DatabaseStorageBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class DatabaseStorageBlock extends BaseEntityBlock {
    public DatabaseStorageBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DatabaseStorageBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack itemStack) {
        super.setPlacedBy(level, pos, state, entity, itemStack);
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof DatabaseStorageBlockEntity storage) {
            // Copy database storage id from item stack tag
            if (itemStack.hasTag()) {
                var compoundTag = itemStack.getTag();

                if (compoundTag.contains(DatabaseStorageBlockEntity.KEY_DATABASE_STORAGE_ID, Tag.TAG_INT)) {
                    storage.setStorageID(compoundTag.getInt(DatabaseStorageBlockEntity.KEY_DATABASE_STORAGE_ID));
                }
            }
        }
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (level.getBlockEntity(pos) instanceof DatabaseStorageBlockEntity storage) {
            if (!level.isClientSide && player.isCreative()) {
                // Drop item stack with block entity tag saved when block is broken by creative player
                ItemStack itemstack = new ItemStack(CCDatabasePeripheralLite.Items.DATABASE_STORAGE);
                storage.saveToItem(itemstack);

                ItemEntity itementity = new ItemEntity(level, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, itemstack);
                itementity.setDefaultPickUpDelay();
                level.addFreshEntity(itementity);
            }
        }

        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        ItemStack itemstack = super.getCloneItemStack(state, target, level, pos, player);

        if (level.getBlockEntity(pos) instanceof DatabaseStorageBlockEntity storage) {
            // Return item stack with block entity tag saved when block is picked by player
            storage.saveToItem(itemstack);
        }

        return itemstack;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> components, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, blockGetter, components, tooltipFlag);

        if (itemStack.hasTag()) {
            // Add database ID to item stack tooltip
            var compoundTag = itemStack.getTag();

            getStorageIDFromTag(compoundTag).ifPresent(dbID -> {
                // TODO: Translate
                MutableComponent component = Component.literal("Storage ID: %d".formatted(dbID)).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
                components.add(component);
            });
        }
    }

    private Optional<Integer> getStorageIDFromTag(CompoundTag compoundTag) {
        if (!compoundTag.contains("BlockEntityTag", Tag.TAG_COMPOUND)) {
            return Optional.empty();
        }

        var beTag = compoundTag.getCompound("BlockEntityTag");

        if (!beTag.contains(DatabaseStorageBlockEntity.KEY_DATABASE_STORAGE_ID, Tag.TAG_INT)) {
            return Optional.empty();
        }

        int dbID = beTag.getInt(DatabaseStorageBlockEntity.KEY_DATABASE_STORAGE_ID);

        if (dbID == -1) {
            return Optional.empty();
        }

        return Optional.of(dbID);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
