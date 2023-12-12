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
package dev.andrei1058.bedwars.common.api.arena;

import dev.andrei1058.bedwars.common.api.locale.LocaleAdapter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.UUID;

/**
 * Defines an arena that can be shown in the arena GUI, on signs etc.
 */
public interface DisplayableArena {

    /**
     * Unique game session id.
     */
    UUID getGameId();

    GameStage getGameState();

    boolean isFull();

    @Nullable String getSpectatePermission();

    String getDisplayName(@Nullable LocaleAdapter language);

    int getMaxPlayers();

    int getMinPlayers();

    int getCurrentPlayers();

    int getCurrentSpectators();

    int getCurrentVips();

    /**
     * World template used for this game instance.
     */
    String getTemplate();

    @Nullable Instant getStartTime();

    // todo
    @ApiStatus.Experimental
    boolean isPrivateGame();

    // todo
    @ApiStatus.Experimental
    void setPrivateGame(boolean toggle);

    // TODO
    @ApiStatus.Experimental
    void setPlayerHost(UUID playerHost);

    @Nullable UUID getPlayerHost();

    String getGroup();

    void setGroup(String group);

    /**
     * Add a player to the game as a Player.
     * This must be used only on WAITING or STARTING states.
     * Will return false if player is already in a game.
     *
     * @param player      user to be added.
     * @param ignoreParty if it does not matter if he is the party owner.
     * @return true if added to the game session.
     */
    boolean joinPlayer(Player player, boolean ignoreParty);

    /**
     * Add a player to the game as a Spectator.
     * @param player user to be added.
     * @param target spectating target (UUID). Null for no spectating target.
     * @param byPass to be defined.
     * @return true if added as spectator successfully.
     */
    boolean joinSpectator(Player player, @Nullable String target, boolean byPass);

    /**
     * Add a player to the game as a Spectator.
     * @param player user to be added.
     * @param target spectating target (UUID). Null for no spectating target.
     * @return true if added as spectator successfully.
     */
    default boolean joinSpectator(Player player, @Nullable String target) {
        return joinSpectator(player, target, false);
    }

    /**
     * Add a player to the game as a Spectator.
     * @param player user to be added.
     * @return true if added as spectator successfully.
     */
    default boolean joinSpectator(Player player) {
        return joinSpectator(player, null, false);
    }

    default int compareTo(@NotNull DisplayableArena other) {
        if (other.getGameState() == GameStage.STARTING && getGameState() == GameStage.STOPPING) {
            return Integer.compare(other.getCurrentPlayers(), getCurrentPlayers());
        }

        return Integer.compare(other.getGameState().getWeight(), getGameState().getWeight());
    }

    // todo provide language, null for server default
    ItemStack getDisplayItem(@Nullable LocaleAdapter localeAdapter);

    /**
     * If arena is hosted on this server instance.
     */
    boolean isLocal();
}
