package at.ac.fhcampuswien.fhmdb.database;
import at.ac.fhcampuswien.fhmdb.models.Genre;
import at.ac.fhcampuswien.fhmdb.models.Movie;

import java.util.List;

import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable (tableName = "WatchlistRepository")
public class WatchlistRepository {

    //Achtung nur Platzhalter, um zu sehen, ob GUI funkt


        private static WatchlistRepository instance;

    private final List<Movie> watchlist = new java.util.ArrayList<>();

        private WatchlistRepository() {}

        public static WatchlistRepository getInstance() {
            if (instance == null) {
                instance = new WatchlistRepository();
            }
            return instance;
        }

    public List<Movie> getAllWatchlistMovies() {
        return new java.util.ArrayList<>(watchlist);  // Gibt aktuelle Watchlist zurück
    }

    public void addMovieToWatchlist(Movie movie) {
        if (!watchlist.contains(movie)) {
            watchlist.add(movie);
            System.out.println(movie.getTitle() + " wurde zur Watchlist hinzugefügt!");
        } else {
            System.out.println(movie.getTitle() + " ist bereits in der Watchlist.");
        }
    }

    public void removeMovieFromWatchlist(Movie movie) {
        watchlist.remove(movie);
        System.out.println(movie.getTitle() + " wurde aus der Watchlist entfernt!");
    }



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
