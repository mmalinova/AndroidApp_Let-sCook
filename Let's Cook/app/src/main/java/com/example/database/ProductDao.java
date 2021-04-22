package com.example.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ProductDao {
    // Insert
    @Insert(onConflict = REPLACE)
    void insert(Product product);

    @Delete
    void delete(Product product);

    // Delete all
    @Delete
    void deleteAll(List<Product> products);

    @Query("UPDATE product SET name = :sName, measure_unit = :sMeasure_unit, quantity = :sQuantity " +
            "WHERE ID = :sID")
    void update(int sID, String sName, String sMeasure_unit, String sQuantity);

    @Query("SELECT * FROM product ORDER BY name")
    List<Product> getAllProducts();
}
