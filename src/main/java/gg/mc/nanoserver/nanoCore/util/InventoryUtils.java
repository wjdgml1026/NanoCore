package gg.mc.nanoserver.nanoCore.util;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InventoryUtils {
    public static int countItems(@NotNull Inventory inventory, @NotNull ItemStack item) {
        int count = 0;
        for (ItemStack i : inventory.getStorageContents()) {
            if ( i != null && item.isSimilar(i) ) {
                count += i.getAmount();
            }
        }
        return count;
    }

    public static boolean hasItem(@NotNull Inventory inventory, @NotNull ItemStack item) {
        for (ItemStack i : inventory.getStorageContents()) {
            if ( i != null && item.isSimilar(i) ) {
                return true;
            }
        }
        return false;
    }

    public static void removeItems(@NotNull Inventory inventory, @NotNull ItemStack item) {
        removeItems(inventory, item, 1);
    }

    public static void removeItems(@NotNull Inventory inventory, @NotNull ItemStack item, int amount) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack it = inventory.getItem(i);
            if ( it != null && item.isSimilar(it) ) {
                if (amount < it.getAmount()) {
                    it.setAmount(it.getAmount() - amount);
                    return;
                } else {
                    amount -= it.getAmount();
                    inventory.clear(i);
                }
            }
        }
    }
}
