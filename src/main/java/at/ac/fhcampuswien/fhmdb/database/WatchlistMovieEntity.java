package at.ac.fhcampuswien.fhmdb.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "watchlist")
public class WatchlistMovieEntity {


    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(unique = true)
    private String apiId;

    public WatchlistMovieEntity() {
    }

    
}