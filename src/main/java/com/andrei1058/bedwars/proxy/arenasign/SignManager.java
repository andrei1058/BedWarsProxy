package com.andrei1058.bedwars.proxy.arenasign;

import com.andrei1058.bedwars.proxy.BedWarsProxy;
import com.andrei1058.spigot.signapi.SignAPI;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SignManager {

    private static SignManager INSTANCE;

    private SignAPI signAPI;
    private LinkedList<ArenaSign> arenaSigns = new LinkedList<>();

    private SignManager() {
        INSTANCE = this;
        signAPI = new SignAPI(BedWarsProxy.getPlugin());
        Bukkit.getPluginManager().registerEvents(new SignListener(), BedWarsProxy.getPlugin());
    }

    public static void init() {
        if (INSTANCE == null) new SignManager();
    }

    public static SignManager get() {
        return INSTANCE;
    }

    public void remove(ArenaSign arenaSign) {
        arenaSigns.remove(arenaSign);
        signAPI.removeSign(arenaSign);
    }

    @NotNull
    @Contract(pure = true)
    public List<ArenaSign> getArenaSigns() {
        return Collections.unmodifiableList(arenaSigns);
    }

    protected void add(ArenaSign arenaSign){
        arenaSigns.add(arenaSign);
    }
}
