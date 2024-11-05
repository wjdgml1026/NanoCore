package gg.mc.nanoserver.nanoCore.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static gg.mc.nanoserver.nanoCore.NanoCore.tradeManager;

public class TradeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player && args.length == 2) {
            if (args[0].equals("수락")) {
                tradeManager.accept(player, UUID.fromString(args[1]));
                return true;
            } else if (args[0].equals("거절")) {
                tradeManager.deny(player, UUID.fromString(args[1]));
                return true;
            }
        }
        return false;
    }
}
