package com.example.movieappproject;

import java.io.Serializable;

public class Movie implements Serializable {
    private String id;
    private String title;
    private String year;
    private String rating;
    private String summary;
    private String large_cover_image;
    private int runtime;

    public Movie(){}

    public Movie(String id, String title, String year, String rating, String summary, String large_cover_image, int runtime) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.rating = rating;
        this.summary = summary;
        this.large_cover_image = large_cover_image;
        this.runtime = runtime;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLarge_cover_image() {
        return large_cover_image;
    }

    public void setLarge_cover_image(String large_cover_image) {
        this.large_cover_image = large_cover_image;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }
}
