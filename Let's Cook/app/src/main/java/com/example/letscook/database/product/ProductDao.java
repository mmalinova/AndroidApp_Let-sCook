package com.example.letscook.database.product;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.letscook.database.product.Product;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ProductDao {
    @Insert(onConflict = REPLACE)
    void insert(Product product);

    @Delete
    void delete(Product product);

    @Delete
    void deleteAll(List<Product> products);

    @Query("UPDATE product SET name = :sName, measure_unit = :sMeasureUnit, quantity = :sQuantity " +
            "WHERE product_id = :sID")
    void update(long sID, String sName, String sMeasureUnit, String sQuantity);

    @Query("SELECT * FROM product ORDER BY name")
    public List<Product> getAllProducts();

    @Transaction
    @Query("SELECT * FROM product WHERE belonging LIKE :belong ORDER BY name")
    public List<Product.ProductHasOwner> getUserProducts(String belong);
}
