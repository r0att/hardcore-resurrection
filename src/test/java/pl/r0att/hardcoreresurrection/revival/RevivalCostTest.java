
package pl.r0att.hardcoreresurrection.revival;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.SharedConstants;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.server.Bootstrap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RevivalCostTest {
    @BeforeAll
    static void initializeMinecraft() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }
    
    @Test
    void shouldAffordWhenEnoughItemsAndXpAreAvailable() {
        ItemStack required = new ItemStack(Items.DIAMOND, 5);
        ItemStack available = new ItemStack(Items.DIAMOND, 10);

        assertTrue(RevivalCost.canAfford(
            10,
            5,
            List.of(required),
            List.of(available)
        ));
    }

    @Test
    void shouldNotAffordWhenItemsAreMissing() {
        assertFalse(RevivalCost.canAfford(
            10,
            0,
            List.of(new ItemStack(Items.DIAMOND, 5)),
            List.of(new ItemStack(Items.IRON_INGOT, 64))
        ));
    }

    @Test
    void shouldSumItemsAcrossMultipleSlots() {
        assertTrue(RevivalCost.canAfford(
            5,
            0,
            List.of(new ItemStack(Items.DIAMOND, 10)),
            List.of(
                new ItemStack(Items.DIAMOND, 6),
                new ItemStack(Items.DIAMOND, 4)
            )
        ));
    }

    @Test
    void shouldNotAffordWhenXpIsInsufficient() {
        assertFalse(RevivalCost.canAfford(
            4,
            5,
            List.of(),
            List.of()
        ));
    }

    @Test
    void shouldTreatDifferentComponentsAsDifferentItems() {
        ItemStack required = new ItemStack(Items.DIAMOND, 1);
        required.set(
            DataComponents.CUSTOM_NAME,
            Component.literal("Required diamond")
        );

        ItemStack available = new ItemStack(Items.DIAMOND, 64);

        assertFalse(RevivalCost.canAfford(
            10,
            0,
            List.of(required),
            List.of(available)
        ));
    }

    @Test
    void shouldMergeRequiredStacksOfTheSameType() {
        assertTrue(RevivalCost.canAfford(
            10,
            0,
            List.of(
                new ItemStack(Items.DIAMOND, 3),
                new ItemStack(Items.DIAMOND, 4)
            ),
            List.of(new ItemStack(Items.DIAMOND, 7))
        ));
    }
}
