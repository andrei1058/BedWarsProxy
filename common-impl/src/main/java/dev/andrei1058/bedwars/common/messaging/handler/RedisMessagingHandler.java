package dev.andrei1058.bedwars.common.messaging.handler;

import dev.andrei1058.bedwars.common.api.messaging.MessagingChannel;
import dev.andrei1058.bedwars.common.api.messaging.MessagingHandler;
import dev.andrei1058.bedwars.common.api.messaging.MessagingPacket;
import dev.andrei1058.bedwars.common.messaging.MessagingCommonManager;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class RedisMessagingHandler implements MessagingHandler {

    private static final ConcurrentHashMap<String, MessagingChannel<?>> channelsByName = new ConcurrentHashMap<>();

    //    private final JedisPool pool;
    private final JedisPool in;
    private final JedisPool out;
    private final Gson gson = new Gson();


    public RedisMessagingHandler(String host, int port, String user, String pass) throws InstantiationException {
        in = new JedisPool(buildPoolConfig(), host, port, user, pass);


        boolean testConnection;
        try (Jedis jedis = in.getResource()) {
            testConnection = jedis.isConnected();
        } catch (Exception exception) {
            exception.printStackTrace();
            testConnection = false;
        }
        if (!testConnection) {
            throw new InstantiationException("Could not connect to redis server!");
        }

        out = new JedisPool(buildPoolConfig(), host, port, user, pass);
    }

    @Override
    public boolean registerIncomingPacketChannel(@NotNull MessagingChannel<?> messagingChannel) {
        if (messagingChannel.getName().trim().length() > 32) {
            return false;
        }
        if (isChannelRegistered(messagingChannel.getName())) {
            return false;
        }
        channelsByName.put(messagingChannel.getName(), messagingChannel);
        MessagingCommonManager.getInstance().getPlugin().getLogger().info("Registered messaging channel: "+messagingChannel.getName());
        Bukkit.getScheduler().runTaskAsynchronously(MessagingCommonManager.getInstance().getPlugin(), () -> {
            try (Jedis jedis = in.getResource()) {
                jedis.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        messagingChannel.read(gson.fromJson(message, (Type) messagingChannel.getType()));
                    }
                }, messagingChannel.getName());
            }
            Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "Channel thread interrupted: " + messagingChannel.getName());
        });
        return true;
    }

    @Override
    public boolean isChannelRegistered(String channel) {
        return channelsByName.containsKey(channel);
    }

    @Override
    public void sendPacket(String channel, MessagingPacket message, boolean async) {
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(
                    MessagingCommonManager.getInstance().getPlugin(),
                    () -> publish(channel, message)
            );
        } else {
            publish(channel, message);
        }
    }

    @Override
    public void sendPackets(String channel, List<MessagingPacket> messages, boolean async) {
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(MessagingCommonManager.getInstance().getPlugin(),
                    () -> messages.forEach(message -> publish(channel, message))
            );
        } else {
            messages.forEach(message -> publish(channel, message));
        }
    }

    @Override
    public @Nullable MessagingChannel<?> getChannelByName(String name) {
        return channelsByName.get(name);
    }

    private void publish(String channel, @NotNull MessagingPacket message) {
        try (Jedis jedis = out.getResource()) {
            jedis.publish(channel, new Gson().toJson(message));
        }
    }

    private @NotNull JedisPoolConfig buildPoolConfig() {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTime(Duration.ofSeconds(60));
        poolConfig.setTimeBetweenEvictionRuns(Duration.ofSeconds(30));
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        return poolConfig;
    }
}
