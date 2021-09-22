package com.example.letscook.server_database.SQLiteToMySQL;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.letscook.database.RoomDB;
import com.example.letscook.database.relationships.UserViewsRecipeCrossRef;
import com.example.letscook.server_database.MySingleton;
import com.example.letscook.server_database.NetworkMonitor;
import com.example.letscook.server_database.URLs;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import static com.example.letscook.server_database.URLs.VIEWS_URL;

public class RecipeViewsRequests {
    public static void viewsPOST(Context context, UserViewsRecipeCrossRef userViewsRecipeCrossRef) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            if (!userViewsRecipeCrossRef.isIs_sync()) {
                String uri = String.format(VIEWS_URL + "?user_id=%1$s&recipe_id=%2$s",
                        userViewsRecipeCrossRef.getUser_id(), userViewsRecipeCrossRef.getRecipe_id());
                StringRequest stringRequest = new StringRequest(Request.Method.GET, uri,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String stringResponse = jsonObject.getString("response");
                                    if (stringResponse.equals("FAILED")) {
                                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.VIEWS_URL,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {
                                                            JSONObject jsonObject = new JSONObject(response);
                                                            String stringResponse = jsonObject.getString("response");
                                                            if (stringResponse.equals("OK")) {
                                                                viewsGET(context, userViewsRecipeCrossRef);
                                                                RoomDB.getInstance(context).userViewsRecipeDao().viewSync(userViewsRecipeCrossRef.getUser_id(), userViewsRecipeCrossRef.getRecipe_id());
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
                                                params.put("user_id", String.valueOf(userViewsRecipeCrossRef.getUser_id()));
                                                params.put("recipe_id", String.valueOf(userViewsRecipeCrossRef.getRecipe_id()));
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

    public static void viewsGET(Context context, UserViewsRecipeCrossRef userViewsRecipeCrossRef) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            String uri = String.format(VIEWS_URL + "?user_id=%1$s&recipe_id=%2$s",
                    userViewsRecipeCrossRef.getUser_id(), userViewsRecipeCrossRef.getRecipe_id());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, uri,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String stringResponse = jsonObject.getString("response");
                                if (stringResponse.equals("OK")) {
                                    JSONObject view = jsonObject.getJSONObject("view");
                                    RoomDB.getInstance(context).userViewsRecipeDao().setServerID(userViewsRecipeCrossRef.getUser_id(),
                                            userViewsRecipeCrossRef.getRecipe_id(), view.getLong("view_id"));
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
