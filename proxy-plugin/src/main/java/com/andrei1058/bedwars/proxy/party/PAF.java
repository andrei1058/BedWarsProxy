package com.andrei1058.bedwars.proxy.party;

import de.simonsator.partyandfriends.api.pafplayers.OnlinePAFPlayer;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayerManager;
import de.simonsator.partyandfriends.api.party.PlayerParty;
import de.simonsator.partyandfriends.api.party.PartyManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PAF implements Party{ //Party And Friends Support Added by JT122406

    private PlayerParty getPAFParty(UUID p) {
        OnlinePAFPlayer pafPlayer = PAFPlayerManager.getInstance().getPlayer(Bukkit.getPlayer(p));
        return PartyManager.getInstance().getParty(pafPlayer);
    }

    @Override
    public boolean hasParty(UUID p) {
        return getPAFParty(p) != null;
    }

    @Override
    public int partySize(UUID p) {
        PlayerParty party = getPAFParty(p);
        if (party == null)
            return 0;
        return party.getAllPlayers().size();
    }

    @Override
    public boolean isOwner(UUID p) {
        OnlinePAFPlayer pafPlayer = PAFPlayerManager.getInstance().getPlayer(Bukkit.getPlayer(p));
        PlayerParty party = PartyManager.getInstance().getParty(pafPlayer);
        if (party == null)
            return false;
        return party.isLeader(pafPlayer);
    }

    @Override
    public List<UUID> getMembers(UUID owner) {
        ArrayList<UUID> playerList = new ArrayList<>();
        PlayerParty party = getPAFParty(owner);
        if (party == null)
            return playerList;
        for (OnlinePAFPlayer players : party.getAllPlayers()) {
            playerList.add(players.getPlayer().getUniqueId());
        }
        return playerList;
    }

    @Override
    public void createParty(Player owner, Player... members) {
        OnlinePAFPlayer pafPlayer = PAFPlayerManager.getInstance().getPlayer(owner);
        PlayerParty party = PartyManager.getInstance().createParty(pafPlayer);
        party.setPrivateState(false);
        for (Player p1 : members){
            party.addPlayer(PAFPlayerManager.getInstance().getPlayer(p1));
        }
        party.setPrivateState(true);
    }

    @Override
    public void addMember(UUID owner, Player member) {
        OnlinePAFPlayer pafPlayer = PAFPlayerManager.getInstance().getPlayer(Bukkit.getPlayer(owner));
        PlayerParty party = pafPlayer.getParty();
        party.setPrivateState(false);
        party.addPlayer(PAFPlayerManager.getInstance().getPlayer(member));
        party.setPrivateState(true);
    }
    }

    @Override
    public void removeFromParty(UUID member) {
        PlayerParty p = PAFPlayerManager.getInstance().getPlayer(Bukkit.getPlayer(member)).getParty();
        p.leaveParty(PAFPlayerManager.getInstance().getPlayer(member));
    }

    @Override
    public void disband(UUID owner) {
        PartyManager.getInstance().deleteParty(PartyManager.getInstance().getParty(PAFPlayerManager.getInstance().getPlayer(Bukkit.getPlayer(owner))));
    }

    @Override
    public boolean isMember(UUID owner, UUID check) {
        PlayerParty party = getPAFParty(owner);
        if (party == null)
            return false;
        return party.isInParty(PAFPlayerManager.getInstance().getPlayer(Bukkit.getPlayer("check")));
    }

    @Override
    public void removePlayer(UUID owner, UUID target) {
        PlayerParty p = getPAFParty(owner);
        p.leaveParty(PAFPlayerManager.getInstance().getPlayer(target));
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
