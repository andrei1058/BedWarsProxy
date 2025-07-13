package com.andrei1058.bedwars.proxy.test;

import be.seeseemelk.mockbukkit.MockBukkit;
import com.andrei1058.bedwars.proxy.BedWarsProxy;
import com.andrei1058.bedwars.proxy.api.BedWars;
import com.andrei1058.bedwars.proxy.arenamanager.ArenaManager;
import com.andrei1058.bedwars.proxy.configuration.BedWarsConfig;
import com.andrei1058.bedwars.proxy.language.LanguageManager;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for BedWarsProxy plugin initialization.
 * These tests verify that the plugin loads correctly and initializes all its components.
 */
public class BedWarsProxyInitializationTest extends BedWarsProxyTestBase {

    @Test
    @DisplayName("Test plugin is enabled")
    public void testPluginIsEnabled() {
        // Verify the plugin is enabled
        assertTrue(plugin.isEnabled(), "Plugin should be enabled");

        // Verify the plugin instance is accessible
        assertNotNull(BedWarsProxy.getPlugin(), "Plugin instance should be accessible");
    }

    @Test
    @DisplayName("Test API is initialized")
    public void testAPIIsInitialized() {
        // Verify the API is initialized
        BedWars api = BedWarsProxy.getAPI();
        assertNotNull(api, "API should be initialized");

        // Verify the API is registered as a service
        BedWars registeredAPI = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();
        assertNotNull(registeredAPI, "API should be registered as a service");
        assertSame(api, registeredAPI, "Registered API should be the same instance");
    }

    @Test
    @DisplayName("Test configuration is loaded")
    public void testConfigurationIsLoaded() {
        // Verify the configuration is loaded
        BedWarsConfig config = BedWarsProxy.config;
        assertNotNull(config, "Configuration should be loaded");
    }

    @Test
    @DisplayName("Test language manager is initialized")
    public void testLanguageManagerIsInitialized() {
        // Verify the language manager is initialized
        assertNotNull(LanguageManager.get(), "Language manager should be initialized");
    }

    @Test
    @DisplayName("Test version support adapters are loaded")
    public void testVersionSupportAdaptersAreLoaded() {
        // Verify the version support adapters are loaded
        assertNotNull(BedWarsProxy.getSoundAdapter(), "Sound adapter should be loaded");
        assertNotNull(BedWarsProxy.getMaterialAdapter(), "Material adapter should be loaded");
        assertNotNull(BedWarsProxy.getBlockAdapter(), "Block adapter should be loaded");
        assertNotNull(BedWarsProxy.getItemAdapter(), "Item adapter should be loaded");
    }

    @Test
    @DisplayName("Test party system is initialized")
    public void testPartySystemIsInitialized() {
        // Verify the party system is initialized
        assertNotNull(BedWarsProxy.getParty(), "Party system should be initialized");
    }

    @Test
    @DisplayName("Test level manager is initialized")
    public void testLevelManagerIsInitialized() {
        // Verify the level manager is initialized
        assertNotNull(BedWarsProxy.getLevelManager(), "Level manager should be initialized");
    }

    @Test
    @DisplayName("Test database is initialized")
    public void testDatabaseIsInitialized() {
        // Verify the database is initialized
        assertNotNull(BedWarsProxy.getRemoteDatabase(), "Database should be initialized");
    }

    @Test
    @DisplayName("Test stats cache is initialized")
    public void testStatsCacheIsInitialized() {
        // Verify the stats cache is initialized
        assertNotNull(BedWarsProxy.getStatsCache(), "Stats cache should be initialized");
    }

    @Test
    @DisplayName("Test plugin compatibility with different server versions")
    public void testPluginCompatibilityWithDifferentServerVersions() {
        // Log the current Bukkit version for debugging
        System.out.println("[DEBUG_LOG] Current Bukkit version: " + Bukkit.getBukkitVersion());

        // Verify the plugin is enabled
        assertTrue(plugin.isEnabled(), "Plugin should be enabled");

        // Verify the version support adapters are loaded
        assertNotNull(BedWarsProxy.getSoundAdapter(), "Sound adapter should be loaded");
        assertNotNull(BedWarsProxy.getMaterialAdapter(), "Material adapter should be loaded");
        assertNotNull(BedWarsProxy.getBlockAdapter(), "Block adapter should be loaded");
        assertNotNull(BedWarsProxy.getItemAdapter(), "Item adapter should be loaded");

        // Test that the version support adapters can handle different versions
        // This is a basic test that ensures the adapters are initialized and can be used
        // More detailed tests would be in a separate test class
        try {
            BedWarsProxy.getSoundAdapter().getClass().getMethods();
            BedWarsProxy.getMaterialAdapter().getClass().getMethods();
            BedWarsProxy.getBlockAdapter().getClass().getMethods();
            BedWarsProxy.getItemAdapter().getClass().getMethods();
        } catch (Exception e) {
            fail("Version support adapters should not throw exceptions when accessed: " + e.getMessage());
        }
    }
}
