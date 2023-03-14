package com.andrei1058.bedwars.proxy.command.party;

import com.andrei1058.bedwars.proxy.api.Messages;
import com.andrei1058.bedwars.proxy.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.andrei1058.bedwars.proxy.BedWarsProxy.getParty;
import static com.andrei1058.bedwars.proxy.language.Language.getMsg;

public class InfoCMD extends SubCommand {

    public InfoCMD(String name, String permission) {
        super(name, permission);
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        if (!(s instanceof Player)) return;
        Player p = (Player) s;
        if (!getParty().hasParty(p.getUniqueId())) {  //No Party
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_GENERAL_DENIED_NOT_IN_PARTY));
        } else {
            Player owner = Bukkit.getPlayer(getParty().getOwner(p.getUniqueId()));
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_INFO_OWNER).replace("{owner}", owner.getName()));
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_INFO_MEMBERS));
            for (UUID p1 : getParty().getMembers(owner.getUniqueId())) {
                p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_INFO_PLAYER).replace("{player}", Bukkit.getPlayer(p1).getName()));
            }
        }
    }
}
