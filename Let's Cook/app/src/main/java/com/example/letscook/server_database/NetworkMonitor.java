package com.example.letscook.server_database;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.example.letscook.database.RoomDB;
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
import com.example.letscook.database.user.User;
import com.example.letscook.database.user.UserDao;
import com.example.letscook.server_database.MySQLToSQLite.PhotosRequest;
import com.example.letscook.server_database.MySQLToSQLite.ProductsRequest;
import com.example.letscook.server_database.MySQLToSQLite.RecipesMarksRequest;
import com.example.letscook.server_database.MySQLToSQLite.RecipesRequest;
import com.example.letscook.server_database.MySQLToSQLite.RecipesViewsRequest;
import com.example.letscook.server_database.MySQLToSQLite.UsersRequest;
import com.example.letscook.server_database.SQLiteToMySQL.PhotoRequests;
import com.example.letscook.server_database.SQLiteToMySQL.ProductRequests;
import com.example.letscook.server_database.SQLiteToMySQL.RecipeMarksRequests;
import com.example.letscook.server_database.SQLiteToMySQL.RecipeRequests;
import com.example.letscook.server_database.SQLiteToMySQL.RecipeViewsRequests;
import com.example.letscook.server_database.SQLiteToMySQL.UserRequests;

import java.util.List;

