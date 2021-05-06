package com.example.letscook.database.relationships;

import androidx.room.Entity;
import androidx.room.Index;


import java.io.Serializable;

@Entity(primaryKeys = {"recipe_id", "product_id"}, tableName = "recipe_has_products",
        indices = {@Index("recipe_id"), @Index("product_id")})
public class RecipeProductCrossRef implements Serializable {
    public long recipe_id;
    public long product_id;
}
