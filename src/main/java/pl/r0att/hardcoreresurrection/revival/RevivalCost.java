
package pl.r0att.hardcoreresurrection.revival;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class RevivalCost {
    private final List<ItemCost> items;
    private final int xpLevels;

    private RevivalCost(List<ItemCost> items, int xpLevels) {
        this.items = List.copyOf(items);
        this.xpLevels = xpLevels;
    }

    public static RevivalCost fromSnapshot(
        CompoundTag snapshot,
        HolderLookup.Provider registries
    ) {
        List<ItemCost> items = new ArrayList<>();
        ListTag inventory = snapshot.getList("inventory", 10);

        for (int i = 0; i < inventory.size(); i++) {
            CompoundTag itemData = inventory.getCompound(i);

            ItemStack stack = ItemStack.parse(
                registries,
                itemData.getCompound("stack")
            ).orElse(ItemStack.EMPTY);

            if (stack.isEmpty()) {
                continue;
            }

            addItem(items, stack, stack.getCount());
        }

        int xpLevels = Math.max(0, snapshot.getInt("xp_level"));

        return new RevivalCost(items, xpLevels);
    }

    public boolean canAfford(Player player) {
        List<ItemStack> requiredItems = new ArrayList<>();

        for (ItemCost item : items) {
            requiredItems.add(item.stack().copyWithCount(item.count()));
        }

        List<ItemStack> availableItems = new ArrayList<>();
        int inventorySize = player.getInventory().getContainerSize();

        for (int slot = 0; slot < inventorySize; slot++) {
            ItemStack stack = player.getInventory().getItem(slot);

            if (!stack.isEmpty()) {
                availableItems.add(stack);
            }
        }

        return canAfford(
            player.experienceLevel,
            xpLevels,
            requiredItems,
            availableItems
        );
    }

    static boolean canAfford(
        int availableXpLevels,
        int requiredXpLevels,
        List<ItemStack> requiredItems,
        List<ItemStack> availableItems
    ) {
        if (availableXpLevels < requiredXpLevels) {
            return false;
        }

        List<ItemStack> required = mergeStacks(requiredItems);
        List<ItemStack> available = mergeStacks(availableItems);

        for (ItemStack requiredStack : required) {
            int availableCount = 0;

            for (ItemStack availableStack : available) {
                if (ItemStack.isSameItemSameComponents(
                    requiredStack,
                    availableStack
                )) {
                    availableCount = availableStack.getCount();
                    break;
                }
            }

            if (availableCount < requiredStack.getCount()) {
                return false;
            }
        }

        return true;
    }

    public int getXpLevels() {
        return xpLevels;
    }

    private static List<ItemStack> mergeStacks(List<ItemStack> stacks) {
        List<ItemStack> merged = new ArrayList<>();

        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                addStack(merged, stack);
            }
        }

        return merged;
    }

    private static void addStack(List<ItemStack> stacks, ItemStack stack) {
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack existing = stacks.get(i);

            if (ItemStack.isSameItemSameComponents(existing, stack)) {
                stacks.set(
                    i,
                    existing.copyWithCount(
                        existing.getCount() + stack.getCount()
                    )
                );
                return;
            }
        }

        stacks.add(stack.copy());
    }

    private static void addItem(
        List<ItemCost> items,
        ItemStack stack,
        int count
    ) {
        for (int i = 0; i < items.size(); i++) {
            ItemCost item = items.get(i);

            if (ItemStack.isSameItemSameComponents(item.stack(), stack)) {
                items.set(
                    i,
                    new ItemCost(item.stack(), item.count() + count)
                );
                return;
            }
        }

        items.add(new ItemCost(stack.copyWithCount(1), count));
    }

    private record ItemCost(ItemStack stack, int count) {}
}
