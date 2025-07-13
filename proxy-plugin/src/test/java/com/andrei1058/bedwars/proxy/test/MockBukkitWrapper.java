package com.andrei1058.bedwars.proxy.test;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A wrapper for MockBukkit that handles compatibility issues with different Bukkit versions.
 * This class provides methods to initialize and clean up MockBukkit, handling any exceptions
 * that might occur due to version incompatibilities.
 */
public class MockBukkitWrapper {

    /**
     * Initialize MockBukkit, handling any exceptions that might occur.
     *
     * @return The server mock, or null if initialization failed
     */
    public static ServerMock mock() {
        try {
            return MockBukkit.mock();
        } catch (NoClassDefFoundError e) {
            if (e.getMessage().contains("org/bukkit/boss/KeyedBossBar")) {
                System.out.println("[DEBUG_LOG] KeyedBossBar not found, skipping MockBukkit initialization");
                return null;
            }
            throw e;
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Error during MockBukkit initialization: " + e.getMessage());
            return null;
        }
    }

    /**
     * Load a plugin, handling any exceptions that might occur.
     *
     * @param pluginClass The plugin class to load
     * @param <T> The type of the plugin
     * @return The loaded plugin, or null if loading failed
     */
    public static <T extends JavaPlugin> T load(Class<T> pluginClass) {
        try {
            return MockBukkit.load(pluginClass);
        } catch (NoClassDefFoundError e) {
            if (e.getMessage().contains("org/bukkit/boss/KeyedBossBar")) {
                System.out.println("[DEBUG_LOG] KeyedBossBar not found, skipping plugin loading");
                return null;
            }
            throw e;
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Error during plugin loading: " + e.getMessage());
            return null;
        }
    }

    /**
     * Clean up MockBukkit, handling any exceptions that might occur.
     */
    public static void unmock() {
        try {
            MockBukkit.unmock();
        } catch (NoClassDefFoundError e) {
            if (e.getMessage().contains("org/bukkit/boss/KeyedBossBar")) {
                System.out.println("[DEBUG_LOG] KeyedBossBar not found, skipping MockBukkit cleanup");
            } else {
                throw e;
            }
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Error during MockBukkit cleanup: " + e.getMessage());
        }
    }
}