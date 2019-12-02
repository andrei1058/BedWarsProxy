package com.andrei1058.bedwars.proxy.command;

import com.andrei1058.bedwars.proxy.configuration.SoundsConfig;
import com.andrei1058.bedwars.proxy.language.Language;
import com.andrei1058.bedwars.proxy.language.Messages;
import com.andrei1058.bedwars.proxy.rejoin.RemoteReJoin;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class RejoinCommand extends BukkitCommand {

    public RejoinCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender s, String st, String[] args) {
        if (s instanceof ConsoleCommandSender) {
            s.sendMessage("This command is for players!");
            return true;
        }

        Player p = (Player) s;

        if (!p.hasPermission("bw.rejoin")) {
            p.sendMessage(Language.getMsg(p, Messages.COMMAND_NOT_FOUND_OR_INSUFF_PERMS));
            return true;
        }

        RemoteReJoin rj = RemoteReJoin.getReJoin(p.getUniqueId());

        if (rj == null) {
            p.sendMessage(Language.getMsg(p, Messages.REJOIN_NO_ARENA));
            SoundsConfig.playSound("rejoin-denied", p);
            return true;
        }

        if (!rj.getArena().reJoin(rj)) {
            p.sendMessage(Language.getMsg(p, Messages.REJOIN_DENIED));
            SoundsConfig.playSound("rejoin-denied", p);
            return true;
        }

        p.sendMessage(Language.getMsg(p, Messages.REJOIN_ALLOWED).replace("{arena}", rj.getArena().getDisplayName(Language.getPlayerLanguage(p))));
        SoundsConfig.playSound("rejoin-allowed", p);
        return true;
    }
}
