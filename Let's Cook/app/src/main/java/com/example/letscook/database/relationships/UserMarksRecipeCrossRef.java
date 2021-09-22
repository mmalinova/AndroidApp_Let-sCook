package com.example.letscook.database.relationships;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "user_marks_recipes",
        indices = {@Index("recipe_id")})
public class UserMarksRecipeCrossRef implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @ColumnInfo(index = true)
    public long user_id;
    public long recipe_id;
    public boolean is_sync;
    public long mark_MySQL_id;
    public boolean deleted;

    public UserMarksRecipeCrossRef(long user_id, long recipe_id, boolean is_sync, long mark_MySQL_id, boolean deleted) {
        this.user_id = user_id;
        this.recipe_id = recipe_id;
        this.is_sync = is_sync;
        this.mark_MySQL_id = mark_MySQL_id;
        this.deleted = deleted;
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
    public long getMark_MySQL_id() {
        return mark_MySQL_id;
    }
    public void setMark_MySQL_id(long mark_MySQL_id) {
        this.mark_MySQL_id = mark_MySQL_id;
    }
    public boolean isDeleted() {
        return deleted;
    }
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
