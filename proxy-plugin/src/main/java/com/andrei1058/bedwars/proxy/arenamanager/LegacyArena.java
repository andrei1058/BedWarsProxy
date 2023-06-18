package com.andrei1058.bedwars.proxy.arenamanager;

import com.andrei1058.bedwars.proxy.api.*;
import com.andrei1058.bedwars.proxy.api.event.ArenaCacheUpdateEvent;
import com.andrei1058.bedwars.proxy.api.event.PlayerArenaJoinEvent;
import com.andrei1058.bedwars.proxy.api.event.PlayerReJoinEvent;
import com.andrei1058.bedwars.proxy.language.LanguageManager;
import com.andrei1058.bedwars.proxy.socketmanager.ArenaSocketTask;
import com.andrei1058.bedwars.proxy.BedWarsProxy;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.andrei1058.bedwars.proxy.BedWarsProxy.getParty;

public class LegacyArena implements CachedArena {

    private String remoteIdentifier;
    private long lastUpdate;
    private String server, group, arenaName;
    private ArenaStatus status;
    private int maxPlayers, currentPlayers, maxInTeam;
    private boolean allowSpectate = false;

    public LegacyArena(String remoteIdentifier, String server, @NotNull String group, String arenaName, ArenaStatus status, int maxPlayers, int currentPlayers, int maxInTeam) {
        this.remoteIdentifier = remoteIdentifier;
        this.lastUpdate = System.currentTimeMillis();
        this.server = server;
        this.status = status;
        this.group = group.toLowerCase();
        this.arenaName = arenaName;
        this.maxPlayers = maxPlayers;
        this.currentPlayers = currentPlayers;
        this.maxInTeam = maxInTeam;

        BedWarsProxy.getAPI().getLanguageUtil().saveIfNotExists(Messages.ARENA_DISPLAY_GROUP_PATH + getArenaGroup().toLowerCase(), group);
        BedWarsProxy.getAPI().getLanguageUtil().saveIfNotExists(Messages.ARENA_DISPLAY_NAME_PATH + getArenaName().toLowerCase(), arenaName);
    }

    public LegacyArena(String remoteIdentifier, String server, @NotNull String group, String arenaName, ArenaStatus status, int maxPlayers, int currentPlayers, int maxInTeam, boolean allowSpectate) {
        this(remoteIdentifier, server, group, arenaName, status, maxPlayers, currentPlayers, maxInTeam);
        this.allowSpectate = allowSpectate;
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
        return iso.getMsg(Messages.ARENA_DISPLAY_NAME_PATH + getArenaName());
    }

    @Override
    public String getArenaGroup() {
        return group;
    }

