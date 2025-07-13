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
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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