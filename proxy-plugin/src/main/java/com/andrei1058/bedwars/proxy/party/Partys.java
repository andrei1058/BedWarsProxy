package com.andrei1058.bedwars.proxy.party;

import me.gamer.party.spigot.MainClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Partys implements Party {

    //Support for Partys by Retr0
    MainClass api = MainClass.getInstance();

    @Override
    public boolean hasParty(UUID p) {
        return api.isInParty(Bukkit.getPlayer(p));
    }

    @Override
    public int partySize(UUID p) {
        return api.getPlayerPartySize(p);
    }

    @Override
    public boolean isOwner(UUID p) {
        return api.isLeader(Bukkit.getPlayer(p));
    }

    @Override
    public List<UUID> getMembers(UUID p) {
        me.gamer.party.spigot.Party party = api.playerParty.get(p);
        if (party == null) return new ArrayList<>();
        return new ArrayList<>(party.players.keySet());
    }

    @Override
    public void createParty(Player owner, Player... members) {
    }

    @Override
    public void addMember(UUID owner, Player member) {
    }

    @Override
    public void removeFromParty(UUID member) {
    }

    @Override
    public void disband(UUID owner) {
    }

    @Override
    public boolean isMember(UUID owner, UUID check) {
        me.gamer.party.spigot.Party party = api.tryGetUser(owner);
        if (party == null) return false;
        return party.players.containsKey(check);
    }

    @Override
    public void removePlayer(UUID owner, UUID target) {
    }

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public UUID getOwner(UUID player) {
        me.gamer.party.spigot.Party party = api.tryGetUser(player);
        if (party == null) return null;
        return party.partyLeaderUUID;
    }
}
