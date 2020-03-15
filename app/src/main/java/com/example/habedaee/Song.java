package com.example.habedaee;

import android.util.Log;
import androidx.annotation.NonNull;

public class Song {
    private static final String TAG ="myLog" ;
    private String title;
    private String artist;
    private String year;

    public Song(String title, String artist, String year) {
        this.title = title;
        this.artist = artist;
        this.year = year;
    }

    public static Song parser(String input){
        if(input.length()>0) {
            String title;
            String artist;
            String year;
            int begin = input.indexOf("[");
            int firstComma = input.indexOf(",");
            int secondComma = input.indexOf(",", firstComma + 1);
            int end = input.indexOf("]");
            title = input.substring(begin + 1, firstComma);
            artist = input.substring(firstComma + 2, secondComma);
            year = input.substring(secondComma + 2, end);
            return new Song(title, artist, year);
        }else{
            return new Song("empty","empty","empty");
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @NonNull
    @Override
    public String toString() {
        return "("+year+") "+artist+" - "+title;
    }
}
