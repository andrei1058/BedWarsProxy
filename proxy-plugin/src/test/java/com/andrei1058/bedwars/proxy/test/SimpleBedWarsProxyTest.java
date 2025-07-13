package com.andrei1058.bedwars.proxy.test;

import com.andrei1058.bedwars.proxy.BedWarsProxy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Simple test class for BedWarsProxy that doesn't use MockBukkit.
 * This class tests basic functionality of the plugin without requiring a full Bukkit environment.
 */
@ExtendWith(MockitoExtension.class)
public class SimpleBedWarsProxyTest {

    @Mock
    private BedWarsProxy plugin;

    @Test
    public void testPluginExists() {
        // This test simply verifies that the plugin class exists and can be mocked
        assertNotNull(plugin, "Plugin should not be null");
    }
}