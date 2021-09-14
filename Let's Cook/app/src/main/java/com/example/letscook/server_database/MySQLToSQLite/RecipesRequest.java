package com.example.letscook.server_database.MySQLToSQLite;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.letscook.database.RoomDB;
import com.example.letscook.database.photo.PhotoDao;
import com.example.letscook.database.product.Product;
import com.example.letscook.database.product.ProductDao;
import com.example.letscook.database.recipe.Recipe;
import com.example.letscook.database.recipe.RecipeDao;
import com.example.letscook.database.typeconverters.ConvertDate;
import com.example.letscook.server_database.MySingleton;
import com.example.letscook.server_database.NetworkMonitor;
import com.example.letscook.server_database.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;

import static com.example.letscook.server_database.URLs.*;

public class RecipesRequest {
    public static void recipeGET(Context context) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, RECIPES_URL,
                    new Response.Listener<String>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String stringResponse = jsonObject.getString("response");
                                if (stringResponse.equals("GET all entries!")) {
                                    JSONArray recipes = jsonObject.getJSONArray("recipes");
                                    for (int i = 0; i < recipes.length(); i++) {
                                        JSONObject recipe = recipes.getJSONObject(i);
                                        PhotoDao photoDao = RoomDB.getInstance(context).photoDao();
                                        ProductDao productDao = RoomDB.getInstance(context).productDao();
                                        RecipeDao recipeDao = RoomDB.getInstance(context).recipeDao();
                                        Recipe localRecipe = recipeDao.getRecipeByServerId(recipe.getLong("recipe_id"));
                                        if (localRecipe != null) {
                                            localRecipe.setName(recipe.getString("name"));
                                            localRecipe.setCategory(recipe.getString("category"));
                                            localRecipe.setVegetarian(recipe.getInt("vegetarian"));
                                            localRecipe.setImage(Base64.getDecoder().decode(recipe.getString("image")));
                                            localRecipe.setPortions(recipe.getInt("portions"));
                                            localRecipe.setSteps(recipe.getString("steps"));
                                            localRecipe.setHours(recipe.getInt("hours"));
                                            localRecipe.setMinutes(recipe.getInt("minutes"));
                                            localRecipe.setCreatedOn(ConvertDate.fromTimestamp(recipe.getLong("created_on")));
                                            localRecipe.setIsApproved(true);
                                            localRecipe.setSync(true);
                                            localRecipe.setOwnerID(recipe.getLong("owner_id"));
                                            localRecipe.setServerID(recipe.getLong("recipe_id"));
                                            recipeDao.insert(localRecipe);
                                        } else if (photoDao.getPhotoByRecipeID(recipe.getLong("recipe_id")) != null
                                                && productDao.getProductByOwnerID(recipe.getLong("recipe_id")) != null) {
                                            Recipe recipeToInsert = new Recipe();
                                            recipeToInsert.setName(recipe.getString("name"));
                                            recipeToInsert.setCategory(recipe.getString("category"));
                                            recipeToInsert.setVegetarian(recipe.getInt("vegetarian"));
                                            recipeToInsert.setImage(Base64.getDecoder().decode(recipe.getString("image")));
                                            recipeToInsert.setPortions(recipe.getInt("portions"));
                                            recipeToInsert.setSteps(recipe.getString("steps"));
                                            recipeToInsert.setHours(recipe.getInt("hours"));
                                            recipeToInsert.setMinutes(recipe.getInt("minutes"));
                                            recipeToInsert.setCreatedOn(ConvertDate.fromTimestamp(recipe.getLong("created_on")));
                                            recipeToInsert.setIsApproved(true);
                                            recipeToInsert.setSync(true);
                                            recipeToInsert.setOwnerID(recipe.getLong("owner_id"));
                                            recipeToInsert.setServerID(recipe.getLong("recipe_id"));
                                            recipeDao.insert(recipeToInsert);
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            MySingleton.getInstance(context).addToRequestQueue(stringRequest);
        }
    }
}
