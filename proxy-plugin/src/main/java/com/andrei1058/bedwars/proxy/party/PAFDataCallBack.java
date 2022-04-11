package com.andrei1058.bedwars.proxy.party;

import de.simonsator.partyandfriendsgui.api.datarequest.DataRequestPlayerInfo;
import de.simonsator.partyandfriendsgui.api.datarequest.party.PartyDataRequestCallbackAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PAFDataCallBack implements Party{
    public boolean hasParty(UUID p) {
        List<Boolean> booleans = new ArrayList<>();
        PartyDataRequestCallbackAPI.getInstance().fetchPartyData(Bukkit.getPlayer(p), (player, partyData, i) -> {
            if (partyData.DOES_EXIST)
                booleans.add(true);
            else
                booleans.add(false);
        });
        return booleans.get(0);
    }

    public int partySize(UUID p) {
        List<Integer> ints = new ArrayList<>();
        PartyDataRequestCallbackAPI.getInstance().fetchPartyData(Bukkit.getPlayer(p), (player, partyData, i) -> {
            if (partyData.DOES_EXIST){
                ints.add(partyData.getAllPlayers().size());
            }
        });
        return ints.get(0);
    }

    public boolean isOwner(UUID p) {
        List<Boolean> booleans = new ArrayList<>();
        PartyDataRequestCallbackAPI.getInstance().fetchPartyData(Bukkit.getPlayer(p), (player, partyData, i) -> {
            if (partyData.DOES_EXIST  && (partyData.getPartyLeader().PLAYER_UUID.equals(p)))
                booleans.add(true);
            else
                booleans.add(false);
        });
        return booleans.get(0);
    }

    public List<UUID> getMembers(UUID owner) {
        List<UUID> partymembers = new ArrayList<>();
        PartyDataRequestCallbackAPI.getInstance().fetchPartyData(Bukkit.getPlayer(owner), (player, partyData, i) -> {
            if (partyData.DOES_EXIST){
                for (DataRequestPlayerInfo p: partyData.getAllPlayers()) {
                    partymembers.add(p.PLAYER_UUID);
                }
            }
        });
        return partymembers;
    }

    public void createParty(Player owner, Player... members) {
    }

    public void addMember(UUID owner, Player member) {
    }

    public void removeFromParty(UUID member) {
    }

    public void disband(UUID owner) {
    }

    public boolean isMember(UUID owner, UUID check) {
        List<Boolean> booleans = new ArrayList<>();
        PartyDataRequestCallbackAPI.getInstance().fetchPartyData(Bukkit.getPlayer(owner), (player, partyData, i) -> {
            if (partyData.DOES_EXIST  && (partyData.getAllPlayers().contains(Bukkit.getPlayer(check))))
                booleans.add(true);
            else
                booleans.add(false);
        });
        return booleans.get(0);
    }

    public void removePlayer(UUID owner, UUID target) {
    }

    public boolean isInternal() {
        return false;
    }

    public UUID getOwner(UUID p1) {
        List<UUID> partymembers = new ArrayList<>();
        PartyDataRequestCallbackAPI.getInstance().fetchPartyData(Bukkit.getPlayer(p1), (player, partyData, i) -> {
            if (partyData.DOES_EXIST){
                for (DataRequestPlayerInfo p: partyData.getAllPlayers()) {
                    if (partyData.getPartyLeader().equals(p))
                        partymembers.add(p.PLAYER_UUID);
                }
            }
        });
        if (!partymembers.isEmpty())
            return partymembers.get(0);

        return null;
    }
}
