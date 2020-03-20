package com.andrei1058.bedwars.proxy.arenasign;

public interface ArenaSign {

    String getGroup();

    String getArena();

    void refresh();

    void remove();

    boolean equals(String world, int x, int y, int z);
}
