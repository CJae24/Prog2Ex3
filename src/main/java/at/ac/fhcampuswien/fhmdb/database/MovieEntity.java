package at.ac.fhcampuswien.fhmdb.database;
import at.ac.fhcampuswien.fhmdb.models.Genre;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "movie")
public class MovieEntity {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(unique = true)
    private String apiId;

    @DatabaseField
    private String title;

    @DatabaseField
    private String description;

    @DatabaseField
    private String genres;

    @DatabaseField
    private int releaseYear;

    @DatabaseField
    private String imgUrl;

    @DatabaseField
    private int lengthInMinutes;

    @DatabaseField
    private double rating;

    public MovieEntity(String apiId, String title, String description, List<Genre> genres, String genresAsString,
            int releaseYear, String imgUrl, int lengthInMinutes, double rating) {
        this.apiId = apiId;
        this.title = title;
        this.description = description;
        this.genres = genresToString(genres);
        this.releaseYear = releaseYear;
        this.imgUrl = imgUrl;
        this.lengthInMinutes = lengthInMinutes;
        this.rating = rating;
    }

    // helper method -> converts list of Genre to comma separated string
    private static String genresToString(List<Genre> genres) {
    if (genres == null || genres.isEmpty()) {
        return "";
    }
    return genres.stream()
            .map(Genre::name)
            .collect(Collectors.joining(","));
    }

    // helper method -> converts comma separated string BACK to a list of Genre
    public static List<Genre> stringToGenres(String genreString) {
    if (genreString == null || genreString.isEmpty()) {
        return List.of();
    }
    return Arrays.stream(genreString.split(","))
            .map(String::trim)
            .map(s -> {

                try {
                    return Genre.valueOf(s);
                } catch (IllegalArgumentException e) {

                    System.err.println("Couldnt parse genre '" + s + "'");
                    return null;
                }

            })

            .filter(Objects::nonNull)
            .collect(Collectors.toList());
}

}
