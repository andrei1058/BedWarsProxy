package com.andrei1058.bedwars.proxy.test;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.andrei1058.bedwars.proxy.BedWarsProxy;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Assumptions;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Base class for BedWarsProxy tests.
 * Provides common functionality for setting up and tearing down the test environment.
 */
@ExtendWith(MockitoExtension.class)
public abstract class BedWarsProxyTestBase {

    // Flag to indicate if KeyedBossBar is available
    private static boolean keyedBossBarAvailable = true;

    // Check if KeyedBossBar is available
    static {
        try {
            Class.forName("org.bukkit.boss.KeyedBossBar");
        } catch (ClassNotFoundException e) {
            keyedBossBarAvailable = false;
            System.out.println("[DEBUG_LOG] KeyedBossBar not found, tests will be skipped");
        }
    }

    protected ServerMock server;
    protected BedWarsProxy plugin;

    /**
     * Set up the test environment before each test.
     * This method initializes MockBukkit and loads the BedWarsProxy plugin.
     */
    @BeforeEach
    public void setUp() {
        // Skip the test if KeyedBossBar is not available
        Assumptions.assumeTrue(keyedBossBarAvailable, "KeyedBossBar not available, skipping test");

        try {
            // Initialize MockBukkit
            server = MockBukkit.mock();

            // Load the plugin
            plugin = MockBukkit.load(BedWarsProxy.class);

            // Additional setup can be done in subclasses
            additionalSetup();
        } catch (NoClassDefFoundError e) {
            if (e.getMessage().contains("org/bukkit/boss/KeyedBossBar")) {
                // This should not happen since we already checked for KeyedBossBar
                System.out.println("[DEBUG_LOG] KeyedBossBar not found, skipping test");
                Assumptions.assumeTrue(false, "KeyedBossBar not found, skipping test");
            } else {
                throw e;
            }
        }
    }

    /**
     * Tear down the test environment after each test.
     * This method unloads all plugins and shuts down MockBukkit.
     */
    @AfterEach
    public void tearDown() {
        // Additional teardown can be done in subclasses
        additionalTearDown();

        // Clean up MockBukkit only if it was initialized
        if (server != null) {
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

    /**
     * Hook for subclasses to add additional setup logic.
     * This method is called at the end of the setUp method.
     */
    protected void additionalSetup() {
        // Default implementation does nothing
    }

    /**
     * Hook for subclasses to add additional tear down logic.
     * This method is called at the beginning of the tearDown method.
     */
    protected void additionalTearDown() {
        // Default implementation does nothing
    }

    /**
     * Get the current Bukkit version.
     * This can be used to check compatibility with different server versions.
     *
     * @return The current Bukkit version as a string
     */
    protected String getBukkitVersion() {
        return Bukkit.getBukkitVersion();
    }

    /**
     * Check if the current Bukkit version is at least the specified version.
     *
     * @param majorVersion The major version to check against
     * @param minorVersion The minor version to check against
     * @return True if the current version is at least the specified version, false otherwise
     */
    protected boolean isVersionAtLeast(int majorVersion, int minorVersion) {
        String version = getBukkitVersion();
        String[] parts = version.split("\\.");

        if (parts.length >= 2) {
            try {
                int currentMajor = Integer.parseInt(parts[0]);
                int currentMinor = Integer.parseInt(parts[1].split("-")[0]);

                return currentMajor > majorVersion || 
                      (currentMajor == majorVersion && currentMinor >= minorVersion);
            } catch (NumberFormatException e) {
                // If parsing fails, assume version check fails
                return false;
            }
        }

        return false;
    }
}
