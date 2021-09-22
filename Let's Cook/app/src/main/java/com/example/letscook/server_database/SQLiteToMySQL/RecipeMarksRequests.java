package com.example.letscook.server_database.SQLiteToMySQL;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.letscook.database.RoomDB;
import com.example.letscook.database.relationships.UserMarksRecipeCrossRef;
import com.example.letscook.server_database.MySingleton;
import com.example.letscook.server_database.NetworkMonitor;
import com.example.letscook.server_database.URLs;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import static com.example.letscook.server_database.URLs.MARKS_URL;

public class RecipeMarksRequests {
    public static void marksPOST(Context context, UserMarksRecipeCrossRef userMarksRecipeCrossRef) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            if (!userMarksRecipeCrossRef.isIs_sync()) {
                String uri = String.format(MARKS_URL + "?user_id=%1$s&recipe_id=%2$s",
                        userMarksRecipeCrossRef.getUser_id(), userMarksRecipeCrossRef.getRecipe_id());
                StringRequest stringRequest = new StringRequest(Request.Method.GET, uri,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String stringResponse = jsonObject.getString("response");
                                    if (stringResponse.equals("FAILED")) {
                                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.MARKS_URL,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {
                                                            JSONObject jsonObject = new JSONObject(response);
                                                            String stringResponse = jsonObject.getString("response");
                                                            if (stringResponse.equals("OK")) {
                                                                marksGET(context, userMarksRecipeCrossRef);
                                                                RoomDB.getInstance(context).userMarksRecipeDao().markSync(userMarksRecipeCrossRef.getUser_id(), userMarksRecipeCrossRef.getRecipe_id());
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
                                                params.put("user_id", String.valueOf(userMarksRecipeCrossRef.getUser_id()));
                                                params.put("recipe_id", String.valueOf(userMarksRecipeCrossRef.getRecipe_id()));
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

    public static void marksGET(Context context, UserMarksRecipeCrossRef userMarksRecipeCrossRef) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            String uri = String.format(MARKS_URL + "?user_id=%1$s&recipe_id=%2$s",
                    userMarksRecipeCrossRef.getUser_id(), userMarksRecipeCrossRef.getRecipe_id());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, uri,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String stringResponse = jsonObject.getString("response");
                                if (stringResponse.equals("OK")) {
                                    JSONObject mark = jsonObject.getJSONObject("mark");
                                    RoomDB.getInstance(context).userMarksRecipeDao().setServerID(userMarksRecipeCrossRef.getUser_id(),
                                            userMarksRecipeCrossRef.getRecipe_id(), mark.getLong("mark_id"));
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

    public static void marksDELETE(Context context, UserMarksRecipeCrossRef userMarksRecipeCrossRef) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            String uri = String.format(MARKS_URL + "?mark_id=%1$s",
                    userMarksRecipeCrossRef.getMark_MySQL_id());
            StringRequest stringRequest = new StringRequest(Request.Method.DELETE, uri,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String stringResponse = jsonObject.getString("response");
                                if (stringResponse.equals("OK")) {
                                    RoomDB.getInstance(context).userDao().deleteUserMarksRecipeCrossRef(userMarksRecipeCrossRef);
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
