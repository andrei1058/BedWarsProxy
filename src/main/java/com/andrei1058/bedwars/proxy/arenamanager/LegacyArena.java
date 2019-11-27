package com.andrei1058.bedwars.proxy.arenamanager;

import com.andrei1058.bedwars.proxy.language.Language;
import com.andrei1058.bedwars.proxy.language.Messages;
import org.bukkit.entity.Player;

public class LegacyArena implements CachedArena {

    private String remoteIdentifier;
    private long lastUpdate;
    private String server, group, arenaName;
    private ArenaStatus status;
    private int maxPlayers, currentPlayers, maxInTeam;

    public LegacyArena(String remoteIdentifier, String server, String group, String arenaName, ArenaStatus status, int maxPlayers, int currentPlayers, int maxInTeam) {
        this.remoteIdentifier = remoteIdentifier;
        this.lastUpdate = System.currentTimeMillis();
        this.server = server;
        this.status = status;
        this.group = group;
        this.arenaName = arenaName;
        this.maxPlayers = maxPlayers;
        this.currentPlayers = currentPlayers;
        this.maxInTeam = maxInTeam;

        Language.saveIfNotExists(Messages.ARENA_DISPLAY_GROUP_PATH + getArenaGroup(), group);
        Language.saveIfNotExists(Messages.ARENA_DISPLAY_NAME_PATH + getArenaName(), arenaName);
    }

    @Override
    public String getRemoteIdentifier() {
        return remoteIdentifier;
    }

    @Override
    public String getArenaName() {
        return arenaName;
    }

    @Override
    public String getServer() {
        return server;
    }

    @Override
    public String getDisplayName(Language iso) {
        return iso.m(Messages.ARENA_DISPLAY_NAME_PATH + getArenaName());
    }

    @Override
    public String getArenaGroup() {
        return group;
    }

    @Override
    public String getDisplayGroup(Language lang) {
        return lang.m(Messages.ARENA_DISPLAY_GROUP_PATH + getArenaGroup());
    }

    @Override
    public void setArenaGroup(String group) {
        this.group = group;
    }

    @Override
    public void setArenaName(String newName) {
        this.arenaName = newName;
    }

    @Override
    public ArenaStatus getStatus() {
        return status;
    }

    public String getDisplayStatus(Language lang) {
        String s = "";
        switch (status) {
            case WAITING:
                s = lang.m(Messages.ARENA_STATUS_WAITING_NAME);
                break;
            case STARTING:
                s = lang.m(Messages.ARENA_STATUS_STARTING_NAME);
                break;
            case RESTARTING:
                s = lang.m(Messages.ARENA_STATUS_RESTARTING_NAME);
                break;
            case PLAYING:
                s = lang.m(Messages.ARENA_STATUS_PLAYING_NAME);
                break;
        }
        return s.replace("{full}", this.getMaxInTeam() == this.getMaxPlayers() ? lang.m(Messages.MEANING_FULL) : "");
    }

    @Override
    public void setStatus(ArenaStatus arenaStatus) {
        this.status = arenaStatus;
    }

    @Override
    public int getMaxPlayers() {
        return maxPlayers;
    }

    @Override
    public int getCurrentPlayers() {
        return currentPlayers;
    }

    @Override
    public long getLastUpdate() {
        return lastUpdate;
    }

    @Override
    public void setCurrentPlayers(int players) {
        this.currentPlayers = players;
    }

    @Override
    public void setLastUpdate(long time) {
        this.lastUpdate = time;
    }

    @Override
    public void setMaxPlayers(int players) {
        this.maxPlayers = players;
    }

    @Override
    public int getMaxInTeam() {
        return maxInTeam;
    }

    @Override
    public void setMaxInTeam(int max) {
        this.maxInTeam = max;
    }

    @Override
    public boolean addSpectator(Player player, String targetPlayer) {
        //target for reporting plugins
        return false;
    }

    @Override
    public boolean addPlayer(Player player) {
        return false;
    }
}
