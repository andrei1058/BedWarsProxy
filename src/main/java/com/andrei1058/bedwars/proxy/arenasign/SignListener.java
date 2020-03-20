package com.andrei1058.bedwars.proxy.arenasign;

import com.andrei1058.bedwars.proxy.BedWarsProxy;
import com.andrei1058.bedwars.proxy.arenamanager.ArenaStatus;
import com.andrei1058.bedwars.proxy.arenamanager.CachedArena;
import com.andrei1058.bedwars.proxy.configuration.ConfigPath;
import com.andrei1058.bedwars.proxy.event.ArenaCacheCreateEvent;
import com.andrei1058.bedwars.proxy.event.ArenaCacheRemoveEvent;
import com.andrei1058.bedwars.proxy.event.ArenaCacheUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SignListener implements Listener {

    @EventHandler
    public void onStatusChange(ArenaCacheUpdateEvent e) {
        if (e == null) return;
        final CachedArena ca = e.getArena();
        if (ca == null) return;
        Bukkit.getScheduler().runTaskAsynchronously(BedWarsProxy.getPlugin(), () -> {
            for (ArenaSign as : SignManager.get().getArenaSigns()) {
                if (as == null) continue;
                if (as.getArena() == null) continue;
                if (ca.getArenaGroup().equals(as.getGroup())) {
                    if (as.getArena().equals(ca.getArenaName()) && as instanceof StaticArenaSign) {
                        as.refresh();
                    } else if (as instanceof DynamicArenaSign) {
                        if (e.getArena().getStatus() == ArenaStatus.PLAYING) {
                            ((DynamicArenaSign)as).setStatus(DynamicArenaSign.SignStatus.NO_DATA);
                            DynamicArenaSign.assignArena((DynamicArenaSign) as);
                        }
                        as.refresh();
                    }
                }
            }
        });
    }

    @EventHandler
    public void onCreate(ArenaCacheCreateEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(BedWarsProxy.getPlugin(), () -> {
            for (ArenaSign as : SignManager.get().getArenaSigns()) {
                if (as instanceof DynamicArenaSign) {
                    if (as.getArena() != null) continue;
                    if (e.getArena().getArenaGroup().equals(as.getGroup())) {
                        DynamicArenaSign.assignArena((DynamicArenaSign) as);
                        break;
                    }
                }
            }
        });
    }

    @EventHandler
    public void onRemove(ArenaCacheRemoveEvent e) {
        if (e == null) return;
        List<ArenaSign> toRemove = new ArrayList<>();
        for (ArenaSign as : SignManager.get().getArenaSigns()) {
            if (as instanceof DynamicArenaSign) {
                if (((DynamicArenaSign) as).getCachedArena() == null) continue;
                if (as.getGroup().equals(e.getArena().getArenaGroup()) && ((DynamicArenaSign) as).getCachedArena().getRemoteIdentifier().equals(e.getArena().getRemoteIdentifier())) {
                    toRemove.add(as);
                }
            }
        }
        Bukkit.getScheduler().runTaskAsynchronously(BedWarsProxy.getPlugin(), () -> {
            for (ArenaSign as : toRemove) {
                ((DynamicArenaSign)as).setStatus(DynamicArenaSign.SignStatus.NO_DATA);
                DynamicArenaSign.assignArena((DynamicArenaSign) as);
            }
        });
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        if (e == null) return;
        if (e.isCancelled()) return;
        if (!e.getPlayer().hasPermission("bw.setup")) return;
        if (e.getLines().length == 0) return;
        if (Objects.requireNonNull(e.getLine(0)).equalsIgnoreCase("[bw]")) {

            List<String> s = new ArrayList<>(SignManager.get().getConfig().getList(ConfigPath.SIGNS_CONFIG_PATH));

            if (e.getLines().length < 2 || e.getLines()[1].isEmpty()) {
                e.getPlayer().sendMessage(ChatColor.RED + "Invalid sign. Pleas check the wiki.");
                return;
            }

            String group = e.getLines()[1], arena = e.getLines().length>=3 ? "" : e.getLines()[2];
            s.add(SignManager.get().getConfig().stringLocationConfigFormat(e.getBlock().getLocation()) + "," + group + "," + arena);

            SignManager.get().getConfig().set(ConfigPath.SIGNS_CONFIG_PATH, s);

            ArenaSign as;
            if (arena.isEmpty()){
                as = new DynamicArenaSign(e.getBlock(), group);
            } else {
                as = new StaticArenaSign(e.getBlock(), group, arena);
            }
            e.setCancelled(true);
            Bukkit.getScheduler().runTaskLater(BedWarsProxy.getPlugin(), as::refresh, 30L);
        }
    }

    @EventHandler
    public void onSignBreak(BlockBreakEvent e){
        if (e == null) return;
        if (e.isCancelled()) return;
        if (!e.getBlock().getType().toString().contains("SIGN")) return;
        for (ArenaSign as : SignManager.get().getArenaSigns()){
            if (as.equals(e.getBlock().getWorld().getName(), e.getBlock().getLocation().getBlockX(), e.getBlock().getLocation().getBlockY(), e.getBlock().getLocation().getBlockZ())){
                if (!e.getPlayer().hasPermission("bw.setup")){
                    e.setCancelled(true);
                    Bukkit.getScheduler().runTaskLater(BedWarsProxy.getPlugin(), as::refresh, 1L);
                    return;
                }
                if (!e.getPlayer().isSneaking()){
                    e.setCancelled(true);
                    Bukkit.getScheduler().runTaskLater(BedWarsProxy.getPlugin(), as::refresh, 1L);
                    return;
                }
                as.remove();
                String toRemove = "";
                List<String> locs = SignManager.get().getConfig().getList(ConfigPath.SIGNS_CONFIG_PATH);
                for (String s : locs){
                    String[] data = s.split(",");
                    if (!data[5].equalsIgnoreCase(Objects.requireNonNull(e.getBlock().getLocation().getWorld()).getName())) continue;
                    try {
                        if (Integer.parseInt(data[0]) == e.getBlock().getLocation().getBlockX() && Integer.parseInt(data[1]) == e.getBlock().getLocation().getBlockY() && Integer.parseInt(data[2]) == e.getBlock().getLocation().getBlockZ()){
                            toRemove = s;
                        }
                    } catch (Exception ignored){
                    }
                }
                if (!toRemove.isEmpty()){
                    locs.remove(toRemove);
                    SignManager.get().getConfig().set(ConfigPath.SIGNS_CONFIG_PATH, locs);
                }
                return;
            }
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e){
        if (e == null) return;
        SignManager.get().loadSignsForWorld(e.getWorld());
    }
}
