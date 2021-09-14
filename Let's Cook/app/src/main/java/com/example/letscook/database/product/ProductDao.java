package com.example.letscook.database.product;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.letscook.database.recipe.Recipe;

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

    @Query("SELECT * FROM product WHERE product_MySQL_id = :serverID")
    Product getProductByServerID(long serverID);

    @Query("SELECT * FROM product WHERE owner_id = :ID")
    Product getProductByOwnerID(long ID);

    @Query("UPDATE product SET name = :sName, measure_unit = :sMeasureUnit, quantity = :sQuantity " +
            "WHERE product_id = :sID")
    void update(long sID, String sName, String sMeasureUnit, float sQuantity);

    @Transaction
    @Query("SELECT * FROM product WHERE belonging = :belong AND (owner_id = :sOwnerID OR owner_id = :sServerID) ORDER BY name")
    List<Product> getUserProducts(String belong, long sOwnerID, long sServerID);

    @Transaction
    @Query("SELECT * FROM product WHERE belonging = :belong AND (owner_id = :sOwnerID OR owner_id = :sServerID) ORDER BY name")
    List<Product> getRecipeProducts(String belong, long sOwnerID, long sServerID);

    @Query("SELECT * FROM product WHERE is_sync = 0 ORDER BY product_id")
    List<Product> getAllUnSyncProducts();

    @Query("UPDATE product SET is_sync = 1 WHERE product_id = :sID")
    void productSync(long sID);

    @Query("UPDATE product SET product_MySQL_id = :serverID WHERE product_id = :sID")
    void setServerID(long sID, long serverID);

    @Query("UPDATE product SET owner_id = :serverID WHERE product_id = :sID")
    void setOwnerID(long sID, long serverID);

    @Query("UPDATE product SET is_sync = 0 WHERE product_id = :sID")
    void productUnSync(long sID);
}
