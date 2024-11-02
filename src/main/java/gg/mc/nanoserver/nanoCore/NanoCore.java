package gg.mc.nanoserver.nanoCore;

import gg.mc.nanoserver.nanoCore.command.TradeCommand;
import gg.mc.nanoserver.nanoCore.listener.EntityListener;
import gg.mc.nanoserver.nanoCore.listener.InventoryEvents;
import gg.mc.nanoserver.nanoCore.manager.TradeManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class NanoCore extends JavaPlugin {
    public static NanoCore plugin;
    public static TradeManager tradeManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        tradeManager = new TradeManager();

        Objects.requireNonNull(getCommand("거래")).setExecutor(new TradeCommand());
        getServer().getPluginManager().registerEvents(new InventoryEvents(), this);
        getServer().getPluginManager().registerEvents(new EntityListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
