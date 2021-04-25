package com.example.letscook.database.relationships;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.letscook.database.product.Product;
import com.example.letscook.database.recipe.Recipe;

import java.io.Serializable;
import java.util.List;

@Entity(primaryKeys = {"recipe_id", "product_id"}, tableName = "recipe_has_products")
public class RecipeProductCrossRef implements Serializable {
    public long recipe_id;
    public long product_id;

    public class RecipeHasProducts {
        @Embedded
        public Recipe recipe;
        @Relation(
                parentColumn = "recipe_id",
                entityColumn = "product_id",
                associateBy = @Junction(RecipeProductCrossRef.class)
        )
        public List<Product> productList;
    }
}
