package gg.mc.nanoserver.nanoCore.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
}
