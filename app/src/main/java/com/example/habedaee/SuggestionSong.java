package com.example.habedaee;

import androidx.annotation.NonNull;

public class SuggestionSong extends Song {
    private String list;
    private String creator;
    private String rejected;

    public SuggestionSong(String title,String artist,String year,String list,String creator,String rejected){
        super(title,artist,year);
        this.list=list;
        this.creator=creator;
        this.rejected=rejected;
    }

    public void setRejected(String s) { rejected = s; }

    public String getList() {
        return list.toLowerCase();
    }

    public String getCreator() {
        return creator;
    }

    public String getRejected() { return rejected; }

    @NonNull
    @Override
    public String toString() {
        return list+" : "+super.toString();
    }
}
