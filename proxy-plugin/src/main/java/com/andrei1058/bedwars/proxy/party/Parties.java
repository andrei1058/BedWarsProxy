package com.andrei1058.bedwars.proxy.party;

import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Parties implements Party {

    //Support for Parties by AlessioDP
    PartiesAPI api = com.alessiodp.parties.api.Parties.getApi();

    @Override
    public boolean hasParty(UUID p) {
        PartyPlayer pp = api.getPartyPlayer(p);
        return pp != null && pp.isInParty();
    }

    @Override
    public int partySize(UUID p) {
        PartyPlayer pp = api.getPartyPlayer(p);
        if (pp == null) return 0;
        if (pp.getPartyId() == null) return 0;
        com.alessiodp.parties.api.interfaces.Party party = api.getParty(pp.getPartyId());
        if (party == null) return 0;
        return party.getMembers().size();
    }

    @Override
    public boolean isOwner(UUID p) {
        PartyPlayer pp = api.getPartyPlayer(p);
        if (pp == null) return false;
        if (pp.getPartyId() == null) return false;
        com.alessiodp.parties.api.interfaces.Party party = api.getParty(pp.getPartyId());
        if (party == null) return false;
        if (party.getLeader() == null) return false;
        return party.getLeader().equals(p);
    }

    @Override
    public List<UUID> getMembers(UUID p) {
        ArrayList<UUID> players = new ArrayList<>();
        PartyPlayer pp = api.getPartyPlayer(p);
        if (pp == null) return players;
        if (pp.getPartyId() == null) return players;
        com.alessiodp.parties.api.interfaces.Party party = api.getParty(pp.getPartyId());
        if (party == null) return players;
        players.addAll(party.getMembers());
        return players;
    }

    @Override
    public void createParty(Player owner, Player... members) {
        if (api.isBungeeCordEnabled()) return; //party creation handled on bungee side
        else
        {
            api.createParty("bedwars"+ owner.getUniqueId(), api.getPartyPlayer(owner.getUniqueId()));
            for (Player player1 : members)
                api.getParty("bedwars"+ owner.getUniqueId()).addMember(api.getPartyPlayer(player1.getUniqueId()));
        }
    }

    @Override
    public void addMember(UUID owner, Player member) {
        if (api.isBungeeCordEnabled()) return;//party operations handled on bungee side
        else
        {
            api.getParty(api.getPartyPlayer(owner).getPartyName()).addMember(api.getPartyPlayer(member.getUniqueId()));
        }
    }

    @Override
    public void removeFromParty(UUID member) {
    }

    @Override
    public void disband(UUID owner) {
    }

    @Override
    public boolean isMember(UUID owner, UUID check) {
        PartyPlayer pp = api.getPartyPlayer(owner);
        if (pp == null) return false;
        if (pp.getPartyId() == null) return false;
        com.alessiodp.parties.api.interfaces.Party party = api.getParty(pp.getPartyId());
        if (party == null) return false;
        return party.getMembers().contains(check);
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
        PartyPlayer pp = api.getPartyPlayer(player);
        if (pp == null) return null;
        if (pp.getPartyId() == null) return null;
        com.alessiodp.parties.api.interfaces.Party party = api.getParty(pp.getPartyId());
        if (party == null) return null;
        if (party.getLeader() == null) return null;
        return party.getLeader();
    }
}
