package com.example.lab09;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GeoDataDao {
    @Query("SELECT * FROM geodata")
    List<GeoData> getAll();

    @Insert
    void insertAll(GeoData... geoData);

    @Delete
    void delete(GeoData geoData);
}
