package com.andrei1058.bedwars.proxy;

import com.andrei1058.bedwars.proxy.api.BedWars;
import com.andrei1058.bedwars.proxy.arenamanager.ArenaManager;
import com.andrei1058.bedwars.proxy.arenamanager.ArenaSelectorListener;
import com.andrei1058.bedwars.proxy.arenasign.SignManager;
import com.andrei1058.bedwars.proxy.command.RejoinCommand;
import com.andrei1058.bedwars.proxy.command.main.MainCommand;
import com.andrei1058.bedwars.proxy.command.party.PartyCommand;
import com.andrei1058.bedwars.proxy.configuration.BedWarsConfig;
import com.andrei1058.bedwars.proxy.configuration.ConfigPath;
import com.andrei1058.bedwars.proxy.configuration.SoundsConfig;
import com.andrei1058.bedwars.proxy.database.*;
import com.andrei1058.bedwars.proxy.language.LangListeners;
import com.andrei1058.bedwars.proxy.language.LanguageManager;
import com.andrei1058.bedwars.proxy.levels.Level;
import com.andrei1058.bedwars.proxy.levels.internal.InternalLevel;
import com.andrei1058.bedwars.proxy.levels.internal.LevelListeners;
import com.andrei1058.bedwars.proxy.party.*;
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
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class BedWarsProxy extends JavaPlugin implements BedWars {

    private static BedWarsProxy plugin;
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
    }

    @Override
    public void onEnable() {
        soundAdapter = SoundSupport.SupportBuilder.load();
        materialAdapter = MaterialSupport.SupportBuilder.load();
        blockAdapter = BlockSupport.SupportBuilder.load();
        itemAdapter = ItemStackSupport.SupportBuilder.load();

        LanguageManager.init();
        config = new BedWarsConfig();
        if (config.getBoolean("database.enable")) {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> remoteDatabase = new MySQL());
        } else {
            remoteDatabase = new NoDatabase();
        }
        statsCache = new StatsCache();

        if (!ServerSocketTask.init(config.getInt(ConfigPath.GENERAL_CONFIGURATION_PORT))) {
            getLogger().severe("Could not register port: " + config.getInt(ConfigPath.GENERAL_CONFIGURATION_PORT));
            getLogger().severe("Please change it in config! Port already in use!");
        }

        getLogger().info("Listening for BedWars1058 arenas on port: " + config.getInt(ConfigPath.GENERAL_CONFIGURATION_PORT));

        registerListeners(new LangListeners(), new ArenaSelectorListener(), new CacheListener());
        //noinspection InstantiationOfUtilityClass
        new SoundsConfig();

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        Bukkit.getScheduler().runTaskTimer(this, new TimeOutTask(), 20L, 10L);

        //Party support
        if (config.getYml().getBoolean(ConfigPath.GENERAL_CONFIGURATION_ALLOW_PARTIES)) {
            if (Bukkit.getServer().getPluginManager().isPluginEnabled("Parties")) {
                getLogger().info("Hook into Parties (by AlessioDP) support!");
                party = new Parties();
            } else if (Bukkit.getServer().getPluginManager().isPluginEnabled("Spigot-Party-API-PAF")) {
                getLogger().info("Hook into Party and Friends Extended Edition for BungeeCord (by Simonsator) support!");
                party = new PAFBungeeCordParty();
            } else if (Bukkit.getServer().getPluginManager().isPluginEnabled("PartyAndFriends")) {
                getLogger().info("Hook into Party and Friends for Spigot (by Simonsator) support!");
                party = new PAF();
            } else  if (Bukkit.getServer().getPluginManager().isPluginEnabled("PartyAndFriendsGUI")){
                getLogger().info("Hook into DataCallBackAPI for Party and Friends Bungee (by Simonsator) support!");
                party = new PAFDataCallBack();
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

        Metrics m = new Metrics(this, 6036);
        m.addCustomChart(new SimplePie("default_language", () -> LanguageManager.get().getDefaultLanguage().getIso()));
        m.addCustomChart(new SimplePie("party_adapter", () -> getParty().getClass().getName()));
        m.addCustomChart(new SimplePie("level_adapter", () -> getLevelManager().getClass().getName()));
        SignManager.init();
    }

    @Override
    public void onDisable() {
        ServerSocketTask.stopTasks();
        Bukkit.getScheduler().cancelTasks(this);
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

    @SuppressWarnings("unused")
    public static BlockSupport getBlockAdapter() {
        return blockAdapter;
    }

    public static ItemStackSupport getItemAdapter() {
        return itemAdapter;
    }

    public static SoundSupport getSoundAdapter() {
        return soundAdapter;
    }

    private static void registerListeners(@NotNull Listener... listeners) {
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
    @NotNull
    public static TextComponent createTC(String text, String suggest, String shot_text) {
        TextComponent tx = new TextComponent(text);
        tx.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggest));
        tx.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(shot_text).create()));
        return tx;
    }

    public static void setRemoteDatabase(Database remoteDatabase) {
        BedWarsProxy.remoteDatabase = remoteDatabase;
    }

    @Override
    public LanguageUtil getLanguageUtil() {
        return LanguageManager.get();
    }

    @Override
    public ArenaUtil getArenaUtil() {
        return ArenaManager.getInstance();
    }

    public static BedWars getAPI() {
        return BedWarsProxy.plugin;
    }
}
