package com.example.habedaee;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SongListFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    ArrayList<Song> arrayList;
    MyAdapter adapter;
    int page;

    public static SongListFragment newInstance(int someInt) {
        SongListFragment myFragment = new SongListFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", someInt);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_songlist, container, false);
        page=getArguments().getInt("someInt", 0);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        arrayList = new ArrayList<>();


        if(page==2) {
            adapter = new MyAdapter(Model.get().getSongLists().get(page).getSongs(),true,getContext());
        }else{
            adapter = new MyAdapter(Model.get().getSongLists().get(page).getSongs(),false,getContext());
        }
        recyclerView.setAdapter(adapter);
        return view;
    }




}