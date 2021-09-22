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
    @ColumnInfo(name = "is_sync")
    private boolean isSync;
    @ColumnInfo(name = "recipe_id")
    private long recipeId;
    @ColumnInfo(name = "photo_MySQL_id")
    private long serverID;

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
    public boolean isSync() {
        return isSync;
    }
    public void setSync(boolean sync) {
        isSync = sync;
    }
    public long getRecipeId() {
        return recipeId;
    }
    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
    }
    public long getServerID() {
        return serverID;
    }
    public void setServerID(long serverID) {
        this.serverID = serverID;
    }
}
