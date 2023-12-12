package dev.andrei1058.bedwars.proxy.messaging;

import com.andrei1058.bedwars.proxy.BedWarsProxy;
import com.andrei1058.bedwars.proxy.arenamanager.ArenaManager;
import com.andrei1058.bedwars.proxy.configuration.ConfigPath;
import dev.andrei1058.bedwars.common.api.messaging.*;
import dev.andrei1058.bedwars.common.api.messaging.packet.*;
import dev.andrei1058.bedwars.common.messaging.MessagingCommonManager;
import dev.andrei1058.bedwars.proxy.api.arena.ProxiedGame;
import dev.andrei1058.bedwars.proxy.arena.ArenaService;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class ConnectorMessaging {

    @Getter
    private static ConnectorMessaging singleton;

    @Getter
    private final int keepAliveTolerance = 5_000;

    public static void init() {
        if (singleton != null) {
            return;
        }

        MessagingCommonManager.init(BedWarsProxy.getPlugin());

        singleton = new ConnectorMessaging();
        singleton.registerChannels();
        Bukkit.getScheduler().scheduleSyncDelayedTask(BedWarsProxy.getPlugin(),
                () -> singleton.queryOnlineArenas(), 20L
        );
    }

    @Getter
    private String identity;
    @Getter
    private final Integer taskId;

    private ConnectorMessaging() {
        identity = BedWarsProxy.config.getString(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_SERVER_ID);

        // life checker
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(BedWarsProxy.getPlugin(), ()-> {
            LinkedList<ISlaveServer> markedForRemoval = new LinkedList<>();

            long bound = System.currentTimeMillis()-keepAliveTolerance;
            ArenaService.getInstance().getSlaves().forEach(slave -> {
                if (slave.getLastPacket() < bound){
                    markedForRemoval.add(slave);
                }
            });

            markedForRemoval.forEach(marked -> ArenaService.getInstance().removeSlave(marked));
        },100L, 20L);
    }

    private void registerChannels() {
        boolean fullDataPacket = MessagingCommonManager.getInstance().getMessagingHandler().registerIncomingPacketChannel(
                new MessagingChannel<PostGameDataPacket>() {
                    @Override
                    public void read(PostGameDataPacket message) {
                        if (isTargetedHere(message, true)) {
                            ArenaService.getInstance().processPacket(message);
                        }
                    }

                    @Override
                    public Class<PostGameDataPacket> getType() {
                        return PostGameDataPacket.class;
                    }

                    @Override
                    public String getName() {
                        return DefaultChannels.ARENA_FULL_DATA.getName();
                    }
                }
        );

        if (!fullDataPacket) {
            BedWarsProxy.getPlugin().getLogger().severe("Could not register FullData channel!");
        }

        boolean playerCountPacket = MessagingCommonManager.getInstance().getMessagingHandler().registerIncomingPacketChannel(
                new MessagingChannel<PostGamePlayerCountPacket>() {
                    @Override
                    public void read(PostGamePlayerCountPacket message) {
                        ArenaService.getInstance().processPacket(message);
                    }

                    @Override
                    public Class<PostGamePlayerCountPacket> getType() {
                        return PostGamePlayerCountPacket.class;
                    }

                    @Override
                    public String getName() {
                        return DefaultChannels.PLAYER_COUNT_CHANNEL.getName();
                    }
                }
        );

        if (!playerCountPacket) {
            BedWarsProxy.getPlugin().getLogger().severe("Could not register PlayerCount channel!");
        }

        boolean arenaStatePacket = MessagingCommonManager.getInstance().getMessagingHandler().registerIncomingPacketChannel(
                new MessagingChannel<PostGameStatePacket>() {
                    @Override
                    public void read(PostGameStatePacket message) {
                        ArenaService.getInstance().processPacket(message);
                    }

                    @Override
                    public Class<PostGameStatePacket> getType() {
                        return PostGameStatePacket.class;
                    }

                    @Override
                    public String getName() {
                        return DefaultChannels.ARENA_STATUS_UPDATE.getName();
                    }
                }
        );

        if (!arenaStatePacket) {
            BedWarsProxy.getPlugin().getLogger().severe("Could not register ArenaState channel!");
        }

        boolean slavePongPacket = MessagingCommonManager.getInstance().getMessagingHandler().registerIncomingPacketChannel(
                new MessagingChannel<PostSlavePongPacket>() {
                    @Override
                    public void read(PostSlavePongPacket message) {
                        ArenaService.getInstance().processPacket(message);
                    }

                    @Override
                    public Class<PostSlavePongPacket> getType() {
                        return PostSlavePongPacket.class;
                    }

                    @Override
                    public String getName() {
                        return DefaultChannels.PING.getName();
                    }
                }
        );

        if (!slavePongPacket) {
            BedWarsProxy.getPlugin().getLogger().severe("Could not register PongPacket channel!");
        }

        boolean gameDropPacket = MessagingCommonManager.getInstance().getMessagingHandler().registerIncomingPacketChannel(
                new MessagingChannel<PostGameDropPacket>() {
                    @Override
                    public void read(PostGameDropPacket message) {
                        ArenaService.getInstance().processPacket(message);
                    }

                    @Override
                    public Class<PostGameDropPacket> getType() {
                        return PostGameDropPacket.class;
                    }

                    @Override
                    public String getName() {
                        return DefaultChannels.GAME_DROP.getName();
                    }
                }
        );

        if (!gameDropPacket) {
            BedWarsProxy.getPlugin().getLogger().severe("Could not register GameDropPacket channel!");
        }

        boolean slaveWakeUpPacket = MessagingCommonManager.getInstance().getMessagingHandler().registerIncomingPacketChannel(
                new MessagingChannel<PostSlaveWakeUpPacket>() {
                    @Override
                    public void read(PostSlaveWakeUpPacket message) {
                        // todo
//                        GameReJoinManager.getInstance().clearAllForSlave(message.getSender());
                        var slave = ArenaService.getInstance().getSlaveByName(message.getSender());
                        if (null != slave) {
                            ArenaService.getInstance().removeSlave(slave);
                        }
                    }

                    @Override
                    public Class<PostSlaveWakeUpPacket> getType() {
                        return PostSlaveWakeUpPacket.class;
                    }

                    @Override
                    public String getName() {
                        return DefaultChannels.SLAVE_WAKE_UP.getName();
                    }
                }
        );

        if (!slaveWakeUpPacket) {
            BedWarsProxy.getPlugin().getLogger().severe("Could not register SlaveWakeUp channel!");
        }

        boolean privateTakeOver = MessagingCommonManager.getInstance().getMessagingHandler().registerIncomingPacketChannel(
                new MessagingChannel<PostGameTakeOverPacket>() {
                    @Override
                    public void read(PostGameTakeOverPacket message) {
                        var slave = ArenaService.getInstance().getSlaveByName(message.getSender());
                        if (null == slave) {
                            return;
                        }
                        slave.pong();
                        var arena = ArenaService.getInstance().getArena(slave, message.getGameId());
                        if (arena == null){
                            return;
                        }
                        // todo
//                        ArenaUpdater.updateFromPacket(arena, message);
                    }

                    @Override
                    public Class<PostGameTakeOverPacket> getType() {
                        return PostGameTakeOverPacket.class;
                    }

                    @Override
                    public String getName() {
                        return DefaultChannels.PRIVATE_TAKE_OVER.getName();
                    }
                }
        );

        if (!privateTakeOver) {
            BedWarsProxy.getPlugin().getLogger().severe("Could not register PrivateTakeOver channel!");
        }

        boolean postReJoin = MessagingCommonManager.getInstance().getMessagingHandler().registerIncomingPacketChannel(
                new MessagingChannel<PostReJoinCreatePacket>() {
                    @Override
                    public void read(PostReJoinCreatePacket message) {
                        // todo
//                        GameReJoinManager.getInstance().storeReJoin(message.getPlayer(), message.getSender(), message.getGameId());
                    }

                    @Override
                    public Class<PostReJoinCreatePacket> getType() {
                        return PostReJoinCreatePacket.class;
                    }

                    @Override
                    public String getName() {
                        return DefaultChannels.POST_REJOIN_CREATED.getName();
                    }
                }
        );

        if (!postReJoin) {
            BedWarsProxy.getPlugin().getLogger().severe("Could not register PostReJoin channel!");
        }

        boolean deleteRejoin = MessagingCommonManager.getInstance().getMessagingHandler().registerIncomingPacketChannel(
                new MessagingChannel<PostReJoinDeletePacket>() {
                    @Override
                    public void read(PostReJoinDeletePacket message) {
                        // todo
//                        GameReJoinManager.getInstance().clearForPlayer(message.getPlayer());
                    }

                    @Override
                    public Class<PostReJoinDeletePacket> getType() {
                        return PostReJoinDeletePacket.class;
                    }

                    @Override
                    public String getName() {
                        return DefaultChannels.POST_REJOIN_DELETED.getName();
                    }
                }
        );

        if (!deleteRejoin) {
            BedWarsProxy.getPlugin().getLogger().severe("Could not register RejoinDelete channel!");
        }

        boolean teleportResponse = MessagingCommonManager.getInstance().getMessagingHandler().registerIncomingPacketChannel(
                new MessagingChannel<PostQueryPlayerResponsePacket>() {
                    @Override
                    public void read(PostQueryPlayerResponsePacket message) {
                        if (!isTargetedHere(message, false)) {
                            return;
                        }
                        var slave = ArenaService.getInstance().getSlaveByName(message.getSender());
                        if (null == slave) {
                            return;
                        }
                        if (message.getGameId() == null){
                            return;
                        }
                        var arena = ArenaService.getInstance().getArena(slave, message.getGameId());
                        if (null == arena) {
                            return;
                        }
                        Player player = Bukkit.getPlayer(message.getPlayerRequester());
                        if (null == player || !player.isOnline()) {
                            return;
                        }
                        arena.joinSpectator(player, message.getPlayerQueried());
                    }

                    @Override
                    public Class<PostQueryPlayerResponsePacket> getType() {
                        return PostQueryPlayerResponsePacket.class;
                    }

                    @Override
                    public String getName() {
                        return DefaultChannels.POST_TELEPORT_RESPONSE.getName();
                    }
                }
        );

        if (!teleportResponse) {
            BedWarsProxy.getPlugin().getLogger().severe("Could not register TeleportResponse channel!");
        }

        boolean playAgainRequest = MessagingCommonManager.getInstance().getMessagingHandler().registerIncomingPacketChannel(
                new MessagingChannel<PlayAgainRequestPacket>() {
                    @Override
                    public void read(PlayAgainRequestPacket message) {
                        // todo
//                        var arena = (RemoteArena) CommonManager.getSingleton().getCommonProvider().requestGame(
//                                message.getGameGroup(), message.getVipJoins(), message.getPartySize(),
//                                message.isPrivateGame()
//                        );
//                        MessagingCommonManager.getSingleton().getMessagingHandler().sendPacket(DefaultChannels.PLAY_AGAIN_RESPONSE.getName(),
//                                new PlayAgainResponsePacket(
//                                        ConnectorMessaging.getSingleton().getIdentity(), message.getSender(),
//                                        null == arena ? null : arena.getServer().getName(), null == arena ? null : arena.getGameId(),
//                                        message.getPlayer(), arena != null && arena.isPrivateGame()
//                                ), false
//                        );
                    }

                    @Override
                    public Class<PlayAgainRequestPacket> getType() {
                        return PlayAgainRequestPacket.class;
                    }

                    @Override
                    public String getName() {
                        return DefaultChannels.PLAY_AGAIN_REQUEST.getName();
                    }
                }
        );

        if (!playAgainRequest) {
            BedWarsProxy.getPlugin().getLogger().severe("Could not register PlayAgain channel!");
        }
    }

    public void postTakeOver(@NotNull ProxiedGame arena) {
        MessagingCommonManager.getInstance().getMessagingHandler().sendPacket(
                DefaultChannels.PRIVATE_TAKE_OVER.getName(),
                new PostGameTakeOverPacket(
                        getIdentity(),
                        arena.getServer().getName(),
                        arena.getGameId(),
                        arena.getPlayerHost()),
                false
        );
    }

    private void queryOnlineArenas() {
        MessagingCommonManager.getInstance().getMessagingHandler().sendPacket(DefaultChannels.ARENA_QUERY.getName(),
                new PostQuerySlaveGamesPacket(getIdentity(), null, DefaultChannels.ARENA_FULL_DATA.getName()),
                false
        );
    }

    private boolean isTargetedHere(MessagingPacket packet, boolean allowBroadcast) {
        if (packet instanceof TargetedPacket) {
            return (((TargetedPacket) packet).getTarget() == null && allowBroadcast) || ((TargetedPacket) packet).getTarget().equals(getIdentity());
        }
        return allowBroadcast;
    }
}
