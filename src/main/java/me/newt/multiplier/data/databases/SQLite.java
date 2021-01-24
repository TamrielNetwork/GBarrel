package me.newt.multiplier.data.databases;

import me.newt.multiplier.MultiplierPlugin;
import me.newt.multiplier.data.Database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite extends Database {

    private final File databaseFile;
    private Connection connection;

    /**
     * Constructor.
     * @param multiplierPlugin Instance of the main class.
     */
    public SQLite(MultiplierPlugin multiplierPlugin) throws IOException {
        File databaseFolder = new File(multiplierPlugin.getDataFolder().getPath() + File.separator + "data");
        if (!databaseFolder.exists()) {
            databaseFolder.mkdirs();
        }
        databaseFile = new File(multiplierPlugin.getDataFolder().getPath() + File.separator + "data", "userdata.db");
        databaseFile.createNewFile();
    }

    /**
     * Start a new connection to the database. Make sure to call {@link #closeConnection()} when done!
     */
    @Override
    public Connection openConnection() {
        connection = null;
        try {
            String url = "jdbc:sqlite:" + databaseFile.getPath();
            connection = DriverManager.getConnection(url);
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
            String query = "CREATE TABLE IF NOT EXISTS multipliers ( 'id' INTEGER PRIMARY KEY AUTOINCREMENT, 'uuid' VARCHAR NOT NULL, 'type' VARCHAR NOT NULL, 'duration' INTEGER NOT NULL, 'multiplier' INTEGER NOT NULL );";
            statement.executeUpdate(query);
            statement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        closeConnection();
    }
}
