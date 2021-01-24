package me.newt.multiplier.command.subcommands.admin;

import me.newt.multiplier.Multiplier;
import me.newt.multiplier.MultiplierPlugin;
import me.newt.multiplier.command.SubCommand;
import me.newt.multiplier.messages.MessagesAPI;
import me.newt.multiplier.util.UtilArgumentParsers;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;
import java.util.UUID;

public class ListCommand extends SubCommand {

    private final MultiplierPlugin multiplierPlugin;
    private final MessagesAPI msg;
    private final String permission;

    /**
     * Constructor.
     * @param multiplierPlugin Instance of the main class.
     * @param permission       Permission required to run this command.
     */
    public ListCommand(MultiplierPlugin multiplierPlugin, String permission) {
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
        // multiplier admin list <player>

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

        String unValidatedReceiver = args[2];

        // Right UUID or player name argument?
        UUID uuid = UtilArgumentParsers.parseUUID(unValidatedReceiver);
        if (uuid == null) {
            sender.sendMessage(msg.fullLine);
            sender.sendMessage(msg.get("command_invalid_uuid"));
            printInvalidArgs(sender);
            return;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        String resolvedName = offlinePlayer.getName();
        boolean neverJoined = false;

        // Has this player joined before?
        if (resolvedName == null) {
            neverJoined = true;
            resolvedName = uuid.toString();
        }

        if (offlinePlayer.isOnline()) {

            // Displaying multipliers from memory.
            sender.sendMessage(msg.get("command_list", resolvedName));
            List<Multiplier> list = multiplierPlugin.getMultiplierAPI().getMultipliers(uuid);
            list.forEach(multiplier -> sender.sendMessage(multiplier.getMultiplierAsText()));
            sender.sendMessage(msg.get("command_list_end", list.size() + ""));

        } else {

            // Displaying multipliers from database.
            sender.sendMessage(msg.get("command_list", resolvedName));
            if (neverJoined) sender.sendMessage(msg.get("command_unknown_player"));
            sender.sendMessage(msg.get("command_wait_for_load"));
            BukkitScheduler scheduler = multiplierPlugin.getServer().getScheduler();
            scheduler.runTaskAsynchronously(multiplierPlugin, () -> {
                List<Multiplier> list = multiplierPlugin.getDatabaseAPI().getMultipliers(uuid);
                list.forEach(multiplier -> sender.sendMessage(multiplier.getMultiplierAsText()));
                sender.sendMessage(msg.get("command_list_end", list.size() + ""));
            });
        }
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
        sender.sendMessage(" §8- §7<§fplayer§7> (§fWord§7) Online player or UUID.");
        sender.sendMessage(msg.fullLine);
    }
}
