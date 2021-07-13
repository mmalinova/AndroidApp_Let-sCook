package com.example.letscook.database.photo;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PhotoDao {
    @Insert(onConflict = REPLACE)
    void insert(Photo photo);

    @Delete
    void delete(Photo photo);

    @Delete
    void deleteAll(List<Photo> photos);

    @Query("SELECT * FROM photo WHERE recipe_id = :sRecipe_id")
    public List<Photo> getAllPhotosFromRecipe(long sRecipe_id);
}
