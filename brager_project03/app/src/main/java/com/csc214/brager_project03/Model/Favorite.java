package com.csc214.brager_project03.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by CarolinaBrager on 4/23/18.
 */

@Entity(tableName = "favorites")
public class Favorite {
    @PrimaryKey
    @ColumnInfo(name = "favorite_id")
    private int favoriteId;

    @ColumnInfo(name = "primary_user_id")
    private int userId;

    @ColumnInfo(name = "favorite_user_id")
    private int favoriteUserId;

    public Favorite(){

    }

    //region getters and setters
    public int getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(int favoriteId) {
        this.favoriteId = favoriteId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFavoriteUserId() {
        return favoriteUserId;
    }

    public void setFavoriteUserId(int favoriteUserId) {
        this.favoriteUserId = favoriteUserId;
    }

    //endregion
}
