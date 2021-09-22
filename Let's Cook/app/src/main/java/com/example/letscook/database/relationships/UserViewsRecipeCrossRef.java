package com.example.letscook.database.relationships;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "user_views_recipes",
        indices = {@Index("recipe_id")})
public class UserViewsRecipeCrossRef implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @ColumnInfo(index = true)
    public long user_id;
    public long recipe_id;
    public boolean is_sync;
    public long view_MySQL_id;

    public UserViewsRecipeCrossRef(long user_id, long recipe_id, boolean is_sync, long view_MySQL_id) {
        this.user_id = user_id;
        this.recipe_id = recipe_id;
        this.is_sync = is_sync;
        this.view_MySQL_id = view_MySQL_id;
    }
    public long getUser_id() {
        return user_id;
    }
    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }
    public long getRecipe_id() {
        return recipe_id;
    }
    public void setRecipe_id(long recipe_id) {
        this.recipe_id = recipe_id;
    }
    public boolean isIs_sync() {
        return is_sync;
    }
    public void setIs_sync(boolean is_sync) {
        this.is_sync = is_sync;
    }
    public long getView_MySQL_id() {
        return view_MySQL_id;
    }
    public void setView_MySQL_id(long view_MySQL_id) {
        this.view_MySQL_id = view_MySQL_id;
    }
}
