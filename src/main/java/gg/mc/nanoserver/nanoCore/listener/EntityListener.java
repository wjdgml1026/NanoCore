package gg.mc.nanoserver.nanoCore.listener;

import de.tr7zw.changeme.nbtapi.NBT;
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
        } else if (e.getDamager() instanceof Arrow arrow) {
            if (arrow.getShooter() instanceof Player player) {
                NBT.get(player.getInventory().getItemInMainHand(), nbt -> {
                    int lv = nbt.getOrDefault("UpgradeLevel", 0);
                    e.setDamage(e.getDamage() + lv);
                });
                player.sendMessage(e.getDamage() + " 데미지");
            }
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

            if (e.getDamage() > lv[0]) {
                e.setDamage(e.getDamage() - lv[0]);
            } else {
                e.setDamage(1);
            }
            player.sendMessage(e.getDamage() + " 데미지");
        }
    }

//    @EventHandler
//    public void onShot(EntityShootBowEvent e) {
//        if (e.getEntity() instanceof Player player) {
//            if (e.getProjectile() instanceof Arrow arrow) {
//                NBT.get(player.getInventory().getItemInMainHand(), nbt -> {
//                    int lv = nbt.getOrDefault("UpgradeLevel", 0);
//                    arrow.getVelocity().multiply(lv);
//                });
//            }
//        }
//    }
}
