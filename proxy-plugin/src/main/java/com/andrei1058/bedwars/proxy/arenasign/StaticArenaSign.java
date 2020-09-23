package com.andrei1058.bedwars.proxy.arenasign;

import com.andrei1058.bedwars.proxy.api.Language;
import com.andrei1058.bedwars.proxy.arenamanager.ArenaManager;
import com.andrei1058.bedwars.proxy.api.ArenaStatus;
import com.andrei1058.bedwars.proxy.api.CachedArena;
import com.andrei1058.bedwars.proxy.configuration.ConfigPath;
import com.andrei1058.bedwars.proxy.api.Messages;
import com.andrei1058.bedwars.proxy.language.LanguageManager;
import com.andrei1058.spigot.signapi.PacketSign;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StaticArenaSign extends PacketSign implements ArenaSign {

    private String group;
    private String arena;
    private CachedArena assignedArena;
    private SignStatus status = SignStatus.NO_DATA;

    protected StaticArenaSign(Block signBlock, String group, String arena) {
        super(signBlock);
        this.group = group;
        this.arena = arena;

        this.setContent(player -> {
            Language l = LanguageManager.get().getPlayerLanguage(player);

            if (getAssignedArena() == null) {
                if (status == SignStatus.REFRESHING) {
                    return l.getList(Messages.SIGN_STATIC_SEARCHING);
                } else {
                    return l.getList(Messages.SIGN_STATIC_NO_GAMES);
                }
            } else {
                List<String> msg;
                if (getAssignedArena().getStatus() == ArenaStatus.WAITING) {
                    msg = l.getList(Messages.SIGN_STATIC_WAITING);
                } else if (getAssignedArena().getStatus() == ArenaStatus.STARTING) {
                    msg = l.getList(Messages.SIGN_STATIC_WAITING);
                } else if (getAssignedArena().getStatus() == ArenaStatus.PLAYING){
                    msg = l.getList(Messages.SIGN_STATIC_PLAYING);
                } else {
                    msg = l.getList(Messages.SIGN_STATIC_SEARCHING);
                }
                msg.replaceAll(o -> o.replace("{group}", getAssignedArena().getDisplayGroup(l))
                        .replace("{current}", String.valueOf(getAssignedArena().getCurrentPlayers()))
                        .replace("{max}", String.valueOf(getAssignedArena().getMaxPlayers()))
                        .replace("{map}", getAssignedArena().getDisplayName(l))
                        .replace("{status}", getAssignedArena().getDisplayStatus(l))
                        .replace("{id}", getAssignedArena().getServer()));
                return msg;
            }
        });

        this.setClickListener((player, action) -> {
            if (action != Action.RIGHT_CLICK_BLOCK) return;
            CachedArena ca = getAssignedArena();
            if (ca != null){
                if (!ca.addPlayer(player, null)){
                    ca.addSpectator(player, null);
                }
            }
        });

        SignManager.get().add(this);
    }

    @Override
    public String getGroup() {
        return group;
    }

    public CachedArena getAssignedArena() {
        return assignedArena;
    }

    @Override
    public String getArena() {
        return arena;
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
    public void setStatus(SignStatus status) {
        if (status == SignStatus.NO_DATA || status == SignStatus.REFRESHING) {
            this.assignedArena = null;
        }
        this.status = status;
        refresh();
    }

    @Override
    public SignStatus getStatus() {
        return status;
    }

    protected static synchronized void assignArena(@NotNull StaticArenaSign sign){
        sign.setStatus(SignStatus.REFRESHING);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<CachedArena> arenas;
        if (SignManager.get().getConfig().getBoolean(ConfigPath.SIGNS_SETTINGS_STATIC_SHOW_PLAYING)){
            arenas = ArenaManager.getSorted(ArenaManager.getArenas()).stream().filter(p -> p.getArenaGroup().equals(sign.getGroup()))
                    .filter(p -> p.getStatus() == ArenaStatus.WAITING || p.getStatus() == ArenaStatus.STARTING || p.getStatus() == ArenaStatus.PLAYING).collect(Collectors.toList());
        } else {
            arenas = ArenaManager.getSorted(ArenaManager.getArenas()).stream().filter(p -> p.getArenaGroup().equals(sign.getGroup()))
                    .filter(p -> p.getStatus() == ArenaStatus.WAITING || p.getStatus() == ArenaStatus.STARTING).collect(Collectors.toList());
        }
        if (arenas.isEmpty()) {
            sign.setStatus(SignStatus.NO_DATA);
            return;
        }

        List<CachedArena> toRemove = new ArrayList<>();
        for (ArenaSign as : SignManager.get().getArenaSigns()) {
            if (as.getAssignedArena() == null) continue;
            if (!(as instanceof StaticArenaSign)) continue;
            toRemove.add(as.getAssignedArena());
        }
        arenas.removeAll(toRemove);
        if (arenas.isEmpty()) {
            sign.setStatus(SignStatus.NO_DATA);
        } else {
            sign.assignedArena = arenas.get(0);
            sign.setStatus(SignStatus.FOUND);
        }
    }
}
