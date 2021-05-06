package com.example.letscook.database.relationships;

import androidx.room.Entity;
import androidx.room.Index;


import java.io.Serializable;
@Entity(primaryKeys = {"user_id", "recipe_id"}, tableName = "user_views_recipes",
        indices = {@Index("recipe_id")})
public class UserViewedRecipeCrossRef implements Serializable {
    public long user_id;
    public long recipe_id;
}
