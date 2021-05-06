package com.example.letscook.database.relationships;

import androidx.room.Entity;

import java.io.Serializable;

@Entity(primaryKeys = {"user_id", "recipe_id"}, tableName = "user_marks_recipes")
public class UserMarksRecipeCrossRef implements Serializable {
    public long user_id;
    public long recipe_id;
}
