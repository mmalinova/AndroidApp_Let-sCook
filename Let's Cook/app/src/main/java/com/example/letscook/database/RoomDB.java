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
import com.example.letscook.database.relationships.RecipeProductCrossRef;
import com.example.letscook.database.relationships.RecipeProductCrossRefDao;
import com.example.letscook.database.relationships.UserMarksRecipeCrossRef;
import com.example.letscook.database.relationships.UserMarksRecipeCrossRefDao;
import com.example.letscook.database.relationships.UserViewedRecipeCrossRef;
import com.example.letscook.database.relationships.UserViewedRecipeCrossRefDao;
import com.example.letscook.database.session.Session;
import com.example.letscook.database.session.SessionDao;
import com.example.letscook.database.typeconverters.ConvertArrayList;
import com.example.letscook.database.typeconverters.ConvertDate;
import com.example.letscook.database.user.User;
import com.example.letscook.database.user.UserDao;

// Create database
@Database(entities = {Product.class, Recipe.class, Session.class, User.class, Photo.class,
        RecipeProductCrossRef.class, UserMarksRecipeCrossRef.class, UserViewedRecipeCrossRef.class},
        version = 2, exportSchema = false)
@TypeConverters({ConvertDate.class, ConvertArrayList.class})
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
    public abstract SessionDao sessionDao();
    public abstract UserDao userDao();
    public abstract PhotoDao photoDao();
    public abstract RecipeProductCrossRefDao recipeProductCrossRefDao();
    public abstract UserMarksRecipeCrossRefDao userMarksRecipeCrossRefDao();
    public abstract UserViewedRecipeCrossRefDao userViewedRecipeCrossRefDao();
}
