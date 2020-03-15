package com.example.habedaee;

import androidx.annotation.NonNull;

public class SuggestionSong extends Song {
    private String list;
    private String creator;

    public SuggestionSong(String title,String artist,String year,String list,String creator){
        super(title,artist,year);
        this.list=list;
        this.creator=creator;
    }

    public String getList() {
        return list.toLowerCase();
    }

    public String getCreator() {
        return creator;
    }

    @NonNull
    @Override
    public String toString() {
        return list+" : "+super.toString();
    }
}
