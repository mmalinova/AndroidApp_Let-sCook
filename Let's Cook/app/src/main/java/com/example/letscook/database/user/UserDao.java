package com.example.letscook.database.user;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.letscook.database.relationships.UserMarksRecipeCrossRef;
import com.example.letscook.database.relationships.UserMarksRecipes;
import com.example.letscook.database.relationships.UserViewsRecipeCrossRef;
import com.example.letscook.database.relationships.UserViewsRecipes;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserDao {
    @Insert(onConflict = REPLACE)
    void register(User user);

    @Query("UPDATE user SET password = :sPassword WHERE user_id = :sID")
    void updatePass(long sID, String sPassword);

    @Query("UPDATE user SET name = :sName WHERE user_id = :sID")
    void updateName(long sID, String sName);

    @Query("UPDATE user SET email = :sEmail WHERE user_id = :sID")
    void updateEmail(long sID, String sEmail);

    @Query("SELECT * FROM user WHERE email = :sEmail")
    User getUserByEmail(String sEmail);

    @Query("UPDATE user SET photo = :sPhoto WHERE user_id = :sID")
    void setPhoto(long sID, byte[] sPhoto);

    @Query("UPDATE user SET photo = NULL WHERE user_id = :sID")
    void removePhoto(long sID);

    // Many-to-many relationship
    @Insert(onConflict = REPLACE)
    void insertUserMarksRecipeCrossRef(UserMarksRecipeCrossRef crossRef);

    @Delete
    void deleteUserMarksRecipeCrossRef(UserMarksRecipeCrossRef crossRef);

    @Transaction
    @Query("SELECT * FROM user WHERE user_id = :sID")
    List<UserMarksRecipes> getUserMarksRecipes(long sID);

    // Many-to-many relationship
    @Insert(onConflict = REPLACE)
    void insertUserViewsRecipeCrossRef(UserViewsRecipeCrossRef crossRef);

    @Transaction
    @Query("SELECT * FROM user WHERE user_id = :sID")
    List<UserViewsRecipes> getUserViewsRecipes(long sID);
}
