package com.example.habedaee;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.*;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.*;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 101;
    private static final String TAG ="myLog" ;
    private static String POSITION = "0";
    private TabLayout mTabLayout;
    private Model model;
    private ViewPager viewPager;
    ArrayList<Fragment> fragments;
    FirebaseUser user;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean firstLogin=false;

        model=Model.get();
        model.addList(new SongList("Oldies"));
        model.addList(new SongList("2000s"));
        model.addList(new SongList("Suggestions"));

        if(FirebaseAuth.getInstance().getCurrentUser()==null) {
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build()/*,
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.TwitterBuilder().build()*/);

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
            firstLogin=true;
        }

        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Suggest a Song");

// Set up the input
                final EditText inputTitle = new EditText(MainActivity.this);
                final EditText inputArtist = new EditText(MainActivity.this);
                final EditText inputYear = new EditText(MainActivity.this);
                final EditText inputList = new EditText(MainActivity.this);
                inputTitle.setHint("Title");
                inputArtist.setHint("Artist");
                inputYear.setHint("Year");
                inputList.setHint("List");


                LinearLayout ll=new LinearLayout(MainActivity.this);
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.addView(inputTitle);
                ll.addView(inputArtist);
                ll.addView(inputYear);
                ll.addView(inputList);

                builder.setView(ll);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String title=inputTitle.getText().toString();
                        String artist=inputArtist.getText().toString();
                        String year=inputYear.getText().toString();
                        String list=inputList.getText().toString();
                        String creator=user.getUid();

                        SuggestionSong suggestion=new SuggestionSong(title,artist,year,list,creator,"false");
                        if(model.containsSong(suggestion,list,MainActivity.this)){
                            return;
                        }
                        if (model.containsSong(suggestion,"suggestions",MainActivity.this)){
                            return;
                        }
                        Model.get().getSongLists().get(2).addSong(suggestion);

                        Map<String, Object> newData = new HashMap<>();
                        newData.put("title",title);
                        newData.put("artist",artist);
                        newData.put("year",year);
                        newData.put("list",list);
                        newData.put("creator",creator);

                        db.collection("suggestions").add(newData);

                        viewPager.getAdapter().notifyDataSetChanged();
                        viewPager.setCurrentItem(2);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
        mTabLayout = findViewById(R.id.tabs);

        viewPager=findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);

        fragments=new ArrayList<>();
        fragments.add(SongListFragment.newInstance(0));
        fragments.add(SongListFragment.newInstance(1));
        fragments.add(SuggestionFragment.newInstance(2));

        FragmentAdapter pagerAdapter = new FragmentAdapter(getSupportFragmentManager(), getApplicationContext(), fragments);
        viewPager.setAdapter(pagerAdapter);

        mTabLayout.setupWithViewPager(viewPager);

        if(!firstLogin){
            user = FirebaseAuth.getInstance().getCurrentUser();
            db = FirebaseFirestore.getInstance();
            requestSongs(db,"oldies",0);
            requestSongs(db,"2000s",1);
            requestSongs(db,"suggestions",2);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reload) {
            requestSongs(db,"oldies",0);
            requestSongs(db,"2000s",1);
            requestSongs(db,"suggestions",2);
            return true;
        }else if(id==R.id.action_sort_year){
            model.sort(0);
        }else if(id==R.id.action_sort_artist){
            model.sort(1);
        }else if(id==R.id.action_sort_title){
            model.sort(2);
        }else if(id==R.id.action_logout){
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // ...
                        }
                    });
            finish();
        }else if(id==R.id.action_copy){
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("current list", model.getSongLists().get(mTabLayout.getSelectedTabPosition()).toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(MainActivity.this,"Copied current list to clip board",Toast.LENGTH_SHORT).show();
            return true;
        }

        RecyclerView recyclerView =  findViewById(R.id.recycler_view);
        recyclerView.getAdapter().notifyDataSetChanged();
        viewPager.getAdapter().notifyDataSetChanged();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                user = FirebaseAuth.getInstance().getCurrentUser();
                db = FirebaseFirestore.getInstance();

                requestSongs(db,"oldies",0);
                requestSongs(db,"2000s",1);
                requestSongs(db,"suggestions",2);

                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }



    private void requestSongs(FirebaseFirestore db,String name,final int index){
        db.collection(name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Song> songs=new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.get("title")==null){
                                    continue;
                                }
                                if(index==2){
                                    SuggestionSong song=new SuggestionSong(document.get("title").toString(), document.get("artist").toString(), document.get("year").toString(),document.get("list").toString(),document.get("creator").toString(),document.get("rejected").toString());
                                    songs.add(song);
                                }else {
                                    Song song = new Song(document.get("title").toString(), document.get("artist").toString(), document.get("year").toString());
                                    songs.add(song);
                                }
                                //System.out.println(song);
                            }
                            model.getSongLists().get(index).setSongs(songs);
                            model.getSongLists().get(index).sortByYear();

                            RecyclerView recyclerView =  findViewById(R.id.recycler_view);

                            // use a linear layout manager
                           /* LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                            recyclerView.setLayoutManager(layoutManager);
                            // specify an adapter (see also next example)
                            MyAdapter mAdapter = new MyAdapter(model.getSongLists().get(index).getSongs());
                            recyclerView.setAdapter(mAdapter);*/
                            recyclerView.getAdapter().notifyDataSetChanged();
                            viewPager.getAdapter().notifyDataSetChanged();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION, mTabLayout.getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        viewPager.setCurrentItem(savedInstanceState.getInt(POSITION));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
