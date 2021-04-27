package com.example.letscook.database.relationships;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.letscook.database.recipe.Recipe;
import com.example.letscook.database.user.User;

import java.util.List;

public class UserViewsRecipes {
    @Embedded
    public User user;
    @Relation(
            parentColumn = "user_id",
            entityColumn = "recipe_id",
            associateBy = @Junction(UserViewedRecipeCrossRef.class)
    )
    public List<Recipe> recipeList;
}
