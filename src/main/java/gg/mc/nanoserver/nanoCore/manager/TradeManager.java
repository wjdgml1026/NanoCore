package gg.mc.nanoserver.nanoCore.manager;

import gg.mc.nanoserver.nanoCore.holder.TradeInventory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

import static gg.mc.nanoserver.nanoCore.NanoCore.plugin;

public class TradeManager {
    private static final int TRADE_TIMER = 15;
    private final HashMap<UUID, UUID> request = new HashMap<>();

    public void request(Player player, Player target) {
        if (request.containsKey(player.getUniqueId())) return;

        TextComponent message = Component.text()
                .append(Component.text(player.getName(), NamedTextColor.GOLD))
                .append(Component.text(" 님이 거래를 요청했습니다. ", NamedTextColor.YELLOW))
                .append(
                        Component.text("[ 수락 ]", NamedTextColor.GREEN)
                                .clickEvent(ClickEvent.runCommand("/거래 수락 " + player.getUniqueId()))
                                .hoverEvent(HoverEvent.showText(Component.text("수락", NamedTextColor.GREEN)))
                )
                .append(Component.space())
                .append(
                        Component.text("[ 거절 ]", NamedTextColor.RED)
                                .clickEvent(ClickEvent.runCommand("/거래 거절 " + player.getUniqueId()))
                                .hoverEvent(HoverEvent.showText(Component.text("거절", NamedTextColor.RED)))
                )
                .build();

        target.sendMessage("");
        target.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_TRADE, 1.5f, 0.8f);
        target.sendMessage(message);

        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_TRADE, 1.5f, 0.8f);
        player.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.YELLOW + " 님에게 거래를 요청했습니다.");

        request.put(player.getUniqueId(), target.getUniqueId());

        final int[] ticks = {0};
        plugin.getServer().getScheduler().runTaskTimer(plugin, task -> {
            if (!request.containsKey(player.getUniqueId())) {
                task.cancel();
            } else if (ticks[0] == TRADE_TIMER) {
                request.remove(player.getUniqueId());
                target.playSound(player.getLocation(), Sound.ENTITY_WANDERING_TRADER_NO, 1.5f, 0.8f);
                target.sendMessage(ChatColor.RED + player.getName() + " 님이 보낸 거래 요청이 만료됬습니다...");
                player.playSound(player.getLocation(), Sound.ENTITY_WANDERING_TRADER_NO, 1.5f, 0.8f);
                player.sendMessage(ChatColor.RED + target.getName() + " 님에게 보낸 거래 요청이 만료됬습니다...");
                task.cancel();
            }
            ticks[0]++;
        }, 0, 20);
    }

    public void accept(Player target, UUID uuid) {
        if (target.getUniqueId().equals(request.get(uuid))) {
            request.remove(uuid);
            if (uuid.equals(request.get(target.getUniqueId())))
                request.remove(target.getUniqueId());

            Player player = plugin.getServer().getPlayer(uuid);
            if ( player != null && player.isOnline() ) {
                TradeInventory holder1 = new TradeInventory(player, target);
                TradeInventory holder2 = new TradeInventory(target, player);
                holder1.setOtherHolder(holder2);
                holder2.setOtherHolder(holder1);

                player.openInventory(holder1.getInventory());
                player.playSound(target.getLocation(), Sound.ENTITY_WANDERING_TRADER_YES, 1.5f, 0.8f);
                target.openInventory(holder2.getInventory());
                target.playSound(target.getLocation(), Sound.ENTITY_WANDERING_TRADER_YES, 1.5f, 0.8f);
            } else {
                target.playSound(target.getLocation(), Sound.ENTITY_WANDERING_TRADER_NO, 1.5f, 0.8f);
                target.sendMessage(ChatColor.RED + "상대방이 오프라인입니다.");
            }
        } else {
            target.sendMessage(ChatColor.RED + "요청 받은 거래 내역이 없습니다.");
        }
    }

    public void deny(Player target, UUID uuid) {
        if (target.getUniqueId().equals(request.get(uuid))) {
            request.remove(uuid);
            Player player = plugin.getServer().getPlayer(uuid);

            target.playSound(target.getLocation(), Sound.ENTITY_WANDERING_TRADER_NO, 1.5f, 0.8f);
            target.sendMessage(ChatColor.RED + "거래 요청을 거절했습니다.");
            if ( player != null && player.isOnline() ) {
                player.playSound(target.getLocation(), Sound.ENTITY_WANDERING_TRADER_NO, 1.5f, 0.8f);
                player.sendMessage(ChatColor.RED + target.getName() + " 님에게 거래 요청을 거절했습니다.");
            }
        } else {
            target.sendMessage(ChatColor.RED + "요청 받은 거래 내역이 없습니다.");
        }
    }
}
