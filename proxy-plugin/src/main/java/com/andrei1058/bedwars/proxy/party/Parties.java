package com.andrei1058.bedwars.proxy.party;

import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Parties implements Party {

    //Support for Parties by AlessioDP
    PartiesAPI api = com.alessiodp.parties.api.Parties.getApi();

    @Override
    public boolean hasParty(UUID p) {
        return api.isPlayerInParty(p);
    }

    @Override
    public int partySize(UUID p) {
        if (!hasParty(p)) return 0; //no party
        else
        {
            return api.getParty(api.getPartyPlayer(p).getPartyId()).getOnlineMembers().size();
        }
    }

    @Override
    public boolean isOwner(UUID p) {
        if (!hasParty(p)) return false; //no party
        else {
            return api.getParty(api.getPartyPlayer(p).getPartyId()).getLeader().equals(p);
        }
    }

    @Override
    public List<UUID> getMembers(UUID p) {
        ArrayList<UUID> players = new ArrayList<>();
        if (hasParty(p)){
            com.alessiodp.parties.api.interfaces.Party party = api.getParty(api.getPartyPlayer(p).getPartyId());
            for (PartyPlayer member : party.getOnlineMembers()){
                players.add(member.getPlayerUUID());
            }
        }
        return players;
    }

    @Override
    public void createParty(Player owner, Player... members) {
        if (api.isBungeeCordEnabled()) return; //party creation handled on bungee side
        else
        {
            api.createParty(null, api.getPartyPlayer(owner.getUniqueId()));
            for (Player player1 : members)
                api.getParty(owner.getUniqueId()).addMember(api.getPartyPlayer(player1.getUniqueId()));
        }
    }

    @Override
    public void addMember(UUID owner, Player member) {
        if (api.isBungeeCordEnabled()) return;//party operations handled on bungee side
        else
        {
            api.getParty(api.getPartyPlayer(owner).getPartyId()).addMember(api.getPartyPlayer(member.getUniqueId()));
        }
    }

    @Override
    public void removeFromParty(UUID member) {
       api.getParty(api.getPartyPlayer(member).getPartyId()).removeMember(api.getPartyPlayer(member));
    }

    @Override
    public void disband(UUID owner) {
        api.getParty(api.getPartyPlayer(owner).getPartyId()).delete();
    }

    @Override
    public boolean isMember(UUID owner, UUID check) {
        if (!hasParty(owner) || !hasParty(check)) return false;
        else
        {
            return api.areInTheSameParty(owner, check);
        }
    }

    @Override
    public void removePlayer(UUID owner, UUID target) {
        api.getParty(api.getPartyPlayer(owner).getPartyId()).removeMember(api.getPartyPlayer(target));
    }

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public UUID getOwner(UUID player) {
        if (!hasParty(player)) return null;
        else {
            return api.getParty(api.getPartyPlayer(player).getPartyId()).getLeader();
        }
    }
}
