package me.newt.multiplier.command;

import me.newt.multiplier.MultiplierPlugin;
import me.newt.multiplier.MultiplierType;
import me.newt.multiplier.command.subcommands.admin.*;
import me.newt.multiplier.command.subcommands.user.MenuCommand;
import me.newt.multiplier.command.subcommands.user.ThankCommand;
import me.newt.multiplier.messages.MessagesAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.logging.Level;

public class BaseCommand implements CommandExecutor, TabCompleter {

    private final MessagesAPI msg;
    private final SubCommand menuCMD;
    private final SubCommand thankCMD;
    private final SubCommand giveCMD;
    private final SubCommand listCMD;
    private final SubCommand reloadCMD;
    private final SubCommand removeCMD;
    private final SubCommand startCMD;
    private final SubCommand stopCMD;

    /**
     * Constructor.
     * @param multiplierPlugin Instance of the main class.
     */
    public BaseCommand(MultiplierPlugin multiplierPlugin) {
        this.msg = multiplierPlugin.getMessagesAPI();

        // User subcommands:
        this.menuCMD = new MenuCommand(multiplierPlugin, "multiplier.user.activate");
        this.thankCMD = new ThankCommand(multiplierPlugin, "multiplier.user.thank");

        // Admin subcommands:
        this.giveCMD = new GiveCommand(multiplierPlugin, "multiplier.admin.give");
        this.listCMD = new ListCommand(multiplierPlugin, "multiplier.admin.list");
        this.reloadCMD = new ReloadCommand(multiplierPlugin, "multiplier.admin.reload");
        this.removeCMD = new RemoveCommand(multiplierPlugin, "multiplier.admin.remove");
        this.startCMD = new StartCommand(multiplierPlugin, "multiplier.admin.start");
        this.stopCMD = new StopCommand(multiplierPlugin, "multiplier.admin.stop");

        PluginCommand command = multiplierPlugin.getCommand("multiplier");
        if (command == null) {
            multiplierPlugin.log(Level.SEVERE, "Failed to initialize root command.");
            return;
        }
        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    /**
     * Command handler.
     * @param sender  Issuer of the command.
     * @param command Executed command.
     * @param label   Alias of the command used.
     * @param args    Arguments used with the command.
     * @return Whether the command was successfully executed (Always true).
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {

            // Menu Command
            if (!(sender instanceof Player)) {
                printHelp(sender, label);
                return true;
            }
            menuCMD.run(sender, label, args);
            return true;

        }
        if (args.length == 1) {

            // Thank Command
            if (args[0].equalsIgnoreCase("thank")) {
                thankCMD.run(sender, label, args);
                return true;
            }

            // Invalid or Help Command
            printHelp(sender, label);
            return true;

        }

        // Admin Command
        if (args[0].equalsIgnoreCase("admin")) {

            // Give Command
            if (args[1].equalsIgnoreCase("give")) {
                giveCMD.run(sender, label, args);
                return true;
            }

            // List Command
            if (args[1].equalsIgnoreCase("list")) {
                listCMD.run(sender, label, args);
                return true;
            }

            // Remove Command
            if (args[1].equalsIgnoreCase("remove")) {
                removeCMD.run(sender, label, args);
                return true;
            }

            // Start Command
            if (args[1].equalsIgnoreCase("start")) {
                startCMD.run(sender, label, args);
                return true;
            }

            // Stop Command
            if (args[1].equalsIgnoreCase("stop")) {
                stopCMD.run(sender, label, args);
                return true;
            }

            // Reload Command
            if (args[1].equalsIgnoreCase("reload")) {
                reloadCMD.run(sender, label, args);
                return true;
            }
        }

        // Invalid Command
        printHelp(sender, label);
        return true;
    }

    /*
     * USER COMMANDS:
     * /multiplier              Opens multiplier gui.
     * /multiplier help         Shows multiplier help.
     * /multiplier thank        Thank the players that activated a multiplier.
     *
     * ADMIN COMMANDS:
     * /multiplier admin give <player/uuid> <type> <duration> <strength>    Give a multiplier.
     * /multiplier admin list <player/uuid>                                 List a player's multipliers.
     * /multiplier admin remove <id>                                        Remove a multiplier.
     * /multiplier admin start <type> <duration> <strength>                 Start a multiplier.
     * /multiplier admin stop <type>                                        Stop all active multipliers for that type.
     * /multiplier admin reload                                             Reload the configuration files.
     */

    /**
     * Send command help to the issuer of a command.
     * @param sender Issuer of the command.
     * @param label  Alias of the command used.
     */
    private void printHelp(CommandSender sender, String label) {
        sender.sendMessage(msg.fullLine);
        if (sender.hasPermission(menuCMD.getPermission()))
            print(sender, "/" + label, null, msg.get("command_description_menu"));
        if (sender.hasPermission(thankCMD.getPermission()))
            print(sender, "/" + label + " thank", null, msg.get("command_description_thank"));
        if (sender.hasPermission(listCMD.getPermission()))
            print(sender, "/" + label + " admin list (...)", "/" + label + " admin <player>", msg.get("command_description_list"));
        if (sender.hasPermission(giveCMD.getPermission()))
            print(sender, "/" + label + " admin give (...)", "/" + label + " admin give <player> <type> <duration> <strength>", msg.get("command_description_give"));
        if (sender.hasPermission(removeCMD.getPermission()))
            print(sender, "/" + label + " admin remove (...)", "/" + label + " admin remove <id>", msg.get("command_description_remove"));
        if (sender.hasPermission(startCMD.getPermission()))
            print(sender, "/" + label + " admin start (...)", "/" + label + " admin start <type> <duration> <strength>", msg.get("command_description_start"));
        if (sender.hasPermission(stopCMD.getPermission()))
            print(sender, "/" + label + " admin stop (...)", "/" + label + " admin stop <type>", msg.get("command_description_stop"));
        if (sender.hasPermission(reloadCMD.getPermission()))
            print(sender, "/" + label + " admin reload", null, msg.get("command_description_reload"));
        print(sender, "/" + label + " help", null, msg.get("command_description_help"));
        sender.sendMessage(msg.fullLine);
    }

    /**
     * Send a TextComponent command help message to a CommandSender.
     * @param sender      Receiver of the message.
     * @param command     Command that should be described.
     * @param description Description of the command.
     */
    private void print(CommandSender sender, String command, String fullCommand, String description) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(command + " - " + description);
            return;
        }

