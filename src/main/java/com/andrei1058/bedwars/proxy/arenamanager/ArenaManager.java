package com.andrei1058.bedwars.proxy.arenamanager;

import com.andrei1058.bedwars.proxy.socketmanager.ArenaSocketTask;

import java.util.*;

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
        for (CachedArena ca : arenas){
            if (ca.getServer().equals(server) && ca.getRemoteIdentifier().equals(remoteIdentifier)) return ca;
        }
        return null;
    }

    public static LinkedList<CachedArena> getArenas() {
        return getInstance().arenas;
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
}
