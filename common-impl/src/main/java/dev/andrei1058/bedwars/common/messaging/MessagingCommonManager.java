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
package dev.andrei1058.bedwars.common.messaging;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import dev.andrei1058.bedwars.common.api.messaging.MessagingHandler;
import dev.andrei1058.bedwars.common.api.messaging.MessagingType;
import dev.andrei1058.bedwars.common.messaging.handler.RedisMessagingHandler;
import dev.andrei1058.bedwars.common.messaging.handler.VoidMessagingHandler;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class MessagingCommonManager {

    @Getter
    private static MessagingCommonManager instance;
    @Getter
    private final Plugin plugin;
    public static void init(Plugin plugin) {
        if (null == instance) {
            instance = new MessagingCommonManager(plugin);
        }
    }

    @Getter
    private final SettingsManager config;
    @Getter
    private final MessagingType messagingType;
    @Getter
    private MessagingHandler messagingHandler = new VoidMessagingHandler();

    /**
     * @param plugin implementation holder.
     */
    private MessagingCommonManager(Plugin plugin) {
        this.plugin = plugin;
        config = SettingsManagerBuilder.withYamlFile(
                new File(getPlugin().getDataFolder(), "messaging.yml")
        ).configurationData(MessagingConfig.class).useDefaultMigrationService().create();
        messagingType = config.getProperty(MessagingConfig.MESSAGING_TYPE);

        if (messagingType == MessagingType.REDIS) {
            String user = config.getProperty(MessagingConfig.REDIS_USER);
            String pass = config.getProperty(MessagingConfig.REDIS_PASS);
            try {
                messagingHandler = new RedisMessagingHandler(
                        config.getProperty(MessagingConfig.REDIS_HOST),
                        config.getProperty(MessagingConfig.REDIS_PORT),
                        user.trim().isEmpty() ? null : user,
                        pass.trim().isEmpty() ? null : pass
                );
                getPlugin().getLogger().info("Using internal Redis messaging adapter.");
            } catch (InstantiationException e) {
                MessagingCommonManager.getInstance().getPlugin().getLogger().severe(e.getMessage());
            }
        }
    }

}
