package com.andrei1058.bedwars.proxy.arenasign;

import com.andrei1058.bedwars.proxy.BedWarsProxy;
import com.andrei1058.bedwars.proxy.arenamanager.CachedArena;
import com.andrei1058.bedwars.proxy.event.ArenaCacheCreateEvent;
import com.andrei1058.bedwars.proxy.event.ArenaCacheRemoveEvent;
import com.andrei1058.bedwars.proxy.event.ArenaCacheUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class SignListener implements Listener {

    @EventHandler
    public void onStatusChange(ArenaCacheUpdateEvent e){
        if (e == null) return;
        final CachedArena ca = e.getArena();
        Bukkit.getScheduler().runTaskAsynchronously(BedWarsProxy.getPlugin(), ()-> {
            for (ArenaSign as : SignManager.get().getArenaSigns()){
                if (ca.getArenaGroup().equals(as.getGroup())){
                    if (as.getArena().equals(ca.getArenaName()) && as instanceof StaticArenaSign){
                        as.refresh();
                    } else if (as instanceof DynamicArenaSign){
                        as.refresh();
                    }
                }
            }
        });
    }

    @EventHandler
    public void onCreate(ArenaCacheCreateEvent e){
        for (ArenaSign as : SignManager.get().getArenaSigns()){
            if (as instanceof DynamicArenaSign) {
                if (as.getArena() != null) continue;
                if (e.getArena().getArenaGroup().equals(as.getGroup())) {
                    ((DynamicArenaSign)as).setArena(e.getArena());
                    ((DynamicArenaSign) as).setStatus(DynamicArenaSign.SignStatus.FOUND);
                }
            }
        }
    }

    @EventHandler
    public void onRemove(ArenaCacheRemoveEvent e){
        if (e == null) return;
        List<ArenaSign> toRemove = new ArrayList<>();
        for (ArenaSign as : SignManager.get().getArenaSigns()){
            if (as instanceof DynamicArenaSign){
                if (as.getGroup().equals(e.getArena().getArenaGroup()) && as.getArena().equals(e.getArena().getArenaName())){
                    toRemove.add(as);
                }
            }
        }
        //Bukkit.getScheduler().runTaskAsynchronously(BedWarsProxy.getPlugin(), Syna)
    }
}
