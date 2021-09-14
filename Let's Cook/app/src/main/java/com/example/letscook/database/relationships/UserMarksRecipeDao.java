package com.example.letscook.database.relationships;

import androidx.room.Dao;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserMarksRecipeDao {
    @Query("SELECT * FROM user_marks_recipes WHERE mark_MySQL_id = :serverID")
    UserMarksRecipeCrossRef getByServerID(long serverID);

    @Query("SELECT * FROM user_marks_recipes WHERE (user_id = :sID OR user_id = :userServerID)")
    List<UserMarksRecipeCrossRef> getRecipes(long sID, long userServerID);

    @Query("SELECT * FROM user_marks_recipes WHERE is_sync = 0")
    List<UserMarksRecipeCrossRef> getAllUnSyncMarks();

    @Query("SELECT * FROM user_marks_recipes WHERE user_id = :userServerID AND recipe_id = :recipeServerID")
    UserMarksRecipeCrossRef getByUserIDAndRecipeID(long userServerID, long recipeServerID);

    @Query("SELECT * FROM user_marks_recipes WHERE (user_id = :sID OR user_id = :userServerID) AND (recipe_id = :ID OR recipe_id = :recipeServerID)")
    UserMarksRecipeCrossRef getByLocalAndServerIDs(long sID, long userServerID, long ID, long recipeServerID);

    @Query("UPDATE user_marks_recipes SET is_sync = 1 WHERE user_id = :sID AND recipe_id = :ID")
    void markSync(long sID, long ID);

    @Query("UPDATE user_marks_recipes SET user_id = :sID WHERE user_id = :ID AND recipe_id = :rID")
    void setUserID(long sID, long ID, long rID);

    @Query("UPDATE user_marks_recipes SET recipe_id = :sID WHERE user_id = :ID AND recipe_id = :rID")
    void setRecipeID(long sID, long ID, long rID);

    @Query("UPDATE user_marks_recipes SET mark_MySQL_id = :serverID WHERE user_id = :sID AND recipe_id = :ID")
    void setServerID(long sID, long ID, long serverID);
}
