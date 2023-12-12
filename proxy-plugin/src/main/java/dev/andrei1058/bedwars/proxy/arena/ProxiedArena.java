package dev.andrei1058.bedwars.proxy.arena;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.andrei1058.bedwars.common.api.arena.GameStage;
import dev.andrei1058.bedwars.common.api.locale.LocaleAdapter;
import dev.andrei1058.bedwars.common.api.messaging.ISlaveServer;
import dev.andrei1058.bedwars.proxy.api.arena.ProxiedGame;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class ProxiedArena implements ProxiedGame {
    private final ISlaveServer server;
    private final String template;
    @Getter
    private String displayName;
    @Getter
    private final UUID gameId;
    private GameStage gameState;
    private String spectatePerm;
    private int maxPlayers;
    private int minPlayers;
    private int currentPlayers;
    private int currentSpectators;
    private int vips;
    private ItemStack displayItem;
    @Setter
    private Instant startTime;
    @Setter @Getter
    private boolean privateGame;
    @Getter @Setter
    private UUID playerHost;
    @Getter
    private String group;

    public ProxiedArena(ISlaveServer server, UUID gameId, String template, String displayName, GameStage gameState,
                       String spectatePerm, int maxPlayers, int minPlayers, int players, int spectators, int vips,
                       @Nullable ItemStack displayItem, @NotNull String group) {
        this.server = server;
        this.template = template;
        this.displayName = displayName;
        this.gameId = gameId;
        this.gameState = gameState;
        this.spectatePerm = spectatePerm;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.currentSpectators = spectators;
        this.currentPlayers = players;
        this.vips = vips;
        this.group = group.trim().toLowerCase();
        setDisplayItem(displayItem);
    }


    @Override
    public ISlaveServer getServer() {
        return server;
    }

    @Override
    public void setDisplayName(String name) {
        if (!this.displayName.equals(name)) {
            this.displayName = name;
            //todo call event
        }
    }

    @Override
    public void setGameState(GameStage gameState) {
        if (getGameState() != gameState) {
            GameStage copy = getGameState();
            this.gameState = gameState;
            // todo
//            Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(this, copy, gameState));
        }
    }

    @Override
    public void setSpectatePermission(String perm) {
        if (!getSpectatePermission().equals(perm)) {
            this.spectatePerm = perm;
            //todo call update event
        }
    }

    @Override
    public void setMinPlayers(int minPlayers) {
        if (this.minPlayers != minPlayers) {
            this.minPlayers = minPlayers;
            //todo call update event
        }
    }

    @Override
    public void setMaxPlayers(int maxPlayers) {
        if (this.maxPlayers != maxPlayers) {
            this.maxPlayers = maxPlayers;
            //todo call update event
        }
    }

    @Override
    public void setCurrentPlayers(int players) {
        this.currentPlayers = players;
    }

    @Override
    public void setCurrentSpectators(int spectators) {
        this.currentSpectators = spectators;
    }

    @Override
    public GameStage getGameState() {
        return gameState;
    }

    @Override
    public boolean isFull() {
        return currentPlayers == maxPlayers;
    }

    @Override
    public String getSpectatePermission() {
        return spectatePerm;
    }

    @Override
    public String getDisplayName(@Nullable LocaleAdapter localeAdapter) {
        // todo localize
        return displayName;
    }

    @Override
    public int getMaxPlayers() {
        return maxPlayers;
    }

    @Override
    public int getMinPlayers() {
        return minPlayers;
    }

    @Override
    public int getCurrentPlayers() {
        return currentPlayers;
    }

    @Override
    public int getCurrentSpectators() {
        return currentSpectators;
    }

    @Override
    public String getTemplateWorld() {
        return template;
    }

    @Override
    public boolean joinPlayer(Player player, boolean ignorePartyAndHost) {
        if (getServer() == null) return false;
        if (!ignorePartyAndHost){
            if (this.getPlayerHost() != null) return false;
        }
        // todo
//        if (CommonManager.getSingleton().getCommonProvider().isInGame(player)) return false;
//        if (PrivateGamesCommonManager.getInstance().isInPrivateSession(player) && !isPrivateGame()) {
//            player.sendMessage(LanguageManager.getINSTANCE().getMsg(player, CommonMessage.ARENA_JOIN_DENIED_NO_PRIVATE));
//            return false;
//        }
        if (!(getGameState() == GameStage.WAITING || getGameState() == GameStage.STARTING)) {
            return false;
        }


        // TODO
//        UUID partyOwner = PartyManager.getINSTANCE().getPartyAdapter().getOwner(player.getUniqueId());
//        Player partyOwnerPlayer = null;

//        if (!ignorePartyAndHost && partyOwner != null) {
//            partyOwnerPlayer = Bukkit.getPlayer(partyOwner);
//            if (!partyOwner.equals(player.getUniqueId())) {
//                // owners only can choose a game
//                player.sendMessage(LanguageManager.getINSTANCE().getMsg(player, CommonMessage.ARENA_JOIN_DENIED_NO_PARTY_LEADER));
//                return false;
//            }
//        }

        int requiredSlots = 1;
//        int partyVips = CommonManager.getSingleton().hasVipJoin(player) ? 1 : 0;
//        requiredSlots -= partyVips;
//
//        // Handle party adapter and add members to this game if possible
//        if (!ignorePartyAndHost) {
//            if (PartyManager.getINSTANCE().getPartyAdapter().hasParty(player.getUniqueId()) && partyOwner != null) {
//                if (partyOwner.equals(player.getUniqueId())) {
//                    for (UUID member : PartyManager.getINSTANCE().getPartyAdapter().getMembers(partyOwner)) {
//                        if (member.equals(player.getUniqueId())) continue;
//                        Player playerMember = Bukkit.getPlayer(member);
//                        if (playerMember != null && playerMember.isOnline() && !CommonManager.getSingleton().getCommonProvider().isInGame(playerMember)) {
//                            requiredSlots++;
//                            if (CommonManager.getSingleton().hasVipJoin(player)) {
//                                partyVips++;
//                            }
//                        }
//                    }
//                }
//            }
//        }

        int actualFreeSlots = maxPlayers - (currentPlayers - vips);

        if (actualFreeSlots < requiredSlots) {
            // if game is full but private game we add them as spectators
            if (isPrivateGame() && !ignorePartyAndHost){
                joinSpectator(player, null);
            }
            return false;
        }

//        if (!ignorePartyAndHost && partyOwner != null) {
//            if (isPrivateGame()) {
//                if (PrivateGamesCommonManager.getInstance().canJoinPrivateGame(player)) {
//                    this.setHost(partyOwner);
//                    ConnectorMessaging.getSingleton().postTakeOver(this);
//                } else {
//                    player.sendMessage(LanguageManager.getINSTANCE().getMsg(player, CommonMessage.ARENA_JOIN_DENIED_NO_PRIVATE));
//                    return false;
//                }
//            }
//        }

        if (getServer().isTimedOut()) {
            player.sendMessage("THIS IS TIMED OUT!: " + getServer().getLastPacket());
            player.sendMessage("THIS IS TIMED OUT!: " + new Date(getServer().getLastPacket()));
            return false;
        }

//        // join allowed
//        PlayerGameJoinEvent playerGameJoinEvent = new PlayerGameJoinEvent(this, player, false);
//        Bukkit.getPluginManager().callEvent(playerGameJoinEvent);
//        if (playerGameJoinEvent.isCancelled()) return false;
//        currentPlayers++;


        //todo here increment player count so we prevent sending two players or more
        //todo here tell other lobbies to fuck off, we got the lock on this
        player.closeInventory();


//        MessagingCommonManager.getSingleton().getMessagingHandler().sendPacket(
//                DefaultChannels.PLAYER_JOIN.getName(),
//                new PostGameJoinPacket(ConnectorMessaging.getSingleton().getIdentity(),
//                        this.getServer().getName(), player.getUniqueId(), this.getGameId(), false,
//                        LanguageManager.getINSTANCE().getLocale(player).getIsoCode(), null
//                ), false
//        );
//
//        GameConnector.debug("Sending " + player.getName() + " to game: " + getTag());

        //noinspection UnstableApiUsage
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(getServer().getName());
//        player.sendPluginMessage(GameConnector.getInstance(), "BungeeCord", out.toByteArray());


//        if (!ignorePartyAndHost && partyOwner != null) {
//            for (UUID member : PartyManager.getINSTANCE().getPartyAdapter().getMembers(partyOwner)) {
//                if (member.equals(player.getUniqueId())) continue;
//
//                // Add party members to current game if they are in lobby
//                Player playerMember = Bukkit.getPlayer(member);
//                if (playerMember != null && playerMember.isOnline() && !CommonManager.getSingleton().getCommonProvider().isInGame(playerMember)) {
//                    joinPlayer(playerMember, true);
//                    playerMember.sendMessage(LanguageManager.getINSTANCE().getMsg(playerMember, CommonMessage.ARENA_JOIN_VIA_PARTY).replace("{arena}", getDisplayName()));
//                }
//            }
//        }

        return true;
    }

    @Override
    public boolean reJoin(@NotNull Player player) {
//        MessagingCommonManager.getSingleton().getMessagingHandler().sendPacket(
//                DefaultChannels.PLAYER_JOIN.getName(),
//                new PostGameJoinPacket(ConnectorMessaging.getSingleton().getIdentity(),
//                        this.getServer().getName(), player.getUniqueId(), this.getGameId(), false,
//                        LanguageManager.getINSTANCE().getLocale(player).getIsoCode(), null
//                ), false
//        );
//
//        GameConnector.debug("Sending " + player.getName() + " to game: " + getTag());

        //noinspection UnstableApiUsage
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(getServer().getName());
//        player.sendPluginMessage(GameConnector.getInstance(), "BungeeCord", out.toByteArray());
        return true;
    }

    @Override
    public int getCurrentVips() {
        return vips;
    }

    @Override
    public String getTemplate() {
        return template;
    }

    public void setVipsPlaying(int vips) {
        this.vips = vips;
    }

    @Override
    public boolean joinSpectator(Player player, @Nullable String target, boolean bypass) {
        if (!bypass){
            if (!(getSpectatePermission().trim().isEmpty() || player.hasPermission(getSpectatePermission()))) {
                return false;
            }
        }

//        if (CommonManager.getSingleton().getCommonProvider().isInGame(player)) return false;
//        if (getGameState() == GameState.LOADING || getGameState() == GameState.ENDING) return false;

        if (getServer().isTimedOut()) {
            return false;
        }

//        PlayerGameJoinEvent playerGameJoinEvent = new PlayerGameJoinEvent(this, player, true);
//        Bukkit.getPluginManager().callEvent(playerGameJoinEvent);
//        if (playerGameJoinEvent.isCancelled()) return false;


        player.closeInventory();

//        MessagingCommonManager.getSingleton().getMessagingHandler().sendPacket(
//                DefaultChannels.PLAYER_JOIN.getName(),
//                new PostGameJoinPacket(ConnectorMessaging.getSingleton().getIdentity(),
//                        this.getServer().getName(), player.getUniqueId(), this.getGameId(), true,
//                        LanguageManager.getINSTANCE().getLocale(player).getIsoCode(), target
//                ), false
//        );
//        GameConnector.debug("Sending " + player.getName() + " (as spectator) to game: " + getTag());

        //noinspection UnstableApiUsage
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(getServer().getName());
//        player.sendPluginMessage(GameConnector.getInstance(), "BungeeCord", out.toByteArray());
        return true;
    }

    @Override
    public ItemStack getDisplayItem(LocaleAdapter lang) {
        if (displayItem == null) return new ItemStack(Material.BEDROCK);
        if (lang == null) return displayItem;

        ItemStack item = displayItem;
        ItemMeta meta = item.getItemMeta();
        if (null == meta) {
            return item;
        }
        String displayName;
//        if (lang.hasPath(ArenaHolderConfig.getNameForState(getGameState()) + "-" + getTemplateWorld())) {
//            // check custom name for template
//            displayName = strReplaceArenaPlaceholders(lang.getMsg(null, ArenaHolderConfig.getNameForState(getGameState()) + "-" + getTemplateWorld()), lang);
//        } else {
//            displayName = strReplaceArenaPlaceholders(lang.getMsg(null, ArenaHolderConfig.getNameForState(getGameState())), lang);
//        }
//        meta.setDisplayName(displayName);
//        String[] replacements = new String[]{
//                "{name}", getDisplayName(),
//                "{template}", getTemplateWorld(),
//                "{status}", lang.getMsg(null, getGameState().getTranslatePath()),
//                "{on}", String.valueOf(getCurrentPlayers()),
//                "{max}", String.valueOf(getMaxPlayers()),
//                "{allowSpectate}", String.valueOf(getSpectatePermission()),
//                "{spectating}", String.valueOf(getCurrentSpectators()),
//                "{group}", lang.getMsg(null, CommonMessage.GROUP_DISPLAY_NAME.toString().replace("{g}", getGroup().toLowerCase()))
//        };
//        String customPath = ArenaHolderConfig.getLoreForState(getGameState()) + "-" + getTemplateWorld();
//        List<String> lore;
//        if (lang.hasPath(customPath)) {
//            lore = lang.getMsgList(null, customPath, replacements);
//        } else {
//            lore = lang.getMsgList(null, ArenaHolderConfig.getLoreForState(getGameState()), replacements);
//        }
//        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private @NotNull String strReplaceArenaPlaceholders(@NotNull String in, @NotNull LocaleAdapter lang) {
//        return in.replace("{name}", getDisplayName())
//                .replace("{template}", getTemplateWorld())
//                .replace("{group}", lang.getMsg(null, CommonMessage.GROUP_DISPLAY_NAME.toString().replace("{g}", getGroup().toLowerCase()))).replace("{status}", lang.getMsg(null, getGameState().getTranslatePath()))
//                .replace("{on}", String.valueOf(getCurrentPlayers())).replace("{max}", String.valueOf(getMaxPlayers()))
//                .replace("{spectating}", String.valueOf(getCurrentSpectators())).replace("{game_tag}", getTag()).replace("{game_id}", String.valueOf(getGameId()));
    return null;
    }

    public void setDisplayItem(ItemStack displayItem) {
        if (displayItem == null) {
            this.displayItem = null;
            return;
        }
        ItemStack temp = displayItem;
//        String tag = CommonManager.getSingleton().getItemSupport().getTag(
//                displayItem, CommonManager.getSingleton().getCommonProvider().getDisplayableArenaNBTTagKey()
//        );
//        if (null != tag) {
//            temp = (CommonManager.getSingleton().getItemSupport().removeTag(temp, tag));
//        }
//        this.displayItem = CommonManager.getSingleton().getItemSupport().addTag(
//                temp, CommonManager.getSingleton().getCommonProvider().getDisplayableArenaNBTTagKey(), getTag()
//        );
    }

    @Override
    public boolean isLocal() {
        return false;
    }

    @Nullable
    @Override
    public Instant getStartTime() {
        return startTime;
    }

    public void setGroup(@NotNull String group) {
        this.group = group.trim().toLowerCase();
    }
}
