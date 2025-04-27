package at.ac.fhcampuswien.fhmdb.database;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

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

    // DAO
    private Dao<WatchlistMovieEntity, Long> watchlistDao;


    // constructor
    private DatabaseManager() throws DatabaseException {
        try {

            System.out.println("DEBUG:DatabaseManager constructor: trying to create ConnectionSource --"); // temporary
            createConnectionSource();
            System.out.println("DEBUG:DatabaseManager constructor: ConnectionSource created"); // temporary

            System.out.println("DEBUG:DatabaseManager constructor: calling createTable..."); // temporary
            createTables();
            System.out.println("DEBUG: DatabaseManager constructor: createTable finished."); // temporary

            initializeWatchlistDao();
            System.out.println("DEBUG: Watchlist DAO initialized."); // temporary

        } catch (SQLException e) {

            System.err.println("ERROR: couldnt create DB Connection Source or Table creatinon !!"); // temporary
            e.printStackTrace();
            throw new DatabaseException("FAIL to initialize database connection or a table", e);

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


    // close connection
    public void closeConnection() throws DatabaseException {
        if (connectionSource != null) {
            try {

                connectionSource.close();
                connectionSource = null; // reset
                instance = null; // -> reset singleton instance
                System.out.println("DEBUG: database connection closed"); // temporary

            } catch (Exception e) { 

                System.err.println("ERROR: FAILED  to close database connection"); // temporary
                e.printStackTrace();
                throw new DatabaseException("FAILED to close database connection", e);
            }
        }
    }

    private void createTables() throws SQLException {
        System.out.println("DEBUG: Checking/Creating table 'movie'..."); // temporary
        TableUtils.createTableIfNotExists(connectionSource, MovieEntity.class);

        System.out.println("DEBUG: Checking/Creating table 'watchlist'..."); // temporary
        TableUtils.createTableIfNotExists(connectionSource, WatchlistMovieEntity.class);

        System.out.println("DEBUG: table check/creation complete for movie + watchlist"); // temporary
    }

    private void initializeWatchlistDao() throws SQLException {
        watchlistDao = DaoManager.createDao(connectionSource, WatchlistMovieEntity.class);
    }

    public Dao<WatchlistMovieEntity, Long> getWatchlistDao() {
        return watchlistDao;
    }

}