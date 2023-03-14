package com.andrei1058.bedwars.proxy.party;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface Party {

    boolean hasParty(UUID p);

    int partySize(UUID p);

    boolean isOwner(UUID p);

    List<UUID> getMembers(UUID owner);

    void createParty(Player owner, Player... members);

    void addMember(UUID owner, Player member);

    void removeFromParty(UUID member);

    void disband(UUID owner);

    boolean isMember(UUID owner, UUID check);

    void removePlayer(UUID owner, UUID target);

    boolean isInternal();

    UUID getOwner(UUID player);

    void promote(UUID owner, UUID target);

}
