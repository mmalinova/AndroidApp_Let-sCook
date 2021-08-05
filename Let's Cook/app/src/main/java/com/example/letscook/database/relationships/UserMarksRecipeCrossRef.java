package com.example.letscook.database.relationships;

import androidx.room.Entity;
import androidx.room.Index;

import java.io.Serializable;

@Entity(primaryKeys = {"user_id", "recipe_id"}, tableName = "user_marks_recipes",
        indices = {@Index("recipe_id")})
public class UserMarksRecipeCrossRef implements Serializable {
    public long user_id;
    public long recipe_id;

    public UserMarksRecipeCrossRef(long user_id, long recipe_id) {
        this.user_id = user_id;
        this.recipe_id = recipe_id;
    }
}
