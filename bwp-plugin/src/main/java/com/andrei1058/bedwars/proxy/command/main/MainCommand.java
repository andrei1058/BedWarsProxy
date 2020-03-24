package com.andrei1058.bedwars.proxy.command.main;

import com.andrei1058.bedwars.proxy.command.ParentCommand;
import com.andrei1058.bedwars.proxy.BedWarsProxy;
import com.andrei1058.bedwars.proxy.language.Language;
import com.andrei1058.bedwars.proxy.api.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class MainCommand extends ParentCommand {

    private static MainCommand instance;

    /**
     * Create a new Parent Command
     *
     * @param name
     */
    public MainCommand(String name) {
        super(name);
        instance = this;
        setAliases(Arrays.asList("bedwars"));
        addSubCommand(new SelectorCMD("gui", ""));
        addSubCommand(new LangCMD("lang", ""));
        addSubCommand(new LangCMD("language", ""));
        addSubCommand(new JoinCMD("join", ""));
        addSubCommand(new ReJoinCMD("rejoin", "bw.rejoin"));
        addSubCommand(new TpCommand("tp", "bw.tp"));
    }

    @Override
    public void sendDefaultMessage(CommandSender s) {
        if (s instanceof ConsoleCommandSender) return;
        Player p = (Player) s;

        s.sendMessage(" ");
        s.sendMessage("§8§l|-" + " §6" + BedWarsProxy.getPlugin().getDescription().getName() + " v" + BedWarsProxy.getPlugin().getDescription().getVersion() + " §7- §cCommands");
        s.sendMessage(" ");
        if (hasSubCommand("gui")) {
            p.spigot().sendMessage(BedWarsProxy.createTC(Language.getMsg(p, Messages.COMMAND_GUI_DISPLAY), "/bw gui", Language.getMsg(p, Messages.COMMAND_GUI_HOVER)));
        }
        if (hasSubCommand("lang")) {
            p.spigot().sendMessage(BedWarsProxy.createTC(Language.getMsg(p, Messages.COMMAND_LANGUAGE_DISPLAY), "/bw lang", Language.getMsg(p, Messages.COMMAND_LANGUAGE_HOVER)));
        }
        if (hasSubCommand("rejoin")) {
            p.spigot().sendMessage(BedWarsProxy.createTC(Language.getMsg(p, Messages.COMMAND_REJOIN_DISPLAY), "/bw rejoin", Language.getMsg(p, Messages.COMMAND_REJOIN_HOVER)));
        }
        if (hasSubCommand("tp") && getSubCommand("tp").hasPermission(s)) {
            p.spigot().sendMessage(BedWarsProxy.createTC(Language.getMsg(p, Messages.COMMAND_TP_DISPLAY), "/bw tp", Language.getMsg(p, Messages.COMMAND_TP_HOVER)));
        }
    }

    public static MainCommand getInstance() {
        return instance;
    }
}
