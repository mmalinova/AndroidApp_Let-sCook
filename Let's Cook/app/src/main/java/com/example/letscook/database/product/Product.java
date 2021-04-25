package com.example.letscook.database.product;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import com.example.letscook.database.user.User;

import java.io.Serializable;
import java.util.List;

//Define table
@Entity(tableName = "product")
public class Product implements Serializable {
    @ColumnInfo(name = "product_id")
    @PrimaryKey(autoGenerate = true)
    private long ID;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "measure_unit")
    private String measureUnit;
    @ColumnInfo(name = "quantity")
    private float quantity;
    @ColumnInfo(name = "mandatory")
    private boolean mandatory;
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

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
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

    // One-to-many relationship
    public class ProductHasOwner {
        @Embedded
        public Product product;
        @Relation(
                parentColumn = "owner_id",
                entityColumn = "user_id"
        )
        public List<User> userList;
    }
}
