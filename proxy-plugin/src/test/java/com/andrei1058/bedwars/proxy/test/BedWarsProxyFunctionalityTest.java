package com.andrei1058.bedwars.proxy.test;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.andrei1058.bedwars.proxy.BedWarsProxy;
import com.andrei1058.bedwars.proxy.api.BedWars;
import com.andrei1058.bedwars.proxy.api.Language;
import com.andrei1058.bedwars.proxy.configuration.ConfigPath;
import com.andrei1058.bedwars.proxy.language.LanguageManager;
import com.andrei1058.bedwars.proxy.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for BedWarsProxy plugin functionality.
 * These tests verify that the plugin's features work correctly.
 */
public class BedWarsProxyFunctionalityTest extends BedWarsProxyTestBase {

    @Test
    @DisplayName("Test language system")
    public void testLanguageSystem() {
        // Get the default language
        Language defaultLang = LanguageManager.get().getDefaultLanguage();
        assertNotNull(defaultLang, "Default language should not be null");

        // Test getting a message
        String message = defaultLang.getMsg(ConfigPath.GENERAL_CONFIGURATION_DISABLED_LANGUAGES);
        assertNotNull(message, "Should be able to get a message from the language file");
        assertFalse(message.isEmpty(), "Message should not be empty");

        // Test language for a player
        PlayerMock player = server.addPlayer();
        Language playerLang = LanguageManager.get().getPlayerLanguage(player);
        assertNotNull(playerLang, "Player language should not be null");
        assertEquals(defaultLang, playerLang, "New player should have default language");
    }

    @Test
    @DisplayName("Test party system")
    public void testPartySystem() {
        // Get the party system
        Party party = BedWarsProxy.getParty();
        assertNotNull(party, "Party system should not be null");

        // Create test players
        PlayerMock leader = server.addPlayer();
        PlayerMock member1 = server.addPlayer();
        PlayerMock member2 = server.addPlayer();

        // Test creating a party
        party.createParty(leader);
        assertTrue(party.hasParty(leader.getUniqueId()), "Leader should be in a party");
        assertTrue(party.isOwner(leader.getUniqueId()), "Leader should be the party owner");

        // Test adding members
        party.addMember(leader.getUniqueId(), member1);
        assertTrue(party.hasParty(member1.getUniqueId()), "Member should be in a party");
        assertFalse(party.isOwner(member1.getUniqueId()), "Member should not be the party owner");

        party.addMember(leader.getUniqueId(), member2);
        assertTrue(party.hasParty(member2.getUniqueId()), "Member should be in a party");

        // Test getting party members
        assertTrue(party.getMembers(leader.getUniqueId()).contains(member1.getUniqueId()), "Party should contain member1");
        assertTrue(party.getMembers(leader.getUniqueId()).contains(member2.getUniqueId()), "Party should contain member2");

        // Test removing members
        party.removeFromParty(member1.getUniqueId());
        assertFalse(party.hasParty(member1.getUniqueId()), "Member should no longer be in a party");
        assertTrue(party.hasParty(member2.getUniqueId()), "Other member should still be in the party");

        // Test disbanding party
        party.disband(leader.getUniqueId());
        assertFalse(party.hasParty(leader.getUniqueId()), "Leader should no longer be in a party");
        assertFalse(party.hasParty(member2.getUniqueId()), "Member should no longer be in a party");
    }

    @Test
    @DisplayName("Test stats cache")
    public void testStatsCache() {
        // Get the stats cache
        assertNotNull(BedWarsProxy.getStatsCache(), "Stats cache should not be null");

        // Create a test player
        PlayerMock player = server.addPlayer();

        // Test initial stats
        assertEquals(0, BedWarsProxy.getStatsCache().getPlayerWins(player.getUniqueId()), "New player should have 0 wins");
        assertEquals(0, BedWarsProxy.getStatsCache().getPlayerKills(player.getUniqueId()), "New player should have 0 kills");
        assertEquals(0, BedWarsProxy.getStatsCache().getPlayerFinalKills(player.getUniqueId()), "New player should have 0 final kills");
        assertEquals(0, BedWarsProxy.getStatsCache().getPlayerLoses(player.getUniqueId()), "New player should have 0 losses");
        assertEquals(0, BedWarsProxy.getStatsCache().getPlayerDeaths(player.getUniqueId()), "New player should have 0 deaths");
        assertEquals(0, BedWarsProxy.getStatsCache().getPlayerBedsDestroyed(player.getUniqueId()), "New player should have 0 beds destroyed");
        assertEquals(0, BedWarsProxy.getStatsCache().getPlayerGamesPlayed(player.getUniqueId()), "New player should have 0 games played");
    }

    @Test
    @DisplayName("Test version support adapters")
    public void testVersionSupportAdapters() {
        // Test sound adapter
        assertNotNull(BedWarsProxy.getSoundAdapter(), "Sound adapter should not be null");

        // Test material adapter
        assertNotNull(BedWarsProxy.getMaterialAdapter(), "Material adapter should not be null");

        // Test block adapter
        assertNotNull(BedWarsProxy.getBlockAdapter(), "Block adapter should not be null");

        // Test item adapter
        assertNotNull(BedWarsProxy.getItemAdapter(), "Item adapter should not be null");
    }
}
