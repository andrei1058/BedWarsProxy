package com.andrei1058.bedwars.proxy.configuration;

import com.andrei1058.bedwars.proxy.BedWarsProxy;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class SoundsConfig {

    /**
     * Load sounds configuration
     */
    public SoundsConfig() {
        saveDefaultSounds();
    }

    private static PluginConfig sounds = new PluginConfig(BedWarsProxy.getPlugin(), "sounds", BedWarsProxy.getPlugin().getDataFolder().getPath());

    @SuppressWarnings("WeakerAccess")
    public static void saveDefaultSounds() {
        YamlConfiguration yml = sounds.getYml();
        yml.addDefault("rejoin-denied", String.valueOf(BedWarsProxy.getSoundAdapter().getForCurrentVersion("VILLAGER_NO", "ENTITY_VILLAGER_NO", "ENTITY_VILLAGER_NO")));
        yml.addDefault("rejoin-allowed", String.valueOf(BedWarsProxy.getSoundAdapter().getForCurrentVersion("SLIME_WALK", "ENTITY_SLIME_JUMP", "ENTITY_SLIME_JUMP")));
        yml.addDefault("spectate-denied", String.valueOf(BedWarsProxy.getSoundAdapter().getForCurrentVersion("VILLAGER_NO", "ENTITY_VILLAGER_NO", "ENTITY_VILLAGER_NO")));
        yml.addDefault("spectate-allowed", String.valueOf(BedWarsProxy.getSoundAdapter().getForCurrentVersion("SLIME_WALK", "ENTITY_SLIME_JUMP", "ENTITY_SLIME_JUMP")));
        yml.addDefault("join-denied", String.valueOf(BedWarsProxy.getSoundAdapter().getForCurrentVersion("VILLAGER_NO", "ENTITY_VILLAGER_NO", "ENTITY_VILLAGER_NO")));
        yml.addDefault("join-allowed", String.valueOf(BedWarsProxy.getSoundAdapter().getForCurrentVersion("SLIME_WALK", "ENTITY_SLIME_JUMP", "ENTITY_SLIME_JUMP")));
        yml.addDefault("spectator-gui-click", String.valueOf(BedWarsProxy.getSoundAdapter().getForCurrentVersion("SLIME_WALK", "ENTITY_SLIME_JUMP", "ENTITY_SLIME_JUMP")));
        yml.addDefault("arena-selector-open", String.valueOf(BedWarsProxy.getSoundAdapter().getForCurrentVersion("CHICKEN_EGG_POP", "ENTITY_CHICKEN_EGG", "ENTITY_CHICKEN_EGG")));
        yml.addDefault("stats-gui-open", String.valueOf(BedWarsProxy.getSoundAdapter().getForCurrentVersion("CHICKEN_EGG_POP", "ENTITY_CHICKEN_EGG", "ENTITY_CHICKEN_EGG")));
        yml.options().copyDefaults(true);
        sounds.save();
    }

    public static Sound getSound(String path) {
        try {
            return Sound.valueOf(sounds.getString(path));
        } catch (Exception ex) {
            return null;
        }
    }

    public static void playSound(String path, List<Player> players) {
        final Sound sound = getSound(path);
        if (sound != null) players.forEach(p -> p.playSound(p.getLocation(), sound, 1f, 1f));
    }

    public static void playSound(Sound sound, List<Player> players) {
        if (sound != null) players.forEach(p -> p.playSound(p.getLocation(), sound, 1f, 1f));
    }

    public static void playSound(String path, Player player) {
        final Sound sound = getSound(path);
        if (sound != null) player.playSound(player.getLocation(), sound, 1f, 1f);
    }

    public static void playSound(Sound sound, Player player) {
        if (sound != null) player.playSound(player.getLocation(), sound, 1f, 1f);
    }

    public static PluginConfig getSounds() {
        return sounds;
    }
}
