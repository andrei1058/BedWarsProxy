package com.andrei1058.bedwars.proxy.rejoin;

import com.andrei1058.bedwars.proxy.arenamanager.CachedArena;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RemoteReJoin {

    private CachedArena arena;
    private UUID uuid;

    private static ConcurrentHashMap<UUID, RemoteReJoin> rejoinByUUID = new ConcurrentHashMap<>();

    public RemoteReJoin(UUID player, CachedArena arena){
        this.uuid = player;
        this.arena = arena;
        rejoinByUUID.put(uuid, this);
    }

    public static RemoteReJoin getReJoin(UUID player){
        return rejoinByUUID.getOrDefault(player, null);
    }

    public CachedArena getArena() {
        return arena;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void destroy(){
        rejoinByUUID.remove(uuid);
    }

    public static ConcurrentHashMap<UUID, RemoteReJoin> getRejoinByUUID() {
        return rejoinByUUID;
    }
}
