package at.ac.fhcampuswien.fhmdb.api;

import at.ac.fhcampuswien.fhmdb.exceptions.MovieApiException;
import at.ac.fhcampuswien.fhmdb.models.Genre;
import at.ac.fhcampuswien.fhmdb.models.Movie;
import okhttp3.*;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.*;

public class MovieAPI {
    public static final String DELIMITER = "&";
    private static final String URL = "http://prog2.fh-campuswien.ac.at/movies";
    private static final OkHttpClient client = new OkHttpClient();

    private String buildUrl(UUID id) {
        StringBuilder url = new StringBuilder(URL);
        if (id != null) {
            url.append("/").append(id);
        }
        return url.toString();
    }

    private static String buildUrl(String query, Genre genre, String releaseYear, String ratingFrom) {
        StringBuilder url = new StringBuilder(URL);

        if ((query != null && !query.isEmpty()) || genre != null || releaseYear != null || ratingFrom != null) {
            url.append("?");
            if (query != null && !query.isEmpty()) url.append("query=").append(query).append(DELIMITER);
            if (genre != null) url.append("genre=").append(genre).append(DELIMITER);
            if (releaseYear != null) url.append("releaseYear=").append(releaseYear).append(DELIMITER);
            if (ratingFrom != null) url.append("ratingFrom=").append(ratingFrom).append(DELIMITER);
        }

        return url.toString();
    }

    public static List<Movie> getAllMovies() throws MovieApiException {
        return getAllMovies(null, null, null, null);
    }

    public static List<Movie> getAllMovies(String query, Genre genre, String releaseYear, String ratingFrom) throws MovieApiException {
        String url = buildUrl(query, genre, releaseYear, ratingFrom);
        Request request = new Request.Builder()
                .url(url)
                .removeHeader("User-Agent")
                .addHeader("User-Agent", "http.agent")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new MovieApiException("HTTP error code: " + response.code());
            }
            String responseBody = response.body().string();
            Movie[] movies = new Gson().fromJson(responseBody, Movie[].class);
            return Arrays.asList(movies);
        } catch (Exception e) {
            throw new MovieApiException("Failed to fetch or parse movie list.", e);
        }
    }

    public Movie requestMovieById(UUID id) throws MovieApiException {
        String url = buildUrl(id);
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new MovieApiException("HTTP error when requesting movie by ID: " + response.code());
            }
            assert response.body() != null;
            return new Gson().fromJson(response.body().string(), Movie.class);
        } catch (Exception e) {
            throw new MovieApiException("Failed to fetch or parse movie by ID.", e);
        }
    }
}
