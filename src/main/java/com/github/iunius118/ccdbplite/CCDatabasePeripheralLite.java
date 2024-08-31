package com.github.iunius118.ccdbplite;

import com.github.iunius118.ccdbplite.block.DatabaseStorageBlock;
import com.github.iunius118.ccdbplite.data.ClientModDataGenerator;
import com.github.iunius118.ccdbplite.data.ServerModDataGenerator;
import com.github.iunius118.ccdbplite.peripheral.databasestorage.DatabaseStorageBlockEntity;
import com.github.iunius118.ccdbplite.peripheral.databasestorage.DatabaseStoragePeripheral;
import com.mojang.logging.LogUtils;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.function.Supplier;

@Mod(CCDatabasePeripheralLite.MOD_ID)
public final class CCDatabasePeripheralLite {
    public static final String MOD_ID = "ccdbplite";
    public static final String MOD_NAME = "CCDatabasePeripheralLite";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CCDatabasePeripheralLite() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        registerGameObjects(modEventBus);
        modEventBus.addListener(this::gatherData);

        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, this::attachCapability);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(CCDatabasePeripheralLite.MOD_ID, path);
    }

    private static final ResourceLocation PERIPHERAL = new ResourceLocation(ComputerCraftAPI.MOD_ID, "peripheral");

    private void attachCapability(AttachCapabilitiesEvent<BlockEntity> event) {
        BlockEntity blockEntity = event.getObject();

        if (blockEntity instanceof DatabaseStorageBlockEntity database) {
            // Attach database peripheral to database block entity
            event.addCapability(PERIPHERAL, new DatabaseStoragePeripheral.Provider(database));
        }
    }

    private void registerGameObjects(IEventBus modEventBus) {
        var blockRegister = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
        var itemRegister = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
        var blockEntityTypeRegister = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID);
        var creativeModeTabRegister = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

        blockRegister.register("database_storage", () -> Blocks.DATABASE_STORAGE);
        itemRegister.register("database_storage", () -> Items.DATABASE_STORAGE);
        blockEntityTypeRegister.register("database_storage", () -> BlockEntityTypes.DATABASE_STORAGE);
        creativeModeTabRegister.register("general", () -> CreativeModeTabs.MAIN);

        blockRegister.register(modEventBus);
        itemRegister.register(modEventBus);
        blockEntityTypeRegister.register(modEventBus);
        creativeModeTabRegister.register(modEventBus);
    }

    public static final class Blocks {
        private static final Supplier<BlockBehaviour.Properties> PROPERTIES = () -> BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(2F).explosionResistance(2000.0F);

        public static final DatabaseStorageBlock DATABASE_STORAGE = new DatabaseStorageBlock(PROPERTIES.get());
    }

    public static final class BlockEntityTypes {
        public static final BlockEntityType<DatabaseStorageBlockEntity> DATABASE_STORAGE = BlockEntityType.Builder.of(DatabaseStorageBlockEntity::new, Blocks.DATABASE_STORAGE).build(null);
    }

    public static final class Items {
        public static final BlockItem DATABASE_STORAGE = new BlockItem(Blocks.DATABASE_STORAGE, new Item.Properties());
    }

    public static final class CreativeModeTabs {
        public static CreativeModeTab MAIN = CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.ccdbplite.main"))
                .icon(() -> new ItemStack(Blocks.DATABASE_STORAGE))
                .displayItems((params, output) -> output.accept(Items.DATABASE_STORAGE))
                .build();
    }

    public static final class Capabilities {
        public static final Capability<IPeripheral> CAPABILITY_PERIPHERAL = CapabilityManager.get(new CapabilityToken<>(){});
    }

    private void gatherData(final GatherDataEvent event) {
        if (event.includeServer()) {
            ServerModDataGenerator.gatherData(event);
        }

        if (event.includeClient()) {
            ClientModDataGenerator.gatherData(event);
        }
    }
}
