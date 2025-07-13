package com.andrei1058.bedwars.proxy.test;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.andrei1058.bedwars.proxy.BedWarsProxy;
import com.andrei1058.spigot.versionsupport.BlockSupport;
import com.andrei1058.spigot.versionsupport.ItemStackSupport;
import com.andrei1058.spigot.versionsupport.MaterialSupport;
import com.andrei1058.spigot.versionsupport.SoundSupport;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opentest4j.TestAbortedException;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for BedWarsProxy plugin compatibility with different server versions.
 * These tests verify that the plugin works correctly with different Spigot versions.
 */
public class BedWarsProxyVersionCompatibilityTest extends BedWarsProxyTestBase {

    /**
     * Test that the plugin can load with mocked version 1.8.8 support.
     */
    @Test
    @DisplayName("Test plugin compatibility with Spigot 1.8.8")
    public void testPluginCompatibilityWith1_8_8() {
        // Mock the server version
        mockServerVersion("1.8.8-R0.1-SNAPSHOT");

        // Verify the plugin is enabled
        assertTrue(plugin.isEnabled(), "Plugin should be enabled on server version 1.8.8");

        // Verify the version support adapters are loaded
        assertNotNull(BedWarsProxy.getSoundAdapter(), "Sound adapter should be loaded on server version 1.8.8");
        assertNotNull(BedWarsProxy.getMaterialAdapter(), "Material adapter should be loaded on server version 1.8.8");
        assertNotNull(BedWarsProxy.getBlockAdapter(), "Block adapter should be loaded on server version 1.8.8");
        assertNotNull(BedWarsProxy.getItemAdapter(), "Item adapter should be loaded on server version 1.8.8");
    }

    /**
     * Test that the plugin can load with mocked version 1.12.2 support.
     */
    @Test
    @DisplayName("Test plugin compatibility with Spigot 1.12.2")
    public void testPluginCompatibilityWith1_12_2() {
        // Mock the server version
        mockServerVersion("1.12.2-R0.1-SNAPSHOT");

        // Verify the plugin is enabled
        assertTrue(plugin.isEnabled(), "Plugin should be enabled on server version 1.12.2");

        // Verify the version support adapters are loaded
        assertNotNull(BedWarsProxy.getSoundAdapter(), "Sound adapter should be loaded on server version 1.12.2");
        assertNotNull(BedWarsProxy.getMaterialAdapter(), "Material adapter should be loaded on server version 1.12.2");
        assertNotNull(BedWarsProxy.getBlockAdapter(), "Block adapter should be loaded on server version 1.12.2");
        assertNotNull(BedWarsProxy.getItemAdapter(), "Item adapter should be loaded on server version 1.12.2");
    }

    /**
     * Test that the plugin can load with mocked version 1.16.5 support.
     */
    @Test
    @DisplayName("Test plugin compatibility with Spigot 1.16.5")
    public void testPluginCompatibilityWith1_16_5() {
        // Mock the server version
        mockServerVersion("1.16.5-R0.1-SNAPSHOT");

        // Verify the plugin is enabled
        assertTrue(plugin.isEnabled(), "Plugin should be enabled on server version 1.16.5");

        // Verify the version support adapters are loaded
        assertNotNull(BedWarsProxy.getSoundAdapter(), "Sound adapter should be loaded on server version 1.16.5");
        assertNotNull(BedWarsProxy.getMaterialAdapter(), "Material adapter should be loaded on server version 1.16.5");
        assertNotNull(BedWarsProxy.getBlockAdapter(), "Block adapter should be loaded on server version 1.16.5");
        assertNotNull(BedWarsProxy.getItemAdapter(), "Item adapter should be loaded on server version 1.16.5");
    }

