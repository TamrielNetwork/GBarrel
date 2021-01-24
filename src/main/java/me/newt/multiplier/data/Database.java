package me.newt.multiplier.data;

import java.sql.Connection;

public abstract class Database {

    /*
     * ID           (INTEGER)   id of the multiplier.
     * UUID         (STRING)    uuid of the owner of the multiplier.
     * TYPE         (STRING)    type of the multiplier.
     * DURATION     (INTEGER)   time (in seconds) that this multiplier lasts.
     * MULTIPLIER   (INTEGER)   amount that this multiplier multiplies with.
     */

    /**
     * Start a new connection to the database. Make sure to call {@link #closeConnection()} when done!
     */
    public abstract Connection openConnection();

    /**
     * Close the connection to a database.
     */
    public abstract void closeConnection();

    /**
     * Prepare the database for its first use.
     */
    public abstract void setup();

}
