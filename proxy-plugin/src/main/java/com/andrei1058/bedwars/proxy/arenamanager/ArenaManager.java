package com.andrei1058.bedwars.proxy.arenamanager;

import com.andrei1058.bedwars.proxy.BedWarsProxy;
import com.andrei1058.bedwars.proxy.api.*;
import com.andrei1058.bedwars.proxy.api.event.ArenaCacheRemoveEvent;
import com.andrei1058.bedwars.proxy.configuration.ConfigPath;
import com.andrei1058.bedwars.proxy.language.LanguageManager;
import com.andrei1058.bedwars.proxy.socketmanager.ArenaSocketTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.andrei1058.bedwars.proxy.BedWarsProxy.*;

public class ArenaManager implements BedWars.ArenaUtil {

    private final LinkedList<CachedArena> arenas = new LinkedList<>();
    private final HashMap<String, ArenaSocketTask> socketByServer = new HashMap<>();

    private static ArenaManager instance = null;

    private ArenaManager() {
        instance = this;
    }

    public static ArenaManager getInstance() {
        return instance == null ? new ArenaManager() : instance;
    }

    public void registerServerSocket(String server, ArenaSocketTask task) {
        if (socketByServer.containsKey(server)) {
            socketByServer.replace(server, task);
            return;
        }
        socketByServer.put(server, task);
    }

    public void registerArena(@NotNull CachedArena arena) {
        if (getArena(arena.getServer(), arena.getRemoteIdentifier()) != null) return;
        arenas.add(arena);
    }

    public CachedArena getArena(String server, String remoteIdentifier) {

        List<CachedArena> arenaList = getArenas();

        for (int i=0; i<arenaList.size(); i++) {
            if (i >= arenaList.size()) break;
            CachedArena ca = arenaList.get(i);
            if (ca.getServer().equals(server) && ca.getRemoteIdentifier().equals(remoteIdentifier)) return ca;
        }
        return null;
    }

    public static List<CachedArena> getArenas() {
        return Collections.unmodifiableList(getInstance().arenas);
    }

    public static Comparator<? super CachedArena> getComparator() {
        return new Comparator<CachedArena>() {
            @Override
            public int compare(CachedArena o1, CachedArena o2) {
                if (o1.getStatus() == ArenaStatus.STARTING && o2.getStatus() == ArenaStatus.STARTING) {
                    return Integer.compare(o2.getCurrentPlayers(), o1.getCurrentPlayers());
                }
                else if (o1.getStatus() == ArenaStatus.STARTING && o2.getStatus() != ArenaStatus.STARTING) {
                    return -1;
                }
                else if (o2.getStatus() == ArenaStatus.STARTING && o1.getStatus() != ArenaStatus.STARTING) {
                    return 1;
                }
                else if (o1.getStatus() == ArenaStatus.WAITING && o2.getStatus() == ArenaStatus.WAITING) {
                    // balance nodes
                    if (o1.getServer().equals(o2.getServer())){
                        return -1;
                    }
                    return Integer.compare(o2.getCurrentPlayers(), o1.getCurrentPlayers());
                }
                else if (o1.getStatus() == ArenaStatus.WAITING && o2.getStatus() != ArenaStatus.WAITING) {
                    return -1;
                }
                else if (o2.getStatus() == ArenaStatus.WAITING && o1.getStatus() != ArenaStatus.WAITING) {
                    return 1;
                }
                else if (o1.getStatus() == ArenaStatus.PLAYING && o2.getStatus() == ArenaStatus.PLAYING) {
                    return -1;
                }
                else if (o1.getStatus() == ArenaStatus.PLAYING && o2.getStatus() != ArenaStatus.PLAYING) {
                    return -1;
                }
                else
                    return 1;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof CachedArena;
            }
        };
    }

    public static ArenaSocketTask getSocketByServer(String server) {
        return getInstance().socketByServer.getOrDefault(server, null);
    }

