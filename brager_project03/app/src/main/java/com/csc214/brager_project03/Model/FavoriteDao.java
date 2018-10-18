package com.csc214.brager_project03.Model;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by CarolinaBrager on 4/24/18.
 */

@Dao
public interface FavoriteDao {
    //Get all the favorites in the table
    @Query("SELECT * FROM favorites")
    List<Favorite> getAll();

    //Get a the id of a user (favorite_user_id) favorited by another user given their id(primary_user_id)
    @Query("SELECT favorite_user_id FROM favorites WHERE primary_user_id=:primaryUserId")
    List<Integer> findUserFavoriteUserId(final int primaryUserId);

    //Get all of the favorites for a given user given their id (primary_user_id)
    @Query("SELECT * FROM favorites WHERE primary_user_id=:primaryUserId")
    List<Favorite> findUserFavorites(final int primaryUserId);

    //Get a the id of a user (favorite_user_id) favorited by another user given their id(primary_user_id)
    @Query("SELECT favorite_user_id FROM favorites WHERE primary_user_id=:primaryUserId")
    List<Integer> findFavoriteUserIdFromUserId(final int primaryUserId);

    //Get the last id used for favorites
    @Query("SELECT favorite_id FROM favorites ORDER BY favorite_id desc LIMIT 1")
    int lastId();

    //Returns the Favorite if the primary_user has favorited the favorite_user
    @Query("SELECT * From favorites WHERE primary_user_id=:primaryUserId AND favorite_user_id=:favoriteUserId")
    Favorite getFavoriteBetweenTwo(int primaryUserId, int favoriteUserId);

    //Returns a boolean indicating if the primary_user has favorited the favorite_user
    @Query("SELECT CAST(CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END AS BIT) FROM favorites WHERE primary_user_id=:primaryUserId AND favorite_user_id=:favoriteUserId")
    boolean isThereFavoriteBetweenTwo(int primaryUserId, int favoriteUserId);

    //Inserts a favorite into the table
    @Insert
    void insertFavorites(Favorite... favorites);

    //Deletes a favorite from the table
    @Delete
    void deleteFavorites(Favorite... favorites);

    //Deletes all of the favorites
    @Query("DELETE FROM favorites")
    public void clear();
}