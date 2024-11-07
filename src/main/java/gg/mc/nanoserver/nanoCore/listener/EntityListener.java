package gg.mc.nanoserver.nanoCore.listener;

import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import static gg.mc.nanoserver.nanoCore.NanoCore.tradeManager;

public class EntityListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof Player target) {
            if (e.getPlayer().isSneaking()) {
                tradeManager.request(e.getPlayer(), target);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player player) {
            NBT.get(player.getInventory().getItemInMainHand(), nbt -> {
                int lv = nbt.getOrDefault("UpgradeLevel", 0);
                e.setDamage(e.getDamage() + lv);
            });
            player.sendMessage(e.getDamage() + " 데미지");
        }
    }
}
