package com.andrei1058.bedwars.proxy.arenasign;

import com.andrei1058.spigot.signapi.PacketSign;
import org.bukkit.block.Block;

public class StaticArenaSign extends PacketSign implements ArenaSign {

    protected StaticArenaSign(Block signBlock, String group, String arena) {
        super(signBlock);
    }

    @Override
    public String getGroup() {
        return null;
    }

    @Override
    public String getArena() {
        return null;
    }

    @Override
    public void remove() {

    }

    @Override
    public boolean equals(String world, int x, int y, int z) {
        return false;
    }
}
