/*
 * BedWars1058 - A bed wars mini-game.
 * Copyright (C) 2023 Andrei Dascălu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package dev.andrei1058.bedwars.proxy.api.arena;

import dev.andrei1058.bedwars.common.api.arena.DisplayableArena;
import dev.andrei1058.bedwars.common.api.arena.GameStage;
import dev.andrei1058.bedwars.common.api.locale.LocaleAdapter;
import dev.andrei1058.bedwars.common.api.messaging.ISlaveServer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;

/**
 * Represents a cached game on the current master/ lobby server.
 */
public interface ProxiedGame extends DisplayableArena {

    /**
     * Change display name.
     */
    void setDisplayName(String name);

    /**
     * Change arena game state.
     */
    void setGameState(GameStage gameState);

    /**
     * Change full-join used slots.
     */
    void setVipsPlaying(int vips);

    /**
     * Toggle spectate rule.
     * This will not update the spectate rule on the remote arena.
     * @param perm permission string.
     */
    void setSpectatePermission(String perm);

    void setStartTime(Instant startTime);

    void setMinPlayers(int minPlayers);

    void setMaxPlayers(int maxPlayers);

    void setCurrentPlayers(int players);

    void setCurrentSpectators(int spectators);

    void setDisplayItem(ItemStack displayItem);

    /**
     * Get bungee server of this arena.
     */
    ISlaveServer getServer();

    String getTemplateWorld();

    boolean reJoin(Player player);

    void setPrivateGame(boolean toggle);
}
