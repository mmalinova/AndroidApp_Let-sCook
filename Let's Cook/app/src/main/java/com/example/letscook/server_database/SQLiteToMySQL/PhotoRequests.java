package com.example.letscook.server_database.SQLiteToMySQL;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.example.letscook.database.RoomDB;
import com.example.letscook.database.photo.Photo;
import com.example.letscook.database.recipe.Recipe;
import com.example.letscook.server_database.MySingleton;
import com.example.letscook.server_database.NetworkMonitor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import static com.example.letscook.server_database.URLs.*;

public class PhotoRequests {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void photoPOST(Context context, Photo photo, Recipe recipe, int index) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            if (!photo.isSync()) {
                String uri = String.format(PHOTOS_URL + "?recipe_id=%1$s",
                        photo.getRecipeId());
                StringRequest stringRequest = new StringRequest(Request.Method.GET, uri,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String stringResponse = jsonObject.getString("response");
                                    if (stringResponse.equals("FAILED")) {
                                        StringRequest stringRequest = new StringRequest(Request.Method.POST, PHOTOS_URL,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {
                                                            JSONObject jsonObject = new JSONObject(response);
                                                            String stringResponse = jsonObject.getString("response");
                                                            if (stringResponse.equals("OK")) {
                                                                photoGET(context, photo, index);
                                                                RoomDB.getInstance(context).photoDao().photoSync(photo.getID());
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                NetworkResponse response = error.networkResponse;
                                                if (error instanceof ServerError && response != null) {
                                                    try {
                                                        String res = new String(response.data,
                                                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                                        // Now you can use any deserializer to make sense of data
                                                        JSONObject obj = new JSONObject(res);
                                                    } catch (UnsupportedEncodingException e1) {
                                                        // Couldn't properly decode data to string
                                                        e1.printStackTrace();
                                                    } catch (JSONException e2) {
                                                        // returned data is not JSONObject?
                                                        e2.printStackTrace();
                                                    }
                                                }
                                            }
                                        }) {
                                            @Override
                                            protected Map<String, String> getParams() {
                                                Map<String, String> params = new HashMap<>();
                                                params.put("photo", Base64.getEncoder().encodeToString(photo.getPhoto()));
                                                params.put("recipe_id", String.valueOf(recipe.getServerID()));
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
                        NetworkResponse response = error.networkResponse;
                        if (error instanceof ServerError && response != null) {
                            try {
                                String res = new String(response.data,
                                        HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                // Now you can use any deserializer to make sense of data
                                JSONObject obj = new JSONObject(res);
                            } catch (UnsupportedEncodingException e1) {
                                // Couldn't properly decode data to string
                                e1.printStackTrace();
                            } catch (JSONException e2) {
                                // returned data is not JSONObject?
                                e2.printStackTrace();
                            }
                        }
                    }
                });
                MySingleton.getInstance(context).addToRequestQueue(stringRequest);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void photoGET(Context context, Photo photo, int index) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            String uri = String.format(PHOTOS_URL + "?recipe_id=%1$s",
                    photo.getRecipeId());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, uri,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String stringResponse = jsonObject.getString("response");
                                if (stringResponse.equals("OK")) {
                                    JSONArray photos = jsonObject.getJSONArray("photos");
                                    JSONObject ph = photos.getJSONObject(index);
                                    RoomDB.getInstance(context).photoDao().setServerID(photo.getID(), ph.getLong("photo_id"));
                                    RoomDB.getInstance(context).photoDao().setOwnerID(photo.getID(), ph.getLong("recipe_id"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse response = error.networkResponse;
                    if (error instanceof ServerError && response != null) {
                        try {
                            String res = new String(response.data,
                                    HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                            // Now you can use any deserializer to make sense of data
                            JSONObject obj = new JSONObject(res);
                        } catch (UnsupportedEncodingException e1) {
                            // Couldn't properly decode data to string
                            e1.printStackTrace();
                        } catch (JSONException e2) {
                            // returned data is not JSONObject?
                            e2.printStackTrace();
                        }
                    }
                }
            });
            MySingleton.getInstance(context).addToRequestQueue(stringRequest);
        }
    }
}
