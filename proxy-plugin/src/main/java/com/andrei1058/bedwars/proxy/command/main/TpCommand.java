package com.andrei1058.bedwars.proxy.command.main;

import com.andrei1058.bedwars.proxy.command.SubCommand;
import com.andrei1058.bedwars.proxy.BedWarsProxy;
import com.andrei1058.bedwars.proxy.api.ArenaStatus;
import com.andrei1058.bedwars.proxy.arenamanager.TpRequest;
import com.andrei1058.bedwars.proxy.language.Language;
import com.andrei1058.bedwars.proxy.api.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class TpCommand extends SubCommand {


    public TpCommand(String name, String permission) {
        super(name, permission);
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        if (s instanceof ConsoleCommandSender) {
            s.sendMessage("This command is for players!");
            return;
        }

        Player p = (Player) s;

        if (!p.hasPermission("bw.tp")) {
            p.sendMessage(Language.getMsg(p, Messages.COMMAND_NOT_FOUND_OR_INSUFF_PERMS));
            return;
        }

        if (args.length != 1) {
            p.sendMessage(Language.getMsg((Player) s, Messages.COMMAND_TP_USAGE));
            return;
        }

        TpRequest tr = TpRequest.getTpRequest(((Player) s).getUniqueId());
        if (tr == null) {
            tr = new TpRequest(((Player) s).getUniqueId(), args[0]);
        } else return;

        TpRequest finalTr = tr; Player pl = (Player) s;
        Bukkit.getScheduler().runTaskLater(BedWarsProxy.getPlugin(), () -> {
            if (pl.isOnline()){
                if (finalTr.getArena() == null){
                    pl.sendMessage(Language.getMsg(pl, Messages.COMMAND_TP_NOT_FOUND).replace("{player}", finalTr.getTarget()));
                } else {
                    if (finalTr.getArena().getStatus() != ArenaStatus.PLAYING){
                        pl.sendMessage(Language.getMsg(pl, Messages.COMMAND_TP_FAIL2).replace("{player}",
                                finalTr.getTarget()).replace("{server}", finalTr.getArena().getServer()));
                    } else {
                        finalTr.getArena().addSpectator(pl, finalTr.getTarget());
                    }
                }
            }
        }, 45L);
    }
}
