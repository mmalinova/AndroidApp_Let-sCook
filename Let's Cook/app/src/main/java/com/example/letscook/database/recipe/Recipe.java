package com.example.letscook.database.recipe;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;


import com.example.letscook.database.typeconverters.ConvertDate;

import java.io.Serializable;
import java.util.Date;

//Define table
@Entity(tableName = "recipe")
public class Recipe implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "recipe_id")
    private long ID;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "category")
    private String category;
    @ColumnInfo(name = "vegetarian")
    private int vegetarian;
    @ColumnInfo(name = "image", typeAffinity = ColumnInfo.BLOB)
    private byte[] image;
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
    @ColumnInfo(name = "is_approved")
    private boolean isApproved;
    @ColumnInfo(name = "is_sync")
    private boolean isSync;
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

    public int getVegetarian() {
        return vegetarian;
    }

    public void setVegetarian(int vegetarian) {
        this.vegetarian = vegetarian;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
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

    public boolean isApproved() {
        return isApproved;
    }

    public void setIsApproved(boolean isApproved) {
        this.isApproved = isApproved;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }

    public long getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(long ownerID) {
        this.ownerID = ownerID;
    }
}
