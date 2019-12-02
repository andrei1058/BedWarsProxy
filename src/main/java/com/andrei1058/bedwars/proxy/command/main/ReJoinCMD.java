package com.andrei1058.bedwars.proxy.command.main;

import com.andrei1058.bedwars.proxy.command.SubCommand;
import com.andrei1058.bedwars.proxy.configuration.SoundsConfig;
import com.andrei1058.bedwars.proxy.language.Language;
import com.andrei1058.bedwars.proxy.language.Messages;
import com.andrei1058.bedwars.proxy.rejoin.RemoteReJoin;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ReJoinCMD extends SubCommand {

    public ReJoinCMD(String name, String permission) {
        super(name, permission);
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        if (s instanceof ConsoleCommandSender) {
            s.sendMessage("This command is for players!");
            return;
        }

        Player p = (Player) s;

        if (!p.hasPermission("bw.rejoin")) {
            p.sendMessage(Language.getMsg(p, Messages.COMMAND_NOT_FOUND_OR_INSUFF_PERMS));
            return;
        }

        RemoteReJoin rj = RemoteReJoin.getReJoin(p.getUniqueId());

        if (rj == null) {
            p.sendMessage(Language.getMsg(p, Messages.REJOIN_NO_ARENA));
            SoundsConfig.playSound("rejoin-denied", p);
            return;
        }

        if (!rj.getArena().reJoin(rj)) {
            p.sendMessage(Language.getMsg(p, Messages.REJOIN_DENIED));
            SoundsConfig.playSound("rejoin-denied", p);
            return;
        }

        p.sendMessage(Language.getMsg(p, Messages.REJOIN_ALLOWED).replace("{arena}", rj.getArena().getDisplayName(Language.getPlayerLanguage(p))));
        SoundsConfig.playSound("rejoin-allowed", p);
        return;
    }
}
