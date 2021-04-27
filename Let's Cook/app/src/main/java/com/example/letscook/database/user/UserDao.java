package com.example.letscook.database.user;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.letscook.database.user.User;

import java.util.List;

@Dao
public interface UserDao {
    // One-to-one relationship
    @Transaction
    @Query("SELECT * FROM user WHERE user_id = :sID")
    public User getUserSession(long sID);
}
