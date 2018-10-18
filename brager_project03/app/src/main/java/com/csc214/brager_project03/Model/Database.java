package com.csc214.brager_project03.Model;

import android.arch.persistence.room.RoomDatabase;

/**
 * Created by CarolinaBrager on 4/23/18.
 * The database includes methods to get the tables in the database
 */

@android.arch.persistence.room.Database(entities = {Favorite.class, Post.class, User.class}, version = 3)
public abstract class Database extends RoomDatabase {
    public abstract FavoriteDao favoriteDao();
    public abstract PostDao postDao();
    public abstract UserDao userDao();
}