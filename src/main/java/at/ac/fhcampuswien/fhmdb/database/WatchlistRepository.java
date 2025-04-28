package at.ac.fhcampuswien.fhmdb.database;
import at.ac.fhcampuswien.fhmdb.exceptions.DatabaseException;
import at.ac.fhcampuswien.fhmdb.models.Genre;
import at.ac.fhcampuswien.fhmdb.models.Movie;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.DatabaseTable;

// @DatabaseTable (tableName = "WatchlistRepository") // auskommentiert - nicht mehr notwendig(?)
public class WatchlistRepository {

    // private static WatchlistRepository instance; // auskommentiert - nicht mehr notwendig(?)
    private final List<Movie> watchlist = new java.util.ArrayList<>();

    private Dao<WatchlistMovieEntity, Long> watchlistDao;

    // NEU 
    public WatchlistRepository() throws DatabaseException { 
        try {
            this.watchlistDao = DatabaseManager.getInstance().getWatchlistDao();
            System.out.println("DEBUG: WatchlistRepository constructor: DAO obtained"); // temporary
        } catch (DatabaseException e) {
            System.err.println("ERROR: WatchlistRepository could not get DAO instance"); // temporary
            throw e;
        }
    }

    public int addToWatchlist(WatchlistMovieEntity entity) throws DatabaseException {
    try {
        WatchlistMovieEntity result = watchlistDao.createIfNotExists(entity);
        if (result != null) {
             System.out.println("DEBUG: Added movie with apiId '" + entity.getApiId() + "' to DB watchlist."); // temporary
            return 1;
        } else {
             System.out.println("DEBUG: Movie with apiId '" + entity.getApiId() + "' already in DB watchlist."); // temporary
            return 0;
        }
    } catch (SQLException e) {
        System.err.println("ERROR: Could not add movie with apiId '" + entity.getApiId() + "' to DB watchlist."); // temporary
        e.printStackTrace();
        throw new DatabaseException("Error adding movie to database watchlist", e);
    }
    }

    public List<WatchlistMovieEntity> getWatchlist() throws DatabaseException {
    try {
        List<WatchlistMovieEntity> result = watchlistDao.queryForAll();
        return result != null ? result : new ArrayList<>();
    } catch (SQLException e) {
        throw new DatabaseException("Error retrieving watchlist from database", e);
    }
    }

    public int removeFromWatchlist(String apiId) throws DatabaseException {
        try {
            List<WatchlistMovieEntity> entitiesToRemove = watchlistDao.queryBuilder().where().eq("apiId", apiId).query();
            if (entitiesToRemove != null && !entitiesToRemove.isEmpty()) {
                int deletedRows = watchlistDao.delete(entitiesToRemove);
                System.out.println("DEBUG: Removed " + deletedRows + " entry/entries with apiId '" + apiId + "' from DB watchlist."); // Temporär
                return deletedRows;
            } else {
                System.out.println("DEBUG: No entry found with apiId '" + apiId + "' to remove from DB watchlist."); // Temporär
                return 0;
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not remove movie with apiId '" + apiId + "' from DB watchlist."); // Temporär
            e.printStackTrace();
            throw new DatabaseException("Error removing movie from database watchlist", e);
        }
    }


    // public List<Movie> getAllWatchlistMovies() {
    //     return new java.util.ArrayList<>(watchlist);  // Gibt aktuelle Watchlist zurück
    // }

    // public void addMovieToWatchlist(Movie movie) {
    //     if (!watchlist.contains(movie)) {
    //         watchlist.add(movie);
    //         System.out.println(movie.getTitle() + " wurde zur Watchlist hinzugefügt!");
    //     } else {
    //         System.out.println(movie.getTitle() + " ist bereits in der Watchlist.");
    //     }
    // }

    // public void removeMovieFromWatchlist(Movie movie) {
    //     watchlist.remove(movie);
    //     System.out.println(movie.getTitle() + " wurde aus der Watchlist entfernt!");
    // }



//        // Dummy: Liste von Beispiel-Filmen
//        public List<Movie> getAllWatchlistMovies() {
//            // Dummy-Daten als Platzhalter
//            return List.of(
//                    new Movie("Interstellar", "Space Adventure", List.of(Genre.SCIENCE_FICTION)),
//                    new Movie("The Godfather", "Classic Mafia Movie", List.of(Genre.DRAMA))
//            );
//        }
//
//        public void removeMovieFromWatchlist(Movie movie) {
//            watchlist.remove(movie);
//        }
//

}
