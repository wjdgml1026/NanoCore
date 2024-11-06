package gg.mc.nanoserver.nanoCore.listener;

import gg.mc.nanoserver.nanoCore.interfaces.CustomInventory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class InventoryEvents implements Listener {

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (e.getInventory().getHolder() instanceof CustomInventory holder) {
            holder.onInventoryOpen(e);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof CustomInventory holder) {
            holder.onInventoryClick(e);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof CustomInventory holder) {
            holder.onInventoryDrag(e);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof CustomInventory holder) {
            holder.onInventoryClose(e);
        }
    }
}