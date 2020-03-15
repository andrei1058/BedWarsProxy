package com.andrei1058.bedwars.proxy.arenasign;

import com.andrei1058.spigot.signapi.ASign;
import org.bukkit.block.Block;

public class StaticArenaSign extends ASign implements ArenaSign {

    protected StaticArenaSign(Block signBlock) {
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
}
