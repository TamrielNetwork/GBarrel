package me.newt.multiplier.command;

import org.bukkit.command.CommandSender;

public abstract class SubCommand {

    /**
     * Method that is run when this subcommand is executed.
     * @param sender Issuer of the command.
     * @param label  Alias of the command used.
     * @param args   Arguments of the command.
     */
    public abstract void run(CommandSender sender, String label, String[] args);

    /**
     * Get the permission string that is required for this command.
     * @return Permission string required to run this command.
     */
    public abstract String getPermission();
}
