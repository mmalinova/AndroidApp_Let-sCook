package com.example.letscook.database.recipe;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface RecipeDao {
    @Insert(onConflict = REPLACE)
    void insert(Recipe recipe);

    @Delete
    void delete(Recipe recipe);

    @Query("SELECT * FROM recipe ORDER BY created_on LIMIT 10")
    List<Recipe> getAllLastAddedRecipes();

    @Query("SELECT * FROM recipe WHERE recipe_id = :sID")
    Recipe getRecipeById(long sID);

    @Query("SELECT * FROM recipe WHERE name = :sName")
    Recipe getRecipeByName(String sName);

    @Query("SELECT * FROM recipe WHERE name LIKE '%' || :sName || '%'")
    List<Recipe> getAllRecipeByName(String sName);

    @Query("SELECT * FROM recipe WHERE category LIKE '%' || :sCategory || '%' AND vegetarian = :sVeg")
    List<Recipe> getAllRecipeByCategoryAndVeg(String sCategory, int sVeg);

    @Query("SELECT * FROM recipe WHERE owner_id = :sID")
    List<Recipe> getRecipesByOwnerId(long sID);
}
