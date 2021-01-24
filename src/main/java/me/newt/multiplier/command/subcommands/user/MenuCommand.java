package me.newt.multiplier.command.subcommands.user;

import me.newt.multiplier.Multiplier;
import me.newt.multiplier.MultiplierPlugin;
import me.newt.multiplier.command.SubCommand;
import me.newt.multiplier.messages.MessagesAPI;
import me.newt.multiplier.util.UtilBook;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class MenuCommand extends SubCommand {

    private final MultiplierPlugin multiplierPlugin;
    private final MessagesAPI msg;
    private final String permission;
    private final UtilBook utilBook;

    /**
     * Constructor.
     * @param multiplierPlugin Instance of the main class.
     * @param permission       Permission required to run this command.
     */
    public MenuCommand(MultiplierPlugin multiplierPlugin, String permission) {
        this.multiplierPlugin = multiplierPlugin;
        this.msg = multiplierPlugin.getMessagesAPI();
        this.permission = permission;
        this.utilBook = new UtilBook(multiplierPlugin);
    }

    /**
     * Subcommand handler.
     * @param sender Issuer of the command (NEEDS TO BE A PLAYER).
     * @param label  Alias of the command used.
     * @param args   Arguments of the command.
     */
    @Override
    public void run(CommandSender sender, String label, String[] args) {
        // multiplier

        // Right permission?
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(msg.get("command_no_permission", permission));
            return;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        // TODO OPEN GUI
        // TODO THIS IS TEMPORARY:

        // Displaying multipliers from memory.
        sender.sendMessage(msg.get("command_list", player.getName()));
        List<Multiplier> list = multiplierPlugin.getMultiplierAPI().getMultipliers(uuid);
        list.forEach(multiplier -> sender.sendMessage(multiplier.getMultiplierAsText()));
        sender.sendMessage(msg.get("command_list_end", list.size() + ""));
        player.openBook(utilBook.getBook(list, multiplierPlugin.getMultiplierAPI().getSessionID(uuid)));
    }

    /**
     * Get the permission string that is required for this command.
     * @return Permission string required to run this command.
     */
    @Override
    public String getPermission() {
        return permission;
    }
}
