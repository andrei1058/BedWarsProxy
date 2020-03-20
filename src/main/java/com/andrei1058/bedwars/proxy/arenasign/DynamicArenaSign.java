package com.andrei1058.bedwars.proxy.arenasign;

import com.andrei1058.bedwars.proxy.arenamanager.ArenaManager;
import com.andrei1058.bedwars.proxy.arenamanager.ArenaStatus;
import com.andrei1058.bedwars.proxy.arenamanager.CachedArena;
import com.andrei1058.bedwars.proxy.language.Language;
import com.andrei1058.bedwars.proxy.language.Messages;
import com.andrei1058.spigot.signapi.PacketSign;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DynamicArenaSign extends PacketSign implements ArenaSign {

    private String group;
    private CachedArena arena;
    private SignStatus status = SignStatus.NO_DATA;

    /**
     * Create a dynamic sign.
     *
     * @param group arena group.
     */
    public DynamicArenaSign(Block block, @NotNull String group) {
        super(block);
        this.group = group.toLowerCase();

        this.setContent(player -> {
            Language l = Language.getPlayerLanguage(player);

            if (arena == null) {
                if (status == SignStatus.REFRESHING) {
                    return l.getList(Messages.SIGN_DYNAMIC_SEARCHING);
                } else {
                    return l.getList(Messages.SIGN_DYNAMIC_NO_GAMES);
                }
            } else {
                List<String> msg;
                if (arena.getStatus() == ArenaStatus.WAITING) {
                    msg = l.getList(Messages.SIGN_DYNAMIC_WAITING);
                } else if (arena.getStatus() == ArenaStatus.STARTING) {
                    msg = l.getList(Messages.SIGN_DYNAMIC_STARTING);
                } else {
                    msg = l.getList(Messages.SIGN_DYNAMIC_SEARCHING);
                }
                msg.replaceAll(o -> o.replace("{group}", arena.getDisplayGroup(l))
                        .replace("{current}", String.valueOf(arena.getCurrentPlayers()))
                        .replace("{max}", String.valueOf(arena.getMaxPlayers()))
                        .replace("{map}", arena.getDisplayName(l))
                        .replace("{status}", arena.getDisplayStatus(l))
                        .replace("{id}", arena.getServer()));
                return msg;
            }
        });

        this.setClickListener(new SignClickEvent() {
            @Override
            public void onInteract(Player player, Action action) {
                if (action != Action.RIGHT_CLICK_BLOCK) return;
                if (getCachedArena() != null) {
                    getCachedArena().addPlayer(player, null);
                }
            }
        });

        SignManager.get().add(this);
    }

    @Override
    public void remove() {
        SignManager.get().remove(this);
    }

    @Override
    public boolean equals(@NotNull String world, int x, int y, int z) {
        return world.equals(getWorld()) && x == getLocation().getBlockX() && y == getLocation().getBlockY() && z == getLocation().getBlockZ();
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public String getArena() {
        return arena == null ? null : arena.getRemoteIdentifier();
    }

    public CachedArena getCachedArena() {
        return arena;
    }

    public static synchronized void assignArena(@NotNull DynamicArenaSign sign) {
        sign.setArena(null);
        sign.setStatus(SignStatus.REFRESHING);
        sign.refresh();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<CachedArena> arenas = ArenaManager.getSorted(ArenaManager.getArenas()).stream().filter(p -> p.getArenaGroup().equals(sign.getGroup()))
                .filter(p -> p.getStatus() == ArenaStatus.WAITING || p.getStatus() == ArenaStatus.STARTING).collect(Collectors.toList());
        if (arenas.isEmpty()) {
            sign.setStatus(SignStatus.NO_DATA);
            return;
        }

        List<CachedArena> toRemove = new ArrayList<>();
        for (ArenaSign as : SignManager.get().getArenaSigns()) {
            if (as instanceof DynamicArenaSign) {
                if (as.getArena() == null) continue;
                toRemove.add(((DynamicArenaSign) as).getCachedArena());
            }
        }
        arenas.removeAll(toRemove);
        if (arenas.isEmpty()) {
            sign.setStatus(SignStatus.NO_DATA);
        } else {
            sign.setArena(arenas.get(0));
            sign.setStatus(SignStatus.FOUND);
        }
    }

    enum SignStatus {
        REFRESHING, NO_DATA, FOUND
    }

    public void setStatus(SignStatus status) {
        if (status == SignStatus.NO_DATA || status == SignStatus.REFRESHING) {
            this.arena = null;
        }
        this.status = status;
        refresh();
    }

    public void setArena(CachedArena arena) {
        this.arena = arena;
    }
}
