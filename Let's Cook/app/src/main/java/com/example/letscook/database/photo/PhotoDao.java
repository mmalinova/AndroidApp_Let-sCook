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
    @Query("SELECT * FROM photo WHERE photo_MySQL_id = :serverID")
    Photo getPhotoByServerID(long serverID);
    @Query("SELECT * FROM photo WHERE recipe_id = :ID")
    Photo getPhotoByRecipeID(long ID);
    @Query("SELECT * FROM photo WHERE recipe_id = :sRecipe_id OR recipe_id = :ID")
    List<Photo> getAllPhotosFromRecipe(long sRecipe_id, long ID);
    @Query("SELECT * FROM photo WHERE is_sync = 0")
    List<Photo> getAllUnSyncPhotosFromRecipe();
    @Query("UPDATE photo SET is_sync = 1 WHERE photo_id = :sID")
    void photoSync(long sID);
    @Query("UPDATE photo SET photo_MySQL_id = :serverID WHERE photo_id = :sID")
    void setServerID(long sID, long serverID);
    @Query("UPDATE photo SET recipe_id = :serverID WHERE photo_id = :sID")
    void setOwnerID(long sID, long serverID);
}
