package com.example.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

//Define tables
@Entity(tableName = "product")
public class Product implements Serializable {
    // Create id column
    @PrimaryKey(autoGenerate = true)
    private int ID;
    // Create text column
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "measure_unit")
    private String measure_unit;
    @ColumnInfo(name = "quantity")
    private String quantity;
    @ColumnInfo(name = "mandatory")
    private boolean mandatory;
    @ColumnInfo(name = "alternate")
    private String alternate;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMeasure_unit() {
        return measure_unit;
    }

    public void setMeasure_unit(String measure_unit) {
        this.measure_unit = measure_unit;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getAlternate() {
        return alternate;
    }

    public void setAlternate(String alternate) {
        this.alternate = alternate;
    }
}