public class NetworkMonitor extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        // Initialize db
        RoomDB database = RoomDB.getInstance(context);
        final ProductDao productDao = database.productDao();
        final RecipeDao recipeDao = database.recipeDao();
        final UserDao userDao = database.userDao();
        final PhotoDao photoDao = database.photoDao();
        final UserViewsRecipeDao userViewsRecipeDao = database.userViewsRecipeDao();
        final UserMarksRecipeDao userMarksRecipeDao = database.userMarksRecipeDao();

        MySQLToSQLiteSync(context, database, productDao, recipeDao, userDao, photoDao, userViewsRecipeDao, userMarksRecipeDao);
        SQLiteToMySQLSync(context, database, productDao, recipeDao, userDao, photoDao, userViewsRecipeDao, userMarksRecipeDao);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void MySQLToSQLiteSync(Context context, RoomDB database, ProductDao productDao, RecipeDao recipeDao, UserDao userDao, PhotoDao photoDao,
                                   UserViewsRecipeDao userViewsRecipeDao, UserMarksRecipeDao userMarksRecipeDao) {
        UsersRequest.userGET(context);
        PhotosRequest.photoGET(context);
        ProductsRequest.productGET(context);
        RecipesRequest.recipeGET(context);
        RecipesViewsRequest.viewsGET(context);
        RecipesMarksRequest.marksGET(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void SQLiteToMySQLSync(Context context, RoomDB database, ProductDao productDao, RecipeDao recipeDao, UserDao userDao, PhotoDao photoDao,
                                   UserViewsRecipeDao userViewsRecipeDao, UserMarksRecipeDao userMarksRecipeDao) {
        if (checkNetworkConnection(context)) {
            List<Product> unSyncProducts = productDao.getAllUnSyncProducts();
            List<Recipe> unSyncRecipes = recipeDao.getAllUnSyncRecipes();
            List<User> unSyncUsers = userDao.getAllUnSyncUsers();
            List<Photo> unSyncPhotosFromRecipe = photoDao.getAllUnSyncPhotosFromRecipe();
            List<UserViewsRecipeCrossRef> allUnSyncViews = userViewsRecipeDao.getAllUnSyncViews();
            List<UserMarksRecipeCrossRef> allUnSyncMarks = userMarksRecipeDao.getAllUnSyncMarks();

            for (User user : unSyncUsers) {
                UserRequests.userPOST(context, user);
            }

            for (Recipe recipe : unSyncRecipes) {
                User userByServerID = database.userDao().getUserByServerID(recipe.getOwnerID());
                RecipeRequests.recipePOST(context, recipe, userByServerID);
            }

            int index = -1;
            long oldId = 0;
            for (Photo photo : unSyncPhotosFromRecipe) {
                index++;
                Recipe recipeById = database.recipeDao().getRecipeById(photo.getRecipeId());
                if (recipeById != null && recipeById.getServerID() > 0) {
                    if (oldId != recipeById.getServerID()) {
                        index = 0;
                    }
                    oldId = recipeById.getServerID();
                    PhotoRequests.photoPOST(context, photo, recipeById, index);
                    photo.setRecipeId(recipeById.getServerID());
                    database.photoDao().insert(photo);
                }
            }

            long ldRecipeId = 0;
            long oldUserId = 0;
            int countForUser = 0;
            int countForRecipe = 0;
            for (Product product : unSyncProducts) {
                if (product.getBelonging().equals("toRecipe")) {
                    Recipe recipeById = database.recipeDao().getRecipeById(product.getOwnerId());
                    if (recipeById != null && recipeById.getServerID() > 0) {
                        countForRecipe++;
                        if (ldRecipeId != recipeById.getServerID()) {
                            countForRecipe = 0;
                        }
                        ldRecipeId = recipeById.getServerID();
                        ProductRequests.productPOST(context, product, null, recipeById, countForRecipe);
                        product.setOwnerId(recipeById.getServerID());
                        database.productDao().insert(product);
                    }
                } else if (product.getBelonging().equals("deleted"))     {
                    ProductRequests.productDELETE(context, product);
                } else {
                    User userById = database.userDao().getUserByID(product.getOwnerId());
                    if (userById != null && userById.getServerID() > 0) {
                        countForUser++;
                        if (oldUserId != userById.getServerID()) {
                            countForUser = 0;
                        }
                        oldUserId = userById.getServerID();
                        ProductRequests.productPOST(context, product, userById, null, countForUser);
                        product.setOwnerId(userById.getServerID());
                        database.productDao().insert(product);
                    }
                }
            }

            for (UserViewsRecipeCrossRef userViewsRecipeCrossRef : allUnSyncViews) {
                User userByID = database.userDao().getUserByID(userViewsRecipeCrossRef.getUser_id());
                Recipe recipeById = database.recipeDao().getRecipeById(userViewsRecipeCrossRef.getRecipe_id());
                if (userByID != null && userByID.getServerID() > 0 && recipeById != null && recipeById.getServerID() > 0) {
                    database.userViewsRecipeDao().setUserID(userByID.getServerID(), userViewsRecipeCrossRef.getUser_id(), userViewsRecipeCrossRef.getRecipe_id());
                    database.userViewsRecipeDao().setRecipeID(recipeById.getServerID(), userByID.getServerID(), userViewsRecipeCrossRef.getRecipe_id());
                    UserViewsRecipeCrossRef byUserIDAndRecipeID = database.userViewsRecipeDao().getByUserIDAndRecipeID(userByID.getServerID(), recipeById.getServerID());
                    RecipeViewsRequests.viewsPOST(context, byUserIDAndRecipeID);
                }
            }

            for (UserMarksRecipeCrossRef userMarksRecipeCrossRef : allUnSyncMarks) {
                User userByID = database.userDao().getUserByID(userMarksRecipeCrossRef.getUser_id());
                Recipe recipeById = database.recipeDao().getRecipeById(userMarksRecipeCrossRef.getRecipe_id());
                if (userByID != null && userByID.getServerID() > 0 && recipeById != null && recipeById.getServerID() > 0) {
                    database.userMarksRecipeDao().setUserID(userByID.getServerID(), userMarksRecipeCrossRef.getUser_id(), userMarksRecipeCrossRef.getRecipe_id());
                    database.userMarksRecipeDao().setRecipeID(recipeById.getServerID(), userByID.getServerID(), userMarksRecipeCrossRef.getRecipe_id());
                    UserMarksRecipeCrossRef byUserIDAndRecipeID = database.userMarksRecipeDao().getByUserIDAndRecipeID(userByID.getServerID(), recipeById.getServerID());
                    if (byUserIDAndRecipeID.isDeleted()) {
                        RecipeMarksRequests.marksDELETE(context, byUserIDAndRecipeID);
                    } else if (!byUserIDAndRecipeID.isDeleted()) {
                        RecipeMarksRequests.marksPOST(context, byUserIDAndRecipeID);
                    }
                }
            }
        }
    }

    public static boolean checkNetworkConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
