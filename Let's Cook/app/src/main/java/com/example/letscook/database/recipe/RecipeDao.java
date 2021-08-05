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
    public List<Recipe> getAllLastAddedRecipes();

    @Query("SELECT * FROM recipe WHERE recipe_id = :sID")
    public Recipe getRecipeById(long sID);

    @Query("SELECT * FROM recipe WHERE name = :sName")
    public Recipe getRecipeByName(String sName);

    @Query("SELECT * FROM recipe WHERE name LIKE '%' || :sName || '%'")
    public List<Recipe> getAllRecipeByName(String sName);

    @Query("SELECT * FROM recipe WHERE category LIKE '%' || :sCategory || '%' AND vegetarian = :sVeg")
    public List<Recipe> getAllRecipeByCategoryAndVeg(String sCategory, int sVeg);

    @Query("SELECT * FROM recipe WHERE owner_id = :sID")
    public List<Recipe> getRecipesByOwnerId(long sID);

    @Transaction
    @Query("SELECT * FROM user")
    public List<RecipeWithOwner> getRecipesWithOwners();
}
