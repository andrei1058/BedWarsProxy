package com.andrei1058.bedwars.proxy.socketmanager;

import com.andrei1058.bedwars.proxy.arenamanager.ArenaManager;
import com.andrei1058.bedwars.proxy.arenamanager.ArenaStatus;
import com.andrei1058.bedwars.proxy.arenamanager.CachedArena;

public class TimeOutTask implements Runnable {

    @Override
    public void run() {
        long time = System.currentTimeMillis()-5000;
        for (CachedArena ca : ArenaManager.getArenas()){
            if (ca.getLastUpdate() < time){
                if (ca.getStatus() != ArenaStatus.RESTARTING && ca.getStatus() != ArenaStatus.UNKNOWN){
                    ca.setStatus(ArenaStatus.UNKNOWN);
                } else if (ca.getStatus() == ArenaStatus.RESTARTING){
                    if (ca.getLastUpdate()+5000 < time) ca.setStatus(ArenaStatus.UNKNOWN);
                }
            }
        }
        ArenaManager.getArenas().removeIf(a -> a.getStatus() == ArenaStatus.UNKNOWN);
    }
}
