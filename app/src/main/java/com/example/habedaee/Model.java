package com.example.habedaee;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

public class Model {
    private ArrayList<SongList> songLists;

    private static Model model;

    public Model(){
        songLists=new ArrayList<>();
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
                    Toast.makeText(context,list+ " already contains "+song.toString(),Toast.LENGTH_SHORT);
                    return true;
                }
            }
        }
        return false;
    }
}
