package com.andrei1058.bedwars.proxy.command.party;

import com.andrei1058.bedwars.proxy.command.SubCommand;
import com.andrei1058.bedwars.proxy.language.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.andrei1058.bedwars.proxy.BedWarsProxy.getParty;
import static com.andrei1058.bedwars.proxy.language.Language.getMsg;

public class RemoveCMD extends SubCommand {

    public RemoveCMD(String name, String permission) {
        super(name, permission);
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        if (!(s instanceof Player)) return;
        Player p = (Player) s;

        if (args.length == 1) {
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_REMOVE_USAGE));
            return;
        }
        if (getParty().hasParty(p) && !getParty().isOwner(p)) {
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_INSUFFICIENT_PERMISSIONS));
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_REMOVE_DENIED_TARGET_NOT_PARTY_MEMBER).replace("{player}", args[1]));
            return;
        }
        if (!getParty().isMember(p, target)) {
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_REMOVE_DENIED_TARGET_NOT_PARTY_MEMBER).replace("{player}", args[1]));
            return;
        }
        getParty().removePlayer(p, target);
    }
}
