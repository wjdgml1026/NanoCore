package gg.mc.nanoserver.nanoCore.command;

import gg.mc.nanoserver.nanoCore.holder.UpgradeInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static gg.mc.nanoserver.nanoCore.NanoCore.tradeManager;

public class UpgradeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            new UpgradeInventory(player);
            return true;
        }
        return false;
    }
}
