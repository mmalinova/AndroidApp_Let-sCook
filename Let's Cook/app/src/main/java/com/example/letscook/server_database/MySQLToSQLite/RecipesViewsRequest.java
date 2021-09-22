package com.example.letscook.server_database.MySQLToSQLite;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.letscook.database.RoomDB;
import com.example.letscook.database.relationships.UserViewsRecipeCrossRef;
import com.example.letscook.database.relationships.UserViewsRecipeDao;
import com.example.letscook.database.user.UserDao;
import com.example.letscook.server_database.MySingleton;
import com.example.letscook.server_database.NetworkMonitor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.letscook.server_database.URLs.*;

public class RecipesViewsRequest {
    public static void viewsGET(Context context) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, VIEWS_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String stringResponse = jsonObject.getString("response");
                                if (stringResponse.equals("GET all entries!")) {
                                    JSONArray views = jsonObject.getJSONArray("views");
                                    for (int i = 0; i < views.length(); i++) {
                                        JSONObject view = views.getJSONObject(i);
                                        UserDao userDao = RoomDB.getInstance(context).userDao();
                                        UserViewsRecipeDao userViewsRecipeDao = RoomDB.getInstance(context).userViewsRecipeDao();
                                        UserViewsRecipeCrossRef userViewsRecipeCrossRef = userViewsRecipeDao.getByServerID(view.getLong("view_id"));
                                        if (userViewsRecipeCrossRef != null) {
                                            userViewsRecipeCrossRef.setUser_id(view.getLong("user_id"));
                                            userViewsRecipeCrossRef.setRecipe_id(view.getLong("recipe_id"));
                                            userViewsRecipeCrossRef.setIs_sync(true);
                                            userViewsRecipeCrossRef.setView_MySQL_id(view.getLong("view_id"));
                                            userDao.insertUserViewsRecipeCrossRef(userViewsRecipeCrossRef);
                                        } else {
                                            UserViewsRecipeCrossRef viewToInsert = new UserViewsRecipeCrossRef(view.getLong("user_id"),
                                                    view.getLong("recipe_id"), true, view.getLong("view_id"));
                                            userDao.insertUserViewsRecipeCrossRef(viewToInsert);
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
