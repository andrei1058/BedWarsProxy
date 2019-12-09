package com.andrei1058.bedwars.proxy.socketmanager;

import com.andrei1058.bedwars.proxy.arenamanager.ArenaManager;
import com.andrei1058.bedwars.proxy.arenamanager.ArenaStatus;
import com.andrei1058.bedwars.proxy.arenamanager.CachedArena;
import com.andrei1058.bedwars.proxy.event.ArenaCacheUpdateEvent;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class TimeOutTask implements Runnable {

    private static List<CachedArena> toRemove = new ArrayList<>();

    @Override
    public void run() {
        long time = System.currentTimeMillis()-5000;
        for (CachedArena ca : ArenaManager.getArenas()){
            if (ca.getLastUpdate() < time){
                if (ca.getStatus() != ArenaStatus.RESTARTING && ca.getStatus() != ArenaStatus.UNKNOWN){
                    ca.setStatus(ArenaStatus.UNKNOWN);
                    Bukkit.getPluginManager().callEvent(new ArenaCacheUpdateEvent(ca));
                } else if (ca.getStatus() == ArenaStatus.RESTARTING){
                    if (ca.getLastUpdate()+5000 < time){
                        ca.setStatus(ArenaStatus.UNKNOWN);
                        toRemove.add(ca);
                    }
                }
            }
        }
        if (!toRemove.isEmpty()){
            toRemove.forEach(ca -> ArenaManager.getInstance().disableArena(ca));
            toRemove.clear();
        }
    }
}
