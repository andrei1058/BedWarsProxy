package com.andrei1058.bedwars.proxy.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ParentCommand extends Command {

    //List of sub commands
    private List<SubCommand> subCommands = new ArrayList<>();

    /**
     * Create a new Parent Command
     */
    public ParentCommand(String name) {
        super(name);
    }

    /**
     * Manage how to run this parent command
     */
    @Override
    public boolean execute(CommandSender s, String st, String[] args) {

        //send default message
        if (args.length == 0) {
            sendDefaultMessage(s);
            return true;
        }

        //check for sub-commands
        for (SubCommand sc : getSubCommands()) {
            if (sc.getName().equalsIgnoreCase(args[0])) {
                sc.execute(s, Arrays.copyOfRange(args, 1, args.length));
                return true;
            }
        }

        //send default message if not sub-command was found
        sendDefaultMessage(s);
        return true;
    }

    /**
     * Manage parent command tab-complete event.
     * If you don't want to show a sub-command on tab-complete consider overriding the hasPermission method
     * of the sub-command. So if it is a setup command check if CommandSender is in setup-mode and if he is not
     * just return false and it will not be shown.
     */
    public List<String> tabComplete(CommandSender s, String alias, String[] args, Location location) throws IllegalArgumentException {
        if (args.length == 1) {
            List<String> sub = new ArrayList<>();
            for (SubCommand sb : getSubCommands()) {
                if (sb.hasPermission(s)) sub.add(sb.getName());
            }
            return sub;
        } else if (args.length == 2) {
            if (hasSubCommand(args[0])) {
                return getSubCommand(args[0]).tabComplete(s, alias, args, location);
            }
        }
        return null;
    }

    /**
     * Add a sub-command to this parent
     */
    public void addSubCommand(SubCommand subCommand) {
        subCommands.add(subCommand);
    }

    /** Remove sub-command */
    public void removeSubCommand(String name){
        SubCommand sc = getSubCommand(name);
        if (sc != null) subCommands.remove(sc);
    }


    /**
     * This is called when parent command is used without specifying a sub-command
     * When parent args length is 0
     */
    public abstract void sendDefaultMessage(CommandSender s);

    /**
     * Check if command has given sub-command.
     *
     * @param name search for give name.
     */
    public boolean hasSubCommand(String name) {
        for (SubCommand sc : getSubCommands()) {
            if (sc.getName().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    /**
     * Get command sub-command.
     *
     * @param name search for given name.
     */
    public SubCommand getSubCommand(String name) {
        for (SubCommand sc : getSubCommands()) {
            if (sc.getName().equalsIgnoreCase(name)) return sc;
        }
        return null;
    }

    /**
     * Get sub-commands list
     */
    public List<SubCommand> getSubCommands() {
        return subCommands;
    }
}
