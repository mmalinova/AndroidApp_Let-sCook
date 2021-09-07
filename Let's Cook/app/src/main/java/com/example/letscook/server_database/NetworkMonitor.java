package com.example.letscook.server_database;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.letscook.controller.register.SignUpActivity;
import com.example.letscook.database.RoomDB;
import com.example.letscook.database.photo.Photo;
import com.example.letscook.database.photo.PhotoDao;
import com.example.letscook.database.product.Product;
import com.example.letscook.database.product.ProductDao;
import com.example.letscook.database.recipe.Recipe;
import com.example.letscook.database.recipe.RecipeDao;
import com.example.letscook.database.user.User;
import com.example.letscook.database.user.UserDao;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkMonitor extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Initialize db
        RoomDB database = RoomDB.getInstance(context);
        final ProductDao productDao = database.productDao();
        final RecipeDao recipeDao = database.recipeDao();
        final UserDao userDao = database.userDao();
        final PhotoDao photoDao = database.photoDao();
        if (checkNetworkConnection(context)) {
            List<Product> unSyncProducts = productDao.getAllUnSyncProducts();
            List<Recipe> unSyncRecipes = recipeDao.getAllUnSyncRecipes();
            List<User> unSyncUsers = userDao.getAllUnSyncUsers();
            List<Photo> unSyncPhotosFromRecipe = photoDao.getAllUnSyncPhotosFromRecipe();

            for (Product product : unSyncProducts) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.PRODUCTS_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String stringResponse = jsonObject.getString("response");
                                    if (stringResponse.equals("OK")) {
                                        productDao.productSync(product.getID());
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("name", product.getName());
                        params.put("measure_unit", product.getMeasureUnit());
                        params.put("quantity", String.valueOf(product.getQuantity()));
                        params.put("belonging", product.getBelonging());
                        params.put("is_SQLite_sync", "1");
                        params.put("owner_id", String.valueOf(product.getOwnerId()));
                        return params;
                    }
                };
                MySingleton.getInstance(context).addToRequestQueue(stringRequest);
            }
        }
    }

    public static boolean checkNetworkConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
