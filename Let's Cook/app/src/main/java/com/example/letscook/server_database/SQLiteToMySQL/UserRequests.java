package com.example.letscook.server_database.SQLiteToMySQL;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.letscook.database.RoomDB;
import com.example.letscook.database.typeconverters.DataConverter;
import com.example.letscook.database.user.User;
import com.example.letscook.server_database.MySingleton;
import com.example.letscook.server_database.NetworkMonitor;
import com.example.letscook.server_database.URLs;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import static com.example.letscook.server_database.URLs.USER_URL;

public class UserRequests {
    public static void userPOST(Context context, User userToReg) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            if (!userToReg.isSync()) {
                String uri = String.format(USER_URL + "?email=%1$s",
                        userToReg.getEmail());
                StringRequest stringRequest = new StringRequest(Request.Method.GET, uri,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String stringResponse = jsonObject.getString("response");
                                    if (stringResponse.equals("OK")) {
                                        JSONObject user = jsonObject.getJSONObject("user");
                                        userPATCH(context, userToReg, userToReg.getName(), userToReg.getEmail(), userToReg.getPhoto() != null ? String.valueOf(DataConverter.byteArrayToImage(userToReg.getPhoto())) : null, userToReg.getPassword());
                                        RoomDB.getInstance(context).userDao().setServerID(userToReg.getID(), user.getLong("user_id"));
                                    }
                                    if (stringResponse.equals("FAILED")) {
                                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.USER_URL,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {
                                                            JSONObject jsonObject = new JSONObject(response);
                                                            String stringResponse = jsonObject.getString("response");
                                                            if (stringResponse.equals("OK")) {
                                                                userGET(context, userToReg);
                                                                RoomDB.getInstance(context).userDao().userSync(userToReg.getID());
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
                                                params.put("name", userToReg.getName());
                                                params.put("email", userToReg.getEmail());
                                                params.put("password", userToReg.getPassword());
                                                params.put("is_admin", String.valueOf(userToReg.isAdmin() ? 1 : 0));
                                                if (userToReg.getPhoto() != null) {
                                                    params.put("photo", Base64.getEncoder().encodeToString(userToReg.getPhoto()));
                                                }
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

    public static void userGET(Context context, User userToReg) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            String uri = String.format(USER_URL + "?email=%1$s",
                    userToReg.getEmail());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, uri,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String stringResponse = jsonObject.getString("response");
                                if (stringResponse.equals("OK")) {
                                    JSONObject user = jsonObject.getJSONObject("user");
                                    RoomDB.getInstance(context).userDao().setServerID(userToReg.getID(), user.getLong("user_id"));
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

    public static void userPATCH(Context context, User user, String name, String email, String photo, String pass) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            StringRequest stringRequest = new StringRequest(Request.Method.PATCH, URLs.USER_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String stringResponse = jsonObject.getString("response");
                                if (stringResponse.equals("OK")) {
                                    RoomDB.getInstance(context).userDao().userSync(user.getID());
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
                    params.put("user_id", String.valueOf(user.getServerID()));
                    if (name != null) {
                        params.put("name", name);
                    }
                    if (email != null) {
                        params.put("email", email);
                    }
                    if (pass != null) {
                        params.put("password", pass);
                    }
                    params.put("photo", photo != null ? photo : "");
                    return params;
                }
            };
            MySingleton.getInstance(context).addToRequestQueue(stringRequest);
        } else {
            RoomDB.getInstance(context).userDao().userUnSync(user.getID());
        }
    }
}
