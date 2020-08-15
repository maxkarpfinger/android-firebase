package com.example.habedaee;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
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

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionHolder>{
    private static final String TAG ="myLog" ;
    private ArrayList<Song> mDataset;
    private Context context;

    SuggestionAdapter(ArrayList<Song> songs, Context c){
        mDataset = songs;
        context = c;
    }

    @NonNull
    @Override
    public SuggestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from((context));
        return new SuggestionHolder(layoutInflater,parent);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionHolder holder, final int position) {
        holder.getSuggestionInfo().setText(mDataset.get(position).toString());
        final SuggestionSong song = (SuggestionSong) mDataset.get(position);
        boolean isCreator = song.getCreator().equals(FirebaseAuth.getInstance().getCurrentUser().getUid());

        if(isCreator){
            //check if song is still under trial and update confirm image
            if(song.getRejected().equals("true")){
                holder.getConfirm().setBackground(context.getDrawable(R.drawable.rejected));
            }else{
                holder.getConfirm().setBackground(context.getDrawable(R.drawable.question));
            }
            holder.getReject().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                                                ViewPager viewPager = ((Activity) context).findViewById(R.id.viewpager);
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
        }else{
            if(song.getRejected().equals("true")){
                //no listeners needed, but show that song was voted to be rejected
                holder.getConfirm().setVisibility(View.INVISIBLE);
                holder.getReject().setBackground(context.getDrawable(R.drawable.rejected));
            }else{
                //confirm song
                holder.getConfirm().setBackground(context.getDrawable(R.drawable.green_check));
                holder.getReject().setBackground(context.getDrawable(R.drawable.rejected));
                holder.getConfirm().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //update local and global data
                        //update ui
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
                                                    ViewPager viewPager =  ((Activity) context).findViewById(R.id.viewpager);
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

                //reject song and update data && ui
                holder.getReject().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //update local and global data
                        //update ui
                        final Map<String, Object> data = new HashMap<>();
                        data.put("title", mDataset.get(position).getTitle());
                        data.put("artist", mDataset.get(position).getArtist());
                        data.put("year", mDataset.get(position).getYear());
                        data.put("rejected","true");
                        FirebaseFirestore.getInstance().collection("suggestions").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (final QueryDocumentSnapshot document : task.getResult()) {
                                        if (document.get("title")==null){
                                            continue;
                                        }
                                        if(song.getTitle().equals(mDataset.get(position).getTitle())&&song.getArtist().equals(mDataset.get(position).getArtist())&&song.getYear().equals(mDataset.get(position).getYear())) {
                                            FirebaseFirestore.getInstance().collection("suggestions").document(document.getId()).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        song.setRejected("true");
                                                        notifyDataSetChanged();
                                                        ViewPager viewPager = (ViewPager) ((Activity) context).findViewById(R.id.viewpager);
                                                        viewPager.getAdapter().notifyDataSetChanged();
                                                    }else{
                                                        Toast.makeText(context,"Sorry that did not work - try again",Toast.LENGTH_SHORT).show();
                                                    }

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
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
