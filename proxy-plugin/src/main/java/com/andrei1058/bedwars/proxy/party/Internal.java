package com.andrei1058.bedwars.proxy.party;

import com.andrei1058.bedwars.proxy.api.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.andrei1058.bedwars.proxy.language.Language.getMsg;

public class Internal implements Party {

    static List<InternalParty> parties = new ArrayList<>();

    @Override
    public boolean hasParty(UUID p) {
        for (InternalParty party : parties) {
            if (party.members.contains(p)) return true;
        }
        return false;
    }

    @Override
    public int partySize(UUID p) {
        for (InternalParty party : parties) {
            if (party.members.contains(p)) {
                return party.members.size();
            }
        }
        return 0;
    }

    @Override
    public boolean isOwner(UUID p) {
        for (InternalParty party : parties) {
            if (party.members.contains(p)) {
                if (party.owner == p) return true;
            }
        }
        return false;
    }

    @Override
    public List<UUID> getMembers(UUID owner) {
        for (InternalParty party : parties) {
            if (party.members.contains(owner)) {
                return party.members;
            }
        }
        return null;
    }

    @Override
    public void createParty(Player owner, Player... members) {
        InternalParty p = new InternalParty(owner.getUniqueId());
        p.addMember(owner);
        for (Player mem : members) {
            p.addMember(mem);
        }
    }

    @Override
    public void addMember(UUID owner, Player member) {
        if (owner == null || member == null) return;
        InternalParty p = getParty(owner);
        if (p == null) return;
        p.addMember(member);

    }

    @Override
    public void removeFromParty(UUID member) {
        for (InternalParty p : parties) {
            if (p.owner == member) {
                disband(member);
            } else if (p.members.contains(member)) {
                Player p1, target = Bukkit.getPlayer(member);
                if (target != null){
                    for (UUID mem : p.members) {
                        p1 = Bukkit.getPlayer(mem);
                        if (p1 != null) {
                            p1.sendMessage(getMsg(p1, Messages.COMMAND_PARTY_LEAVE_SUCCESS).replace("{player}", target.getName()));
                        }
                    }
                }
                p.members.remove(member);
                if (p.members.isEmpty() || p.members.size() == 1) {
                    disband(p.owner);
                    parties.remove(p);
                }
                return;
            }
        }
    }

    @Override
    public void disband(UUID owner) {
        InternalParty party = getParty(owner);
        if (party != null) {
            Player player;
            for (UUID p : party.members) {
                player = Bukkit.getPlayer(p);
                if (player != null)
                    player.sendMessage(getMsg(player, Messages.COMMAND_PARTY_DISBAND_SUCCESS));
            }
            party.members.clear();
            parties.remove(party);
        }
    }

    @Override
    public boolean isMember(UUID owner, UUID check) {
        for (InternalParty p : parties) {
            if (p.owner.equals(owner)) {
                if (p.members.contains(check)) return true;
            }
        }
        return false;
    }

    @Override
    public void removePlayer(UUID owner, UUID target) {
        InternalParty p = getParty(owner);
        if (p != null) {
            if (p.members.contains(target)) {
                Player pl, t1 = Bukkit.getPlayer(target);
                if (t1 != null) {
                    for (UUID mem : p.members) {
                        pl = Bukkit.getPlayer(mem);
                        if (pl != null) {
                            pl.sendMessage(getMsg(pl, Messages.COMMAND_PARTY_REMOVE_SUCCESS).replace("{player}", t1.getName()));
                        }
                    }
                }
                p.members.remove(target);
                if (p.members.isEmpty() || p.members.size() == 1) {
                    disband(p.owner);
                    parties.remove(p);
                }
            }
        }
    }

    @Override
    public boolean isInternal() {
        return true;
    }

    @Override
    public UUID getOwner(UUID player) {
        InternalParty p = getParty(player);
        if (p == null) return null;
        return p.getOwner();
    }

    @Override
    public void promote(UUID owner, UUID target) {
        InternalParty p = getParty(owner);
        if (p != null){
            p.owner = target;
        }
    }

    private InternalParty getParty(UUID owner) {
        for (InternalParty p : parties) {
            if (p.getOwner().equals(owner)) return p;
        }
        return null;
    }
}

class InternalParty {

    List<UUID> members = new ArrayList<>();
    UUID owner;

    public InternalParty(UUID p) {
        owner = p;
        Internal.parties.add(this);
    }

    public UUID getOwner() {
        return owner;
    }

    void addMember(Player p) {
        members.add(p.getUniqueId());
    }
}
