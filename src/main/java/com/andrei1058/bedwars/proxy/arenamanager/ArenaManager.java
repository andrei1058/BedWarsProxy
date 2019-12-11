package com.andrei1058.bedwars.proxy.arenamanager;

import com.andrei1058.bedwars.proxy.BedWarsProxy;
import com.andrei1058.bedwars.proxy.event.ArenaCacheRemoveEvent;
import com.andrei1058.bedwars.proxy.language.Messages;
import com.andrei1058.bedwars.proxy.socketmanager.ArenaSocketTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

import static com.andrei1058.bedwars.proxy.BedWarsProxy.getParty;
import static com.andrei1058.bedwars.proxy.language.Language.getMsg;

public class ArenaManager {

    private LinkedList<CachedArena> arenas = new LinkedList<>();
    private HashMap<String, ArenaSocketTask> socketByServer = new HashMap<>();

    private static ArenaManager instance = null;

    private ArenaManager(){
        instance = this;
    }

    public static ArenaManager getInstance() {
        return instance == null ? new ArenaManager() : instance;
    }

    public void registerServerSocket(String server, ArenaSocketTask task){
        if (socketByServer.containsKey(server)){
            socketByServer.replace(server, task);
            return;
        }
        socketByServer.put(server, task);
    }

    public void registerArena(CachedArena arena){
        if (getArena(arena.getServer(), arena.getRemoteIdentifier()) != null) return;
        arenas.add(arena);
    }

    public CachedArena getArena(String server, String remoteIdentifier){
        for (CachedArena ca : getArenas()){
            if (ca.getServer().equals(server) && ca.getRemoteIdentifier().equals(remoteIdentifier)) return ca;
        }
        return null;
    }

    public static Collection<CachedArena> getArenas() {
        return Collections.unmodifiableCollection(getInstance().arenas);
    }

    public static List<CachedArena> getSorted(List<CachedArena> arenas) {
        List<CachedArena> sorted = new ArrayList<>(arenas);
        sorted.sort(new Comparator<CachedArena>() {
            @Override
            public int compare(CachedArena o1, CachedArena o2) {
                if (o1.getStatus() == ArenaStatus.STARTING && o2.getStatus() == ArenaStatus.STARTING) {
                    return Integer.compare(o2.getCurrentPlayers(), o1.getCurrentPlayers());
                } else if (o1.getStatus() == ArenaStatus.STARTING && o2.getStatus() != ArenaStatus.STARTING) {
                    return -1;
                } else if (o2.getStatus() == ArenaStatus.STARTING && o1.getStatus() != ArenaStatus.STARTING) {
                    return 1;
                } else if (o1.getStatus() == ArenaStatus.WAITING && o2.getStatus() == ArenaStatus.WAITING) {
                    return Integer.compare(o2.getCurrentPlayers(), o1.getCurrentPlayers());
                } else if (o1.getStatus() == ArenaStatus.WAITING && o2.getStatus() != ArenaStatus.WAITING) {
                    return -1;
                } else if (o2.getStatus() == ArenaStatus.WAITING && o1.getStatus() != ArenaStatus.WAITING) {
                    return 1;
                } else if (o1.getStatus() == ArenaStatus.PLAYING && o2.getStatus() == ArenaStatus.PLAYING) {
                    return 0;
                } else if (o1.getStatus() == ArenaStatus.PLAYING && o2.getStatus() != ArenaStatus.PLAYING) {
                    return -1;
                } else return 1;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof CachedArena;
            }
        });
        return sorted;
    }

    public static ArenaSocketTask getSocketByServer(String server){
        return getInstance().socketByServer.getOrDefault(server, null);
    }

