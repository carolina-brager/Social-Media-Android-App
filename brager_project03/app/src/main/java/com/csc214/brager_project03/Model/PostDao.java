package com.csc214.brager_project03.Model;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by CarolinaBrager on 4/23/18.
 */

@Dao
public interface PostDao {
    //Gets all of the posts in the table
    @Query("SELECT * FROM posts")
    List<Post> getAll();

    //gets posts from a given user
    @Query("SELECT * FROM posts WHERE post_user_id=:userId")
    List<Post> findPostsForUser(final int userId);

    //gets post from a given user in descending chronological order
    @Query("SELECT * FROM posts WHERE post_user_id=:userId ORDER BY post_id desc")
    List<Post> findPostsForUserReverse(final int userId);

    //gets the last id used for posts
    @Query("SELECT post_id FROM posts ORDER BY post_id desc LIMIT 1")
    int lastId();

    //gets the user id of the user that created the post
    @Query("SELECT post_user_id FROM posts WHERE post_id=:postId")
    int getUserIdFromPostId(int postId);

    //gets all posts in reverse chronological order
    @Query("SELECT * FROM posts ORDER BY post_id desc")
    List<Post> getAllReverseChronologicalOrder();

    //Gets the posts given a boolean
    @Query("SELECT * FROM posts WHERE :areFavorites=1 ORDER BY post_id desc")
    List<Post> getFavoritesReverseChronologicalOrder(boolean areFavorites);

    //Inserts posts into the table
    @Insert
    void insertPosts(Post... posts);

    //Deletes posts from the table
    @Delete
    void deletePosts(Post... posts);

    //Deletes all posts from the table
    @Query("DELETE FROM posts")
    public void clear();
}