package gg.mc.nanoserver.nanoCore.listener;

import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import gg.mc.nanoserver.nanoCore.holder.UpgradeInventory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ItemsAdderEvents implements Listener {
    @EventHandler
    public void onLoadDataEvent(ItemsAdderLoadDataEvent e) {
        UpgradeInventory.MATERIAL = CustomStack.getInstance("upgrade_stone").getItemStack();
    }
}
