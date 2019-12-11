package com.andrei1058.bedwars.proxy.arenamanager;

import com.andrei1058.bedwars.proxy.BedWarsProxy;
import com.andrei1058.bedwars.proxy.configuration.SoundsConfig;
import com.andrei1058.bedwars.proxy.event.ArenaCacheCreateEvent;
import com.andrei1058.bedwars.proxy.event.ArenaCacheRemoveEvent;
import com.andrei1058.bedwars.proxy.event.ArenaCacheUpdateEvent;
import com.andrei1058.bedwars.proxy.language.Language;
import com.andrei1058.bedwars.proxy.language.Messages;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class ArenaSelectorListener implements Listener {

    @EventHandler
    public void onArenaSelectorClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != null && e.getClickedInventory().getHolder() instanceof ArenaGUI.SelectorHolder) {
            e.setCancelled(true);
            Player p = (Player) e.getWhoClicked();
            ItemStack i = e.getCurrentItem();

            if (i == null) return;
            if (i.getType() == Material.AIR) return;

            if (!BedWarsProxy.getItemAdapter().hasTag(i, "server")) return;
            if (!BedWarsProxy.getItemAdapter().hasTag(i, "world_identifier")) return;
            String server = BedWarsProxy.getItemAdapter().getTag(i, "server");
            String identifier = BedWarsProxy.getItemAdapter().getTag(i, "world_identifier");

            CachedArena a = ArenaManager.getInstance().getArena(server, identifier);
            if (a == null) return;

            if (e.getClick() == ClickType.LEFT) {
                if ((a.getStatus() == ArenaStatus.WAITING || a.getStatus() == ArenaStatus.STARTING) && a.addPlayer(p, null)) {
                    SoundsConfig.playSound("join-allowed", p);
                } else {
                    SoundsConfig.playSound("join-denied", p);
                    p.sendMessage(Language.getMsg(p, Messages.ARENA_JOIN_DENIED_SELECTOR));
                }
            } else if (e.getClick() == ClickType.RIGHT) {
                if (a.getStatus() == ArenaStatus.PLAYING && a.addSpectator(p, null)) {
                    SoundsConfig.playSound("spectate-allowed", p);
                } else {
                    p.sendMessage(Language.getMsg(p, Messages.ARENA_SPECTATE_DENIED_SELECTOR));
                    SoundsConfig.playSound("spectate-denied", p);
                }
            }
            p.closeInventory();
        }
    }

    @EventHandler
    public void onArenaSelectorClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player)) return;
        ArenaGUI.getRefresh().remove(e.getPlayer());
    }

    @EventHandler
    public void onCacheCreate(ArenaCacheCreateEvent e) {
        for (Player p : ArenaGUI.getRefresh().keySet()) {
            ArenaGUI.refreshInv(p, ArenaGUI.getRefresh().get(p));
        }
    }

    @EventHandler
    public void onCacheUpdate(ArenaCacheUpdateEvent e) {
        for (Player p : ArenaGUI.getRefresh().keySet()) {
            ArenaGUI.refreshInv(p, ArenaGUI.getRefresh().get(p));
        }
    }

    @EventHandler
    public void onCacheDelete(ArenaCacheRemoveEvent e) {
        for (Player p : ArenaGUI.getRefresh().keySet()) {
            ArenaGUI.refreshInv(p, ArenaGUI.getRefresh().get(p));
        }
    }
}
