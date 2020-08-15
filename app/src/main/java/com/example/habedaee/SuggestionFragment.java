package com.example.habedaee;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SuggestionFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private ArrayList<Song> arrayList;
    private SuggestionAdapter adapter;
    private int page;

    public static SuggestionFragment newInstance(int someInt) {
        SuggestionFragment myFragment = new SuggestionFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", someInt);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_suggestions, container, false);
        page=getArguments().getInt("someInt", 0);
        recyclerView = view.findViewById(R.id.suggestion_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        arrayList = new ArrayList<>();

        adapter = new SuggestionAdapter(Model.get().getSongLists().get(page).getSongs(),getActivity());

        recyclerView.setAdapter(adapter);
        return view;
    }
}
