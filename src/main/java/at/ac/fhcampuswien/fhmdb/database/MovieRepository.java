// src/main/java/at/ac/fhcampuswien/fhmdb/database/MovieRepository.java
package at.ac.fhcampuswien.fhmdb.database;

import at.ac.fhcampuswien.fhmdb.exceptions.DatabaseException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder; // Import für QueryBuilder
import java.sql.SQLException;
import java.util.Collections; // Import für leere Liste
import java.util.List;

public class MovieRepository {

    private Dao<MovieEntity, Long> movieDao;

    public MovieRepository() throws DatabaseException {
        try {

             this.movieDao = DatabaseManager.getInstance().getMovieDao();
             if (this.movieDao == null) {
                 throw new DatabaseException("MovieDao could not be retrieved from DatabaseManager.");
             }

            System.out.println("DEBUG: MovieRepository constructor: DAO obtained/created for MovieEntity.");
        } catch (DatabaseException e) {
            System.err.println("ERROR: MovieRepository failed during DatabaseManager access or DAO retrieval.");
            throw e; 
        }
    }



}