package com.example.letscook.server_database.MySQLToSQLite;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.letscook.database.RoomDB;
import com.example.letscook.database.relationships.UserMarksRecipeCrossRef;
import com.example.letscook.database.relationships.UserMarksRecipeDao;
import com.example.letscook.database.user.UserDao;
import com.example.letscook.server_database.MySingleton;
import com.example.letscook.server_database.NetworkMonitor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.letscook.server_database.URLs.*;

public class RecipesMarksRequest {
    public static void marksGET(Context context) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, MARKS_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String stringResponse = jsonObject.getString("response");
                                if (stringResponse.equals("GET all entries!")) {
                                    JSONArray marks = jsonObject.getJSONArray("marks");
                                    for (int i = 0; i < marks.length(); i++) {
                                        JSONObject mark = marks.getJSONObject(i);
                                        UserDao userDao = RoomDB.getInstance(context).userDao();
                                        UserMarksRecipeDao userMarksRecipeDao = RoomDB.getInstance(context).userMarksRecipeDao();
                                        UserMarksRecipeCrossRef userMarksRecipeCrossRef = userMarksRecipeDao.getByServerID(mark.getLong("mark_id"));
                                        if (userMarksRecipeCrossRef != null) {
                                            userMarksRecipeCrossRef.setUser_id(mark.getLong("user_id"));
                                            userMarksRecipeCrossRef.setRecipe_id(mark.getLong("recipe_id"));
                                            userMarksRecipeCrossRef.setIs_sync(true);
                                            userMarksRecipeCrossRef.setMark_MySQL_id(mark.getLong("mark_id"));
                                            userMarksRecipeCrossRef.setDeleted(false);
                                            userDao.insertUserMarksRecipeCrossRef(userMarksRecipeCrossRef);
                                        } else {
                                            UserMarksRecipeCrossRef markToInsert = new UserMarksRecipeCrossRef(mark.getLong("user_id"),
                                                    mark.getLong("recipe_id"), true, mark.getLong("mark_id"), false);
                                            userDao.insertUserMarksRecipeCrossRef(markToInsert);
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