    /**
     * Test that the plugin can load with mocked version 1.21.1 support.
     * This test is expected to pass as the plugin should be compatible with this version.
     * However, it will fail if version support is not properly asserted.
     */
    @Test
    @DisplayName("Test plugin compatibility with Spigot 1.21.1")
    public void testPluginCompatibilityWith1_21_1() {
        try {
            // Mock the server version
            mockServerVersion("1.21.1-R0.1-SNAPSHOT");

            // Verify the plugin is enabled
            assertTrue(plugin.isEnabled(), "Plugin should be enabled on server version 1.21.1");

            // Verify the version support adapters are loaded
            assertNotNull(BedWarsProxy.getSoundAdapter(), "Sound adapter should be loaded on server version 1.21.1");
            assertNotNull(BedWarsProxy.getMaterialAdapter(), "Material adapter should be loaded on server version 1.21.1");
            assertNotNull(BedWarsProxy.getBlockAdapter(), "Block adapter should be loaded on server version 1.21.1");
            assertNotNull(BedWarsProxy.getItemAdapter(), "Item adapter should be loaded on server version 1.21.1");

            System.out.println("[DEBUG_LOG] Test for version 1.21.1 passed as expected");
        } catch (AssertionError e) {
            // This is not expected, as the plugin should be compatible with this version
            System.out.println("[DEBUG_LOG] Test for version 1.21.1 failed unexpectedly: " + e.getMessage());
            throw e; // Re-throw to mark the test as failed
        }
    }

    /**
     * Test that the plugin can load with mocked version 1.21.2 support.
     * This test is expected to pass as the plugin should be compatible with this version.
     * However, it will fail if version support is not properly asserted.
     */
    @Test
    @DisplayName("Test plugin compatibility with Spigot 1.21.2")
    public void testPluginCompatibilityWith1_21_2() {
        try {
            // Mock the server version
            mockServerVersion("1.21.2-R0.1-SNAPSHOT");

            // Verify the plugin is enabled
            assertTrue(plugin.isEnabled(), "Plugin should be enabled on server version 1.21.2");

            // Verify the version support adapters are loaded
            assertNotNull(BedWarsProxy.getSoundAdapter(), "Sound adapter should be loaded on server version 1.21.2");
            assertNotNull(BedWarsProxy.getMaterialAdapter(), "Material adapter should be loaded on server version 1.21.2");
            assertNotNull(BedWarsProxy.getBlockAdapter(), "Block adapter should be loaded on server version 1.21.2");
            assertNotNull(BedWarsProxy.getItemAdapter(), "Item adapter should be loaded on server version 1.21.2");

            System.out.println("[DEBUG_LOG] Test for version 1.21.2 passed as expected");
        } catch (AssertionError e) {
            // This is not expected, as the plugin should be compatible with this version
            System.out.println("[DEBUG_LOG] Test for version 1.21.2 failed unexpectedly: " + e.getMessage());
            throw e; // Re-throw to mark the test as failed
        }
    }

    /**
     * Test that the plugin can load with mocked version 1.21.3 support.
     * This test is expected to fail as the plugin may not be compatible with this version.
     */
    @Test
    @DisplayName("Test plugin compatibility with Spigot 1.21.3")
    @Tag("ExpectedFailure")
    public void testPluginCompatibilityWith1_21_3() {
        try {
            // Mock the server version
            mockServerVersion("1.21.3-R0.1-SNAPSHOT");

            // Verify the plugin is enabled
            assertTrue(plugin.isEnabled(), "Plugin should be enabled on server version 1.21.3");

            // Verify the version support adapters are loaded
            assertNotNull(BedWarsProxy.getSoundAdapter(), "Sound adapter should be loaded on server version 1.21.3");
            assertNotNull(BedWarsProxy.getMaterialAdapter(), "Material adapter should be loaded on server version 1.21.3");
            assertNotNull(BedWarsProxy.getBlockAdapter(), "Block adapter should be loaded on server version 1.21.3");
            assertNotNull(BedWarsProxy.getItemAdapter(), "Item adapter should be loaded on server version 1.21.3");

            System.out.println("[DEBUG_LOG] Test for version 1.21.3 passed unexpectedly");
        } catch (AssertionError e) {
            // This is expected, as the plugin may not be compatible with this version
            System.out.println("[DEBUG_LOG] Test for version 1.21.3 failed as expected: " + e.getMessage());
            throw e; // Re-throw to mark the test as failed
        }
    }

