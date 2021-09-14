package com.example.letscook.server_database.MySQLToSQLite;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.letscook.database.RoomDB;
import com.example.letscook.database.product.Product;
import com.example.letscook.database.product.ProductDao;
import com.example.letscook.server_database.MySingleton;
import com.example.letscook.server_database.NetworkMonitor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;

import static com.example.letscook.server_database.URLs.*;

public class ProductsRequest {
    public static void productGET(Context context) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, PRODUCTS_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String stringResponse = jsonObject.getString("response");
                                if (stringResponse.equals("GET all entries!")) {
                                    JSONArray products = jsonObject.getJSONArray("products");
                                    for (int i = 0; i < products.length(); i++) {
                                        JSONObject product = products.getJSONObject(i);
                                        ProductDao productDao = RoomDB.getInstance(context).productDao();
                                        Product localProduct = productDao.getProductByServerID(product.getLong("product_id"));
                                        if (localProduct != null) {
                                            localProduct.setName(product.getString("name"));
                                            localProduct.setMeasureUnit(product.getString("measure_unit").trim());
                                            localProduct.setQuantity((float) product.getDouble("quantity"));
                                            localProduct.setBelonging(product.getString("belonging"));
                                            localProduct.setSync(true);
                                            localProduct.setOwnerId(product.getLong("owner_id"));
                                            localProduct.setServerID(product.getLong("product_id"));
                                            productDao.insert(localProduct);
                                        } else {
                                            Product productToInsert = new Product();
                                            productToInsert.setName(product.getString("name"));
                                            productToInsert.setMeasureUnit(product.getString("measure_unit").trim());
                                            productToInsert.setQuantity((float) product.getDouble("quantity"));
                                            productToInsert.setBelonging(product.getString("belonging"));
                                            productToInsert.setSync(true);
                                            productToInsert.setOwnerId(product.getLong("owner_id"));
                                            productToInsert.setServerID(product.getLong("product_id"));
                                            productDao.insert(productToInsert);
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
