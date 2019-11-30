package com.andrei1058.bedwars.proxy.command;

import org.bukkit.command.Command;

public abstract class ParentCommand extends Command implements ICommand {


    protected ParentCommand(String name) {
        super(name);
    }

    @Override
    public String getName() {
        return null;
    }
}
