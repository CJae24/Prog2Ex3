package at.ac.fhcampuswien.fhmdb.database;
import at.ac.fhcampuswien.fhmdb.models.Genre;
import at.ac.fhcampuswien.fhmdb.models.Movie;

import java.util.List;

import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable (tableName = "WatchlistRepository")
public class WatchlistRepository {

    //Achtung nur Platzhalter, um zu sehen, ob GUI funkt


        private static WatchlistRepository instance;

        private WatchlistRepository() {}

        public static WatchlistRepository getInstance() {
            if (instance == null) {
                instance = new WatchlistRepository();
            }
            return instance;
        }

        // Dummy: Liste von Beispiel-Filmen
        public List<Movie> getAllWatchlistMovies() {
            // Dummy-Daten als Platzhalter
            return List.of(
                    new Movie("Interstellar", "Space Adventure", List.of(Genre.SCIENCE_FICTION)),
                    new Movie("The Godfather", "Classic Mafia Movie", List.of(Genre.DRAMA))
            );
        }


}
