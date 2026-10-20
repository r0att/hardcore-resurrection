package pl.r0att.hardcoreresurrection;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class ModBlocks {
    public static final Block REVIVAL_BLOCK = new Block(
        BlockBehaviour.Properties.of().strength(3.5F)
    );

    private ModBlocks() {}

    public static void initialize() {
        Registry.register(
            BuiltInRegistries.BLOCK,
            HardcoreResurrection.id("revival_block"),
            REVIVAL_BLOCK
        );

        Registry.register(
            BuiltInRegistries.ITEM,
            HardcoreResurrection.id("revival_block"),
            new BlockItem(REVIVAL_BLOCK, new Item.Properties())
        );

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
            .register(entries -> entries.accept(REVIVAL_BLOCK));
    }
}