    /**
     * Check if given string is an integer.
     */
    @SuppressWarnings("unused")
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
    public boolean joinRandomFromGroup(@NotNull Player p, String group) {
        //rewrite by JT122406
        //checks for party leader
        if (getParty().hasParty(p.getUniqueId()) && !getParty().isOwner(p.getUniqueId())) {
            p.sendMessage(LanguageManager.get().getMsg(p, Messages.COMMAND_JOIN_DENIED_NOT_PARTY_LEADER));
            return false;
        }
        //puts only arenas from group into arraylist
        List<CachedArena> arenaList = new ArrayList<>();
        for (CachedArena current : getArenas()) {
            if ((current.getArenaGroup().equalsIgnoreCase(group)) && ((current.getStatus() == ArenaStatus.WAITING) || (current.getStatus() == ArenaStatus.STARTING) || (current.getCurrentPlayers() < current.getMaxPlayers())))
                arenaList.add(current);
        }
        //shuffle if determined in config
        if (config.getYml().getBoolean(ConfigPath.GENERAL_CONFIGURATION_RANDOMARENAS)){
            Collections.shuffle(arenaList);
            //randomize it then we will sort by players in arena
        }

        //Reorder based on players in game
        for (int i = 0; i < arenaList.size(); i++) {
            for (int j = 0; j < arenaList.size(); j++) {
                if (j == i)
                    continue;
                else if ((i < j) && (arenaList.get(i).getCurrentPlayers() < arenaList.get(j).getCurrentPlayers())) {
                    CachedArena temp = arenaList.get(i);
                    arenaList.set(i, arenaList.get(j));
                    arenaList.set(j, temp);
                } else if ((j < i) && (arenaList.get(i).getCurrentPlayers() > arenaList.get(j).getCurrentPlayers())) {
                    CachedArena temp = arenaList.get(j);
                    arenaList.set(j, arenaList.get(i));
                    arenaList.set(i, temp);

                }
            }
        }

        int amount = BedWarsProxy.getParty().hasParty(p.getUniqueId()) ? BedWarsProxy.getParty().getMembers(p.getUniqueId()).size() : 1;
        for (CachedArena a : arenaList) {
            if ((a.getMaxPlayers() - a.getCurrentPlayers()) >= amount) {
                a.addPlayer(p, null);
                return true;
            }
        }
        p.sendMessage(LanguageManager.get().getMsg(p, Messages.COMMAND_JOIN_NO_EMPTY_FOUND));
        return true;
    }

    /**
     * Check if arena group exists.
     */
    public static boolean hasGroup(String arenaGroup) {
        for (CachedArena ad : getArenas()) {
            if (ad.getArenaGroup().equalsIgnoreCase(arenaGroup)) return true;
        }
        return false;
    }

    /**
     * Add a player to the most filled arena.
     * Check if is the party owner first.
     */
    public boolean joinRandomArena(@NotNull Player p) {
        //rewrite by JT122406
        //verifies if party leader is one joining game
        if (getParty().hasParty(p.getUniqueId()) && !getParty().isOwner(p.getUniqueId())) {
            p.sendMessage(LanguageManager.get().getMsg(p, Messages.COMMAND_JOIN_DENIED_NOT_PARTY_LEADER));
            return false;
        }

        //only adds arena that are joinable to arraylist
        List<CachedArena> arenaList = new ArrayList<>();
        for (CachedArena current : getArenas()) {
            if (((current.getStatus() == ArenaStatus.WAITING) || (current.getStatus() == ArenaStatus.STARTING) || (current.getCurrentPlayers() < current.getMaxPlayers())))
                arenaList.add(current);

        }

        //shuffle if determined in config
        if (config.getYml().getBoolean(ConfigPath.GENERAL_CONFIGURATION_RANDOMARENAS)){
            Collections.shuffle(arenaList);
            //randomize it then we will sort by players in arena
        }

        //Reorder based on players in game
        for (int i = 0; i < arenaList.size(); i++){
            for (int j = 0; j < arenaList.size(); j++){
                if (j == i)
                    continue;
                else if ((i < j) && (arenaList.get(i).getCurrentPlayers() < arenaList.get(j).getCurrentPlayers())){
                    CachedArena temp = arenaList.get(i);
                    arenaList.set(i, arenaList.get(j));
                    arenaList.set(j, temp);
                }else if ((j < i)  && (arenaList.get(i).getCurrentPlayers() > arenaList.get(j).getCurrentPlayers())){
                    CachedArena temp = arenaList.get(j);
                    arenaList.set(j, arenaList.get(i));
                    arenaList.set(i, temp);
                }
            }

        }

        int amount = BedWarsProxy.getParty().hasParty(p.getUniqueId()) ? BedWarsProxy.getParty().getMembers(p.getUniqueId()).size() : 1;
        for (CachedArena a : arenaList) {
            if ((a.getMaxPlayers() - a.getCurrentPlayers()) >= amount) {
                a.addPlayer(p, null);
                break;
            }
        }
        return true;
    }

    public void disableArena(CachedArena a) {
        arenas.remove(a);
        Bukkit.getPluginManager().callEvent(new ArenaCacheRemoveEvent(a));
    }

    public HashMap<String, ArenaSocketTask> getSocketByServer() {
        return socketByServer;
    }

    @SuppressWarnings("unused")
    @Nullable
    public static CachedArena getArenaByIdentifier(String identifier) {
        for (CachedArena ca : getArenas()) {
            if (ca.getRemoteIdentifier().equals(identifier)) {
                return ca;
            }
        }
        return null;
    }

    @Override
    public void destroyReJoins(CachedArena arena) {
        List<RemoteReJoin> toRemove = new ArrayList<>();
        for (Map.Entry<UUID, RemoteReJoin> rrj : com.andrei1058.bedwars.proxy.rejoin.RemoteReJoin.getRejoinByUUID().entrySet()) {
            if (rrj.getValue().getArena().equals(arena)) {
                toRemove.add(rrj.getValue());
            }
        }
        toRemove.forEach(RemoteReJoin::destroy);
    }

    @Override
    public RemoteReJoin getReJoin(UUID player) {
        return com.andrei1058.bedwars.proxy.rejoin.RemoteReJoin.getReJoin(player);
    }
}
