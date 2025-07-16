package com.andrei1058.bedwars.proxy;

import com.andrei1058.bedwars.proxy.api.BedWars;
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
import com.andrei1058.spigot.versionsupport.*;
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
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.logging.Logger;

public class BedWarsProxy extends JavaPlugin {

    private static BedWarsProxy plugin;
    private static BedWars api;
    public static BedWarsConfig config;
    private static Database remoteDatabase;
    private static StatsCache statsCache;

    private static SoundSupport soundAdapter;
    private static MaterialSupport materialAdapter;
    private static BlockSupport blockAdapter;
    private static ItemStackSupport itemAdapter;

    private static Party party;
    private static Level levelManager;

    private static final Logger LOGGER = Logger.getLogger("BedWarsProxy");

    @Override
    public void onLoad() {
        plugin = this;
        api = new API();
        Bukkit.getServicesManager().register(BedWars.class, api, this, ServicePriority.Highest);
    }

    @Override
    public void onEnable() {
        loadSupportAdapters();
        LanguageManager.init();

        config = new BedWarsConfig();
        initDatabase();

        statsCache = new StatsCache();
        setupSocketCommunication();

        registerListeners(
                new LangListeners(),
                new ArenaSelectorListener(),
                new CacheListener()
        );

        new SoundsConfig();
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getScheduler().runTaskTimer(this, new TimeOutTask(), 20L, 10L);

        setupParties();
        levelManager = new InternalLevel();
        Bukkit.getPluginManager().registerEvents(new LevelListeners(), this);

        registerCommands();
        registerPlaceholders();
        setupMetrics();
        SignManager.init();
    }

    @Override
    public void onDisable() {
        ServerSocketTask.stopTasks();
        Bukkit.getScheduler().cancelTasks(this);
    }

    private void loadSupportAdapters() {
        soundAdapter = SoundSupport.SupportBuilder.load();
        materialAdapter = MaterialSupport.SupportBuilder.load();
        blockAdapter = BlockSupport.SupportBuilder.load();
        itemAdapter = ItemStackSupport.SupportBuilder.load();

        if (soundAdapter == null) {
            soundAdapter = new sound_v1_18_R1();
        }
    }

    private void initDatabase() {
        if (config.getBoolean("database.enable")) {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                LOGGER.info("Connecting to MySQL database...");
                remoteDatabase = new MySQL();
                if (remoteDatabase instanceof NoDatabase) {
                    LOGGER.warning("Failed to connect to MySQL. Using NoDatabase fallback.");
                } else {
                    LOGGER.info("Successfully connected to MySQL.");
                }
            });
        } else {
            LOGGER.info("Database is disabled in config. Using NoDatabase.");
            remoteDatabase = new NoDatabase();
        }
    }

    private void setupSocketCommunication() {
        int port = config.getInt(ConfigPath.GENERAL_CONFIGURATION_PORT);
        if (!ServerSocketTask.init(port)) {
            LOGGER.severe("Could not register port: " + port);
            LOGGER.severe("Please change it in config! Port already in use!");
        } else {
            LOGGER.info("Listening for BedWars1058 arenas on port: " + port);
        }
    }

    private void setupParties() {
        if (!config.getYml().getBoolean(ConfigPath.GENERAL_CONFIGURATION_ALLOW_PARTIES)) {
            party = new Internal();
            LOGGER.info("Parties disabled. Using internal Party system.");
            return;
        }

        if (Bukkit.getPluginManager().isPluginEnabled("Parties")) {
            LOGGER.info("Hooked into Parties (by AlessioDP).");
            party = new Parties();
        } else if (Bukkit.getPluginManager().isPluginEnabled("Spigot-Party-API-PAF")) {
            LOGGER.info("Hooked into Party and Friends Extended Edition for BungeeCord.");
            party = new PAFBungeeCordParty();
        } else if (Bukkit.getPluginManager().isPluginEnabled("PartyAndFriends")) {
            LOGGER.info("Hooked into Party and Friends for Spigot.");
            party = new PAF();
        } else {
            party = new Internal();
            LOGGER.info("No external party plugin found. Using internal Party system.");
        }
    }

    private void registerCommands() {
        try {
            Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            commandMap.register("bw", new MainCommand("bw"));
            commandMap.register("rejoin", new RejoinCommand("rejoin"));

            if (config.getBoolean(ConfigPath.GENERAL_ENABLE_PARTY_CMD)) {
                commandMap.register("party", new PartyCommand("party"));
            }
        } catch (Exception e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Failed to register commands.", e);
        }
    }

    private void registerPlaceholders() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            LOGGER.info("Hooked into PlaceholderAPI.");
            new SupportPAPI().register();
        }
    }

    private void setupMetrics() {
        Metrics metrics = new Metrics(this, 6036);
        metrics.addCustomChart(new SimplePie("default_language", () -> LanguageManager.get().getDefaultLanguage().getIso()));
        metrics.addCustomChart(new SimplePie("party_adapter", () -> getParty().getClass().getName()));
        metrics.addCustomChart(new SimplePie("level_adapter", () -> getLevelManager().getClass().getName()));
    }

    private static void registerListeners(@NotNull Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, getPlugin());
        }
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
    public static TextComponent createTC(String text, String suggest, String hoverText) {
        TextComponent tx = new TextComponent(text);
        tx.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggest));
        tx.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverText).create()));
        return tx;
    }

    public static void setRemoteDatabase(Database remoteDatabase) {
        BedWarsProxy.remoteDatabase = remoteDatabase;
    }

    public static BedWars getAPI() {
        return api;
    }
}
