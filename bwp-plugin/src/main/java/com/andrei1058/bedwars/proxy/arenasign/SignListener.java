package com.andrei1058.bedwars.proxy.arenasign;

import com.andrei1058.bedwars.proxy.api.ArenaStatus;
import com.andrei1058.bedwars.proxy.api.CachedArena;
import com.andrei1058.bedwars.proxy.configuration.ConfigPath;
import com.andrei1058.bedwars.proxy.api.event.ArenaCacheCreateEvent;
import com.andrei1058.bedwars.proxy.api.event.ArenaCacheRemoveEvent;
import com.andrei1058.bedwars.proxy.api.event.ArenaCacheUpdateEvent;
import com.andrei1058.bedwars.proxy.BedWarsProxy;
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
import java.util.stream.Collectors;

public class SignListener implements Listener {

    @EventHandler
    public void onStatusChange(ArenaCacheUpdateEvent e) {
        if (e == null) return;
        final CachedArena ca = e.getArena();
        if (ca == null) return;
        Bukkit.getScheduler().runTaskAsynchronously(BedWarsProxy.getPlugin(), () -> {
            for (ArenaSign as : SignManager.get().getArenaSigns().stream().filter(p -> p instanceof DynamicArenaSign).collect(Collectors.toList())) {
                if (as == null) continue;
                if (as.getAssignedArena() == null) continue;
                if (ca.equals(as.getAssignedArena())) {
                    if (e.getArena().getStatus() == ArenaStatus.PLAYING || e.getArena().getStatus() == ArenaStatus.RESTARTING || e.getArena().getStatus() == ArenaStatus.UNKNOWN) {
                        DynamicArenaSign.assignArena((DynamicArenaSign) as);
                    }
                    as.refresh();
                }
            }
        });
        Bukkit.getScheduler().runTaskAsynchronously(BedWarsProxy.getPlugin(), () -> {
            for (ArenaSign as : SignManager.get().getArenaSigns().stream().filter(p -> p instanceof StaticArenaSign).collect(Collectors.toList())) {
                if (as == null) continue;
                if (as.getAssignedArena() == null) continue;
                if (ca.equals(as.getAssignedArena())) {
                    if (e.getArena().getStatus() == ArenaStatus.PLAYING && !SignManager.get().getConfig().getBoolean(ConfigPath.SIGNS_SETTINGS_STATIC_SHOW_PLAYING)
                            || e.getArena().getStatus() == ArenaStatus.RESTARTING || e.getArena().getStatus() == ArenaStatus.UNKNOWN) {
                        StaticArenaSign.assignArena((StaticArenaSign) as);
                    }
                    as.refresh();
                }
            }
        });
    }

    @EventHandler
    public void onCreate(ArenaCacheCreateEvent e) {
        if (e == null) return;
        Bukkit.getScheduler().runTaskAsynchronously(BedWarsProxy.getPlugin(), () -> {
            for (ArenaSign as : SignManager.get().getArenaSigns().stream().filter(p -> p instanceof DynamicArenaSign).collect(Collectors.toList())) {
                if (as.getAssignedArena() != null) continue;
                if (e.getArena().getArenaGroup().equals(as.getGroup())) {
                    DynamicArenaSign.assignArena((DynamicArenaSign) as);
                    break;
                }
            }
        });
        Bukkit.getScheduler().runTaskAsynchronously(BedWarsProxy.getPlugin(), () -> {
            for (ArenaSign as : SignManager.get().getArenaSigns().stream().filter(p -> p instanceof StaticArenaSign).collect(Collectors.toList())) {
                if (as.getAssignedArena() != null) continue;
                if (e.getArena().getArenaName().equalsIgnoreCase(as.getArena()) && e.getArena().getArenaGroup().equalsIgnoreCase(as.getGroup())) {
                    StaticArenaSign.assignArena((StaticArenaSign) as);
                    break;
                }
            }
        });
    }

