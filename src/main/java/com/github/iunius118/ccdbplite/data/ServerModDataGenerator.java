package com.github.iunius118.ccdbplite.data;

import com.github.iunius118.ccdbplite.CCDatabasePeripheralLite;
import com.github.iunius118.ccdbplite.peripheral.databasestorage.DatabaseStorageBlockEntity;
import com.google.common.collect.ImmutableList;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.ComputerCraftTags;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.loot.packs.VanillaLootTableProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.common.Tags;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ServerModDataGenerator {
    // RecipeProvider
    private static Consumer<GatherDataEvent> recipeGenerator = event -> {
        var dataGenerator = event.getGenerator();
        var recipeProvider = new RecipeProvider(dataGenerator.getPackOutput()) {
            @Override
            protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
                ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, CCDatabasePeripheralLite.Items.DATABASE_STORAGE)
                        .pattern("#D#")
                        .pattern("#R#")
                        .pattern("#i#")
                        .define('#', Tags.Items.STONE)
                        .define('D', ForgeRegistries.ITEMS.getValue(new ResourceLocation(ComputerCraftAPI.MOD_ID, "disk_drive")))
                        .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                        .define('i', Tags.Items.INGOTS_IRON)
                        .unlockedBy("has_disk", has(ComputerCraftTags.Items.COMPUTER))
                        .save(consumer, ForgeRegistries.ITEMS.getKey(CCDatabasePeripheralLite.Items.DATABASE_STORAGE));
            }
        };
        dataGenerator.addProvider(event.includeServer(), recipeProvider);
    };

    // LootTableProvider
    private static Consumer<GatherDataEvent> lootTableGenerator = event -> {
        Block databaseStorageBlock = CCDatabasePeripheralLite.Blocks.DATABASE_STORAGE;
        Supplier<LootTableSubProvider> blockLootSubProvider = () -> new BlockLootSubProvider(Set.of(), FeatureFlags.REGISTRY.allFlags()) {
            final LootTable.Builder lootTableBuilder = LootTable.lootTable()
                    .withPool(this.applyExplosionCondition(databaseStorageBlock, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                            .add(LootItem.lootTableItem(databaseStorageBlock)
                                    .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                            .copy(DatabaseStorageBlockEntity.KEY_DATABASE_STORAGE_ID, "%s.%s".formatted(BlockItem.BLOCK_ENTITY_TAG, DatabaseStorageBlockEntity.KEY_DATABASE_STORAGE_ID))
                                            .copy("id", "BlockEntityTag.id")
                                            .copy("ForgeCaps", "BlockEntityTag.ForgeCaps")))));

            @Override
            protected void generate() {
                add(databaseStorageBlock, lootTableBuilder);
            }

            @Override
            protected Iterable<Block> getKnownBlocks() {
                return ImmutableList.of(databaseStorageBlock);
            }
        };
        var dataGenerator = event.getGenerator();
        var packOutput = dataGenerator.getPackOutput();
        var lootTableProvider = new LootTableProvider(packOutput, Set.of(), VanillaLootTableProvider.create(packOutput).getTables()) {
            @Override
            public List<SubProviderEntry> getTables() {
                return ImmutableList.of( new LootTableProvider.SubProviderEntry(blockLootSubProvider, LootContextParamSets.BLOCK));
            }
        };
        dataGenerator.addProvider(event.includeServer(), lootTableProvider);
    };

    public static void gatherData(final GatherDataEvent event) {
        recipeGenerator.accept(event);
        lootTableGenerator.accept(event);
    }
}
