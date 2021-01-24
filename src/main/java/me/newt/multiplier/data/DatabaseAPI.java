package me.newt.multiplier.data;

import me.newt.multiplier.Multiplier;
import me.newt.multiplier.MultiplierPlugin;
import me.newt.multiplier.MultiplierType;
import me.newt.multiplier.data.databases.MySQL;
import me.newt.multiplier.data.databases.SQLite;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseAPI {

    private final MultiplierPlugin multiplierPlugin;
    private final BukkitScheduler scheduler;
    private Database database;

    /**
     * Constructor.
     * @param multiplierPlugin Instance of the main class.
     */
    public DatabaseAPI(MultiplierPlugin multiplierPlugin) {
        this.multiplierPlugin = multiplierPlugin;
        this.scheduler = multiplierPlugin.getServer().getScheduler();

        String option = multiplierPlugin.getConfig().getString("database_option");
        assert option != null : "Database option in config.yml cannot be null!";

        if (option.equalsIgnoreCase("MYSQL")) {
            database = new MySQL(multiplierPlugin);
            multiplierPlugin.log(Level.INFO, "Using MySQL to store data.");
        } else if (option.equalsIgnoreCase("SQLITE")) {
            try {
                database = new SQLite(multiplierPlugin);
            } catch (IOException exception) {
                multiplierPlugin.log(Level.SEVERE, "Failed to initialize SQLite database.");
                exception.printStackTrace();
            }
            multiplierPlugin.log(Level.INFO, "Using SQLite to store data.");
        } else {
            try {
                database = new SQLite(multiplierPlugin);
            } catch (IOException exception) {
                multiplierPlugin.log(Level.SEVERE, "Failed to initialize SQLite database.");
                exception.printStackTrace();
            }
            multiplierPlugin.log(Level.WARNING, "Database option in config.yml is configured incorrectly. Falling back to SQLite.");
        }
        if (database != null) {
            scheduler.runTaskAsynchronously(multiplierPlugin, database::setup);
        }
    }

    /**
     * (IS RAN ASYNC) Adds a multiplier to the database that is currently being used.
     * @param uuid       The UUID of the Player who should receive this multiplier.
     * @param multiplier The multiplier that should be added.
     * @param updateID   If the multiplier ID should be updated in memory afterwards.
     */
    public void addMultiplier(UUID uuid, Multiplier multiplier, boolean updateID) {
        scheduler.runTaskAsynchronously(multiplierPlugin, () -> {
            Connection connection = database.openConnection();

            // Add entry
            try {
                PreparedStatement statement = connection.prepareStatement("REPLACE INTO multipliers (uuid,type,duration,multiplier) VALUES(?,?,?,?);");
                statement.setString(1, uuid.toString());
                statement.setString(2, multiplier.getType().toString());
                statement.setInt(3, multiplier.getDuration());
                statement.setInt(4, multiplier.getMultiplierStrength());
                statement.executeUpdate();
                statement.close();
            } catch (SQLException exception) {
                multiplierPlugin.log(Level.SEVERE, "Failed to add multiplier to player in the database.");
                exception.printStackTrace();
            }

            if (updateID) {
                // Determine ID.
                try {
                    Statement statement = connection.createStatement();
                    String query = "SELECT last_insert_rowid();";
                    if (database instanceof MySQL) {
                        query = "SELECT LAST_INSERT_ID();";
                    }
                    ResultSet resultSet = statement.executeQuery(query);
                    while (resultSet.next()) {
                        int id = resultSet.getInt(1);
                        scheduler.runTask(multiplierPlugin, () -> {
                            multiplierPlugin.getMultiplierAPI().updateID(uuid, multiplier, id);
                        });
                    }
                } catch (SQLException exception) {
                    multiplierPlugin.log(Level.SEVERE, "Failed to get multiplier ID in the database.");
                    exception.printStackTrace();
                }
            }

            database.closeConnection();
        });
    }

    /**
     * (IS RAN ASYNC) Removes a multiplier from the database that is currently being used.
     * @param id The id of the multiplier that should be removed.
     */
    public void removeMultiplier(int id) {
        scheduler.runTaskAsynchronously(multiplierPlugin, () -> {
            Connection connection = database.openConnection();
            try {
                Statement statement = connection.createStatement();
                String query = "DELETE FROM multipliers WHERE id = " + id + ";";
                statement.executeUpdate(query);
                statement.close();
            } catch (SQLException exception) {
                multiplierPlugin.log(Level.SEVERE, "Failed to remove multiplier from player in the database.");
                exception.printStackTrace();
            }
            database.closeConnection();
        });
    }

    /**
     * (SHOULD BE RAN ASYNC) Obtain a list of multipliers owned by a Player.
     * @param uuid The UUID of the Player.
     */
    public List<Multiplier> getMultipliers(UUID uuid) {
        Connection connection = database.openConnection();
        List<Multiplier> multipliers = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT id, type, duration, multiplier FROM multipliers WHERE uuid = '" + uuid + "';";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {

                int id = resultSet.getInt("id");
                int duration = resultSet.getInt("duration");
                int multiplierStrength = resultSet.getInt("multiplier");
                MultiplierType type = MultiplierType.valueOf(resultSet.getString("type").toUpperCase());

                Multiplier multiplier = new Multiplier(id, type, duration, multiplierStrength);
                multipliers.add(multiplier);
            }
        } catch (SQLException exception) {
            multiplierPlugin.log(Level.SEVERE, "Failed to get multipliers from player in the database.");
            exception.printStackTrace();
        }
        database.closeConnection();
        return multipliers;
    }
}
