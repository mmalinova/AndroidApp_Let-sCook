package com.example.letscook.database.user;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.letscook.database.relationships.UserMarksRecipeCrossRef;
import com.example.letscook.database.relationships.UserViewsRecipeCrossRef;
import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserDao {
    @Insert(onConflict = REPLACE)
    void register(User user);
    @Query("UPDATE user SET password = :sPassword  WHERE user_id = :sID")
    void updatePass(long sID, String sPassword);
    @Query("UPDATE user SET name = :sName WHERE user_id = :sID")
    void updateName(long sID, String sName);
    @Query("UPDATE user SET email = :sEmail WHERE user_id = :sID")
    void updateEmail(long sID, String sEmail);
    @Query("SELECT * FROM user WHERE email = :sEmail")
    User getUserByEmail(String sEmail);
    @Query("SELECT * FROM user WHERE user_id = :sID")
    User getUserByID(long sID);
    @Query("UPDATE user SET photo = :sPhoto WHERE user_id = :sID")
    void setPhoto(long sID, byte[] sPhoto);
    @Query("UPDATE user SET photo = NULL WHERE user_id = :sID")
    void removePhoto(long sID);
    // Many-to-many relationship
    @Insert(onConflict = REPLACE)
    void insertUserMarksRecipeCrossRef(UserMarksRecipeCrossRef crossRef);
    @Delete
    void deleteUserMarksRecipeCrossRef(UserMarksRecipeCrossRef crossRef);
    // Many-to-many relationship
    @Insert(onConflict = REPLACE)
    void insertUserViewsRecipeCrossRef(UserViewsRecipeCrossRef crossRef);
    @Query("SELECT * FROM user WHERE is_sync = 0")
    List<User> getAllUnSyncUsers();
    @Query("UPDATE user SET is_sync = 1 WHERE user_id = :sID")
    void userSync(long sID);
    @Query("UPDATE user SET is_sync = 0 WHERE user_id = :sID")
    void userUnSync(long sID);
    @Query("UPDATE user SET user_MySQL_id = :serverID WHERE user_id = :sID")
    void setServerID(long sID, long serverID);
    @Query("SELECT * FROM user WHERE user_MySQL_id = :sID")
    User getUserByServerID(long sID);
}
