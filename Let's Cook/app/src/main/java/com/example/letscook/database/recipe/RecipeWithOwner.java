package com.example.letscook.database.recipe;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.letscook.database.user.User;

import java.util.List;

// One-to-many relationship
public class RecipeWithOwner {
    @Embedded
    public User user;
    @Relation(
            parentColumn = "user_id",
            entityColumn = "owner_id"
    )
    public List<Recipe> recipeList;
}
