package at.ac.fhcampuswien.fhmdb;

import at.ac.fhcampuswien.fhmdb.api.MovieAPI;
import at.ac.fhcampuswien.fhmdb.database.WatchlistMovieEntity;
import at.ac.fhcampuswien.fhmdb.database.WatchlistRepository;
import at.ac.fhcampuswien.fhmdb.exceptions.DatabaseException;
import at.ac.fhcampuswien.fhmdb.exceptions.MovieApiException;
import at.ac.fhcampuswien.fhmdb.models.ClickEventHandler;
import at.ac.fhcampuswien.fhmdb.models.Genre;
import at.ac.fhcampuswien.fhmdb.models.Movie;
import at.ac.fhcampuswien.fhmdb.models.SortedState;
import at.ac.fhcampuswien.fhmdb.ui.MovieCell;
import at.ac.fhcampuswien.fhmdb.util.Helpers;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HomeController implements Initializable {
    @FXML
    public JFXButton searchBtn;

    @FXML
    public TextField searchField;

    @FXML
    public JFXListView<Movie> movieListView;

    @FXML
    public JFXComboBox genreComboBox;

    @FXML
    public JFXComboBox releaseYearComboBox;

    @FXML
    public JFXComboBox ratingFromComboBox;

    @FXML
    public JFXButton sortBtn;

    @FXML
    public JFXButton watchBtn;

    public List<Movie> allMovies;

    protected ObservableList<Movie> observableMovies = FXCollections.observableArrayList();

    protected SortedState sortedState;

    private WatchlistRepository watchlistRepository;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    // === Repository initialisieren ===
    try {
        this.watchlistRepository = new WatchlistRepository();
        System.out.println("DEBUG: WatchlistRepository initialized in HomeController."); // Optional
    } catch (DatabaseException e) {
        // Fehler anzeigen und Repository auf null setzen
        Helpers.showToast("Database Error: Could not initialize Watchlist. " + e.getMessage());
        System.err.println("ERROR initializing WatchlistRepository: " + e.getMessage());
        e.printStackTrace();
        this.watchlistRepository = null; // Wichtig für spätere Null-Prüfungen
    }

    // Bestehende Initialisierungsaufrufe
    initializeState();
    initializeLayout();
    showHome(); // Starte mit der Home-Ansicht
    }

    public void initializeState() {
        List<Movie> result = null;
        try {
            result = MovieAPI.getAllMovies();
        } catch (MovieApiException e) {
            Helpers.showToast(e.getMessage());
        }
        setMovies(result);
        setMovieList(result);
        sortedState = SortedState.NONE;

        // test stream methods
        System.out.println("getMostPopularActor");
        System.out.println(getMostPopularActor(allMovies));

        System.out.println("getLongestMovieTitle");
        System.out.println(getLongestMovieTitle(allMovies));

        System.out.println("count movies from Zemeckis");
        System.out.println(countMoviesFrom(allMovies, "Robert Zemeckis"));

        System.out.println("count movies from Steven Spielberg");
        System.out.println(countMoviesFrom(allMovies, "Steven Spielberg"));

        System.out.println("getMoviewsBetweenYears");
        List<Movie> between = getMoviesBetweenYears(allMovies, 1994, 2000);
        System.out.println(between.size());
        System.out.println(between.stream().map(Objects::toString).collect(Collectors.joining(", ")));
    }

    public void initializeLayout() {

        movieListView.setItems(observableMovies);   // set the items of the listview to the observable list

        // genre combobox
        Object[] genres = Genre.values();   // get all genres
        genreComboBox.getItems().add("No filter");  // add "no filter" to the combobox
        genreComboBox.getItems().addAll(genres);    // add all genres to the combobox
        genreComboBox.setPromptText("Filter by Genre");

        // year combobox
        releaseYearComboBox.getItems().add("No filter");  // add "no filter" to the combobox
        // fill array with numbers from 1900 to 2023
        Integer[] years = new Integer[124];
        for (int i = 0; i < years.length; i++) {
            years[i] = 1900 + i;
        }
        releaseYearComboBox.getItems().addAll(years);    // add all years to the combobox
        releaseYearComboBox.setPromptText("Filter by Release Year");

        // rating combobox
        ratingFromComboBox.getItems().add("No filter");  // add "no filter" to the combobox
        // fill array with numbers from 0 to 10
        Integer[] ratings = new Integer[11];
        for (int i = 0; i < ratings.length; i++) {
            ratings[i] = i;
        }
        ratingFromComboBox.getItems().addAll(ratings);    // add all ratings to the combobox
        ratingFromComboBox.setPromptText("Filter by Rating");
    }

    public void setMovies(List<Movie> movies) {
        allMovies = movies;
    }

    public void setMovieList(List<Movie> movies) {
        observableMovies.clear();
        observableMovies.addAll(movies);
    }

    public void sortMovies() {
        if (sortedState == SortedState.NONE || sortedState == SortedState.DESCENDING) {
            sortMovies(SortedState.ASCENDING);
        } else if (sortedState == SortedState.ASCENDING) {
            sortMovies(SortedState.DESCENDING);
        }
    }

    // sort movies based on sortedState
    // by default sorted state is NONE
    // afterwards it switches between ascending and descending
    public void sortMovies(SortedState sortDirection) {
        if (sortDirection == SortedState.ASCENDING) {
            observableMovies.sort(Comparator.comparing(Movie::getTitle));
            sortedState = SortedState.ASCENDING;
        } else {
            observableMovies.sort(Comparator.comparing(Movie::getTitle).reversed());
            sortedState = SortedState.DESCENDING;
        }
    }

    public List<Movie> filterByQuery(List<Movie> movies, String query) {
        if (query == null || query.isEmpty()) return movies;

        if (movies == null) {
            throw new IllegalArgumentException("movies must not be null");
        }

        return movies.stream().filter(movie ->
                        movie.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                                movie.getDescription().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }

    public List<Movie> filterByGenre(List<Movie> movies, Genre genre) {
        if (genre == null) return movies;

        if (movies == null) {
            throw new IllegalArgumentException("movies must not be null");
        }

        return movies.stream().filter(movie -> movie.getGenres().contains(genre)).toList();
    }

    public void applyAllFilters(String searchQuery, Object genre) {
        List<Movie> filteredMovies = allMovies;

        if (!searchQuery.isEmpty()) {
            filteredMovies = filterByQuery(filteredMovies, searchQuery);
        }

        if (genre != null && !genre.toString().equals("No filter")) {
            filteredMovies = filterByGenre(filteredMovies, Genre.valueOf(genre.toString()));
        }

        observableMovies.clear();
        observableMovies.addAll(filteredMovies);
    }

    public void searchBtnClicked(ActionEvent actionEvent) {
        String searchQuery = searchField.getText().trim().toLowerCase();
        String releaseYear = validateComboboxValue(releaseYearComboBox.getSelectionModel().getSelectedItem());
        String ratingFrom = validateComboboxValue(ratingFromComboBox.getSelectionModel().getSelectedItem());
        String genreValue = validateComboboxValue(genreComboBox.getSelectionModel().getSelectedItem());

        Genre genre = null;
        if (genreValue != null) {
            genre = Genre.valueOf(genreValue);
        }
        List<Movie> movies = null;
        try {
            movies = getMovies(searchQuery, genre, releaseYear, ratingFrom);
        } catch (MovieApiException e) {
            Helpers.showToast(e.getMessage() + ", movies were loaded from the cache");
            //TODO Load movies from DB Cache
        }

        setMovies(movies);
        setMovieList(movies);
        // applyAllFilters(searchQuery, genre);

        sortMovies(sortedState);
    }

    public String validateComboboxValue(Object value) {
        if (value != null && !value.toString().equals("No filter")) {
            return value.toString();
        }
        return null;
    }

    public List<Movie> getMovies(String searchQuery, Genre genre, String releaseYear, String ratingFrom) throws MovieApiException {
        return MovieAPI.getAllMovies(searchQuery, genre, releaseYear, ratingFrom);
    }

    public void sortBtnClicked(ActionEvent actionEvent) {
        sortMovies();
    }

    // count which actor is in the most movies
    public String getMostPopularActor(List<Movie> movies) {
        String actor = movies.stream()
                .flatMap(movie -> movie.getMainCast().stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");

        return actor;
    }

    public int getLongestMovieTitle(List<Movie> movies) {
        return movies.stream()
                .mapToInt(movie -> movie.getTitle().length())
                .max()
                .orElse(0);
    }

    public long countMoviesFrom(List<Movie> movies, String director) {
        return movies.stream()
                .filter(movie -> movie.getDirectors().contains(director))
                .count();
    }

    public List<Movie> getMoviesBetweenYears(List<Movie> movies, int startYear, int endYear) {
        return movies.stream()
                .filter(movie -> movie.getReleaseYear() >= startYear && movie.getReleaseYear() <= endYear)
                .collect(Collectors.toList());
    }


    private final ClickEventHandler<Movie> onAddToWatchlistClicked = (clickedMovie) -> {
    // Prüfen, ob das Repository initialisiert wurde
    if (this.watchlistRepository == null) {
        Helpers.showToast("Watchlist feature is currently unavailable (DB connection failed).");
        return;
    }
    // Prüfen, ob das Movie-Objekt und seine ID gültig sind
    if (clickedMovie == null || clickedMovie.getId() == null) {
        Helpers.showToast("Cannot add invalid movie to watchlist.");
        System.err.println("Error: Attempted to add null movie or movie with null ID to watchlist.");
         return;
    }

    try {
        // WatchlistMovieEntity mit der ID des Films erstellen
        WatchlistMovieEntity entity = new WatchlistMovieEntity(clickedMovie.getId());
        // Repository-Instanz verwenden
        int result = watchlistRepository.addToWatchlist(entity);
        if (result > 0) { // addToWatchlist gibt 1 zurück, wenn hinzugefügt, 0 wenn bereits vorhanden
            System.out.println("Film zur Watchlist DB hinzugefügt: " + clickedMovie.getTitle());
            Helpers.showToast(clickedMovie.getTitle() + " added to Watchlist");
        } else {
            System.out.println("Film war bereits in der Watchlist DB: " + clickedMovie.getTitle());
            Helpers.showToast(clickedMovie.getTitle() + " is already in the Watchlist");
        }
    } catch (DatabaseException e) {
        Helpers.showToast("Database Error: Could not add movie. " + e.getMessage());
        System.err.println("Failed to add movie to watchlist DB: " + e.getMessage());
        e.printStackTrace();
    }
};

private final ClickEventHandler<Movie> onRemoveFromWatchlistClicked = (clickedMovie) -> {
    // Prüfen, ob das Repository initialisiert wurde
    if (this.watchlistRepository == null) {
        Helpers.showToast("Watchlist feature is currently unavailable (DB connection failed).");
        return;
    }
    // Prüfen, ob das Movie-Objekt und seine ID gültig sind
    if (clickedMovie == null || clickedMovie.getId() == null) {
        Helpers.showToast("Cannot remove invalid movie from watchlist.");
         System.err.println("Error: Attempted to remove null movie or movie with null ID from watchlist.");
        return;
    }

    try {
        // Repository-Instanz verwenden und ID übergeben
        int result = watchlistRepository.removeFromWatchlist(clickedMovie.getId());
         if (result > 0) { // removeFromWatchlist gibt Anzahl gelöschter Zeilen zurück
             System.out.println(clickedMovie.getTitle() + " wurde aus der Watchlist DB entfernt!");
             Helpers.showToast(clickedMovie.getTitle() + " removed from Watchlist");
         } else {
             System.out.println(clickedMovie.getTitle() + " wurde nicht in der Watchlist DB gefunden.");
             // Optional: Toast anzeigen, dass Film nicht gefunden wurde
         }
        // Watchlist-Ansicht aktualisieren, nachdem entfernt wurde (oder versucht wurde)
        showWatchlist();
    } catch (DatabaseException e) {
        Helpers.showToast("Database Error: Could not remove movie. " + e.getMessage());
        System.err.println("Failed to remove movie from watchlist DB: " + e.getMessage());
        e.printStackTrace();
        // Eventuell Ansicht trotzdem aktualisieren?
         showWatchlist();
    }
};

    public void showHome() {
        try {
            // Filme laden (z.B. alle von der API für die Home-Ansicht)
            List<Movie> movies = MovieAPI.getAllMovies(null, null, null, null); // Beispiel: Alle Filme
            setMovies(movies);
            setMovieList(movies);

            // WICHTIG: CellFactory für Home-Modus setzen (Add-Button)
            movieListView.setCellFactory(listView -> new MovieCell(onAddToWatchlistClicked, false));

            sortMovies(sortedState); // Sortierung anwenden
            System.out.println("DEBUG: Showing Home View");
        } catch (MovieApiException e) {
            Helpers.showToast("API Error loading movies for Home: " + e.getMessage());
            System.err.println("Fehler beim Laden der Filme für Home: " + e.getMessage());
            // TODO: Lade aus DB Cache (MovieRepository)
            setMovieList(new ArrayList<>()); // Leere Liste bei Fehler
        }
    }

   public void showWatchlist() {
    // Prüfen, ob das Repository initialisiert wurde
    if (this.watchlistRepository == null) {
        Helpers.showToast("Watchlist feature is currently unavailable (DB connection failed).");
        setMovieList(new ArrayList<>()); // Leere Liste anzeigen
        // Setze CellFactory trotzdem, um Fehler zu vermeiden, falls Liste später gefüllt wird
        movieListView.setCellFactory(listView -> new MovieCell(onRemoveFromWatchlistClicked, true));
        return;
    }

    List<Movie> watchlistMovies = new ArrayList<>();
    try {
        // 1. Entities aus der DB holen
        List<WatchlistMovieEntity> watchlistEntities = watchlistRepository.getWatchlist();

        // 2. API-IDs extrahieren
        List<String> movieApiIds = watchlistEntities.stream()
                .map(WatchlistMovieEntity::getApiId)
                .collect(Collectors.toList());

        // 3. Entsprechende Movie-Objekte finden (Temporäre Lösung!)
        if (!movieApiIds.isEmpty()) {
            // Annahme: allMovies enthält die notwendigen Details.
            // Dies ist NICHT robust und sollte durch MovieRepository ersetzt werden!
            if (this.allMovies != null && !this.allMovies.isEmpty()) {
                watchlistMovies = this.allMovies.stream()
                        .filter(movie -> movie != null && movieApiIds.contains(movie.getId()))
                        .collect(Collectors.toList());
                System.out.println("DEBUG: Loaded " + watchlistMovies.size() + " watchlist movies by filtering allMovies.");
            } else {
                // Fallback, wenn allMovies leer ist
                System.err.println("WARN: 'allMovies' is empty or null in showWatchlist. Cannot display details.");
                Helpers.showToast("Could not load watchlist details (source list empty).");
                // Hier könnte man versuchen, die Filme einzeln per API zu laden (ineffizient)
                // oder einfach eine leere Liste anzeigen.
            }
        } else {
             System.out.println("DEBUG: Watchlist DB is empty.");
        }

    } catch (DatabaseException e) {
        Helpers.showToast("Database Error: Could not load watchlist. " + e.getMessage());
        System.err.println("Failed to load watchlist from DB: " + e.getMessage());
        e.printStackTrace();
        watchlistMovies = new ArrayList<>(); // Leere Liste bei DB-Fehler
    } catch (Exception e) {
        // Unerwartete Fehler beim Filtern abfangen
        Helpers.showToast("Error processing watchlist data: " + e.getMessage());
        System.err.println("Unexpected error processing watchlist: " + e.getMessage());
        e.printStackTrace();
        watchlistMovies = new ArrayList<>();
    }

    // 4. UI aktualisieren
    // CellFactory für Watchlist-Modus setzen (Remove-Button)
    movieListView.setCellFactory(listView -> new MovieCell(onRemoveFromWatchlistClicked, true));
    // Liste im UI setzen
    setMovieList(watchlistMovies);
    // Sortierung anwenden
    sortMovies(sortedState);
    System.out.println("DEBUG: Showing Watchlist View");
}


}





