package com.andrei1058.bedwars.proxy.command.party;

import com.andrei1058.bedwars.proxy.api.Messages;
import com.andrei1058.bedwars.proxy.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

import static com.andrei1058.bedwars.proxy.BedWarsProxy.getParty;
import static com.andrei1058.bedwars.proxy.language.Language.getMsg;

public class PromoteCMD extends SubCommand {

    public PromoteCMD(String name, String permission) {
        super(name, permission);
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        if (!(s instanceof Player)) return;
        Player p = (Player) s;
        if (!getParty().hasParty(p.getUniqueId())) {  //No Party
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_GENERAL_DENIED_NOT_IN_PARTY));
        } else if (!getParty().isOwner(p.getUniqueId())) {  //Not Owner
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_INSUFFICIENT_PERMISSIONS));
        } else if (args.length == 0) { //No Player Specified
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_PROMOTE_USAGE));
        } else if (Objects.equals(args[1], s.getName())){  //Promoting self
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_PROMOTE_ALREADY_LEADER));
        } else if (!getParty().isMember(p.getUniqueId(), Bukkit.getPlayer(args[1]).getUniqueId())) {  //Player not in party
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_GENERAL_DENIED_NOT_IN_PARTY));
        } else {
            Player target = Bukkit.getPlayer(args[1]);
            getParty().promote(p.getUniqueId(), target.getUniqueId());
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_PROMOTE_SUCCESS).replace("{player}", args[1]));
            target.sendMessage(getMsg(target, Messages.COMMAND_PARTY_PROMOTE_SUCCESS_TARGET).replace("{player}", p.getName()));
            for (UUID pl: getParty().getMembers(p.getUniqueId()))
                if (pl != target.getUniqueId() && pl != p.getUniqueId())
                    Bukkit.getPlayer(pl).sendMessage(getMsg(Bukkit.getPlayer(pl), Messages.COMMAND_PARTY_PROMOTE_SUCCESS_MEMBERS).replace("{player}", target.getName()));
        }
    }
}
