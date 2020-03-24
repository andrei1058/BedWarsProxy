package com.andrei1058.bedwars.proxy.arenasign;

import com.andrei1058.bedwars.proxy.configuration.ConfigPath;
import com.andrei1058.bedwars.proxy.configuration.SignsConfig;
import com.andrei1058.bedwars.proxy.BedWarsProxy;
import com.andrei1058.spigot.signapi.PacketSign;
import com.andrei1058.spigot.signapi.SpigotSignAPI;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SignManager {

    private static SignManager INSTANCE;

    private SpigotSignAPI signAPI;
    private LinkedList<ArenaSign> arenaSigns = new LinkedList<>();
    private SignsConfig signsConfig = new SignsConfig();

    private SignManager() {
        INSTANCE = this;
        signAPI = new SpigotSignAPI(BedWarsProxy.getPlugin());
        signAPI.setDelay(20);
        Bukkit.getPluginManager().registerEvents(new SignListener(), BedWarsProxy.getPlugin());

        for (String s : getConfig().getList(ConfigPath.SIGNS_LIST_PATH)){
            String[] data = s.split(",");
            World w = Bukkit.getWorld(data[5]);
            if (w == null) continue;
            Block b;
            try {
                b = w.getBlockAt(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));
            } catch (Exception ignored){
                continue;
            }
            if (!b.getType().toString().contains("SIGN")) continue;
            if (data.length >= 7){
                if (data.length < 8 || data[7].isEmpty()){
                    new DynamicArenaSign(b, data[6]);
                } else {
                    new StaticArenaSign(b, data[6], data[7]);
                }
            }
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(BedWarsProxy.getPlugin(), ()-> {
            for (ArenaSign as : arenaSigns){
                if (as instanceof DynamicArenaSign) {
                    DynamicArenaSign.assignArena((DynamicArenaSign) as);
                } else if (as instanceof StaticArenaSign){
                    StaticArenaSign.assignArena((StaticArenaSign) as);
                }
            }
        }, 20*10L);
    }

    public static void init() {
        if (INSTANCE == null) new SignManager();
    }

    public static SignManager get() {
        return INSTANCE;
    }

    public void remove(ArenaSign arenaSign) {
        arenaSigns.remove(arenaSign);
        signAPI.removeSign((PacketSign) arenaSign);
    }

    @NotNull
    @Contract(pure = true)
    public List<ArenaSign> getArenaSigns() {
        return Collections.unmodifiableList(arenaSigns);
    }

    protected void add(ArenaSign arenaSign){
        arenaSigns.add(arenaSign);
        signAPI.addSign((PacketSign) arenaSign);
    }

    public SignsConfig getConfig() {
        return signsConfig;
    }

    public void loadSignsForWorld(World world){
        List<ArenaSign> signs = new ArrayList<>();
        for (String s : getConfig().getList(ConfigPath.SIGNS_LIST_PATH)){
            String[] data = s.split(",");
            if (!data[5].equalsIgnoreCase(world.getName())) continue;
            Block b;
            try {
                b = world.getBlockAt(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));
            } catch (Exception ignored){
                continue;
            }
            if (!b.getType().toString().contains("SIGN")) continue;
            if (data.length >= 7){
                if (data.length < 8 || data[7].isEmpty()){
                    signs.add(new DynamicArenaSign(b, data[6]));
                } else {
                    signs.add(new StaticArenaSign(b, data[6], data[7]));
                }
            }
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(BedWarsProxy.getPlugin(), ()-> {
            for (ArenaSign as : signs){
                if (as instanceof DynamicArenaSign) {
                    DynamicArenaSign.assignArena((DynamicArenaSign) as);
                } else if (as instanceof StaticArenaSign){
                    StaticArenaSign.assignArena((StaticArenaSign) as);
                }
            }
        }, 20*10L);
    }
}
