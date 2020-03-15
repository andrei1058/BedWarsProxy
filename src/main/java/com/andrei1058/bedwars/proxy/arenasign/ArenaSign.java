package com.andrei1058.bedwars.proxy.arenasign;

import com.andrei1058.spigot.signapi.SignBoard;

public interface ArenaSign extends SignBoard {

    String getGroup();

    String getArena();

    void refresh();

    void remove();
}
