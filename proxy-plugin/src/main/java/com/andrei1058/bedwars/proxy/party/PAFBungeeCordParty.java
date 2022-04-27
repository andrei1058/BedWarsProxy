package com.andrei1058.bedwars.proxy.party;

import de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayer;
import de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayerManager;
import de.simonsator.partyandfriends.spigot.api.party.PartyManager;
import de.simonsator.partyandfriends.spigot.api.party.PlayerParty;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PAFBungeeCordParty implements Party {

    private PlayerParty getPAFParty(UUID p) {
        PAFPlayer pafPlayer = PAFPlayerManager.getInstance().getPlayer(p);
        return PartyManager.getInstance().getParty(pafPlayer);
    }

    @Override
    public boolean hasParty(UUID p) {
        return getPAFParty(p) != null;
    }

    @Override
    public int partySize(UUID p) {
        return getMembers(p).size();
    }

    @Override
    public boolean isOwner(UUID p) {
        PAFPlayer pafPlayer = PAFPlayerManager.getInstance().getPlayer(p);
        return PartyManager.getInstance().getParty(pafPlayer).isLeader(pafPlayer);
    }

    @Override
    public List<UUID> getMembers(UUID owner) {
        ArrayList<UUID> playerList = new ArrayList<>();
        PlayerParty party = getPAFParty(owner);
        for (PAFPlayer players : party.getAllPlayers()) {
            playerList.add(players.getUniqueId());
        }
        return playerList;
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
        return getPAFParty(owner).isInParty(PAFPlayerManager.getInstance().getPlayer(check));
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
        return getPAFParty(player).getLeader().getUniqueId();
    }
}
