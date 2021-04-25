package com.example.letscook.database.relationships;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface UserMarksRecipeCrossRefDao {
    @Transaction
    @Query("SELECT * FROM recipe")
    public List<UserMarksRecipeCrossRef.UserMarksRecipes> getUserFavoriteRecipes();
}
