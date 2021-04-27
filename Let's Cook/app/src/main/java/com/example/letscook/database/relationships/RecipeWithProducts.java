package com.example.letscook.database.relationships;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.letscook.database.product.Product;
import com.example.letscook.database.recipe.Recipe;

import java.util.List;

public class RecipeWithProducts {
    @Embedded
    public Recipe recipe;
    @Relation(
            parentColumn = "recipe_id",
            entityColumn = "product_id",
            associateBy = @Junction(RecipeProductCrossRef.class)
    )
    public List<Product> productList;
}