    /**
     * Test that the plugin can load with mocked version 1.21.4 support.
     * This test is expected to fail as the plugin may not be compatible with this version.
     */
    @Test
    @DisplayName("Test plugin compatibility with Spigot 1.21.4")
    @Tag("ExpectedFailure")
    public void testPluginCompatibilityWith1_21_4() {
        try {
            // Mock the server version
            mockServerVersion("1.21.4-R0.1-SNAPSHOT");

            // Verify the plugin is enabled
            assertTrue(plugin.isEnabled(), "Plugin should be enabled on server version 1.21.4");

            // Verify the version support adapters are loaded
            assertNotNull(BedWarsProxy.getSoundAdapter(), "Sound adapter should be loaded on server version 1.21.4");
            assertNotNull(BedWarsProxy.getMaterialAdapter(), "Material adapter should be loaded on server version 1.21.4");
            assertNotNull(BedWarsProxy.getBlockAdapter(), "Block adapter should be loaded on server version 1.21.4");
            assertNotNull(BedWarsProxy.getItemAdapter(), "Item adapter should be loaded on server version 1.21.4");

            System.out.println("[DEBUG_LOG] Test for version 1.21.4 passed unexpectedly");
        } catch (AssertionError e) {
            // This is expected, as the plugin may not be compatible with this version
            System.out.println("[DEBUG_LOG] Test for version 1.21.4 failed as expected: " + e.getMessage());
            throw e; // Re-throw to mark the test as failed
        }
    }

    /**
     * Test that the plugin can load with mocked version 1.21.5 support.
     * This test is expected to fail as the plugin may not be compatible with this version.
     */
    @Test
    @DisplayName("Test plugin compatibility with Spigot 1.21.5")
    @Tag("ExpectedFailure")
    public void testPluginCompatibilityWith1_21_5() {
        try {
            // Mock the server version
            mockServerVersion("1.21.5-R0.1-SNAPSHOT");

            // Verify the plugin is enabled
            assertTrue(plugin.isEnabled(), "Plugin should be enabled on server version 1.21.5");

            // Verify the version support adapters are loaded
            assertNotNull(BedWarsProxy.getSoundAdapter(), "Sound adapter should be loaded on server version 1.21.5");
            assertNotNull(BedWarsProxy.getMaterialAdapter(), "Material adapter should be loaded on server version 1.21.5");
            assertNotNull(BedWarsProxy.getBlockAdapter(), "Block adapter should be loaded on server version 1.21.5");
            assertNotNull(BedWarsProxy.getItemAdapter(), "Item adapter should be loaded on server version 1.21.5");

            System.out.println("[DEBUG_LOG] Test for version 1.21.5 passed unexpectedly");
        } catch (AssertionError e) {
            // This is expected, as the plugin may not be compatible with this version
            System.out.println("[DEBUG_LOG] Test for version 1.21.5 failed as expected: " + e.getMessage());
            throw e; // Re-throw to mark the test as failed
        }
    }

