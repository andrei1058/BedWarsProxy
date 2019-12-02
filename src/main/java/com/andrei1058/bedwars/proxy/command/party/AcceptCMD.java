package com.andrei1058.bedwars.proxy.command.party;

import com.andrei1058.bedwars.proxy.command.SubCommand;
import com.andrei1058.bedwars.proxy.language.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.andrei1058.bedwars.proxy.BedWarsProxy.getParty;
import static com.andrei1058.bedwars.proxy.language.Language.getMsg;

public class AcceptCMD extends SubCommand {

    public AcceptCMD(String name, String permission) {
        super(name, permission);
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        if (!(s instanceof Player)) return;
        Player p = (Player) s;
        if (args.length < 2) {
            return;
        }
        if (getParty().hasParty(p)) {
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_ACCEPT_DENIED_ALREADY_IN_PARTY));
            return;
        }
        if (Bukkit.getPlayer(args[1]) == null || !Bukkit.getPlayer(args[1]).isOnline()) {
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_INVITE_DENIED_PLAYER_OFFLINE).replace("{player}", args[1]));
            return;
        }
        if (!PartyCommand.getPartySessionRequest().containsKey(Bukkit.getPlayer(args[1]).getUniqueId())) {
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_ACCEPT_DENIED_NO_INVITE));
            return;
        }
        if (PartyCommand.getPartySessionRequest().get(Bukkit.getPlayer(args[1]).getUniqueId()).toString().equalsIgnoreCase(p.getUniqueId().toString())) {
            PartyCommand.getPartySessionRequest().remove(Bukkit.getPlayer(args[1]).getUniqueId());
            if (getParty().hasParty(Bukkit.getPlayer(args[1]))) {
                getParty().addMember(Bukkit.getPlayer(args[1]), p);
                for (Player on : getParty().getMembers(Bukkit.getPlayer(args[1]))) {
                    on.sendMessage(getMsg(p, Messages.COMMAND_PARTY_ACCEPT_SUCCESS).replace("{player}", p.getName()));
                }
            } else {
                getParty().createParty(Bukkit.getPlayer(args[1]), p);
                for (Player on : getParty().getMembers(Bukkit.getPlayer(args[1]))) {
                    on.sendMessage(getMsg(p, Messages.COMMAND_PARTY_ACCEPT_SUCCESS).replace("{player}", p.getName()));
                }
            }
        } else {
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_ACCEPT_DENIED_NO_INVITE));
        }
    }
}
