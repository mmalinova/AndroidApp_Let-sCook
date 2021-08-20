package com.example.letscook.database.photo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "photo")
public class Photo implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "photo_id")
    private long ID;
    @ColumnInfo(name = "photo", typeAffinity = ColumnInfo.BLOB)
    private byte[] photo;
    @ColumnInfo(name = "recipe_id")
    private long recipe_id;

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] image) {
        this.photo = image;
    }

    public long getRecipe_id() {
        return recipe_id;
    }

    public void setRecipe_id(long recipe_id) {
        this.recipe_id = recipe_id;
    }
}
