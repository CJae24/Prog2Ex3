// src/main/java/at/ac/fhcampuswien/fhmdb/database/MovieRepository.java
package at.ac.fhcampuswien.fhmdb.database;

import at.ac.fhcampuswien.fhmdb.exceptions.DatabaseException;
import at.ac.fhcampuswien.fhmdb.models.Movie;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<Movie> getMovies(String searchQuery,
                                 String genre,
                                 String releaseYear,
                                 String ratingFrom) throws DatabaseException {
        try {
            // 0. Wenn keine Filter gesetzt sind: alle Movies zurückgeben
            boolean noFilters = (searchQuery == null || searchQuery.isBlank())
                    && genre == null
                    && releaseYear == null
                    && ratingFrom == null;
            if (noFilters) {
                List<MovieEntity> allEntities = movieDao.queryForAll();
                return allEntities.stream()
                        .map(this::entityToDomain)
                        .collect(Collectors.toList());
            }

            // 1. QueryBuilder aufsetzen
            QueryBuilder<MovieEntity, Long> qb = movieDao.queryBuilder();
            Where<MovieEntity, Long> where = qb.where();
            boolean hasCondition = false;

            // 2. Suchtext in Titel oder Beschreibung
            if (searchQuery != null && !searchQuery.isBlank()) {
                where.like("title", "%" + searchQuery + "%")
                        .or()
                        .like("description", "%" + searchQuery + "%");
                hasCondition = true;
            }

            // 3. Genre-Filter (CSV-Feld)
            if (genre != null) {
                if (hasCondition) where.and();
                where.like("genres", "%" + genre + "%");
                hasCondition = true;
            }

            // 4. ReleaseYear-Filter
            if (releaseYear != null) {
                if (hasCondition) where.and();
                where.eq("releaseYear", releaseYear);
                hasCondition = true;
            }

            // 5. Mindest-Rating
            if (ratingFrom != null) {
                if (hasCondition) where.and();
                where.ge("rating", ratingFrom);
                hasCondition = true;
            }

            // 6. Abfrage ausführen
            List<MovieEntity> entities = hasCondition
                    ? qb.query()
                    : movieDao.queryForAll();

            // 7. Mapping Entity → Domain-Movie
            return entities.stream()
                    .map(this::entityToDomain)
                    .collect(Collectors.toList());

        } catch (SQLException e) {
            System.err.println("ERROR: Could not retrieve filtered movies from database.");
            e.printStackTrace();
            throw new DatabaseException("Error retrieving filtered movies from database", e);
        }
    }

    /**
     * Hilfsmethode, um eine MovieEntity in dein Domain-Objekt Movie zu transformieren.
     */
    private Movie entityToDomain(MovieEntity e) {
        return new Movie(
                e.getApiId(),                    // id
                e.getTitle(),                    // title
                e.getDescription(),              // description
                e.getGenresAsEnum(),             // genres
                e.getReleaseYear(),              // releaseYear
                e.getImgUrl(),                   // imgUrl
                e.getLengthInMinutes(),          // lengthInMinutes
                e.getRating()                    // rating
        );
    }



    public int addAll(List<MovieEntity> movies) throws DatabaseException {
        int count = 0;
        if (movies == null) {
            return 0;
        }
        try {
            for (MovieEntity movie : movies) {
                MovieEntity existing = getMovieByApiId(movie.getApiId());
                if (existing == null) {
                    // Movie does not exist -> create
                    movieDao.create(movie);
                    count++;
                    System.out.println("DEBUG: Created movie with apiId: " + movie.getApiId());
                } else {
                    // Movie exists -> update (optional, if desired)
                    // Assumption: Update if anything might have changed (except PK).
                    movie.setId(existing.getId()); // IMPORTANT: Take over ID from existing object!
                    movieDao.update(movie);
                    count++; // Count updates as well
                    System.out.println("DEBUG: Updated movie with apiId: " + movie.getApiId());
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not add/update movies in database.");
            e.printStackTrace();
            throw new DatabaseException("Error adding/updating movies to database", e);
        }
        return count;
    }


    public MovieEntity getMovieByApiId(String apiId) throws DatabaseException {
        try {
            QueryBuilder<MovieEntity, Long> queryBuilder = movieDao.queryBuilder();
            queryBuilder.where().eq("apiId", apiId); // Assumes the field in MovieEntity is named "apiId"
            return queryBuilder.queryForFirst();
        } catch (SQLException e) {
            System.err.println("ERROR: Could not retrieve movie by apiId '" + apiId + "' from database.");
            e.printStackTrace();
            throw new DatabaseException("Error retrieving movie by apiId from database", e);
        }
    }

    public List<MovieEntity> getMoviesByIds(List<String> apiIds) throws DatabaseException {
        if (apiIds == null || apiIds.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            QueryBuilder<MovieEntity, Long> queryBuilder = movieDao.queryBuilder();
            queryBuilder.where().in("apiId", apiIds);
            List<MovieEntity> result = queryBuilder.query();
            return result != null ? result : Collections.emptyList();
        } catch (SQLException e) {
            System.err.println("ERROR: Could not retrieve movies by list of apiIds from database.");
            e.printStackTrace();
            throw new DatabaseException("Error retrieving movies by apiIds from database", e);
        }
    }

}

