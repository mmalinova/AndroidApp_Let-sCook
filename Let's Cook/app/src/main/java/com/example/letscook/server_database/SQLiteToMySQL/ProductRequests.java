package com.example.letscook.server_database.SQLiteToMySQL;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.letscook.database.RoomDB;
import com.example.letscook.database.product.Product;
import com.example.letscook.database.recipe.Recipe;
import com.example.letscook.database.user.User;
import com.example.letscook.server_database.MySingleton;
import com.example.letscook.server_database.NetworkMonitor;
import com.example.letscook.server_database.URLs;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import static com.example.letscook.server_database.URLs.PRODUCTS_URL;

public class ProductRequests {
    public static void productPOST(Context context, Product product, User user, Recipe recipe, int index) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            if (!product.isSync()) {
                String uri = String.format(PRODUCTS_URL + "?owner_id=%1$s&belonging=%2$s",
                        product.getOwnerId(), product.getBelonging());
                StringRequest stringRequest = new StringRequest(Request.Method.GET, uri,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String stringResponse = jsonObject.getString("response");
                                    if (stringResponse.equals("FAILED")) {
                                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.PRODUCTS_URL,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {
                                                            JSONObject jsonObject = new JSONObject(response);
                                                            String stringResponse = jsonObject.getString("response");
                                                            if (stringResponse.equals("OK")) {
                                                                productGET(context, product, user, recipe, index);
                                                                RoomDB.getInstance(context).productDao().productSync(product.getID());
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
                                                params.put("owner_id", user == null ? String.valueOf(recipe.getServerID()) : String.valueOf(user.getServerID()));
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

    public static void productGET(Context context, Product product, User user, Recipe recipe, int index) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            String uri = String.format(PRODUCTS_URL + "?owner_id=%1$s&belonging=%2$s",
                    product.getOwnerId(), product.getBelonging());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, uri,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String stringResponse = jsonObject.getString("response");
                                if (stringResponse.equals("OK")) {
                                    JSONArray products = jsonObject.getJSONArray("products");
                                    JSONObject prod = products.getJSONObject(index);
                                    RoomDB.getInstance(context).productDao().setServerID(product.getID(), prod.getLong("product_id"));
                                    RoomDB.getInstance(context).productDao().setOwnerID(product.getID(), prod.getLong("owner_id"));
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

    public static void productDELETE(Context context, Product product) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            String uri = String.format(PRODUCTS_URL + "?product_id=%1$s",
                    product.getServerID());
            StringRequest stringRequest = new StringRequest(Request.Method.DELETE, uri,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String stringResponse = jsonObject.getString("response");
                                if (stringResponse.equals("OK")) {
                                    RoomDB.getInstance(context).productDao().delete(product);
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

    public static void deleteAllProducts(Context context, String belonging) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            String uri = String.format(PRODUCTS_URL + "?belonging=%1$s",
                    belonging);
            StringRequest stringRequest = new StringRequest(Request.Method.DELETE, uri,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String stringResponse = jsonObject.getString("response");
                                if (stringResponse.equals("OK")) {
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

    public static void productPATCH(Context context, Product product, String name, String measureUnit, String quantity) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            StringRequest stringRequest = new StringRequest(Request.Method.PATCH, URLs.PRODUCTS_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String stringResponse = jsonObject.getString("response");
                                if (stringResponse.equals("OK")) {
                                    RoomDB.getInstance(context).productDao().productSync(product.getID());
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
                    params.put("product_id", String.valueOf(product.getServerID()));
                    if (name != null) {
                        params.put("name", name);
                    }
                    if (measureUnit != null) {
                        params.put("measure_unit", measureUnit);
                    }
                    if (quantity != null) {
                        params.put("quantity", quantity);
                    }
                    return params;
                }
            };
            MySingleton.getInstance(context).addToRequestQueue(stringRequest);
        } else {
            RoomDB.getInstance(context).productDao().productUnSync(product.getID());
        }
    }
}