    @EventHandler
    public void onRemove(ArenaCacheRemoveEvent e) {
        if (e == null) return;
        Bukkit.getScheduler().runTaskAsynchronously(BedWarsProxy.getPlugin(), () -> {
            List<ArenaSign> toRemove = new ArrayList<>();
            for (ArenaSign as : SignManager.get().getArenaSigns()) {
                if (as.getAssignedArena() == null) continue;
                if (as.getAssignedArena().equals(e.getArena())) {
                    toRemove.add(as);
                }
            }
            for (ArenaSign as : toRemove) {
                if (as instanceof DynamicArenaSign) {
                    DynamicArenaSign.assignArena((DynamicArenaSign) as);
                } else if (as instanceof StaticArenaSign){
                    StaticArenaSign.assignArena((StaticArenaSign) as);
                }
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

            List<String> s = new ArrayList<>(SignManager.get().getConfig().getList(ConfigPath.SIGNS_LIST_PATH));

            if (e.getLines().length < 2 || e.getLines()[1].isEmpty()) {
                e.getPlayer().sendMessage(ChatColor.RED + "Invalid sign. Pleas check the wiki.");
                return;
            }

            String group = e.getLines()[1], arena = e.getLines().length >= 3 ? e.getLines()[2] : "";
            s.add(SignManager.get().getConfig().stringLocationConfigFormat(e.getBlock().getLocation()) + "," + group + "," + arena);

            SignManager.get().getConfig().set(ConfigPath.SIGNS_LIST_PATH, s);

            ArenaSign as;
            if (arena.isEmpty()) {
                as = new DynamicArenaSign(e.getBlock(), group);
            } else {
                as = new StaticArenaSign(e.getBlock(), group, arena);
            }
            e.setCancelled(true);
            Bukkit.getScheduler().runTaskLater(BedWarsProxy.getPlugin(), () -> {
                as.refresh();
                Bukkit.getScheduler().runTaskAsynchronously(BedWarsProxy.getPlugin(), () -> {
                    if (as instanceof DynamicArenaSign) {
                        DynamicArenaSign.assignArena((DynamicArenaSign) as);
                    } else {
                        StaticArenaSign.assignArena((StaticArenaSign) as);
                    }
                });
            }, 30L);
        }
    }

    @EventHandler
    public void onSignBreak(BlockBreakEvent e) {
        if (e == null) return;
        if (e.isCancelled()) return;
        if (!e.getBlock().getType().toString().contains("SIGN")) return;
        for (ArenaSign as : SignManager.get().getArenaSigns()) {
            if (as.equals(e.getBlock().getWorld().getName(), e.getBlock().getLocation().getBlockX(), e.getBlock().getLocation().getBlockY(), e.getBlock().getLocation().getBlockZ())) {
                if (!e.getPlayer().hasPermission("bw.setup")) {
                    e.setCancelled(true);
                    Bukkit.getScheduler().runTaskLater(BedWarsProxy.getPlugin(), as::refresh, 1L);
                    return;
                }
                if (!e.getPlayer().isSneaking()) {
                    e.setCancelled(true);
                    Bukkit.getScheduler().runTaskLater(BedWarsProxy.getPlugin(), as::refresh, 1L);
                    return;
                }
                as.remove();
                String toRemove = "";
                List<String> locs = SignManager.get().getConfig().getList(ConfigPath.SIGNS_LIST_PATH);
                for (String s : locs) {
                    String[] data = s.split(",");
                    if (!data[5].equalsIgnoreCase(Objects.requireNonNull(e.getBlock().getLocation().getWorld()).getName()))
                        continue;
                    try {
                        if (Integer.parseInt(data[0]) == e.getBlock().getLocation().getBlockX() && Integer.parseInt(data[1]) == e.getBlock().getLocation().getBlockY() && Integer.parseInt(data[2]) == e.getBlock().getLocation().getBlockZ()) {
                            toRemove = s;
                        }
                    } catch (Exception ignored) {
                    }
                }
                if (!toRemove.isEmpty()) {
                    locs.remove(toRemove);
                    SignManager.get().getConfig().set(ConfigPath.SIGNS_LIST_PATH, locs);
                }

                List<ArenaSign> emptySigns = SignManager.get().getArenaSigns().stream().filter(p -> p.getAssignedArena() == null).collect(Collectors.toList());
                for (ArenaSign s : emptySigns) {
                    if (s instanceof DynamicArenaSign && as instanceof DynamicArenaSign) {
                        DynamicArenaSign.assignArena((DynamicArenaSign) s);
                        break;
                    } else if (s instanceof StaticArenaSign && as instanceof StaticArenaSign) {
                        StaticArenaSign.assignArena((StaticArenaSign) s);
                        break;
                    }
                }
                return;
            }
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        if (e == null) return;
        SignManager.get().loadSignsForWorld(e.getWorld());
    }
}
