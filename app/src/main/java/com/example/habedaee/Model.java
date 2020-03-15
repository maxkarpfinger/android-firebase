package com.example.habedaee;

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
}
