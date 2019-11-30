package com.andrei1058.bedwars.proxy.command;

import org.bukkit.command.CommandSender;

public class BedWarsCommand extends ParentCommand {

    protected BedWarsCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute() {
        return false;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        return false;
    }
}
