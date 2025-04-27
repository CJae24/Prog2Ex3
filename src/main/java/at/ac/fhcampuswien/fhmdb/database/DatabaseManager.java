package at.ac.fhcampuswien.fhmdb.database;

import java.sql.SQLException;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
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
    private DatabaseManager() throws DatabaseException {
        try {

            System.out.println("DEBUG:DatabaseManager constructor: trying to create ConnectionSource --"); // temporary
            createConnectionSource();
            System.out.println("DEBUG:DatabaseManager constructor: ConnectionSource created"); // temporary

        } catch (SQLException e) {

            System.err.println("ERROR: couldnt create DB Connection Source !!"); // temporary
            e.printStackTrace();
            throw new DatabaseException("FAIL to initialize database connection", e);

        }
    }

    // method to get one instance
    public static synchronized DatabaseManager getInstance() throws DatabaseException {
        if (instance == null) {
            System.out.println("DEBUG: instance is nulll"); // temporary
            instance = new DatabaseManager();
        }
        
        return instance;
    }

    private void createConnectionSource() throws SQLException {
        connectionSource = new JdbcConnectionSource(DB_CONNECTION_STRING, DB_USER, DB_PASSWORD);
    }

    // getter for  ConnectionSource
    public ConnectionSource getConnectionSource() {
        return connectionSource;
    }

    

}