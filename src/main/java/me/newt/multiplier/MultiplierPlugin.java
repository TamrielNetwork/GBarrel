package me.newt.multiplier;

import me.newt.multiplier.command.BaseCommand;
import me.newt.multiplier.data.DatabaseAPI;
import me.newt.multiplier.listener.PlayerConnectionListener;
import me.newt.multiplier.messages.MessagesAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class MultiplierPlugin extends JavaPlugin {

    private MultiplierAPI multiplierAPI;
    private DatabaseAPI databaseAPI;
    private MessagesAPI messagesAPI;

    /**
     * Enabling the plugin.
     */
    @Override
    public void onEnable() {
        // Config version checking
        saveDefaultConfig();
        if (getConfig().getInt("config_version") != 1) {
            log(Level.SEVERE, "Your configuration file is outdated! This plugin cannot start!");
            log(Level.SEVERE, "Delete your old config.yml and let te plugin generate a fresh file.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // API initialization
        multiplierAPI = new MultiplierAPI(this);
        databaseAPI = new DatabaseAPI(this);
        messagesAPI = new MessagesAPI(this);
        new BaseCommand(this);

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerConnectionListener(this), this);

        // Deprecated, reload support
        if (Bukkit.getOnlinePlayers().size() > 0) {
            log(Level.WARNING, "Please do not use the 'reload' command. This may cause issues.");
            log(Level.WARNING, "Reloading is a bad practice. Restart your server instead!");
            Bukkit.getOnlinePlayers().forEach(player -> multiplierAPI.loadMultipliersAsync(player.getUniqueId()));
        }
    }

    /**
     * Disabling the plugin.
     */
    @Override
    public void onDisable() {

    }

    /**
     * Get an instance of the MultiplierAPI. Through this you can start and stop multipliers, give and take multipliers, and get all active multipliers.
     * @return Instance of the MultiplierAPI.
     */
    public MultiplierAPI getMultiplierAPI() {
        return multiplierAPI;
    }

    /**
     * Get an instance of the DatabaseAPI. Through this you can set and get multipliers in the database directly.
     * @return Instance of the DatabaseAPI.
     */
    public DatabaseAPI getDatabaseAPI() {
        return databaseAPI;
    }

    /**
     * Get an instance of the DatabaseAPI. Through this you can get messages from the language.yml file.
     * @return Instance of the DatabaseAPI.
     */
    public MessagesAPI getMessagesAPI() {
        return messagesAPI;
    }

    /**
     * Log a message in the console.
     * @param level   The severity-level of the message.
     * @param message The message that should be logged.
     */
    public void log(Level level, String message) {
        getLogger().log(level, message);
    }

    /**
     * Reload the language file.
     */
    public void reloadLanguage() {
        messagesAPI = null;
        messagesAPI = new MessagesAPI(this);
    }
}
