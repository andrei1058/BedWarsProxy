package com.andrei1058.bedwars.proxy.party;

import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Player;

public class Parties implements Party {
    PartiesAPI api = com.alessiodp.parties.api.Parties.getApi();

    public boolean hasParty(UUID p) {
        PartyPlayer pp = api.getPartyPlayer(p);
        return pp != null && pp.isInParty();
    }

    public int partySize(UUID p) {
        if (!hasParty(p)) {
            return 0;
        } else {
            PartyPlayer pp = api.getPartyPlayer(p);
            if (pp == null) {
                return 0;
            } else if (pp.getPartyId() == null) {
                return 0;
            } else {
                com.alessiodp.parties.api.interfaces.Party party = api.getParty(pp.getPartyId());
                return party == null ? 0 : party.getMembers().size();
            }
        }
    }

    public boolean isOwner(UUID p) {
        if (!hasParty(p)) {
            return false;
        } else {
            PartyPlayer pp = api.getPartyPlayer(p);
            if (pp == null) {
                return false;
            } else if (pp.getPartyId() == null) {
                return false;
            } else {
                com.alessiodp.parties.api.interfaces.Party party = api.getParty(pp.getPartyId());
                if (party == null) {
                    return false;
                } else {
                    return party.getLeader() == null ? false : party.getLeader().equals(p);
                }
            }
        }
    }

    public List<UUID> getMembers(UUID p) {
        ArrayList<UUID> players = new ArrayList();
        if (!hasParty(p)) {
            return players;
        } else {
            com.alessiodp.parties.api.interfaces.Party party = api.getParty(api.getPartyPlayer(p).getPartyId());
            UUID[] array = new UUID[party.getMembers().size()];
            party.getMembers().toArray(array);

            for(int i = 0; i < array.length; ++i) {
                players.add(array[i]);
            }

            return players;
        }
    }

    public void createParty(Player owner, Player... members) {
        if (!api.isBungeeCordEnabled()) {
            api.createParty(null, api.getPartyPlayer(owner.getUniqueId()));
            Player[] var6 = members;
            int var5 = members.length;

            for(int var4 = 0; var4 < var5; ++var4) {
                Player player1 = var6[var4];
                this.api.getParty(api.getPartyPlayer(owner.getUniqueId()).getPartyId()).addMember(api.getPartyPlayer(player1.getUniqueId()));
            }

        }
    }

    public void addMember(UUID owner, Player member) {
        if (!api.isBungeeCordEnabled()) {
            api.getParty(api.getPartyPlayer(owner).getPartyId()).addMember(api.getPartyPlayer(member.getUniqueId()));
        }
    }

    public void removeFromParty(UUID member) {
    }

    public void disband(UUID owner) {
    }

    public boolean isMember(UUID owner, UUID check) {
        PartyPlayer pp = api.getPartyPlayer(owner);
        if (pp == null) {
            return false;
        } else if (pp.getPartyId() == null) {
            return false;
        } else {
            com.alessiodp.parties.api.interfaces.Party party = api.getParty(pp.getPartyId());
            return party == null ? false : party.getMembers().contains(check);
        }
    }

    public void removePlayer(UUID owner, UUID target) {
    }

    public boolean isInternal() {
        return false;
    }

    public UUID getOwner(UUID player) {
        PartyPlayer pp = api.getPartyPlayer(player);
        if (pp == null) {
            return null;
        } else if (pp.getPartyId() == null) {
            return null;
        } else {
            com.alessiodp.parties.api.interfaces.Party party = api.getParty(pp.getPartyId());
            if (party == null) {
                return null;
            } else {
                return party.getLeader() == null ? null : party.getLeader();
            }
        }
    }
}