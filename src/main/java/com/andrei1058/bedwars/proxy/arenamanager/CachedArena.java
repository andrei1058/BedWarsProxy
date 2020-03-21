package com.andrei1058.bedwars.proxy.arenamanager;

import com.andrei1058.bedwars.proxy.language.Language;
import com.andrei1058.bedwars.proxy.rejoin.RemoteReJoin;
import org.bukkit.entity.Player;

public interface CachedArena {

    String getRemoteIdentifier();

    String getArenaName();

    String getServer();

    String getDisplayName(Language lang);

    String getArenaGroup();

    String getDisplayGroup(Language lang);

    void setArenaGroup(String group);

    void setArenaName(String newName);

    ArenaStatus getStatus();

    String getDisplayStatus(Language lang);

    void setStatus(ArenaStatus arenaStatus);

    int getMaxPlayers();

    int getCurrentPlayers();

    long getLastUpdate();

    void setCurrentPlayers(int players);

    void setLastUpdate(long time);

    void setMaxPlayers(int players);

    int getMaxInTeam();

    void setMaxInTeam(int max);

    boolean addSpectator(Player player, String targetPlayer);

    // NULL if not party
    boolean addPlayer(Player player, String partyOwnerName);

    boolean reJoin(RemoteReJoin player);

    boolean equals(CachedArena arena);
}
