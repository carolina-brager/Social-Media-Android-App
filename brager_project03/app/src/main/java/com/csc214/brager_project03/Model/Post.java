package com.csc214.brager_project03.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by CarolinaBrager on 4/20/18.
 */

@Entity(tableName = "posts")
public class Post {
    @PrimaryKey
    @ColumnInfo(name = "post_id")
    private int postId;

    @ColumnInfo(name = "post_user_id")
    private int userId;

    @ColumnInfo(name = "post_text")
    private String text;

    @ColumnInfo(name = "post_title")
    private String title;

    @ColumnInfo(name = "post_image_path")
    private String imagePath;

    public Post(){

    }

    //region getters and setters
    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    //endregion
}