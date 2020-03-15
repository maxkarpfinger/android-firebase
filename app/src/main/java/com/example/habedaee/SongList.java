package com.example.habedaee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SongList {
    private String name;
    private ArrayList<Song> songs;

    public SongList(String name) {
        this.name = name;
        songs=new ArrayList<>();
    }

    public void sort(){
        Collections.sort(songs, new Comparator<Song>() {
            @Override
            public int compare(Song song1, Song song2)
            {
                return  song1.getTitle().toLowerCase().compareTo(song2.getTitle().toLowerCase());
            }
        });
        Collections.sort(songs, new Comparator<Song>() {
            @Override
            public int compare(Song song1, Song song2)
            {
                return  song1.getArtist().toLowerCase().compareTo(song2.getArtist().toLowerCase());
            }
        });
        Collections.sort(songs, new Comparator<Song>() {
            @Override
            public int compare(Song song1, Song song2)
            {
                return  song1.getYear().toLowerCase().compareTo(song2.getYear().toLowerCase());
            }
        });
    }

    public void addSong(Song s){
        songs.add(s);
        sort();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }
}