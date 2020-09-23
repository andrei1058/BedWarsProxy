package com.andrei1058.bedwars.proxy.arenasign;

import com.andrei1058.bedwars.proxy.api.Language;
import com.andrei1058.bedwars.proxy.arenamanager.ArenaManager;
import com.andrei1058.bedwars.proxy.api.ArenaStatus;
import com.andrei1058.bedwars.proxy.api.CachedArena;
import com.andrei1058.bedwars.proxy.api.Messages;
import com.andrei1058.bedwars.proxy.language.LanguageManager;
import com.andrei1058.spigot.signapi.PacketSign;
import org.bukkit.block.Block;
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
            Language l = LanguageManager.get().getPlayerLanguage(player);

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

        this.setClickListener((player, action) -> {
            if (action != Action.RIGHT_CLICK_BLOCK) return;
            if (getAssignedArena() != null) {
                getAssignedArena().addPlayer(player, null);
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

    public CachedArena getAssignedArena() {
        return arena;
    }

    protected static synchronized void assignArena(@NotNull DynamicArenaSign sign) {
        sign.setStatus(SignStatus.REFRESHING);
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
            sign.setStatus(SignStatus.NO_DATA);
            return;
        }
        List<CachedArena> arenas = ArenaManager.getSorted(ArenaManager.getArenas()).stream().filter(p -> p.getArenaGroup().equals(sign.getGroup()))
                .filter(p -> p.getStatus() == ArenaStatus.WAITING || p.getStatus() == ArenaStatus.STARTING).collect(Collectors.toList());
        if (arenas.isEmpty()) {
            sign.setStatus(SignStatus.NO_DATA);
            return;
        }

        List<CachedArena> toRemove = new ArrayList<>();
        for (ArenaSign as : SignManager.get().getArenaSigns()) {
            if (as.getAssignedArena() == null) continue;
            if (!(as instanceof DynamicArenaSign)) continue;
            toRemove.add(as.getAssignedArena());
        }
        arenas.removeAll(toRemove);
        if (arenas.isEmpty()) {
            sign.setStatus(SignStatus.NO_DATA);
        } else {
            sign.setArena(arenas.get(0));
            sign.setStatus(SignStatus.FOUND);
        }
    }


    public void setStatus(SignStatus status) {
        if (status == SignStatus.NO_DATA || status == SignStatus.REFRESHING) {
            this.arena = null;
        }
        this.status = status;
        refresh();
    }

    @Override
    public SignStatus getStatus() {
        return status;
    }

    public void setArena(CachedArena arena) {
        this.arena = arena;
    }
}
