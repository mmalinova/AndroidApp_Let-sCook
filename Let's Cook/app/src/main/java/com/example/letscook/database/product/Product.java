package com.example.letscook.database.product;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

//Define table
@Entity(tableName = "product")
public class Product implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "product_id")
    private long ID;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "measure_unit")
    private String measureUnit;
    @ColumnInfo(name = "quantity")
    private float quantity;
    @ColumnInfo(name = "belonging")
    private String belonging;
    @ColumnInfo(name = "owner_id")
    private long ownerId;

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMeasureUnit() {
        return measureUnit;
    }

    public void setMeasureUnit(String measure_unit) {
        this.measureUnit = measure_unit;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public String getBelonging() {
        return belonging;
    }

    public void setBelonging(String belonging) {
        this.belonging = belonging;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }
}
