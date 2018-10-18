package com.csc214.brager_project03.Model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by CarolinaBrager on 4/23/18.
 */

@Dao
public interface UserDao {
    //only selects users that have completed their profile
    @Query("SELECT * FROM users WHERE just_registered=:justRegistered")
    List<User> getAll(boolean justRegistered);

    //get the most recently used id for the users
    @Query("SELECT user_id FROM users ORDER BY user_id desc LIMIT 1")
    int lastId();

    //get the given password of a user given their username
    @Query("SELECT password FROM users WHERE username=:username")
    List<String> getPassword(String username);

    //get an entire user given their username
    @Query("SELECT * FROM users WHERE username=:username")
    User getUserFromUsername(String username);

    //get the entire user given their userid
    @Query("SELECT * FROM users WHERE user_id=:userId")
    User getUserFromUserId(int userId);

    //get the user id of a user given their username
    @Query("SELECT user_id FROM users WHERE username=:username")
    int getUserIdFromUsername(String username);

    //get the number of users with the given username (to check for repitition)
    @Query("SELECT COUNT(*) FROM users WHERE username=:username")
    int getUsernameCount(String username);

    //was going to be used to try and search for users -> I gave up on that attempt
    @Query("SELECT * FROM users WHERE user_first_name LIKE :search OR user_last_name LIKE :search")
    List<User> searchForUsers(String search);

    //Insert a user
    @Insert
    void insertUsers(User... users);

    //update a user
    @Update
    void updateUsers(User... users);

    //delete a user
    @Delete
    void deleteUsers(User... users);

    //delete all users
    @Query("DELETE FROM users")
    public void clear();
}