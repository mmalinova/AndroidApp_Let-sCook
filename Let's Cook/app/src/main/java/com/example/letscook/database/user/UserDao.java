package com.example.letscook.database.user;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserDao {
    @Insert(onConflict = REPLACE)
    void register(User user);

    @Query("UPDATE user SET password = :sPassword WHERE user_id = :sID")
    void updatePass(long sID, String sPassword);

    @Query("SELECT * FROM user WHERE email = :sEmail")
    public User getUserByEmail(String sEmail);

    @Query("UPDATE user SET photo = :sPhoto WHERE user_id = :sID")
    void setPhoto(long sID, byte[] sPhoto);

    @Query("UPDATE user SET photo = NULL WHERE user_id = :sID")
    void removePhoto(long sID);

    // One-to-one relationship
    @Transaction
    @Query("SELECT * FROM user WHERE user_id = :sID")
    public User getUserSession(long sID);
}
