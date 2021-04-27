package com.example.letscook.database.relationships;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface UserViewedRecipeCrossRefDao {
    @Transaction
    @Query("SELECT * FROM user")
    public List<UserViewsRecipes> getUserViewedRecipes();
}
