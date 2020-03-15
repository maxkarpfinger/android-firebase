package com.example.habedaee;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private static final String TAG ="myLog" ;
    private ArrayList<Song> mDataset;
    private boolean canSuggest;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public MyViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<Song> myDataset,boolean suggest,Context c) {
        canSuggest=suggest;
        mDataset = myDataset;
        context=c;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_song, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.setText(mDataset.get(position).toString());
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canSuggest) {
                    FirebaseAuth.getInstance().getCurrentUser();

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Validate a Song");
                    TextView output = new TextView(context);

                    if (mDataset.get(position) instanceof SuggestionSong) {
                        if (((SuggestionSong) mDataset.get(position)).getCreator().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            output.setText("You cannot validate your own suggestion, do you want to remove it?");
                            builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseFirestore.getInstance().collection("suggestions").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    if (document.get("title")==null){
                                                        continue;
                                                    }
                                                    Song song=new Song(document.get("title").toString(),  document.get("artist").toString(),document.get("year").toString());
                                                    if(song.getTitle().equals(mDataset.get(position).getTitle())&&song.getArtist().equals(mDataset.get(position).getArtist())&&song.getYear().equals(mDataset.get(position).getYear())) {
                                                        FirebaseFirestore.getInstance().collection("suggestions").document(document.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Log.d(TAG,"deleted song from suggestions");
                                                                mDataset.remove(position);
                                                                notifyDataSetChanged();
                                                                ViewPager viewPager = (ViewPager) ((Activity) context).findViewById(R.id.viewpager);
                                                                viewPager.getAdapter().notifyDataSetChanged();
                                                            }
                                                        });
                                                        break;
                                                    }
                                                }
                                            } else {
                                                Log.w(TAG, "Error getting documents.", task.getException());
                                            }
                                        }
                                    });
                                }
                            });
                        } else {
                            output.setText("Do you want to validate " + ((SuggestionSong) mDataset.get(position)).toString());
                            builder.setPositiveButton("Validate", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Map<String, Object> data = new HashMap<>();
                                    data.put("title", mDataset.get(position).getTitle());
                                    data.put("artist", mDataset.get(position).getArtist());
                                    data.put("year", mDataset.get(position).getYear());
                                    if (((SuggestionSong) mDataset.get(position)).getList().equals("oldies")) {
                                        FirebaseFirestore.getInstance().collection("oldies").add(data);
                                    } else if (((SuggestionSong) mDataset.get(position)).getList().equals("2000s")) {
                                        FirebaseFirestore.getInstance().collection("2000s").add(data);
                                    }
                                    FirebaseFirestore.getInstance().collection("suggestions").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (final QueryDocumentSnapshot document : task.getResult()) {
                                                    if (document.get("title")==null){
                                                        continue;
                                                    }
                                                    final Song song=new Song(document.get("title").toString(),  document.get("artist").toString(),document.get("year").toString());
                                                    if(song.getTitle().equals(mDataset.get(position).getTitle())&&song.getArtist().equals(mDataset.get(position).getArtist())&&song.getYear().equals(mDataset.get(position).getYear())) {
                                                        FirebaseFirestore.getInstance().collection("suggestions").document(document.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Log.d(TAG,"deleted song from suggestions");
                                                                if (((SuggestionSong) mDataset.get(position)).getList().equals("oldies")) {
                                                                    Model.get().getSongLists().get(0).getSongs().add(song);
                                                                } else if (((SuggestionSong) mDataset.get(position)).getList().equals("2000s")) {
                                                                    Model.get().getSongLists().get(1).getSongs().add(song);
                                                                }
                                                                mDataset.remove(position);
                                                                notifyDataSetChanged();
                                                                ViewPager viewPager = (ViewPager) ((Activity) context).findViewById(R.id.viewpager);
                                                                viewPager.getAdapter().notifyDataSetChanged();
                                                            }
                                                        });
                                                        break;
                                                    }
                                                }
                                            } else {
                                                Log.w(TAG, "Error getting documents.", task.getException());
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
// Set up the input
                    builder.setView(output);
// Set up the buttons
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}