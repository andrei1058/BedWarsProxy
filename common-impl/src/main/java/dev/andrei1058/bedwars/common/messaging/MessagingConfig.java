package dev.andrei1058.bedwars.common.messaging;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.EnumProperty;
import ch.jalu.configme.properties.Property;
import dev.andrei1058.bedwars.common.api.messaging.MessagingType;
import org.jetbrains.annotations.NotNull;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class MessagingConfig implements SettingsHolder {
    @Comment({
            "Messaging handler defines who is in charge for delivering messages to remote lobbies in bungee mode.",
            "Available options:",
            "REDIS - requires a redis server.",
            "NONE - disables messaging.",
            "CUSTOM - allow other plugins to register their own handlers."
    })
    public static final Property<MessagingType> MESSAGING_TYPE = new EnumProperty<>(
            MessagingType.class, "messaging-handler", MessagingType.NONE
    );

    public static final Property<String> SOCKET_HOST = newProperty("socket.host", "127.0.0.1");
    public static final Property<Integer> SOCKET_PORT = newProperty("socket.port", 11058);

    public static final Property<String> REDIS_HOST = newProperty("redis.host", "127.0.0.1");
    public static final Property<Integer> REDIS_PORT = newProperty("redis.port", 6379);
    public static final Property<String> REDIS_USER = newProperty("redis.user", "");
    public static final Property<String> REDIS_PASS = newProperty("redis.pass", "");


    @Override
    public void registerComments(@NotNull CommentsConfiguration conf) {
        conf.setComment(
                "",
                "Made with love and coffee by andrei1058",
                "Website: https://andrei1058.dev",
                "Discord server: https://discord.gg/XdJfN2X",
                ""
        );
        conf.setComment("redis", "", "Redis server configuration.", "");
    }
}
