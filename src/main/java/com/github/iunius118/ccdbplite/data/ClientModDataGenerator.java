package com.github.iunius118.ccdbplite.data;

import com.github.iunius118.ccdbplite.CCDatabasePeripheralLite;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.function.Consumer;

public final class ClientModDataGenerator {
    // BlockStateProvider
    private static final Consumer<GatherDataEvent> blockStateGenerator = event -> {
        var dataGenerator = event.getGenerator();
        var blockStateProvider = new BlockStateProvider(dataGenerator.getPackOutput(), CCDatabasePeripheralLite.MOD_ID, event.getExistingFileHelper()) {
            @Override
            protected void registerStatesAndModels() {
                BlockModelBuilder databaseStorageModel = models().cubeBottomTop("database_storage", CCDatabasePeripheralLite.id("block/database_storage_side"), CCDatabasePeripheralLite.id("block/database_storage_top"), CCDatabasePeripheralLite.id("block/database_storage_top"));
                simpleBlock(CCDatabasePeripheralLite.Blocks.DATABASE_STORAGE, databaseStorageModel);
            }
        };
        dataGenerator.addProvider(event.includeClient(), blockStateProvider);
    };

    // ItemModelProvider
    private static final Consumer<GatherDataEvent> itemModelGenerator = event -> {
        var dataGenerator = event.getGenerator();
        var itemModelProvider = new ItemModelProvider(dataGenerator.getPackOutput(), CCDatabasePeripheralLite.MOD_ID, event.getExistingFileHelper()) {
            @Override
            protected void registerModels() {
                getBuilder("database_storage").parent(new ModelFile.UncheckedModelFile(modLoc("block/database_storage")));
            }
        };
        dataGenerator.addProvider(event.includeClient(), itemModelProvider);
    };

    // LanguageProvider
    private static final Consumer<GatherDataEvent> languageGenerator = event -> {
        var dataGenerator = event.getGenerator();
        final String modID = CCDatabasePeripheralLite.MOD_ID;
        var languageProvider = new LanguageProvider(dataGenerator.getPackOutput(), modID, "en_us") {
            @Override
            protected void addTranslations() {
                // Item groups
                add("itemGroup.ccdbplite.main", CCDatabasePeripheralLite.MOD_NAME);
                // Block
                add(CCDatabasePeripheralLite.Blocks.DATABASE_STORAGE, "Database Storage");
                // Item tooltip
                add("tooltip.ccdbplite.storage_id", "Storage ID: %s");
            }
        };
        dataGenerator.addProvider(event.includeClient(), languageProvider);
    };


    public static void gatherData(final GatherDataEvent event) {
        blockStateGenerator.accept(event);
        itemModelGenerator.accept(event);
        languageGenerator.accept(event);
    }
}
