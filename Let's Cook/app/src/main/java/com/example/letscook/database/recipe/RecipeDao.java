package com.example.letscook.database.recipe;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface RecipeDao {
    @Insert(onConflict = REPLACE)
    void insert(Recipe recipe);

    @Delete
    void delete(Recipe recipe);

    @Query("SELECT * FROM recipe WHERE is_approved = 0 ORDER BY created_on")
    List<Recipe> getAllUnapprovedRecipes();

    @Query("UPDATE recipe SET is_approved = 1 WHERE recipe_id = :sID")
    void approveRecipeById(long sID);

    @Query("SELECT * FROM recipe WHERE is_approved = 1 ORDER BY created_on LIMIT 10")
    List<Recipe> getAllLastAddedRecipes();

    @Query("SELECT * FROM recipe WHERE recipe_id = :sID")
    Recipe getRecipeById(long sID);

    @Query("SELECT * FROM recipe WHERE name = :sName")
    Recipe getRecipeByName(String sName);

    @Query("SELECT * FROM recipe WHERE is_approved = 1 AND name LIKE '%' || :sName || '%'")
    List<Recipe> getAllRecipeByName(String sName);

    @Query("SELECT * FROM recipe WHERE is_approved = 1 AND category LIKE '%' || :sCategory || '%' AND vegetarian = :sVeg")
    List<Recipe> getAllRecipeByCategoryAndVeg(String sCategory, int sVeg);

    @Query("SELECT * FROM recipe WHERE owner_id = :sID")
    List<Recipe> getRecipesByOwnerId(long sID);
}
