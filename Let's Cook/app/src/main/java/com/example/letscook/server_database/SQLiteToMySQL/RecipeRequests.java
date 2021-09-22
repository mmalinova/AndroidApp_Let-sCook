package com.example.letscook.server_database.SQLiteToMySQL;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.letscook.database.RoomDB;
import com.example.letscook.database.recipe.Recipe;
import com.example.letscook.database.typeconverters.ConvertDate;
import com.example.letscook.database.user.User;
import com.example.letscook.server_database.MySingleton;
import com.example.letscook.server_database.NetworkMonitor;
import com.example.letscook.server_database.URLs;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import static com.example.letscook.server_database.URLs.RECIPES_URL;

public class RecipeRequests {
    public static void recipePOST(Context context, Recipe recipe, User user) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            if (!recipe.isSync() && recipe.isApproved()) {
                String uri = String.format(RECIPES_URL + "?name=%1$s&owner_id=%2$s",
                        recipe.getName(), recipe.getOwnerID());
                StringRequest stringRequest = new StringRequest(Request.Method.GET, uri,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String stringResponse = jsonObject.getString("response");
                                    if (stringResponse.equals("OK")) {
                                        recipeGET(context, recipe);
                                        RoomDB.getInstance(context).recipeDao().recipeSync(recipe.getID());
                                    }
                                    if (stringResponse.equals("FAILED")) {
                                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.RECIPES_URL,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {
                                                            JSONObject jsonObject = new JSONObject(response);
                                                            String stringResponse = jsonObject.getString("response");
                                                            if (stringResponse.equals("OK")) {
                                                                recipeGET(context, recipe);
                                                                RoomDB.getInstance(context).recipeDao().recipeSync(recipe.getID());
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
                                            @RequiresApi(api = Build.VERSION_CODES.O)
                                            @Override
                                            protected Map<String, String> getParams() {
                                                Map<String, String> params = new HashMap<>();
                                                params.put("name", recipe.getName());
                                                params.put("category", recipe.getCategory());
                                                params.put("vegetarian", String.valueOf(recipe.getVegetarian()));
                                                params.put("image", Base64.getEncoder().encodeToString(recipe.getImage()));
                                                params.put("portions", String.valueOf(recipe.getPortions()));
                                                params.put("steps", recipe.getSteps());
                                                params.put("hours", String.valueOf(recipe.getHours()));
                                                params.put("minutes", String.valueOf(recipe.getMinutes()));
                                                params.put("created_on", String.valueOf(ConvertDate.dateToTimestamp(recipe.getCreatedOn())));
                                                params.put("is_approved", String.valueOf(recipe.isApproved() ? 1 : 0));
                                                params.put("owner_id", String.valueOf(user.getServerID()));
                                                return params;
                                            }
                                        };
                                        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
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

    public static void recipeGET(Context context, Recipe recipe) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            String uri = String.format(RECIPES_URL + "?name=%1$s&owner_id=%2$s",
                    recipe.getName(), recipe.getOwnerID());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, uri,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String stringResponse = jsonObject.getString("response");
                                if (stringResponse.equals("OK")) {
                                    JSONObject rec = jsonObject.getJSONObject("recipe");
                                    RoomDB.getInstance(context).recipeDao().setServerID(recipe.getID(), rec.getLong("recipe_id"));
                                    RoomDB.getInstance(context).recipeDao().setOwnerID(recipe.getID(), rec.getLong("owner_id"));
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
