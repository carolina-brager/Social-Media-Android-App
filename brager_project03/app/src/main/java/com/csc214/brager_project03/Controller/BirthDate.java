package com.csc214.brager_project03.Controller;

/**
 * Created by CarolinaBrager on 4/29/18.
 * This class is used to convert values from year, month, and day into a single item called birthdate
 */

public class BirthDate {

    private int year;
    private int month;
    private int day;

    public BirthDate(int year, int month, int day){
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
