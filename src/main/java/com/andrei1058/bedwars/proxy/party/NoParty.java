package com.andrei1058.bedwars.proxy.party;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NoParty implements Party {
    @Override
    public boolean hasParty(UUID p) {
        return false;
    }

    @Override
    public int partySize(UUID p) {
        return 0;
    }

    @Override
    public boolean isOwner(UUID p) {
        return false;
    }

    @Override
    public List<UUID> getMembers(UUID owner) {
        return new ArrayList<>();
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
        return false;
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
        return null;
    }
}