    /**
     * Check if given string is an integer.
     */
    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    /**
     * Add a player to the most filled arena from a group.
     */
    public static boolean joinRandomFromGroup(Player p, String group) {
        if (getParty().hasParty(p) && !getParty().isOwner(p)) {
            p.sendMessage(getMsg(p, Messages.COMMAND_JOIN_DENIED_NOT_PARTY_LEADER));
            return false;
        }
        List<CachedArena> arenaList = new ArrayList<>();
        getArenas().forEach(a -> {
            if (a.getArenaGroup().equalsIgnoreCase(group)) arenaList.add(a);
        });
        arenaList.sort((c, a2) -> {
            if (c.getStatus() == ArenaStatus.STARTING && a2.getStatus() == ArenaStatus.STARTING) {
                if (c.getCurrentPlayers() > a2.getCurrentPlayers()) {
                    return -1;
                }
                if (c.getCurrentPlayers() == a2.getCurrentPlayers()) {
                    return 0;
                } else return 1;
            } else if (c.getStatus() == ArenaStatus.STARTING && a2.getStatus() != ArenaStatus.STARTING) {
                return -1;
            } else if (a2.getStatus() == ArenaStatus.STARTING && c.getStatus() != ArenaStatus.STARTING) {
                return 1;
            } else if (c.getStatus() == ArenaStatus.WAITING && a2.getStatus() == ArenaStatus.WAITING) {
                if (c.getCurrentPlayers() > a2.getCurrentPlayers()) {
                    return -1;
                }
                if (c.getCurrentPlayers() == a2.getCurrentPlayers()) {
                    return 0;
                } else return 1;
            } else if (c.getStatus() == ArenaStatus.WAITING && a2.getStatus() != ArenaStatus.WAITING) {
                return -1;
            } else if (a2.getStatus() == ArenaStatus.WAITING && c.getStatus() != ArenaStatus.WAITING) {
                return 1;
            } else if (c.getStatus() == ArenaStatus.PLAYING && a2.getStatus() == ArenaStatus.PLAYING) {
                return 0;
            } else if (c.getStatus() == ArenaStatus.PLAYING && a2.getStatus() != ArenaStatus.PLAYING) {
                return -1;
            } else return 1;
        });

        int amount = BedWarsProxy.getParty().hasParty(p) ? BedWarsProxy.getParty().getMembers(p).size() : 1;
        for (CachedArena a : arenaList) {
            if (a.getCurrentPlayers() >= a.getMaxPlayers()) continue;
            if (a.getMaxPlayers() - a.getCurrentPlayers() >= amount) {
                a.addPlayer(p, false);
                return true;
            }
        }
        p.sendMessage(getMsg(p, Messages.COMMAND_JOIN_NO_EMPTY_FOUND));
        return true;
    }

    /** Check if arena group exists.*/
    public static boolean hasGroup(String arenaGroup){
        for (CachedArena ad : getArenas()){
            if (ad.getArenaGroup().equalsIgnoreCase(arenaGroup)) return true;
        }
        return false;
    }

    /**
     * Add a player to the most filled arena.
     * Check if is the party owner first.
     */
    public static boolean joinRandomArena(Player p) {
        if (getParty().hasParty(p) && !getParty().isOwner(p)) {
            p.sendMessage(getMsg(p, Messages.COMMAND_JOIN_DENIED_NOT_PARTY_LEADER));
            return false;
        }
        List<CachedArena> arenaList = new ArrayList<>(getArenas());
        arenaList.sort((c, a2) -> {
            if (c.getStatus() == ArenaStatus.STARTING && a2.getStatus() == ArenaStatus.STARTING) {
                if (c.getCurrentPlayers() > a2.getCurrentPlayers()) {
                    return -1;
                }
                if (c.getCurrentPlayers() == a2.getCurrentPlayers()) {
                    return 0;
                } else return 1;
            } else if (c.getStatus() == ArenaStatus.STARTING && a2.getStatus() != ArenaStatus.STARTING) {
                return -1;
            } else if (a2.getStatus() == ArenaStatus.STARTING && c.getStatus() != ArenaStatus.STARTING) {
                return 1;
            } else if (c.getStatus() == ArenaStatus.WAITING && a2.getStatus() == ArenaStatus.WAITING) {
                if (c.getCurrentPlayers() > a2.getCurrentPlayers()) {
                    return -1;
                }
                if (c.getCurrentPlayers() == a2.getCurrentPlayers()) {
                    return 0;
                } else return 1;
            } else if (c.getStatus() == ArenaStatus.WAITING && a2.getStatus() != ArenaStatus.WAITING) {
                return -1;
            } else if (a2.getStatus() == ArenaStatus.WAITING && c.getStatus() != ArenaStatus.WAITING) {
                return 1;
            } else if (c.getStatus() == ArenaStatus.PLAYING && a2.getStatus() == ArenaStatus.PLAYING) {
                return 0;
            } else if (c.getStatus() == ArenaStatus.PLAYING && a2.getStatus() != ArenaStatus.PLAYING) {
                return -1;
            } else return 1;
        });

        int amount = BedWarsProxy.getParty().hasParty(p) ? BedWarsProxy.getParty().getMembers(p).size() : 1;
        for (CachedArena a : arenaList) {
            if (a.getCurrentPlayers() >= a.getMaxPlayers()) continue;
            if (a.getMaxPlayers() - a.getCurrentPlayers() >= amount) {
                a.addPlayer(p, false);
                break;
            }
        }

        return true;
    }

    public void disableArena(CachedArena a){
        arenas.remove(a);
        Bukkit.getPluginManager().callEvent(new ArenaCacheRemoveEvent(a));
    }

    public HashMap<String, ArenaSocketTask> getSocketByServer() {
        return socketByServer;
    }
}
