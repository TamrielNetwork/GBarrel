package me.newt.multiplier.command.subcommands.admin;

import me.newt.multiplier.Multiplier;
import me.newt.multiplier.MultiplierPlugin;
import me.newt.multiplier.MultiplierType;
import me.newt.multiplier.command.SubCommand;
import me.newt.multiplier.messages.MessagesAPI;
import me.newt.multiplier.util.UtilArgumentParsers;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GiveCommand extends SubCommand {

    private final MultiplierPlugin multiplierPlugin;
    private final MessagesAPI msg;
    private final String permission;

    /**
     * Constructor.
     * @param multiplierPlugin Instance of the main class.
     * @param permission       Permission required to run this command.
     */
    public GiveCommand(MultiplierPlugin multiplierPlugin, String permission) {
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
        // multiplier admin give <player/uuid> <type> <duration> <strength>

        // Right permission?
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(msg.get("command_no_permission", permission));
            return;
        }

        // Right number of arguments?
        if (args.length != 6) {
            sender.sendMessage(msg.fullLine);
            sender.sendMessage(msg.get("command_invalid_arguments"));
            printInvalidArgs(sender);
            return;
        }

        String unValidatedReceiver = args[2];
        String unValidatedType = args[3];
        String unValidatedDuration = args[4];
        String unValidatedStrength = args[5];

        // Right UUID or player name argument?
        UUID uuid = UtilArgumentParsers.parseUUID(unValidatedReceiver);
        if (uuid == null) {
            sender.sendMessage(msg.fullLine);
            sender.sendMessage(msg.get("command_invalid_uuid"));
            printInvalidArgs(sender);
            return;
        }

        // Right multiplier type argument?
        MultiplierType type = UtilArgumentParsers.parseType(unValidatedType);
        if (type == null) {
            sender.sendMessage(msg.fullLine);
            sender.sendMessage(msg.get("command_invalid_type"));
            printInvalidArgs(sender);
            return;
        }

        // Right duration argument?
        Integer duration = UtilArgumentParsers.parseInteger(unValidatedDuration);
        if (duration == null) {
            sender.sendMessage(msg.fullLine);
            sender.sendMessage(msg.get("command_invalid_duration"));
            printInvalidArgs(sender);
            return;
        }

        // Right strength argument?
        Integer strength = UtilArgumentParsers.parseInteger(unValidatedStrength);
        if (strength == null) {
            sender.sendMessage(msg.fullLine);
            sender.sendMessage(msg.get("command_invalid_strength"));
            printInvalidArgs(sender);
            return;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        String resolvedName = offlinePlayer.getName();

        // Has this player joined before?
        if (resolvedName == null) {
            sender.sendMessage(msg.get("command_unknown_player"));
            resolvedName = uuid.toString();
        }

        // Giving the multiplier.
        sender.sendMessage(msg.get("command_give", type.getCapitalizedName(), resolvedName));
        Multiplier multiplier = new Multiplier(-999, type, duration, strength);
        multiplierPlugin.getMultiplierAPI().giveMultiplier(multiplier, uuid);

        // Notifying the player.
        if (offlinePlayer.isOnline()) {
            Player player = offlinePlayer.getPlayer();
            if (player != null) {
                msg.getList("command_give_notification", "" + strength, type.getCapitalizedName()).forEach(player::sendMessage);
            }
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
        sender.sendMessage(" §8- §7<§ftype§7> (§fWord§7) Multiplier type.");
        sender.sendMessage(" §8- §7<§fduration§7> (§fSeconds§7) Duration of the multiplier.");
        sender.sendMessage(" §8- §7<§fstrength§7> (§fNumber§7) Strength of the multiplier.");
        sender.sendMessage(" §7A duration of -1 will last until the next restart.");
        sender.sendMessage(msg.fullLine);
    }
}

