package gg.mc.nanoserver.nanoCore;

import de.tr7zw.changeme.nbtapi.NBT;
import gg.mc.nanoserver.nanoCore.command.TradeCommand;
import gg.mc.nanoserver.nanoCore.command.UpgradeCommand;
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

        if (!NBT.preloadApi()) {
            getLogger().warning("NBT-API wasn't initialized properly, disabling the plugin");
            getPluginLoader().disablePlugin(this);
            return;
        }

        Objects.requireNonNull(getCommand("거래")).setExecutor(new TradeCommand());
        Objects.requireNonNull(getCommand("강화")).setExecutor(new UpgradeCommand());
        getServer().getPluginManager().registerEvents(new InventoryEvents(), this);
        getServer().getPluginManager().registerEvents(new EntityListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
