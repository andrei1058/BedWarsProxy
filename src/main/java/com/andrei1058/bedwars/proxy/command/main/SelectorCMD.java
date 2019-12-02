package com.andrei1058.bedwars.proxy.command.main;

import com.andrei1058.bedwars.proxy.arenamanager.ArenaGUI;
import com.andrei1058.bedwars.proxy.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SelectorCMD extends SubCommand {
    /**
     * Create a new sub-command.
     * Do not forget to add it to a parent.
     *
     * @param name       sub-command name.
     * @param permission sub-command permission, leave empty if no permission is required.
     */
    public SelectorCMD(String name, String permission) {
        super(name, permission);
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        if (s instanceof ConsoleCommandSender) return;
        Player p = (Player) s;

        String group = "default";

        if (args.length == 1) {
            group = args[0];
        }

        ArenaGUI.openGui(p, group);
    }
}
