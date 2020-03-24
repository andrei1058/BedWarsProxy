package com.andrei1058.bedwars.proxy.command.party;

import com.andrei1058.bedwars.proxy.command.SubCommand;
import com.andrei1058.bedwars.proxy.api.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.andrei1058.bedwars.proxy.BedWarsProxy.getParty;
import static com.andrei1058.bedwars.proxy.language.Language.getMsg;

public class DisbandCMD extends SubCommand {

    public DisbandCMD(String name, String permission) {
        super(name, permission);
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        if (!(s instanceof Player)) return;
        Player p = (Player) s;

        if (!getParty().hasParty(p.getUniqueId())) {
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_GENERAL_DENIED_NOT_IN_PARTY));
            return;
        }
        if (!getParty().isOwner(p.getUniqueId())) {
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_INSUFFICIENT_PERMISSIONS));
            return;
        }
        getParty().disband(p.getUniqueId());
    }
}
