package hr.franjkovic.ivan.myway.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface TrackDao {
    @Query("SELECT * FROM tracks ORDER BY trackId DESC")
    List<Track> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Track track);

    @Insert
    void insertAll(Track... tracks);

    @Delete
    void delete(Track track);

    @Query("DELETE FROM tracks")
    void deleteAllData();
}
