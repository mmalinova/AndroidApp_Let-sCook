package com.example.letscook.server_database.MySQLToSQLite;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.letscook.database.RoomDB;
import com.example.letscook.database.user.User;
import com.example.letscook.database.user.UserDao;
import com.example.letscook.server_database.MySingleton;
import com.example.letscook.server_database.NetworkMonitor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;

import static com.example.letscook.server_database.URLs.*;

public class UsersRequest {
    public static void userGET(Context context) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, USER_URL,
                    new Response.Listener<String>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String stringResponse = jsonObject.getString("response");
                                if (stringResponse.equals("GET all entries!")) {
                                    JSONArray users = jsonObject.getJSONArray("users");
                                    for (int i = 0; i < users.length(); i++) {
                                        JSONObject user = users.getJSONObject(i);
                                        UserDao userDao = RoomDB.getInstance(context).userDao();
                                        User localUser = userDao.getUserByServerID(user.getLong("user_id"));
                                        if (localUser != null) {
                                            localUser.setName(user.getString("name"));
                                            localUser.setEmail(user.getString("email"));
                                            localUser.setPassword(user.getString("password"));
                                            localUser.setAdmin(user.getInt("is_admin") == 1);
                                            localUser.setSync(true);
                                            user.getString("photo");
                                            if (user.getString("photo").length() > 0) {
                                                localUser.setPhoto(Base64.getDecoder().decode(user.getString("photo")));
                                            } else {
                                                localUser.setPhoto(null);
                                            }
                                            localUser.setServerID(user.getLong("user_id"));
                                            userDao.register(localUser);
                                        } else {
                                            User userToInsert = new User();
                                            userToInsert.setName(user.getString("name"));
                                            userToInsert.setEmail(user.getString("email"));
                                            userToInsert.setPassword(user.getString("password"));
                                            userToInsert.setAdmin(user.getInt("is_admin") == 1);
                                            userToInsert.setSync(true);
                                            if (user.getString("photo").length() > 0) {
                                                userToInsert.setPhoto(Base64.getDecoder().decode(user.getString("photo")));
                                            } else {
                                                userToInsert.setPhoto(null);
                                            }
                                            userToInsert.setServerID(user.getLong("user_id"));
                                            userDao.register(userToInsert);
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