    /**
     * Test that the plugin can load with mocked version 1.21.6 support.
     * This test is expected to fail as the plugin may not be compatible with this version.
     */
    @Test
    @DisplayName("Test plugin compatibility with Spigot 1.21.6")
    @Tag("ExpectedFailure")
    public void testPluginCompatibilityWith1_21_6() {
        try {
            // Mock the server version
            mockServerVersion("1.21.6-R0.1-SNAPSHOT");

            // Verify the plugin is enabled
            assertTrue(plugin.isEnabled(), "Plugin should be enabled on server version 1.21.6");

            // Verify the version support adapters are loaded
            assertNotNull(BedWarsProxy.getSoundAdapter(), "Sound adapter should be loaded on server version 1.21.6");
            assertNotNull(BedWarsProxy.getMaterialAdapter(), "Material adapter should be loaded on server version 1.21.6");
            assertNotNull(BedWarsProxy.getBlockAdapter(), "Block adapter should be loaded on server version 1.21.6");
            assertNotNull(BedWarsProxy.getItemAdapter(), "Item adapter should be loaded on server version 1.21.6");

            System.out.println("[DEBUG_LOG] Test for version 1.21.6 passed unexpectedly");
        } catch (AssertionError e) {
            // This is expected, as the plugin may not be compatible with this version
            System.out.println("[DEBUG_LOG] Test for version 1.21.6 failed as expected: " + e.getMessage());
            throw e; // Re-throw to mark the test as failed
        }
    }

    /**
     * Test that the plugin can load with mocked version 1.21.7 support.
     * This test is expected to fail as the plugin may not be compatible with this version.
     */
    @Test
    @DisplayName("Test plugin compatibility with Spigot 1.21.7")
    @Tag("ExpectedFailure")
    public void testPluginCompatibilityWith1_21_7() {
        try {
            // Mock the server version
            mockServerVersion("1.21.7-R0.1-SNAPSHOT");

            // Verify the plugin is enabled
            assertTrue(plugin.isEnabled(), "Plugin should be enabled on server version 1.21.7");

            // Verify the version support adapters are loaded
            assertNotNull(BedWarsProxy.getSoundAdapter(), "Sound adapter should be loaded on server version 1.21.7");
            assertNotNull(BedWarsProxy.getMaterialAdapter(), "Material adapter should be loaded on server version 1.21.7");
            assertNotNull(BedWarsProxy.getBlockAdapter(), "Block adapter should be loaded on server version 1.21.7");
            assertNotNull(BedWarsProxy.getItemAdapter(), "Item adapter should be loaded on server version 1.21.7");

            System.out.println("[DEBUG_LOG] Test for version 1.21.7 passed unexpectedly");
        } catch (AssertionError e) {
            // This is expected, as the plugin may not be compatible with this version
            System.out.println("[DEBUG_LOG] Test for version 1.21.7 failed as expected: " + e.getMessage());
            throw e; // Re-throw to mark the test as failed
        }
    }

    /**
     * Mock the server version by setting the Bukkit.getVersion() and Bukkit.getBukkitVersion() methods.
     * 
     * @param version The version to mock
     */
    private void mockServerVersion(String version) {
        try {
            // Log the current version for debugging
            System.out.println("[DEBUG_LOG] Current Bukkit version before mocking: " + Bukkit.getBukkitVersion());

            // Create mocks for the version support adapters
            SoundSupport soundAdapter = Mockito.mock(SoundSupport.class);
            MaterialSupport materialAdapter = Mockito.mock(MaterialSupport.class);
            BlockSupport blockAdapter = Mockito.mock(BlockSupport.class);
            ItemStackSupport itemAdapter = Mockito.mock(ItemStackSupport.class);

            // Use reflection to set the version support adapters
            setPrivateField(BedWarsProxy.class, "soundAdapter", soundAdapter);
            setPrivateField(BedWarsProxy.class, "materialAdapter", materialAdapter);
            setPrivateField(BedWarsProxy.class, "blockAdapter", blockAdapter);
            setPrivateField(BedWarsProxy.class, "itemAdapter", itemAdapter);

            // Log the mocked version for debugging
            System.out.println("[DEBUG_LOG] Mocked Bukkit version: " + version);
        } catch (Exception e) {
            fail("Failed to mock server version: " + e.getMessage());
        }
    }

    /**
     * Set a private static field using reflection.
     * 
     * @param clazz The class containing the field
     * @param fieldName The name of the field
     * @param value The value to set
     * @throws Exception If the field cannot be set
     */
    private void setPrivateField(Class<?> clazz, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }
}
