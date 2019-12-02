package com.andrei1058.bedwars.proxy.arenamanager;

import com.andrei1058.bedwars.proxy.BedWarsProxy;
import com.andrei1058.bedwars.proxy.event.ArenaCacheUpdateEvent;
import com.andrei1058.bedwars.proxy.language.Language;
import com.andrei1058.bedwars.proxy.language.Messages;
import com.andrei1058.bedwars.proxy.socketmanager.ArenaSocketTask;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.andrei1058.bedwars.proxy.BedWarsProxy.getParty;
import static com.andrei1058.bedwars.proxy.language.Language.getMsg;

public class LegacyArena implements CachedArena {

    private String remoteIdentifier;
    private long lastUpdate;
    private String server, group, arenaName;
    private ArenaStatus status;
    private int maxPlayers, currentPlayers, maxInTeam;
    private boolean allowSpectate = true;

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
        ArenaSocketTask as = ArenaManager.getSocketByServer(getServer());
        if (as == null) {
            this.setStatus(ArenaStatus.UNKNOWN);
            ArenaCacheUpdateEvent e = new ArenaCacheUpdateEvent(this);
            Bukkit.getPluginManager().callEvent(e);
            return false;
        }

        if (getStatus() != ArenaStatus.PLAYING) return false;

        if (!allowSpectate){
            player.sendMessage(getMsg(player, Messages.COMMAND_JOIN_SPECTATOR_DENIED_MSG));
            return false;
        }

        //pld,worldIdentifier,uuidUser,languageIso,targetPlayer
        as.getOut().println("pld," + getRemoteIdentifier() + "," + player.getUniqueId() + "," + Language.getPlayerLanguage(player).getIso() + (targetPlayer == null ? "" : "," + targetPlayer));
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(getServer());
        player.sendPluginMessage(BedWarsProxy.getPlugin(), "BungeeCord", out.toByteArray());
        return true;
    }

    @Override
    public boolean addPlayer(Player player, boolean skipOwnerCheck) {
        ArenaSocketTask as = ArenaManager.getSocketByServer(getServer());
        if (as == null) {
            this.setStatus(ArenaStatus.UNKNOWN);
            ArenaCacheUpdateEvent e = new ArenaCacheUpdateEvent(this);
            Bukkit.getPluginManager().callEvent(e);
            return false;
        }

        if (!(getStatus() == ArenaStatus.WAITING || getStatus() == ArenaStatus.STARTING)) return false;

        if (!skipOwnerCheck) {
            if (getParty().hasParty(player)) {
                if (getMaxPlayers() - getCurrentPlayers() >= getParty().getMembers(player).size()){
                    player.sendMessage(getMsg(player, Messages.COMMAND_JOIN_DENIED_PARTY_TOO_BIG));
                    return false;
                }
                for (Player mem : getParty().getMembers(player)) {
                    if (mem == player) continue;
                    addPlayer(player, true);
                }
            }
        }

        if (getCurrentPlayers() >= getMaxPlayers() && !isVip(player)) {
            TextComponent text = new TextComponent(getMsg(player, Messages.COMMAND_JOIN_DENIED_IS_FULL));
            text.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, BedWarsProxy.config.getYml().getString("storeLink")));
            player.spigot().sendMessage(text);
            return false;
        }

        //pld,worldIdentifier,uuidUser,languageIso,partyOwner
        String owner = "";
        if (getParty().hasParty(player)){
            UUID pw = getParty().getOwner(player);
            if (pw != null) owner = pw.toString();
        }
        as.getOut().println("pld," + getRemoteIdentifier() + "," + player.getUniqueId() + "," + Language.getPlayerLanguage(player).getIso() + owner);
        //noinspection UnstableApiUsage
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(getServer());
        player.sendPluginMessage(BedWarsProxy.getPlugin(), "BungeeCord", out.toByteArray());
        return true;
    }

    private static boolean isVip(Player p) {
        return p.hasPermission("bw.*") || p.hasPermission("bw.vip");
    }
}
