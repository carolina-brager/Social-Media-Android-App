package com.csc214.brager_project03.Model;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.media.Image;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by CarolinaBrager on 4/17/18.
 */

@Entity(tableName = "users")
public class User{

    @PrimaryKey
    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "username")
    private String username;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "user_first_name")
    private String firstName;

    @ColumnInfo(name = "user_last_name")
    private String lastName;

    @ColumnInfo(name = "user_dob")
    private String dob;

    @ColumnInfo(name = "user_hometown")
    private String hometown;

    @ColumnInfo(name = "user_profile_pic_path")
    private String profilePicPath;

    @ColumnInfo(name= "user_bio")
    private String bio;

    @ColumnInfo(name = "just_registered")
    private boolean justRegistered;

    public User(){

    }

    //region getters and setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int user_id) {
        this.userId = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public String getProfilePicPath() {
        return profilePicPath;
    }

    public void setProfilePicPath(String profilePicPath) {
        this.profilePicPath = profilePicPath;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public boolean isJustRegistered() {
        return justRegistered;
    }

    public void setJustRegistered(boolean justRegistered) {
        this.justRegistered = justRegistered;
    }

    //endregion
}