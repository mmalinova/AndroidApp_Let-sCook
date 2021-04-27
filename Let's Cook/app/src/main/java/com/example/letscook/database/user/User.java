package com.example.letscook.database.user;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import com.example.letscook.database.session.Session;

import java.io.Serializable;

//Define table
@Entity(tableName = "user")
public class User implements Serializable {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    private long ID;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "email")
    private String email;
    @ColumnInfo(name = "password")
    private String password;
    @ColumnInfo(name = "photo")
    private byte[] photo;
    @ColumnInfo(name = "my_products")
    private long myProducts;
    @ColumnInfo(name = "shopping_list")
    private long shoppingList;
    @ColumnInfo(name = "fav_recipes")
    private long favRecipes;
    @ColumnInfo(name = "last_viewed")
    private long lastViewed;
    @ColumnInfo(name = "my_recipes")
    private long myRecipes;
    @ColumnInfo(name = "my_session")
    private long mySession;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public long getMyProducts() {
        return myProducts;
    }

    public void setMyProducts(long myProducts) {
        this.myProducts = myProducts;
    }

    public long getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(long shoppingList) {
        this.shoppingList = shoppingList;
    }

    public long getFavRecipes() {
        return favRecipes;
    }

    public void setFavRecipes(long favRecipes) {
        this.favRecipes = favRecipes;
    }

    public long getLastViewed() {
        return lastViewed;
    }

    public void setLastViewed(long lastViewed) {
        this.lastViewed = lastViewed;
    }

    public long getMyRecipes() {
        return myRecipes;
    }

    public void setMyRecipes(long myRecipes) {
        this.myRecipes = myRecipes;
    }

    public long getMySession() {
        return mySession;
    }

    public void setMySession(long mySession) {
        this.mySession = mySession;
    }
}