        Player player = (Player) sender;
        Text hoverText = new Text(msg.get("command_copy_command"));
        HoverEvent onHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText);
        ClickEvent onClick;
        if (fullCommand == null) {
            onClick = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command);
        } else {
            onClick = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, fullCommand);
        }

        BaseComponent[] message = new ComponentBuilder(command)
                .color(msg.primaryColor)
                .event(onHover)
                .event(onClick)
                .append(" ")
                .reset()
                .append(description)
                .color(ChatColor.GRAY)
                .create();

        player.spigot().sendMessage(message);
    }

    /**
     * TabComplete handler.
     * @param sender  Issuer of the command.
     * @param command Executed command.
     * @param label   Alias of the command used.
     * @param args    Arguments used with the command.
     * @return List of tab completions.
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("");
            completions.add("help");
            if (sender.hasPermission(thankCMD.getPermission())) completions.add("thank");
            if (sender.hasPermission(giveCMD.getPermission())
                    || sender.hasPermission(listCMD.getPermission())
                    || sender.hasPermission(reloadCMD.getPermission())
                    || sender.hasPermission(removeCMD.getPermission())
                    || sender.hasPermission(startCMD.getPermission())
                    || sender.hasPermission(stopCMD.getPermission())) {
                completions.add("admin");
            }
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("admin")) {
            if (sender.hasPermission(giveCMD.getPermission())) completions.add("give");
            if (sender.hasPermission(listCMD.getPermission())) completions.add("list");
            if (sender.hasPermission(reloadCMD.getPermission())) completions.add("reload");
            if (sender.hasPermission(removeCMD.getPermission())) completions.add("remove");
            if (sender.hasPermission(startCMD.getPermission())) completions.add("start");
            if (sender.hasPermission(stopCMD.getPermission())) completions.add("stop");
        }

        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("admin")) {
                if (args[1].equalsIgnoreCase("give")) {
                    if (args.length == 3)
                        Bukkit.getOnlinePlayers().forEach(player -> completions.add(player.getName()));
                    if (args.length == 4)
                        EnumSet.allOf(MultiplierType.class).forEach(type -> completions.add(type.getCapitalizedName()));
                }
                if (args[1].equalsIgnoreCase("list")) {
                    if (args.length == 3)
                        Bukkit.getOnlinePlayers().forEach(player -> completions.add(player.getName()));
                }
                if (args[1].equalsIgnoreCase("start")) {
                    if (args.length == 3)
                        EnumSet.allOf(MultiplierType.class).forEach(type -> completions.add(type.getCapitalizedName()));
                }
                if (args[1].equalsIgnoreCase("stop")) {
                    if (args.length == 3)
                        EnumSet.allOf(MultiplierType.class).forEach(type -> completions.add(type.getCapitalizedName()));
                }
            }
        }
        Collections.sort(completions);
        return completions;
    }
}
