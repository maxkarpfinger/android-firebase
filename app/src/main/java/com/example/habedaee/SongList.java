package com.example.habedaee;

import androidx.annotation.NonNull;

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

    public void sortByYear(){
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

    public void sortByArtist(){
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
                return  song1.getYear().toLowerCase().compareTo(song2.getYear().toLowerCase());
            }
        });
        Collections.sort(songs, new Comparator<Song>() {
            @Override
            public int compare(Song song1, Song song2)
            {
                return  song1.getArtist().toLowerCase().compareTo(song2.getArtist().toLowerCase());
            }
        });
    }

    public void sortByTitle(){
        Collections.sort(songs, new Comparator<Song>() {
            @Override
            public int compare(Song song1, Song song2)
            {
                return  song1.getYear().toLowerCase().compareTo(song2.getYear().toLowerCase());
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
                return  song1.getTitle().toLowerCase().compareTo(song2.getTitle().toLowerCase());
            }
        });
    }


    public void addSong(Song s){
        songs.add(s);
        sortByYear();
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

    @NonNull
    @Override
    public String toString() {
        StringBuilder stringBuilder=new StringBuilder();
        for(Song s:songs){
            stringBuilder.append(s);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
