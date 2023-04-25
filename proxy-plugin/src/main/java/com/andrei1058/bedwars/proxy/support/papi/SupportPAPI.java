package com.andrei1058.bedwars.proxy.support.papi;

import com.andrei1058.bedwars.proxy.BedWarsProxy;
import com.andrei1058.bedwars.proxy.arenamanager.ArenaManager;
import com.andrei1058.bedwars.proxy.api.CachedArena;
import com.andrei1058.bedwars.proxy.api.Messages;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;

import static com.andrei1058.bedwars.proxy.language.Language.getMsg;

public class SupportPAPI extends PlaceholderExpansion {

    @Override
    public boolean persist() {
        return true;
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "bw1058";
    }

    @NotNull
    @Override
    public String getAuthor() {
        return "andrei1058";
    }

    @NotNull
    @Override
    public String getVersion() {
        return BedWarsProxy.getPlugin().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player p, String s) {

        if (s.contains("group_count_")){
            String group = s.replace("group_count_", "");
            int amount = ArenaManager.getArenas().stream().filter(a -> a.getArenaGroup().equalsIgnoreCase(group)).mapToInt(CachedArena::getCurrentPlayers).sum();
            return String.valueOf(amount);
        }

        String replay = "";
        switch (s){
            case "current_online":
                replay = String.valueOf(ArenaManager.getArenas().stream().mapToInt(CachedArena::getCurrentPlayers).sum());
                break;
            case "current_arenas":
                replay = String.valueOf(ArenaManager.getArenas().size());
                break;
        }
        if (!replay.isEmpty()) return replay;
        if (p == null) return null;
        if (p.getUniqueId() == null) return null;
        switch (s) {
            case "stats_firstplay":
                replay = new SimpleDateFormat(getMsg(p, Messages.FORMATTING_STATS_DATE_FORMAT)).format(BedWarsProxy.getStatsCache().getPlayerFirstPlay(p.getUniqueId()));
                break;
            case "stats_lastplay":
                replay = new SimpleDateFormat(getMsg(p, Messages.FORMATTING_STATS_DATE_FORMAT)).format(BedWarsProxy.getStatsCache().getPlayerLastPlay(p.getUniqueId()));
                break;
            case "stats_kills":
                replay = String.valueOf(BedWarsProxy.getStatsCache().getPlayerKills(p.getUniqueId()));
                break;
            case "stats_total_kills":
                replay = String.valueOf(BedWarsProxy.getStatsCache().getPlayerKills(p.getUniqueId()) + BedWarsProxy.getStatsCache().getPlayerFinalKills(p.getUniqueId()));
                break;
            case "stats_wins":
                replay = String.valueOf(BedWarsProxy.getStatsCache().getPlayerWins(p.getUniqueId()));
                break;
            case "stats_finalkills":
                replay = String.valueOf(BedWarsProxy.getStatsCache().getPlayerFinalKills(p.getUniqueId()));
                break;
            case "stats_deaths":
                replay = String.valueOf(BedWarsProxy.getStatsCache().getPlayerDeaths(p.getUniqueId()));
                break;
            case "stats_losses":
                replay = String.valueOf(BedWarsProxy.getStatsCache().getPlayerLoses(p.getUniqueId()));
                break;
            case "stats_finaldeaths":
                replay = String.valueOf(BedWarsProxy.getStatsCache().getPlayerFinalDeaths(p.getUniqueId()));
                break;
            case "stats_bedsdestroyed":
                replay = String.valueOf(BedWarsProxy.getStatsCache().getPlayerBedsDestroyed(p.getUniqueId()));
                break;
            case "stats_gamesplayed":
                replay = String.valueOf(BedWarsProxy.getStatsCache().getPlayerGamesPlayed(p.getUniqueId()));
                break;
            case "player_level":
                replay = BedWarsProxy.getLevelManager().getLevel(p).isEmpty() ? BedWarsProxy.config.getString("levels-settings.default-name").replace("{number}", String.valueOf(1)) : BedWarsProxy.getLevelManager().getLevel(p);
                break;
            case "player_level_strip":
                replay = BedWarsProxy.getLevelManager().getLevel(p).isEmpty() ? BedWarsProxy.config.getString("levels-settings.default-name").replace("{number}", String.valueOf(1)).replaceAll("\\[", "").replaceAll("]","") : BedWarsProxy.getLevelManager().getLevel(p).replaceAll("\\[", "").replaceAll("]","");
                break;
            case "player_level_raw":
                replay = String.valueOf(BedWarsProxy.getLevelManager().getPlayerLevel(p)).isEmpty() ? String.valueOf(1) : String.valueOf(BedWarsProxy.getLevelManager().getPlayerLevel(p));
                break;
            case "player_progress":
                replay = BedWarsProxy.getLevelManager().getProgressBar(p);
                break;
            case "player_xp_formatted":
                replay = BedWarsProxy.getLevelManager().getCurrentXpFormatted(p);
                break;
            case "player_xp":
                replay = String.valueOf(BedWarsProxy.getLevelManager().getCurrentXp(p));
                break;
            case "player_rerq_xp_formatted":
            case "player_req_xp_formatted":
                replay = BedWarsProxy.getLevelManager().getRequiredXpFormatted(p);
                break;
            case "player_rerq_xp":
            case "player_req_xp":
                replay = String.valueOf(BedWarsProxy.getLevelManager().getRequiredXp(p));
                break;
        }
        return ChatColor.translateAlternateColorCodes('&',replay);
    }
}
