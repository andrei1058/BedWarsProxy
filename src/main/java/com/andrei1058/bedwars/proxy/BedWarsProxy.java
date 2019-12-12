package com.andrei1058.bedwars.proxy;

import com.andrei1058.bedwars.proxy.arenamanager.ArenaSelectorListener;
import com.andrei1058.bedwars.proxy.command.RejoinCommand;
import com.andrei1058.bedwars.proxy.command.main.MainCommand;
import com.andrei1058.bedwars.proxy.command.party.PartyCommand;
import com.andrei1058.bedwars.proxy.configuration.BedWarsConfig;
import com.andrei1058.bedwars.proxy.configuration.ConfigPath;
import com.andrei1058.bedwars.proxy.configuration.SoundsConfig;
import com.andrei1058.bedwars.proxy.database.Database;
import com.andrei1058.bedwars.proxy.database.MySQL;
import com.andrei1058.bedwars.proxy.database.NoDatabase;
import com.andrei1058.bedwars.proxy.database.StatsCache;
import com.andrei1058.bedwars.proxy.language.English;
import com.andrei1058.bedwars.proxy.language.LangListeners;
import com.andrei1058.bedwars.proxy.levels.Level;
import com.andrei1058.bedwars.proxy.levels.internal.InternalLevel;
import com.andrei1058.bedwars.proxy.levels.internal.LevelListeners;
import com.andrei1058.bedwars.proxy.party.Internal;
import com.andrei1058.bedwars.proxy.party.Parties;
import com.andrei1058.bedwars.proxy.party.Party;
import com.andrei1058.bedwars.proxy.socketmanager.ServerSocketTask;
import com.andrei1058.bedwars.proxy.socketmanager.TimeOutTask;
import com.andrei1058.bedwars.proxy.support.papi.SupportPAPI;
import com.andrei1058.spigot.versionsupport.BlockSupport;
import com.andrei1058.spigot.versionsupport.ItemStackSupport;
import com.andrei1058.spigot.versionsupport.MaterialSupport;
import com.andrei1058.spigot.versionsupport.SoundSupport;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class BedWarsProxy extends JavaPlugin {

    private static Plugin plugin;
    public static BedWarsConfig config;
    private static Database remoteDatabase = null;
    private static StatsCache statsCache;

    private static SoundSupport soundAdapter;
    private static MaterialSupport materialAdapter;
    private static BlockSupport blockAdapter;
    private static ItemStackSupport itemAdapter;

    private static Party party;
    private static Level levelManager;

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
        if (config.getBoolean("database.enable")) {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> remoteDatabase = new MySQL());
        } else {
            remoteDatabase = new NoDatabase();
        }
        statsCache = new StatsCache();

        //todo make port configurable
        if (!ServerSocketTask.init(config.getInt(ConfigPath.GENERAL_CONFIGURATION_PORT))) {
            //todo could not register socket on given port
        }

        registerListeners(new LangListeners(), new ArenaSelectorListener());
        new SoundsConfig();

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        Bukkit.getScheduler().runTaskTimer(this, new TimeOutTask(), 20L, 10L);

        //Party support
        if (config.getYml().getBoolean(ConfigPath.GENERAL_CONFIGURATION_ALLOW_PARTIES)) {
            if (Bukkit.getPluginManager().getPlugin("Parties") != null) {
                getLogger().info("Hook into Parties (by AlessioDP) support!");
                party = new Parties();
            }
        }
        if (party == null) {
            party = new Internal();
            getLogger().info("Loading internal Party system. /party");
        }

        levelManager = new InternalLevel();
        Bukkit.getPluginManager().registerEvents(new LevelListeners(), this);

        try {
            Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            commandMap.register("bw", new MainCommand("bw"));
            commandMap.register("rejoin", new RejoinCommand("rejoin"));
            if (config.getBoolean(ConfigPath.GENERAL_ENABLE_PARTY_CMD)) {
                commandMap.register("party", new PartyCommand("party"));
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        /* PlaceholderAPI Support */
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getLogger().info("Hook into PlaceholderAPI support!");
            new SupportPAPI().register();
        }

        new Metrics(this);
    }

    @Override
    public void onDisable() {
        ServerSocketTask.stopTasks();
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

    private static void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, getPlugin());
        }
    }

    public static Party getParty() {
        return party;
    }

    public static Level getLevelManager() {
        return levelManager;
    }

    /**
     * Create a text component.
     */
    public static TextComponent createTC(String text, String suggest, String shot_text) {
        TextComponent tx = new TextComponent(text);
        tx.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggest));
        tx.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(shot_text).create()));
        return tx;
    }
}
