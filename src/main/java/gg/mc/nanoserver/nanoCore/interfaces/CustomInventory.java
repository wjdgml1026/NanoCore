package gg.mc.nanoserver.nanoCore.interfaces;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;

public interface CustomInventory extends InventoryHolder {
    void onInventoryClick(InventoryClickEvent e);
    default void onInventoryOpen(InventoryOpenEvent e) { }
    default void onInventoryClose(InventoryCloseEvent e) { }
    default void onInventoryDrag(InventoryDragEvent e) {
        e.setCancelled(true);
    }
}

