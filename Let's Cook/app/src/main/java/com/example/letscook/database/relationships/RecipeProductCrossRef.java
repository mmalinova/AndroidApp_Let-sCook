package com.example.letscook.database.relationships;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.letscook.database.product.Product;
import com.example.letscook.database.recipe.Recipe;

import java.io.Serializable;
import java.util.List;

@Entity(primaryKeys = {"recipe_id", "product_id"}, tableName = "recipe_has_products",
        indices = {@Index("recipe_id"), @Index("product_id")})
public class RecipeProductCrossRef implements Serializable {
    public long recipe_id;
    public long product_id;
}
