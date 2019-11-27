package com.andrei1058.bedwars.proxy;

import com.andrei1058.bedwars.proxy.arenamanager.ArenaSelectorListener;
import com.andrei1058.bedwars.proxy.arenamanager.RefreshGUI;
import com.andrei1058.bedwars.proxy.configuration.BedWarsConfig;
import com.andrei1058.bedwars.proxy.configuration.SoundsConfig;
import com.andrei1058.bedwars.proxy.database.Database;
import com.andrei1058.bedwars.proxy.database.MySQL;
import com.andrei1058.bedwars.proxy.database.NoDatabase;
import com.andrei1058.bedwars.proxy.database.StatsCache;
import com.andrei1058.bedwars.proxy.language.English;
import com.andrei1058.bedwars.proxy.language.LangListeners;
import com.andrei1058.bedwars.proxy.language.Language;
import com.andrei1058.bedwars.proxy.socketmanager.ServerSocketTask;
import com.andrei1058.spigot.versionsupport.BlockSupport;
import com.andrei1058.spigot.versionsupport.ItemStackSupport;
import com.andrei1058.spigot.versionsupport.MaterialSupport;
import com.andrei1058.spigot.versionsupport.SoundSupport;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class BedWarsProxy extends JavaPlugin {

    private static String defaultLanguage = "ro";
    private static Plugin plugin;
    public static BedWarsConfig config;
    private static Database remoteDatabase = null;
    private static StatsCache statsCache;

    private static SoundSupport soundAdapter;
    private static MaterialSupport materialAdapter;
    private static BlockSupport blockAdapter;
    private static ItemStackSupport itemAdapter;

    @Override
    public void onLoad() {
        plugin = this;
        // Setup languages
        new English();
    }

    @Override
    public void onEnable() {
        soundAdapter = SoundSupport.SupportBuilder.load();
        materialAdapter = MaterialSupport.SupportBuilder.load();
        blockAdapter = BlockSupport.SupportBuilder.load();
        itemAdapter = ItemStackSupport.SupportBuilder.load();

        config = new BedWarsConfig();
        if (config.getBoolean("database.enable")){
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> remoteDatabase = new MySQL());
        } else {
            remoteDatabase = new NoDatabase();
        }
        statsCache = new StatsCache();

        //todo make port configurable
        if (!ServerSocketTask.init(25569)){
            //todo could not register socket on given port
        }

        //Leave this code at the end of the enable method
        for (Language l : Language.getLanguages()) {
            Language.addDefaultMessagesCommandItems(l);
        }

        registerListeners(new LangListeners(), new RefreshGUI(), new ArenaSelectorListener());
        new SoundsConfig();
    }

    @Override
    public void onDisable() {
        ServerSocketTask.stopTasks();
    }

    public static String getDefaultLanguage() {
        return defaultLanguage;
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static Database getRemoteDatabase() {
        return remoteDatabase;
    }

    public static StatsCache getStatsCache() {
        return statsCache;
    }

    public static MaterialSupport getMaterialAdapter() {
        return materialAdapter;
    }

    public static BlockSupport getBlockAdapter() {
        return blockAdapter;
    }

    public static ItemStackSupport getItemAdapter() {
        return itemAdapter;
    }

    public static SoundSupport getSoundAdapter() {
        return soundAdapter;
    }

    private static void registerListeners(Listener... listeners){
        for (Listener listener : listeners){
            Bukkit.getPluginManager().registerEvents(listener, getPlugin());
        }
    }
}
