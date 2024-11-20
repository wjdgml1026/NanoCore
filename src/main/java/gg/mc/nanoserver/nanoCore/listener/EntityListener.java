package gg.mc.nanoserver.nanoCore.listener;

import de.tr7zw.changeme.nbtapi.NBT;
import gg.mc.nanoserver.nanoCore.holder.UpgradeInventory;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import static gg.mc.nanoserver.nanoCore.NanoCore.tradeManager;

public class EntityListener implements Listener {

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof Player target) {
            if (e.getPlayer().isSneaking()) {
                tradeManager.request(e.getPlayer(), target);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player player) {
            NBT.get(player.getInventory().getItemInMainHand(), nbt -> {
                int lv = nbt.getOrDefault("UpgradeLevel", 0);
                double n = UpgradeInventory.SWORD_DAMAGE[lv];
                double damage = e.getDamage() + n;
                if (player.hasPermission("nano.upgrade")) {
                    player.sendMessage(String.format("데미지: %.1f ( +%.1f ) → %.1f", e.getDamage(), n, damage));
                }
                e.setDamage(damage);
            });
        } else if (e.getEntity() instanceof Player player) {
            final int[] lv = {0};

            ItemStack item = player.getInventory().getHelmet();
            if (item != null) {
                NBT.get(item, nbt -> { lv[0] += nbt.getOrDefault("UpgradeLevel", 0); });
            }
            item = player.getInventory().getChestplate();
            if (item != null) {
                NBT.get(item, nbt -> { lv[0] += nbt.getOrDefault("UpgradeLevel", 0); });
            }
            item = player.getInventory().getLeggings();
            if (item != null) {
                NBT.get(item, nbt -> { lv[0] += nbt.getOrDefault("UpgradeLevel", 0); });
            }
            item = player.getInventory().getBoots();
            if (item != null) {
                NBT.get(item, nbt -> { lv[0] += nbt.getOrDefault("UpgradeLevel", 0); });
            }
            double n = 1 - 0.025 * lv[0];
            double damage = e.getDamage() * n;
            if (player.hasPermission("nano.upgrade")) {
                player.sendMessage(String.format("데미지: %.1f ( %.1f%% ) → %.1f", e.getDamage(), n*100, damage));
            }
            e.setDamage(damage);
        }
    }
}
