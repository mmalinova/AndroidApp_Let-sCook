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

    @Query("SELECT * FROM photo WHERE recipe_id = :sRecipe_id")
    List<Photo> getAllPhotosFromRecipe(long sRecipe_id);

    @Query("SELECT * FROM photo WHERE is_sync = 0")
    List<Photo> getAllUnSyncPhotosFromRecipe();

    @Query("UPDATE photo SET is_sync = 1 WHERE photo_id = :sID")
    void photoSync(long sID);
}
