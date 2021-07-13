package com.example.letscook.database.recipe;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;


import com.example.letscook.database.product.Product;
import com.example.letscook.database.typeconverters.ConvertDate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

//Define table
@Entity(tableName = "recipe")
public class Recipe implements Serializable {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "recipe_id")
    private long ID;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "category")
    private String category;
    @ColumnInfo(name = "vegetarian")
    private boolean vegetarian;
    @ColumnInfo(name = "images")
    private long images;
    @ColumnInfo(name = "portions")
    private int portions;
    @ColumnInfo(name = "steps")
    private String steps;
    @ColumnInfo(name = "hours")
    private int hours;
    @ColumnInfo(name = "minutes")
    private int minutes;
    @ColumnInfo(name = "created_on")
    @TypeConverters({ConvertDate.class})
    private Date createdOn;
    @ColumnInfo(name = "ingredients")
    private long ingredients;
    @ColumnInfo(name = "owner_id")
    private long ownerID;

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        this.vegetarian = vegetarian;
    }

    public long getImages() {
        return images;
    }

    public void setImages(long images) {
        this.images = images;
    }

    public int getPortions() {
        return portions;
    }

    public void setPortions(int portions) {
        this.portions = portions;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public long getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(long ownerID) {
        this.ownerID = ownerID;
    }

    public long getIngredients() {
        return ingredients;
    }

    public void setIngredients(long ingredients) {
        this.ingredients = ingredients;
    }
}