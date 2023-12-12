package dev.andrei1058.bedwars.common.api;

import dev.andrei1058.bedwars.common.api.arena.DisplayableArena;

import java.util.Collection;

public interface CommonProvider {

    Collection<DisplayableArena> getGames();
}
