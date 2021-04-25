package com.example.letscook.database.recipe;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.letscook.database.recipe.Recipe;

import java.util.List;

@Dao
public interface RecipeDao {
    @Transaction
    @Query("SELECT * FROM recipe")
    public List<Recipe.RecipeHasOwner> getRecipesWithOwners();
}
