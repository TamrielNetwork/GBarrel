package me.newt.multiplier.data.databases;

import me.newt.multiplier.MultiplierPlugin;
import me.newt.multiplier.data.Database;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL extends Database {

    private Connection connection;
    private final String host;
    private final String port;
    private final String name;
    private final String user;
    private final String pass;

    /**
     * Constructor.
     * @param multiplierPlugin Instance of the main class.
     */
    public MySQL(MultiplierPlugin multiplierPlugin) {
        FileConfiguration config = multiplierPlugin.getConfig();
        host = config.getString("database_host");
        port = config.getString("database_port");
        name = config.getString("database_name");
        user = config.getString("database_username");
        pass = config.getString("database_password");

        assert host != null : "Database credentials (host) in config.yml cannot be null!";
        assert port != null : "Database credentials (port) in config.yml cannot be null!";
        assert name != null : "Database credentials (name) in config.yml cannot be null!";
        assert user != null : "Database credentials (username) in config.yml cannot be null!";
        assert pass != null : "Database credentials (password) in config.yml cannot be null!";
    }

    /**
     * Start a new connection to the database. Make sure to call {@link #closeConnection()} when done!
     */
    @Override
    public Connection openConnection() {
        connection = null;
        try {
            String url = "jdbc:mysql://" + host + ":" + port + "/" + name;
            connection = DriverManager.getConnection(url, user, pass);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return connection;
    }

    /**
     * Close the connection to a database.
     */
    @Override
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Prepare the database for its first use.
     */
    @Override
    public void setup() {
        openConnection();
        try {
            Statement statement = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS multipliers (" +
                    "'id' INT NOT NULL AUTO_INCREMENT, " +
                    "'uuid' VARCHAR NOT NULL," +
                    "'type' VARCHAR NOT NULL," +
                    "'duration' INT NOT NULL," +
                    "'multiplier' INT NOT NULL," +
                    "PRIMARY KEY ('id'));";
            statement.executeUpdate(query);
            statement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        closeConnection();
    }
}
