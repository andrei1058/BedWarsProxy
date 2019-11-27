package com.andrei1058.bedwars.proxy.arenamanager;

import com.andrei1058.bedwars.proxy.events.ArenaCacheCreateEvent;
import com.andrei1058.bedwars.proxy.events.ArenaCacheUpdateEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RefreshGUI implements Listener {

    @EventHandler
    public void onCacheCreate(ArenaCacheCreateEvent e){
        for (Player p : ArenaGUI.getRefresh().keySet()){
            ArenaGUI.refreshInv(p, ArenaGUI.getRefresh().get(p));
        }
    }

    @EventHandler
    public void onCacheUpdate(ArenaCacheUpdateEvent e){
        for (Player p : ArenaGUI.getRefresh().keySet()){
            ArenaGUI.refreshInv(p, ArenaGUI.getRefresh().get(p));
        }
    }
}
