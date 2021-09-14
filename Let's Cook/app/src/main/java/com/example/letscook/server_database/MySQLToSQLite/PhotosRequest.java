package com.example.letscook.server_database.MySQLToSQLite;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.letscook.database.RoomDB;
import com.example.letscook.database.photo.Photo;
import com.example.letscook.database.photo.PhotoDao;
import com.example.letscook.server_database.MySingleton;
import com.example.letscook.server_database.NetworkMonitor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;

import static com.example.letscook.server_database.URLs.PHOTOS_URL;

public class PhotosRequest {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void photoGET(Context context) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, PHOTOS_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String stringResponse = jsonObject.getString("response");
                                if (stringResponse.equals("GET all entries!")) {
                                    JSONArray photos = jsonObject.getJSONArray("photos");
                                    for (int i = 0; i < photos.length(); i++) {
                                        JSONObject photo = photos.getJSONObject(i);
                                        PhotoDao photoDao = RoomDB.getInstance(context).photoDao();
                                        Photo localPhoto = photoDao.getPhotoByServerID(photo.getLong("photo_id"));
                                        if (localPhoto != null) {
                                            localPhoto.setPhoto(Base64.getDecoder().decode(photo.getString("photo")));
                                            localPhoto.setSync(true);
                                            localPhoto.setRecipeId(photo.getLong("recipe_id"));
                                            localPhoto.setServerID(photo.getLong("photo_id"));
                                            photoDao.insert(localPhoto);
                                        } else {
                                            Photo photoToInsert = new Photo();
                                            photoToInsert.setPhoto(Base64.getDecoder().decode(photo.getString("photo")));
                                            photoToInsert.setSync(true);
                                            photoToInsert.setRecipeId(photo.getLong("recipe_id"));
                                            photoToInsert.setServerID(photo.getLong("photo_id"));
                                            photoDao.insert(photoToInsert);
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
