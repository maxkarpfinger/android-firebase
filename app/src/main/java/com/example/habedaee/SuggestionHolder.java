package com.example.habedaee;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SuggestionHolder extends RecyclerView.ViewHolder {

    private final TextView suggestionInfo;
    private final Button confirm;
    private final Button reject;

    public SuggestionHolder(LayoutInflater inflater, ViewGroup parent) {
        super(inflater.inflate(R.layout.list_suggestion,parent,false));
        suggestionInfo = itemView.findViewById(R.id.textview_info);
        confirm = itemView.findViewById(R.id.button_confirm);
        reject = itemView.findViewById(R.id.button_reject);
    }

    public TextView getSuggestionInfo() {
        return suggestionInfo;
    }

    public Button getConfirm() {
        return confirm;
    }

    public Button getReject() {
        return reject;
    }
}
