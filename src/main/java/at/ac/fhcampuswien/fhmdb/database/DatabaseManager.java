package at.ac.fhcampuswien.fhmdb.database;

import com.j256.ormlite.support.ConnectionSource;

import at.ac.fhcampuswien.fhmdb.exceptions.DatabaseException;

public class DatabaseManager {

    // constants for the db-connection -> ensures Database.DB_URL existance and that its correct
    public static final String DB_CONNECTION_STRING = Database.DB_URL + "fhmdb";
    public static final String DB_USER = ""; 
    public static final String DB_PASSWORD = "";

    // singleton
    private static DatabaseManager instance;

    // ormlite connectionsource field
    private ConnectionSource connectionSource;

    // constructor
    private DatabaseManager() {}

    // method to get one instance
    public static synchronized DatabaseManager getInstance() throws DatabaseException {
        if (instance == null) {
            System.out.println("DEBUG: instance is nulll"); // temporary
        }

        // placeholder -> return null
        return null;
        // todo: return instance;
    }

}