    @Override
    public String getDisplayGroup(Language lang) {
        return lang.getMsg(Messages.ARENA_DISPLAY_GROUP_PATH + getArenaGroup());
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
                s = lang.getMsg(Messages.ARENA_STATUS_WAITING_NAME);
                break;
            case STARTING:
                s = lang.getMsg(Messages.ARENA_STATUS_STARTING_NAME);
                break;
            case RESTARTING:
                s = lang.getMsg(Messages.ARENA_STATUS_RESTARTING_NAME);
                break;
            case PLAYING:
                s = lang.getMsg(Messages.ARENA_STATUS_PLAYING_NAME);
                break;
        }
        return s;
    }

    @Override
    public void setStatus(ArenaStatus arenaStatus) {
        if (status == arenaStatus) return;
        this.status = arenaStatus;
        if (status != ArenaStatus.PLAYING) {
            BedWarsProxy.getAPI().getArenaUtil().destroyReJoins(this);
        }
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
        ArenaSocketTask as = ArenaManager.getSocketByServer(getServer());
        if (as == null) {
            this.setStatus(ArenaStatus.UNKNOWN);
            ArenaCacheUpdateEvent e = new ArenaCacheUpdateEvent(this);
            Bukkit.getPluginManager().callEvent(e);
            return false;
        }

        if (getParty().hasParty(player.getUniqueId())) {
            player.sendMessage(LanguageManager.get().getMsg(player, Messages.COMMAND_JOIN_DENIED_NOT_PARTY_LEADER));
            return false;
        }

        if (getStatus() != ArenaStatus.PLAYING) return false;

        if (!allowSpectate) {
            player.sendMessage(LanguageManager.get().getMsg(player, Messages.COMMAND_JOIN_SPECTATOR_DENIED_MSG));
            return false;
        }

        PlayerArenaJoinEvent event = new PlayerArenaJoinEvent(player, this, true);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        //pld,worldIdentifier,uuidUser,languageIso,targetPlayer
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", "PLD");
        map.put("uuid", player.getUniqueId().toString());
        map.put("lang_iso", LanguageManager.get().getPlayerLanguage(player).getIso());
        map.put("target", targetPlayer == null ? "" : targetPlayer);
        map.put("arena_identifier", getRemoteIdentifier());
        JSONObject json = new JSONObject(map);
        as.getOut().println(json.toJSONString());
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(getServer());
        player.sendPluginMessage(BedWarsProxy.getPlugin(), "BungeeCord", out.toByteArray());
        return true;
    }

    @Override
    public boolean addPlayer(Player player, String partyOwnerName) {
        ArenaSocketTask as = ArenaManager.getSocketByServer(getServer());

        if (as == null) {
            this.setStatus(ArenaStatus.UNKNOWN);
            ArenaCacheUpdateEvent e = new ArenaCacheUpdateEvent(this);
            Bukkit.getPluginManager().callEvent(e);
            return false;
        }

        if (getParty().hasParty(player.getUniqueId()) && !getParty().isOwner(player.getUniqueId()) && partyOwnerName == null) {
            player.sendMessage(LanguageManager.get().getMsg(player, Messages.COMMAND_JOIN_DENIED_NOT_PARTY_LEADER));
            return false;
        }

        if (!(getStatus() == ArenaStatus.WAITING || getStatus() == ArenaStatus.STARTING)) return false;

        if (partyOwnerName == null) {
            if (getParty().hasParty(player.getUniqueId())) {
                if (getMaxPlayers() - getCurrentPlayers() < getParty().getMembers(player.getUniqueId()).size()) {
                    player.sendMessage(LanguageManager.get().getMsg(player, Messages.COMMAND_JOIN_DENIED_PARTY_TOO_BIG));
                    return false;
                }
                partyOwnerName = player.getName();
                for (UUID mem : getParty().getMembers(player.getUniqueId())) {
                    if (mem.equals(player.getUniqueId())) continue;
                    Player pl = Bukkit.getPlayer(mem);
                    if (pl != null) {
                        addPlayer(pl, player.getName());
                    }
                }
            }
        }

        if (getCurrentPlayers() >= getMaxPlayers() && !isVip(player)) {
            TextComponent text = new TextComponent(LanguageManager.get().getMsg(player, Messages.COMMAND_JOIN_DENIED_IS_FULL));
            text.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, BedWarsProxy.config.getYml().getString("storeLink")));
            player.spigot().sendMessage(text);
            return false;
        }

        PlayerArenaJoinEvent event = new PlayerArenaJoinEvent(player, this);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        if (null == partyOwnerName) {
            getParty().disband(player.getUniqueId());
        }

        //pld,worldIdentifier,uuidUser,languageIso,partyOwner

        JsonObject json = new JsonObject();
        json.addProperty("type", "PLD");
        json.addProperty("uuid", player.getUniqueId().toString());
        json.addProperty("lang_iso", LanguageManager.get().getPlayerLanguage(player).getIso());
        json.addProperty("target", partyOwnerName == null ? "" : partyOwnerName);
        json.addProperty("arena_identifier", getRemoteIdentifier());
        as.getOut().println(json.toString());
        //noinspection UnstableApiUsage
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(getServer());
        player.sendPluginMessage(BedWarsProxy.getPlugin(), "BungeeCord", out.toByteArray());
        return true;
    }

    @Override
    public boolean reJoin(RemoteReJoin rj) {
        ArenaSocketTask as = ArenaManager.getSocketByServer(getServer());
        if (as == null) {
            this.setStatus(ArenaStatus.UNKNOWN);
            ArenaCacheUpdateEvent e = new ArenaCacheUpdateEvent(this);
            Bukkit.getPluginManager().callEvent(e);
            rj.destroy();
            return false;
        }

        Player player = Bukkit.getPlayer(rj.getUUID());
        if (player == null) {
            rj.destroy();
            return false;
        }

        if (status != ArenaStatus.PLAYING) {
            rj.destroy();
            return false;
        }

        Bukkit.getPluginManager().callEvent(new PlayerReJoinEvent(player, rj.getArena()));
        JsonObject json = new JsonObject();
        json.addProperty("type", "PLD");
        json.addProperty("uuid", player.getUniqueId().toString());
        json.addProperty("lang_iso", BedWarsProxy.getAPI().getLanguageUtil().getPlayerLanguage(player).getIso());
        json.addProperty("target", "");
        json.addProperty("arena_identifier", getRemoteIdentifier());
        as.getOut().println(json.toString());
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(getServer());
        player.sendPluginMessage(BedWarsProxy.getPlugin(), "BungeeCord", out.toByteArray());
        return true;
    }

    @Override
    public boolean equals(@NotNull CachedArena arena) {
        return getServer().equals(arena.getServer()) && getRemoteIdentifier().equals(arena.getRemoteIdentifier()) && getArenaGroup().equalsIgnoreCase(arena.getArenaGroup()) && getArenaName().equalsIgnoreCase(arena.getArenaName());
    }

    private static boolean isVip(@NotNull Player p) {
        return p.hasPermission("bw.*") || p.hasPermission("bw.vip");
    }
}
