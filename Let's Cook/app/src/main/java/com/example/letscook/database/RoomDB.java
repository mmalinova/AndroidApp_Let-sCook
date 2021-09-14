package com.example.letscook.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.letscook.database.photo.Photo;
import com.example.letscook.database.photo.PhotoDao;
import com.example.letscook.database.product.Product;
import com.example.letscook.database.product.ProductDao;
import com.example.letscook.database.recipe.Recipe;
import com.example.letscook.database.recipe.RecipeDao;
import com.example.letscook.database.relationships.UserMarksRecipeCrossRef;
import com.example.letscook.database.relationships.UserMarksRecipeDao;
import com.example.letscook.database.relationships.UserViewsRecipeCrossRef;
import com.example.letscook.database.relationships.UserViewsRecipeDao;
import com.example.letscook.database.typeconverters.ConvertDate;
import com.example.letscook.database.user.User;
import com.example.letscook.database.user.UserDao;

// Create database
@Database(entities = {Product.class, Recipe.class, User.class, Photo.class,
        UserMarksRecipeCrossRef.class, UserViewsRecipeCrossRef.class},
        version = 1, exportSchema = false)
@TypeConverters({ConvertDate.class})
public abstract class RoomDB extends RoomDatabase {
    // Create db instance
    private static RoomDB database;
    // Define db name
    private static final String DATABASE_NAME = "LetsCookDB";

    public synchronized static RoomDB getInstance(Context context) {
        if (database == null) {
            // Initialize db
            database = Room.databaseBuilder(context.getApplicationContext(), RoomDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }
    // DAO
    public abstract ProductDao productDao();
    public abstract RecipeDao recipeDao();
    public abstract UserDao userDao();
    public abstract PhotoDao photoDao();
    public abstract UserViewsRecipeDao userViewsRecipeDao();
    public abstract UserMarksRecipeDao userMarksRecipeDao();
}