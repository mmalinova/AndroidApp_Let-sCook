package com.example.letscook.database.relationships;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.letscook.database.recipe.Recipe;
import com.example.letscook.database.user.User;

import java.io.Serializable;
import java.util.List;

@Entity(primaryKeys = {"user_id", "recipe_id"}, tableName = "user_views_recipes",
        indices = {@Index("recipe_id")})
public class UserViewedRecipeCrossRef implements Serializable {
    public long user_id;
    public long recipe_id;
}
