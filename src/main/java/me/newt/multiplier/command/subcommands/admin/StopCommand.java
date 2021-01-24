package me.newt.multiplier.command.subcommands.admin;

import me.newt.multiplier.MultiplierPlugin;
import me.newt.multiplier.MultiplierType;
import me.newt.multiplier.command.SubCommand;
import me.newt.multiplier.messages.MessagesAPI;
import me.newt.multiplier.util.UtilArgumentParsers;
import org.bukkit.command.CommandSender;

public class StopCommand extends SubCommand {

    private final MultiplierPlugin multiplierPlugin;
    private final MessagesAPI msg;
    private final String permission;

    /**
     * Constructor.
     * @param multiplierPlugin Instance of the main class.
     * @param permission       Permission required to run this command.
     */
    public StopCommand(MultiplierPlugin multiplierPlugin, String permission) {
        this.multiplierPlugin = multiplierPlugin;
        this.msg = multiplierPlugin.getMessagesAPI();
        this.permission = permission;
    }

    /**
     * Subcommand handler.
     * @param sender Issuer of the command.
     * @param label  Alias of the command used.
     * @param args   Arguments of the command.
     */
    @Override
    public void run(CommandSender sender, String label, String[] args) {
        // multiplier admin stop <type>

        // Right permission?
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(msg.get("command_no_permission", permission));
            return;
        }

        // Right number of arguments?
        if (args.length != 3) {
            sender.sendMessage(msg.fullLine);
            sender.sendMessage(msg.get("command_invalid_arguments"));
            printInvalidArgs(sender);
            return;
        }

        String unValidatedType = args[2];

        // Right multiplier type argument?
        MultiplierType type = UtilArgumentParsers.parseType(unValidatedType);
        if (type == null) {
            sender.sendMessage(msg.fullLine);
            sender.sendMessage(msg.get("command_invalid_type"));
            printInvalidArgs(sender);
            return;
        }

        // Stopping multiplier(s).
        sender.sendMessage(msg.get("command_stop", type.getCapitalizedName()));
        multiplierPlugin.getMultiplierAPI().endAllActiveMultipliersOfType(type);
    }

    /**
     * Get the permission string that is required for this command.
     * @return Permission string required to run this command.
     */
    @Override
    public String getPermission() {
        return permission;
    }

    /**
     * Let the CommandSender know that they've entered invalid arguments.
     * @param sender Issuer of the command.
     */
    private void printInvalidArgs(CommandSender sender) {
        sender.sendMessage(" §8- §7<§ftype§7> (§fWord§7) Multiplier type.");
        sender.sendMessage(msg.fullLine);
    }
}
