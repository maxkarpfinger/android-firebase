package com.example.habedaee;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

public class Model {
    private final ArrayList<SongList> songLists;
    private String[] searchStrings;

    private static Model model;

    public Model(){
        songLists=new ArrayList<>();
        searchStrings = new String[0];
    }

    public static Model get(){
        if(model==null){
            model= new Model();
        }
        return model;
    }

    public void addList(SongList list){
        songLists.add(list);
    }

    public ArrayList<SongList> getSongLists() {
        return songLists;
    }

    public void sort(int i){
        if(i==0){// by year
            for (SongList list:songLists){
                list.sortByYear();
            }
        }else if(i==1){ //by artist
            for (SongList list:songLists){
                list.sortByArtist();
            }
        }else{ //by title
            for (SongList list:songLists){
                list.sortByTitle();
            }
        }
    }

    public boolean containsSong(Song song, String listId, Context context){
        for(SongList list:songLists){
            if(list.getName().equals(listId)) {
                if (list.getSongs().contains(song)) {
                    Toast.makeText(context,list+ " already contains "+song.toString(),Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        }
        return false;
    }

    void createStrings(){
        ArrayList<String> collection = new ArrayList<>();
        for(SongList list:songLists){
            for(Song song:list.getSongs()){
               collection.add(song.getArtist()+" "+song.getTitle());
            }
        }
        searchStrings = new String[collection.size()];
        int counter = 0;
        for(String s: collection){
            searchStrings[counter++]=s;
        }
    }

    public String[] getSearchStrings() {
        return searchStrings;
    }
}
