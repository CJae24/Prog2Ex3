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

    // constructor
    public WatchlistMovieEntity(String apiId) {
        this.apiId = apiId;
    }


    // getter for Id and apiId
    public long getId() {
        return id;
    }

    public String getApiId() {
        return apiId;
    }
    
    // [temporär -> nur zum ausprobieren]
    public static void main(String[] args) {
        WatchlistMovieEntity entity1 = new WatchlistMovieEntity();
        System.out.println("Entity 1 (no-arg): ID=" + entity1.getId() + ", ApiID=" + entity1.getApiId());
        // expected Entity 1 (no-arg): ID=0, ApiID=null

        String apiIdFromMovie = "film-xyz-789";
        WatchlistMovieEntity entity2 = new WatchlistMovieEntity(apiIdFromMovie);
        System.out.println("Entity 2 (param): ID=" + entity2.getId() + ", ApiID=" + entity2.getApiId());
        // expected Entity 2 (param): ID=0, ApiID=film-xyz-789
    }

}