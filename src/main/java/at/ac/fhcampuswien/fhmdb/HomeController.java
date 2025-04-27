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
        // === Initialize Repository ===
        try {
            this.watchlistRepository = new WatchlistRepository();
            System.out.println("DEBUG: WatchlistRepository initialized in HomeController."); // Optional
        } catch (DatabaseException e) {
            // Display error and set Repository to null
            Helpers.showToast("Database Error: Could not initialize Watchlist. " + e.getMessage());
            System.err.println("ERROR initializing WatchlistRepository: " + e.getMessage());
            e.printStackTrace();
            this.watchlistRepository = null;
        }

        // Existing initialization calls
        initializeState();
        initializeLayout();
        showHome(); // Start with the Home view
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

        System.out.println("getMoviesBetweenYears");
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
        if(movies != null) { // Check if movies is null before adding
            observableMovies.addAll(movies);
        }
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

        if (allMovies == null) { // Handle case where allMovies might be null
             setMovieList(new ArrayList<>()); // Set to empty list
             return;
        }

        if (!searchQuery.isEmpty()) {
            filteredMovies = filterByQuery(filteredMovies, searchQuery);
        }

        if (genre != null && !genre.toString().equals("No filter")) {
            filteredMovies = filterByGenre(filteredMovies, Genre.valueOf(genre.toString()));
        }

        setMovieList(filteredMovies); // Update the observable list
    }


    public void searchBtnClicked(ActionEvent actionEvent) {
        String searchQuery = searchField.getText().trim().toLowerCase();
        String releaseYear = validateComboboxValue(releaseYearComboBox.getSelectionModel().getSelectedItem());
        String ratingFrom = validateComboboxValue(ratingFromComboBox.getSelectionModel().getSelectedItem());
        String genreValue = validateComboboxValue(genreComboBox.getSelectionModel().getSelectedItem());

        Genre genre = null;
        if (genreValue != null) {
            try { // Add try-catch for potential IllegalArgumentException
                genre = Genre.valueOf(genreValue);
            } catch (IllegalArgumentException e) {
                Helpers.showToast("Invalid Genre selected.");
                // Handle the error, maybe reset genre or return
                genre = null;
            }
        }
        List<Movie> movies = null;
        try {
            movies = getMovies(searchQuery, genre, releaseYear, ratingFrom);
        } catch (MovieApiException e) {
            Helpers.showToast(e.getMessage() + ", movies were loaded from the cache");
            //TODO Load movies from DB Cache
            // For now, set movies to an empty list or handle appropriately
            movies = new ArrayList<>(); // Example: Set to empty list
        }

        setMovies(movies); // Update the 'allMovies' list
        setMovieList(movies); // Update the 'observableMovies' list for the UI

        // applyAllFilters is redundant here because getMovies already applies filters via API
        // applyAllFilters(searchQuery, genre);

        if (sortedState != SortedState.NONE) { // Apply sorting if it was previously set
             sortMovies(sortedState);
        }
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
        if (movies == null || movies.isEmpty()) return ""; // Handle empty or null list

        return movies.stream()
                .filter(Objects::nonNull) // Ensure movie objects are not null
                .map(Movie::getMainCast)
                .filter(Objects::nonNull) // Ensure mainCast list is not null
                .flatMap(List::stream)    // Flatten the lists of actors
                .filter(Objects::nonNull) // Ensure actor strings are not null
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())) // Group by actor and count occurrences
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue()) // Find the entry with the highest count
                .map(Map.Entry::getKey)           // Get the actor's name (key)
                .orElse("");                      // Return empty string if no actors found
    }


    public int getLongestMovieTitle(List<Movie> movies) {
         if (movies == null) return 0; // Handle null list
         return movies.stream()
                 .filter(Objects::nonNull) // Ensure movie objects are not null
                 .map(Movie::getTitle)
                 .filter(Objects::nonNull) // Ensure title is not null
                 .mapToInt(String::length) // Map titles to their lengths
                 .max()                    // Find the maximum length
                 .orElse(0);               // Return 0 if the stream is empty
     }


    public long countMoviesFrom(List<Movie> movies, String director) {
        if (movies == null || director == null) return 0; // Handle null input
        return movies.stream()
                .filter(Objects::nonNull) // Ensure movie objects are not null
                .filter(movie -> movie.getDirectors() != null && movie.getDirectors().contains(director)) // Check non-null directors list and presence of the director
                .count(); // Count the matching movies
    }


    public List<Movie> getMoviesBetweenYears(List<Movie> movies, int startYear, int endYear) {
        if (movies == null) return new ArrayList<>(); // Handle null list
        return movies.stream()
                .filter(Objects::nonNull) // Ensure movie objects are not null
                .filter(movie -> movie.getReleaseYear() >= startYear && movie.getReleaseYear() <= endYear) // Filter by release year range
                .collect(Collectors.toList()); // Collect the results into a new list
    }


    private final ClickEventHandler<Movie> onAddToWatchlistClicked = (clickedMovie) -> {
        // Check if the repository has been initialized
        if (this.watchlistRepository == null) {
            Helpers.showToast("Watchlist feature is currently unavailable (DB connection failed).");
            return;
        }
        // Check if the Movie object and its ID are valid
        if (clickedMovie == null || clickedMovie.getId() == null) {
            Helpers.showToast("Cannot add invalid movie to watchlist.");
            System.err.println("Error: Attempted to add null movie or movie with null ID to watchlist.");
            return;
        }

        try {
            // Create WatchlistMovieEntity with the movie's ID
            WatchlistMovieEntity entity = new WatchlistMovieEntity(clickedMovie.getId());
            // Use Repository instance
            int result = watchlistRepository.addToWatchlist(entity);
            if (result > 0) { // addToWatchlist returns 1 if added, 0 if already present
                System.out.println("Movie added to Watchlist DB: " + clickedMovie.getTitle());
                Helpers.showToast(clickedMovie.getTitle() + " added to Watchlist");
            } else {
                System.out.println("Movie was already in the Watchlist DB: " + clickedMovie.getTitle());
                Helpers.showToast(clickedMovie.getTitle() + " is already in the Watchlist");
            }
        } catch (DatabaseException e) {
            Helpers.showToast("Database Error: Could not add movie. " + e.getMessage());
            System.err.println("Failed to add movie to watchlist DB: " + e.getMessage());
            e.printStackTrace();
        }
    };

    private final ClickEventHandler<Movie> onRemoveFromWatchlistClicked = (clickedMovie) -> {
        // Check if the repository has been initialized
        if (this.watchlistRepository == null) {
            Helpers.showToast("Watchlist feature is currently unavailable (DB connection failed).");
            return;
        }
        // Check if the Movie object and its ID are valid
        if (clickedMovie == null || clickedMovie.getId() == null) {
            Helpers.showToast("Cannot remove invalid movie from watchlist.");
            System.err.println("Error: Attempted to remove null movie or movie with null ID from watchlist.");
            return;
        }

        try {
            // Use Repository instance and pass the ID
            int result = watchlistRepository.removeFromWatchlist(clickedMovie.getId());
            if (result > 0) { // removeFromWatchlist returns the number of deleted rows
                System.out.println(clickedMovie.getTitle() + " was removed from the Watchlist DB!");
                Helpers.showToast(clickedMovie.getTitle() + " removed from Watchlist");
            } else {
                System.out.println(clickedMovie.getTitle() + " was not found in the Watchlist DB.");

            }
            // Update Watchlist view after removal (or attempted removal)
            showWatchlist();
        } catch (DatabaseException e) {
            Helpers.showToast("Database Error: Could not remove movie. " + e.getMessage());
            System.err.println("Failed to remove movie from watchlist DB: " + e.getMessage());
            e.printStackTrace();
            showWatchlist();
        }
    };

    public void showHome() {
        try {
            // Load movies (e.g., all from the API for the Home view)
            List<Movie> movies = MovieAPI.getAllMovies(null, null, null, null); // Example: All movies
            setMovies(movies);
            setMovieList(movies);

            // IMPORTANT: Set CellFactory for Home mode (Add button)
            movieListView.setCellFactory(listView -> new MovieCell(onAddToWatchlistClicked, false));

            if (sortedState != SortedState.NONE) { // Apply sorting if it was previously set
                sortMovies(sortedState);
            } else {
                 observableMovies.sort(Comparator.comparing(Movie::getTitle)); // Default ASC sort maybe?
                 sortedState = SortedState.ASCENDING; // Set state if default sort applied
            }
            System.out.println("DEBUG: Showing Home View");
        } catch (MovieApiException e) {
            Helpers.showToast("API Error loading movies for Home: " + e.getMessage());
            System.err.println("Error loading movies for Home: " + e.getMessage());
            // TODO: Load from DB Cache (MovieRepository) - Implement fallback logic
            setMovieList(new ArrayList<>()); // Empty list on error
        }
    }

    public void showWatchlist() {
        // Check if the repository has been initialized
        if (this.watchlistRepository == null) {
            Helpers.showToast("Watchlist feature is currently unavailable (DB connection failed).");
            setMovieList(new ArrayList<>()); // Show empty list
            // Set CellFactory anyway to avoid errors if the list is populated later
            movieListView.setCellFactory(listView -> new MovieCell(onRemoveFromWatchlistClicked, true));
            return;
        }

        List<Movie> watchlistMovies = new ArrayList<>();
        try {
            // 1. fetch  entities from the DB
            List<WatchlistMovieEntity> watchlistEntities = watchlistRepository.getWatchlist();

            // 2. extract API IDs
            List<String> movieApiIds = watchlistEntities.stream()
                    .map(WatchlistMovieEntity::getApiId)
                    .collect(Collectors.toList());

            // 3. find corresponding Movie objects
            if (!movieApiIds.isEmpty()) {
                List<Movie> currentAllMovies = MovieAPI.getAllMovies(); // Fetch all movies again

                if (currentAllMovies != null && !currentAllMovies.isEmpty()) {
                    watchlistMovies = currentAllMovies.stream()
                            .filter(movie -> movie != null && movie.getId() != null && movieApiIds.contains(movie.getId()))
                            .collect(Collectors.toList());
                    System.out.println("DEBUG: Loaded " + watchlistMovies.size() + " watchlist movies by filtering current API movies.");
                } else {
                    // Fallback if API fetch fails or returns empty
                    System.err.println("WARN: Could not fetch movie details for watchlist IDs.");
                    Helpers.showToast("Could not load watchlist details (source list empty or fetch failed).");

                }
            } else {
                System.out.println("DEBUG: Watchlist DB is empty.");
            }

        } catch (DatabaseException e) {
            Helpers.showToast("Database Error: Could not load watchlist. " + e.getMessage());
            System.err.println("Failed to load watchlist from DB: " + e.getMessage());
            e.printStackTrace();
            watchlistMovies = new ArrayList<>(); // Empty list on DB error
        } catch (MovieApiException e) { // Catch API exception from fetching all movies
             Helpers.showToast("API Error: Could not fetch movie details for watchlist. " + e.getMessage());
             System.err.println("Failed to fetch all movies for watchlist details: " + e.getMessage());
             watchlistMovies = new ArrayList<>(); // Empty list on API error
        } catch (Exception e) {
            // Catch unexpected errors during filtering/processing
            Helpers.showToast("Error processing watchlist data: " + e.getMessage());
            System.err.println("Unexpected error processing watchlist: " + e.getMessage());
            e.printStackTrace();
            watchlistMovies = new ArrayList<>();
        }

        // 4. Update UI
        movieListView.setCellFactory(listView -> new MovieCell(onRemoveFromWatchlistClicked, true));
        // Set list in UI
        setMovieList(watchlistMovies);
        // Apply sorting
        if (sortedState != SortedState.NONE) { // Apply sorting if it was previously set
            sortMovies(sortedState);
        } else {
            // Optional: Apply default sort for watchlist view
            observableMovies.sort(Comparator.comparing(Movie::getTitle)); // Default ASC sort maybe?
            sortedState = SortedState.ASCENDING; // Set state if default sort applied
        }
        System.out.println("DEBUG: Showing Watchlist View");
    }


}