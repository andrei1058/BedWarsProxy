package com.andrei1058.bedwars.proxy.command;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class SubCommand {

    private String name;
    private String permission;

    /**
     * Create a new sub-command.
     * Do not forget to add it to a parent.
     *
     * @param name       sub-command name.
     * @param permission sub-command permission, leave empty if no permission is required.
     */
    public SubCommand(String name, String permission) {
        this.name = name;
        this.permission = permission;
    }


    /**
     * Use this method to specify what your command should do.
     */
    public abstract void execute(CommandSender s, String[] args);

    /**
     * Manage what to show on sub-command tab-complete.
     * If you do not want to shop this sub command in certain cases at mainCommand plus tab,
     * consider overriding {@link #hasPermission(CommandSender)} and make it return false when required.
     * This method only manages the tab-complete of mainCommand plus subCommand.
     */
    public List<String> tabComplete(CommandSender s, String alias, String[] args, Location location) {
        return null;
    }

    /**
     * Check if someone has permission to run this sub-command or if it can appear on tab complete.
     */
    public boolean hasPermission(CommandSender s) {
        return getPermission().isEmpty() || s.hasPermission(getPermission());
    }

    /**
     * Get sub-command name
     */
    public String getName() {
        return name;
    }

    /**
     * Get sub-command permission
     */
    public String getPermission() {
        return permission;
    }
}
