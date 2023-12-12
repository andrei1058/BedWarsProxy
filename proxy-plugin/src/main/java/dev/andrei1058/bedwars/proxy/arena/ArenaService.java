package dev.andrei1058.bedwars.proxy.arena;

import com.andrei1058.bedwars.proxy.BedWarsProxy;
import dev.andrei1058.bedwars.common.api.arena.DisplayableArena;
import dev.andrei1058.bedwars.common.api.arena.GameStage;
import dev.andrei1058.bedwars.common.api.messaging.ISlaveServer;
import dev.andrei1058.bedwars.common.api.messaging.TargetedPacket;
import dev.andrei1058.bedwars.common.api.messaging.packet.*;
import dev.andrei1058.bedwars.proxy.api.arena.ProxiedGame;
import dev.andrei1058.bedwars.proxy.messaging.ConnectorMessaging;
import dev.andrei1058.bedwars.proxy.selector.GameSelectorListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ArenaService {

    @Getter
    private static ArenaService instance;

    private static final List<DisplayableArena> displayableArenas = new ArrayList<>();
    private final ConcurrentHashMap<String, ISlaveServer> slavesByName = new ConcurrentHashMap<>();

    private static long lastPlayerCountRequest = 0L;
    private static int lastPlayerCount = 0;
    private static long lastSpectatorCountRequest = 0L;
    private static int lastSpectatorCount = 0;
    private static long lastOnlineCountRequest = 0L;
    private static int lastOnlineCount = 0;

    private final HashMap<String, Integer> counterByGroup = new HashMap<>();
    private final HashMap<String, Long> counterRequestByGroup = new HashMap<>();

    private ArenaService() {
    }

    public List<DisplayableArena> getArenas() {
        return displayableArenas;
    }


    public static void init() {
        if (instance == null) {
            instance = new ArenaService();
            Bukkit.getPluginManager().registerEvents(new GameSelectorListener(), BedWarsProxy.getPlugin());

            // register admin games list command
            // todo
            //GamesCommand.register(CommonManager.getSingleton().getCommonProvider().getMainCommand());
        }
    }


    @Nullable
    public DisplayableArena getFromTag(String tag) {
        // todo
//        return getArenas().stream().filter(a -> a.getTag().equals(tag)).findFirst().orElse(null);
        return null;
    }

    public void remove(DisplayableArena arena) {
        if (displayableArenas.remove(arena)) {
            // todo
//            GameConnector.debug("Removing DisplayableArena: " + arena.getTag());
//            Bukkit.getPluginManager().callEvent(new GameDropEvent(arena));
//            SelectorManager.getINSTANCE().refreshArenaSelector();
        }
    }

    @SuppressWarnings("unused")
    public void add(DisplayableArena arena) {
        if (!displayableArenas.contains(arena)) {
            displayableArenas.add(arena);
            // todo
//            if (arena.getTag().equalsIgnoreCase("notSet")){
//                GameConnector.getInstance().getLogger().severe("Registering new arena with missing bungee name slave.");
//            }
//            GameConnector.debug("Added new DisplayableArena: " + arena.getTag());
//            Bukkit.getPluginManager().callEvent(new GameRegisterEvent(arena));
//            SelectorManager.getINSTANCE().refreshArenaSelector();
        }
    }

    public @Nullable ProxiedGame getArena(ISlaveServer server, UUID gameId) {
        return (ProxiedGame) getArenas().stream().filter(arena -> arena instanceof ProxiedGame)
                .filter(arena -> ((ProxiedGame) arena).getServer().equals(server))
                .filter(arena -> arena.getGameId().equals(gameId)).findFirst().orElse(null);
    }

    public int getPlayerCount() {
        if (System.currentTimeMillis() < lastPlayerCountRequest) {
            return lastPlayerCount;
        }
        lastPlayerCount = 0;
        getArenas().forEach(arena -> lastPlayerCount += arena.getCurrentPlayers());
        // 50 should be a server tick
        lastPlayerCountRequest = System.currentTimeMillis() + 50;
        return lastPlayerCount;
    }

    public int getSpectatorCount() {
        if (System.currentTimeMillis() < lastSpectatorCountRequest) {
            return lastSpectatorCount;
        }
        lastSpectatorCount = 0;
        getArenas().forEach(arena -> lastSpectatorCount += arena.getCurrentSpectators());
        // 50 should be a server tick
        lastSpectatorCountRequest = System.currentTimeMillis() + 50;
        return lastSpectatorCount;
    }

    public int getOnlineCount() {
        if (System.currentTimeMillis() < lastOnlineCountRequest) {
            return lastOnlineCount;
        }
        lastOnlineCount = 0;
        getArenas().forEach(arena -> lastOnlineCount += arena.getCurrentSpectators() + arena.getCurrentPlayers());
        // 50 should be a server tick
        lastOnlineCountRequest = System.currentTimeMillis() + 50;
        return lastOnlineCount;
    }

    public @Nullable ISlaveServer getSlaveByName(String name) {
        return this.slavesByName.getOrDefault(name, null);
    }

    public Collection<ISlaveServer> getSlaves() {
        return slavesByName.values();
    }

    public void removeSlave(@NotNull ISlaveServer marked) {
        //remove arenas
        displayableArenas.stream().filter(arena -> arena instanceof ProxiedGame)
                .filter(arena -> ((ProxiedGame)arena).getServer().getName().equals(marked.getName()))
                .collect(Collectors.toList()).forEach(this::remove);
        //remove slave
        slavesByName.remove(marked.getName());
        // todo
//        Bukkit.getScheduler().runTask(BedWarsProxy.getPlugin(), () -> new SlaveDisconnectedEvent(marked));
    }

    public void addSlave(@NotNull ISlaveServer slave) {
        if (null != getSlaveByName(slave.getName())) {
            throw new RuntimeException("Slave already registered!");
        }
        slavesByName.put(slave.getName(), slave);
        // todo
//        Bukkit.getScheduler().runTask(BedWarsProxy.getPlugin(), () -> new SlaveConnectedEvent(slave));
    }

    public int getGroupCounter(@NotNull String group) {
        if (System.currentTimeMillis() < counterRequestByGroup.getOrDefault(group.toLowerCase(), 0L)) {
            return counterByGroup.getOrDefault(group.toLowerCase(), 0);
        }
        AtomicInteger counter = new AtomicInteger();
        getArenas().forEach(arena -> {
            if (arena.getGroup().equalsIgnoreCase(group)) {
                counter.addAndGet(arena.getCurrentPlayers()+arena.getCurrentSpectators());
            }
        });
        // 50 should be a server tick
        counterRequestByGroup.put(group.toLowerCase(), System.currentTimeMillis() + 50);
        counterByGroup.put(group.toLowerCase(), counter.intValue());
        return counter.intValue();
    }

    public void processPacket(@NotNull PostGameDataPacket packet) {
        ISlaveServer slave = packet instanceof TargetedPacket ? getSlaveByName(packet.getSender()) : null;
        final String name = packet.getSender();
        if (null == slave) {
            slave = new ISlaveServer() {
                long lastPacket;

                @Override
                public String getName() {
                    return name;
                }

                @Override
                public void pong() {
                    lastPacket = System.currentTimeMillis();
                }

                @Override
                public long getLastPacket() {
                    return lastPacket;
                }

                @Override
                public boolean isTimedOut() {
                    return getLastPacket() <
                            System.currentTimeMillis() - ConnectorMessaging.getSingleton().getKeepAliveTolerance();
                }
            };
            slave.pong();

            // this means the arena is new as well
            var arena = ArenaBuilder.buildFromPacket(slave, packet);
            this.addSlave(slave);
            this.add(arena);
            // todo
//            SelectorManager.getINSTANCE().refreshArenaSelector();
            return;
        }
        var arena = getArena(slave, packet.getGameId());
        if (null == arena) {
            slave.pong();
            this.add(ArenaBuilder.buildFromPacket(slave, packet));
            // todo
//            SelectorManager.getINSTANCE().refreshArenaSelector();
            return;
        }
        // todo
//        ArenaUpdater.updateFromPacket(arena, packet);
//        SelectorManager.getINSTANCE().refreshArenaSelector();
    }

    public void processPacket(@NotNull PostGameStatePacket packet) {
        ISlaveServer slave = getSlaveByName(packet.getSender());
        if (null == slave) {
            return;
        }
        slave.pong();

        var arena = getArena(slave, packet.getGameId());
        if (null == arena) {
            return;
        }
        if (arena.getGameState() != GameStage.IN_GAME) {
            // todo
//            GameReJoinManager.getInstance().clearAllForSlaveGame(slave.getName(), packet.getGameId());
        }
        // todo
//        ArenaUpdater.updateFromPacket(arena, packet);
//        SelectorManager.getINSTANCE().refreshArenaSelector();
    }

    public void processPacket(@NotNull PostGamePlayerCountPacket packet) {
        ISlaveServer slave = getSlaveByName(packet.getSender());
        if (null == slave) {
            return;
        }
        slave.pong();

        var arena = getArena(slave, packet.getGameId());
        if (null == arena) {
            return;
        }
        // todo
//        ArenaUpdater.updateFromPacket(arena, packet);
//        SelectorManager.getINSTANCE().refreshArenaSelector();
    }

    public void processPacket(@NotNull PostSlavePongPacket packet) {
        ISlaveServer slave = getSlaveByName(packet.getSender());
        if (null == slave) {
            return;
        }
        Bukkit.broadcastMessage("RECEIVED HEARTBEAT FOR: "+slave.getName());
        slave.pong();
    }

    public void processPacket(@NotNull PostGameDropPacket packet) {
        // todo
//        GameReJoinManager.getInstance().clearAllForSlaveGame(packet.getSender(), packet.getGameId());
        ISlaveServer slave = getSlaveByName(packet.getSender());
        if (null == slave) {
            return;
        }
        var arena = getArena(slave, packet.getGameId());
        if (null == arena) {
            return;
        }
        ArenaService.getInstance().remove(arena);
    }
}
