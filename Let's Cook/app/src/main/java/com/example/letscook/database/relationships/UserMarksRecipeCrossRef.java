package com.example.letscook.database.relationships;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.letscook.database.recipe.Recipe;
import com.example.letscook.database.user.User;

import java.io.Serializable;
import java.util.List;

@Entity(primaryKeys = {"user_id", "recipe_id"}, tableName = "user_marks_recipes")
public class UserMarksRecipeCrossRef implements Serializable {
    public long user_id;
    public long recipe_id;

    public class UserMarksRecipes {
        @Embedded
        public User user;
        @Relation(
                parentColumn = "user_id",
                entityColumn = "recipe_id",
                associateBy = @Junction(UserViewedRecipeCrossRef.class)
        )
        public List<Recipe> recipeList;
    }
}
