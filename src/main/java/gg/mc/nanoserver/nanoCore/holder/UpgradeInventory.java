package gg.mc.nanoserver.nanoCore.holder;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static gg.mc.nanoserver.nanoCore.NanoCore.plugin;

public class UpgradeInventory implements InventoryHolder {
    private final static ItemStack PREV_INFO, PREV_ITEM, ANVIL, RESULT_INFO, RESULT_ITEM;
    private final static int PREV_INFO_SLOT = 2;
    private final static int PREV_ITEM_SLOT = 11;
    private final static int ANVIL_SLOT = 13;
    private final static int RESULT_INFO_SLOT = 6;
    private final static int RESULT_ITEM_SLOT = 15;

    private final Inventory inventory;
    private final Player player;

    static {
        ItemMeta itemMeta;

        PREV_INFO = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        itemMeta = PREV_INFO.getItemMeta();
        itemMeta.setDisplayName("장비");
        PREV_INFO.setItemMeta(itemMeta);

        PREV_ITEM = new ItemStack(Material.IRON_NUGGET);
        itemMeta = PREV_ITEM.getItemMeta();
        itemMeta.setDisplayName("장비");
        PREV_ITEM.setItemMeta(itemMeta);

        ANVIL = new ItemStack(Material.ANVIL);
        itemMeta = ANVIL.getItemMeta();
        itemMeta.setDisplayName("강화");
        ANVIL.setItemMeta(itemMeta);

        RESULT_INFO = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        itemMeta = RESULT_INFO.getItemMeta();
        itemMeta.setDisplayName("결과");
        RESULT_INFO.setItemMeta(itemMeta);

        RESULT_ITEM = new ItemStack(Material.IRON_NUGGET);
        itemMeta = RESULT_ITEM.getItemMeta();
        itemMeta.setDisplayName("결과");
        RESULT_ITEM.setItemMeta(itemMeta);
    }

    public UpgradeInventory(Player player) {
        this.inventory = plugin.getServer().createInventory(this, 36, "강화");
        this.player = player;

        inventory.setItem(PREV_INFO_SLOT, PREV_INFO);
        inventory.setItem(PREV_ITEM_SLOT, PREV_ITEM);
        inventory.setItem(ANVIL_SLOT, ANVIL);
        inventory.setItem(RESULT_INFO_SLOT, RESULT_INFO);
        inventory.setItem(RESULT_ITEM_SLOT, RESULT_ITEM);

        player.openInventory(this.inventory);
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public Player getPlayer() {
        return player;
    }
}
