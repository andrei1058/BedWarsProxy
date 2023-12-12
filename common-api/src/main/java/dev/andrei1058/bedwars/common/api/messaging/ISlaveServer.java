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
package dev.andrei1058.bedwars.common.api.messaging;

/**
 * Interface for message producers.
 * This is used in a form of heart beat.
 */
public interface ISlaveServer {
    /**
     * Bungee name.
     */
    String getName();

    /**
     * Update the latest pong received.
     * Using {@link DefaultChannels#PING}
     */
    void pong();

    long getLastPacket();

    /**
     * Check if dead.
     */
    @SuppressWarnings("unused")
    boolean isTimedOut();
}
