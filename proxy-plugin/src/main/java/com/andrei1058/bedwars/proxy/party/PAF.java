package com.andrei1058.bedwars.proxy.party;

import de.simonsator.partyandfriends.api.pafplayers.OnlinePAFPlayer;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayerManager;
import de.simonsator.partyandfriends.api.party.PlayerParty;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PAF implements Party{
    @Override
    public boolean hasParty(UUID p) {
        OnlinePAFPlayer p1 = PAFPlayerManager.getInstance().getPlayer(Bukkit.getPlayer(p));
        if ((p1.getParty() == null) || p1.getParty().isInParty(p1) == false)
            return false;
        else
            return true;
    }

    @Override
    public int partySize(UUID p) {
        if (hasParty(p) == false) return 0;
        else {
            return PAFPlayerManager.getInstance().getPlayer(Bukkit.getPlayer(p)).getParty().getAllPlayers().size();
        }
    }

    @Override
    public boolean isOwner(UUID p) {
        if (hasParty(p) == false) return  false;
        else {
            return PAFPlayerManager.getInstance().getPlayer(Bukkit.getPlayer(p)).getParty().isLeader(PAFPlayerManager.getInstance().getPlayer(Bukkit.getPlayer(p)));
        }
    }

    @Override
    public List<UUID> getMembers(UUID owner) {
        ArrayList<UUID> players = new ArrayList<>();
        if (hasParty(owner) == false) return players;
        ArrayList<OnlinePAFPlayer> playersPAF = new ArrayList<>();
        OnlinePAFPlayer p1 = PAFPlayerManager.getInstance().getPlayer(Bukkit.getPlayer(owner));
        for (int i = 0; i < p1.getParty().getAllPlayers().size(); i++) {
            players.add(p1.getParty().getAllPlayers().get(i).getPlayer().getUniqueId());
        }
        return players;
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
        return PAFPlayerManager.getInstance().getPlayer(Bukkit.getPlayer(owner)).getParty().isInParty(PAFPlayerManager.getInstance().getPlayer(Bukkit.getPlayer(check)));
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
        for (int i = 0; i < getMembers(player).size(); i++){
            UUID p2 = getMembers(player).get(i);
            Player p = Bukkit.getPlayer(p2);
            OnlinePAFPlayer partypl= PAFPlayerManager.getInstance().getPlayer(p);
            if (partypl.getParty().isLeader(partypl)){
                return  getMembers(player).get(i);
            }
        }
        return player;
    }
}
