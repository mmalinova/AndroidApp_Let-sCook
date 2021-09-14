package com.example.letscook.database.relationships;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserViewsRecipeDao {
    @Query("SELECT * FROM user_views_recipes WHERE view_MySQL_id = :serverID")
    UserViewsRecipeCrossRef getByServerID(long serverID);

    @Query("SELECT * FROM user_views_recipes WHERE (user_id = :sID OR user_id = :userServerID)")
    List<UserViewsRecipeCrossRef> getRecipes(long sID, long userServerID);

    @Query("SELECT * FROM user_views_recipes WHERE is_sync = 0")
    List<UserViewsRecipeCrossRef> getAllUnSyncViews();

    @Query("SELECT * FROM user_views_recipes WHERE user_id = :uID AND recipe_id = :rID")
    UserViewsRecipeCrossRef getByUserIDAndRecipeID(long uID, long rID);

    @Query("SELECT * FROM user_views_recipes WHERE (user_id = :sID OR user_id = :userServerID) AND (recipe_id = :ID OR recipe_id = :recipeServerID)")
    UserViewsRecipeCrossRef getByLocalAndServerIDs(long sID, long userServerID, long ID, long recipeServerID);

    @Query("UPDATE user_views_recipes SET user_id = :sID WHERE user_id = :uID AND recipe_id = :rID")
    void setUserID(long sID, long uID, long rID);

    @Query("UPDATE user_views_recipes SET recipe_id = :sID WHERE user_id = :uID AND recipe_id = :rID")
    void setRecipeID(long sID, long uID, long rID);

    @Query("UPDATE user_views_recipes SET is_sync = 1 WHERE user_id = :sID AND recipe_id = :ID")
    void viewSync(long sID, long ID);

    @Query("UPDATE user_views_recipes SET view_MySQL_id = :serverID WHERE user_id = :sID AND recipe_id = :ID")
    void setServerID(long sID, long ID, long serverID);
}
