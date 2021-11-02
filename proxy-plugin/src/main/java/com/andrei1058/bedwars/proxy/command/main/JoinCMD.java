package com.andrei1058.bedwars.proxy.command.main;

import com.andrei1058.bedwars.proxy.arenamanager.ArenaManager;
import com.andrei1058.bedwars.proxy.api.ArenaStatus;
import com.andrei1058.bedwars.proxy.api.CachedArena;
import com.andrei1058.bedwars.proxy.command.SubCommand;
import com.andrei1058.bedwars.proxy.api.Messages;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.andrei1058.bedwars.proxy.BedWarsProxy.getParty;
import static com.andrei1058.bedwars.proxy.language.Language.getMsg;

public class JoinCMD extends SubCommand {

    public JoinCMD(String name, String permission) {
        super(name, permission);
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        if (s instanceof ConsoleCommandSender) return;
        Player p = (Player) s;
        if (args.length < 1) {
            s.sendMessage(getMsg(p, Messages.COMMAND_JOIN_USAGE));
            return;
        }
        if (!p.hasPermission("bw.join." + args[0].toLowerCase())) {
            p.sendMessage(getMsg(p, Messages.COMMAND_NOT_FOUND_OR_INSUFF_PERMS));
            return;
        }
        if (args[0].equalsIgnoreCase("random")) {
            if (!ArenaManager.getInstance().joinRandomArena(p)) {
                s.sendMessage(getMsg(p, Messages.COMMAND_JOIN_NO_EMPTY_FOUND));
            }
            return;
        }
        if (ArenaManager.hasGroup(args[0])) {
            ArenaManager.getInstance().joinRandomFromGroup(p, args[0]);
            return;
        } else  {
            if (getParty().hasParty(p.getUniqueId()) && !getParty().isOwner(p.getUniqueId())) {
                p.sendMessage(getMsg(p, Messages.COMMAND_JOIN_DENIED_NOT_PARTY_LEADER));
                return;
            }
            ArrayList<CachedArena> arenas = new ArrayList<>();
            ArenaManager.getArenas().forEach(a -> {
                if (a.getArenaName().contains(args[0])) arenas.add(a);
            });
            arenas.removeIf(a -> !(a.getStatus() == ArenaStatus.WAITING || a.getStatus() == ArenaStatus.STARTING));
            arenas.sort(ArenaManager.getComparator());
            if (!arenas.isEmpty()){
                arenas.get(0).addPlayer(p, null);
                return;
            }
        }
        s.sendMessage(getMsg(p, Messages.COMMAND_JOIN_GROUP_OR_ARENA_NOT_FOUND).replace("{name}", args[0]));
    }

    @Override
    public List<String> tabComplete(CommandSender s, String alias, String[] args, Location location) {
        List<String> tab = new ArrayList<>();
        for (CachedArena ad : ArenaManager.getArenas()) {
            if (!tab.contains(ad.getArenaGroup())) tab.add(ad.getArenaGroup());
        }
        for (CachedArena arena : ArenaManager.getArenas()) {
            tab.add(arena.getArenaName());
        }
        return tab;
    }
}
