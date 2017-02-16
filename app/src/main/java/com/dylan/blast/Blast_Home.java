package com.dylan.blast;

import extras.SharedPreferencesEditor;
import extras.WebRequester;
import extras.locationUpdater;
import extras.BlastMediaPlayer;

import extras.variables;
import tabswipe.adapter.TabsPagerAdapter;


import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;


import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;

import android.net.Uri;

import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;

import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.content.Intent;
import android.os.Bundle;
import extras.CustomViewPager;



import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.roughike.bottombar.*;
import android.support.v7.app.ActionBar;

public class Blast_Home extends AppCompatActivity implements
        Blast_Radius.OnFragmentInteractionListener,
        Trailing.OnFragmentInteractionListener,
        Trending.OnFragmentInteractionListener,
        Past_Blasts.OnFragmentInteractionListener{
    public static  BottomBar mBottomBar;
	public static CustomViewPager viewPager;
    private TabsPagerAdapter mAdapter = null;
    private ActionBar actionBar;
    private ListView mDrawerList;
    private ArrayAdapter<String> navAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    // Tab titles
    private String[] tabs = { "Blast Radius","Trailing", "Trending", "Past Blasts" };
    private static BlastMediaPlayer player = BlastMediaPlayer.getInstance();
    public static BlastAdapter.ViewHolder latestView = null;
    public static Post lastPost = null;
    public static CountDownTimer second5 = null;
    public static Post postForDetail = null;
    public static List<String> followList = null;
    public static ArrayList<User> searchUsers = null;
    public static int currentTab = 0;
    public static int playingTab = 0;
    public static String radiusResult = null;
    public boolean showHome=true;
    public Context c;
    public List<ShowcaseView.Builder> scvs = new ArrayList<ShowcaseView.Builder>();
    Post nowPlaying = null;
    @Override
    public void onBackPressed() {
        if(second5 != null)second5.cancel();
            getSupportFragmentManager().popBackStack();

                    if (Blast_Radius.adapter != null) Blast_Radius.adapter.notifyDataSetChanged();

                    if (Trailing.adapter != null) Trailing.adapter.notifyDataSetChanged();

                    if (Trending.adapter != null) Trending.adapter.notifyDataSetChanged();

                    if (Past_Blasts.adapter != null) Past_Blasts.adapter.notifyDataSetChanged();

        if(BlastAdapterTrending.timer != null){BlastAdapterTrending.timer.cancel();}
        stopStreamMusic();
        if(showHome){
            //mBottomBar.show();
            showHome=false;
        }
        else{
            //mBottomBar.hide();
            showHome=true;
        }
    }


    public static void startStreamMusic(final Context c,final Post post,final BlastAdapter.ViewHolder view, final CountDownTimer timer, final BlastAdapter adapter){

        if (second5!=null){
            second5.cancel();
        }

                try {
                    stopStreamMusic();
                    player.mp = new MediaPlayer();

                    player.mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(final MediaPlayer mp) {
                            post.isLoading = false;
                            post.isPlaying = true;
                            if (view != null) {
                                view.alubmOverlay.setVisibility(View.VISIBLE);
                                //adapter.notifyDataSetChanged();
                            }
                            mp.start();
                            if(adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                            //if (timer != null) timer.start();
                        }
                    });


                    player.mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

                    if(post.path_to_song.split("/").length == 2) {

                        player.mp.setDataSource(Uri.parse(String.format("%s/blast/users/%s/%s.mp3", variables.server, post.path_to_song, post.songID)).toString());
                    }
                    else{
                        player.mp.setDataSource(Uri.parse(post.path_to_song).toString());
                    }

                    player.mp.prepareAsync();

                    player.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            adapter.stop(post);
                            if(variables.autoPlay)adapter.autoplay(post);
                            System.out.println("Completed");
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error connecting to server");
                }


    }

    public static void startStreamMusic(final Context c,final Post post,final BlastAdapterTrending.ViewHolder view, final CountDownTimer timer, final BlastAdapterTrending adapter){

        if (second5!=null){
            second5.cancel();
        }

        try {
            stopStreamMusic();
            player.mp = new MediaPlayer();

            player.mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mp) {
                    post.isLoading = false;
                    post.isPlaying=true;
                    if (view != null) {
                        view.alubmOverlay.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    }
                    mp.start();
                    //if (timer != null) timer.start();
                }
            });

            player.mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

            if(post.path_to_song.split("/").length == 2) {

                player.mp.setDataSource(Uri.parse(String.format("%s/blast/users/%s/%s.mp3", variables.server, post.path_to_song, post.songID)).toString());
            }
            else{
                player.mp.setDataSource(Uri.parse(post.path_to_song).toString());
            }


            player.mp.prepareAsync();

            player.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    adapter.stop(post);
                    if(variables.autoPlay)adapter.autoplay(post);
                    System.out.println("Completed");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error connecting to server");
        }


    }

    public static void stopStreamMusic(){
        System.out.println("Im here");
        if(lastPost != null && lastPost.isPlaying && mBottomBar.getCurrentTabPosition() != playingTab){
            //dont stop
            System.out.println("here");
        }
        else {
            if (second5 != null) {
                second5.cancel();
            }
            if (player.mp != null) {
                player.mp.stop();
                player.mp.reset();
            }
            if (lastPost != null) {
                lastPost.isPaused = false;
                lastPost.isLoading = false;
                lastPost.isPlaying = false;
            }
        }
    }

    public static void stopStreamMusic(boolean force){
            if (second5 != null) {
                second5.cancel();
            }
            if (player.mp != null) {
                player.mp.stop();
                player.mp.reset();
            }
            if (lastPost != null) {
                lastPost.isPaused = false;
                lastPost.isLoading = false;
                lastPost.isPlaying = false;
            }
        }

    public static void pauseStreamMusic(){
        if(player.mp!=null) {
            player.mp.pause();
        }
    }

    public static void resumeStreamMusic(){
        if(player.mp!=null) {
            player.mp.start();
        }
    }


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            System.gc();
//            getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
            setContentView(R.layout.layout_blast_home);
            c = getBaseContext();
            Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
            toolbar.inflateMenu(R.menu.bottombar_menu);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setElevation(6);
            toolbar.setNavigationIcon(R.drawable.ic_menu_black);
            toolbar.setTitleTextColor(Color.BLACK);
            mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
            mActivityTitle = getSupportActionBar().getTitle().toString();
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                    R.string.drawer_open, R.string.drawer_close) {

                /** Called when a drawer has settled in a completely open state. */
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    mActivityTitle = getSupportActionBar().getTitle().toString();
                    getSupportActionBar().setTitle("Menu");
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }

                /** Called when a drawer has settled in a completely closed state. */
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    getSupportActionBar().setTitle(mActivityTitle);
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }
            };
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerToggle.setHomeAsUpIndicator(getDrawable(R.drawable.ic_menu_black));
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerList = (ListView)findViewById(R.id.navList);
            navAdapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_black, getResources().getStringArray(R.array.menu_items));
            mDrawerList.setAdapter(navAdapter);
            mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0:
                            displayAvailableFiles(null);
                            break;
                        case 1:
                            spotifySearch(null);
                            break;
                        case 2:
                            profile(null);
                            break;
                        case 3:
                            radius(null);
                            break;
                        case 4:
                            autoplay(null);
                            break;
                        case 5:
                            search(null);
                            break;
                        case 6:
                            logout(null);
                            break;
                        default:
                            mDrawerLayout.closeDrawers();
                            mBottomBar.setSelected(false);
                            mBottomBar.clearFocus();
                            break;
                }
            }});
        followList = variables.followList;
        variables.autoPlay = SharedPreferencesEditor.recieveBoolean(getBaseContext(),"autoplay");
        if(mAdapter!= null) refresh();
        viewPager = (CustomViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(3);
        actionBar = getSupportActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
            mBottomBar = (BottomBar) findViewById(R.id.bottomBar);
            mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
                @Override
                public void onTabSelected(@IdRes int menuItemId) {
                    switch (menuItemId) {
                        case R.id.bb_menu_radius:
                            mDrawerLayout.closeDrawers();
                            viewPager.setCurrentItem(0);
                            currentTab = 0;
                            mActivityTitle = actionBar.getTitle().toString();
                            break;
                        case R.id.bb_menu_trailing:
                            mDrawerLayout.closeDrawers();
                            viewPager.setCurrentItem(1);
                            currentTab = 1;
                            mActivityTitle = actionBar.getTitle().toString();

                            break;
                        case R.id.bb_menu_trending:
                            mDrawerLayout.closeDrawers();
                            viewPager.setCurrentItem(2);
                            currentTab = 2;
                            mActivityTitle = actionBar.getTitle().toString();
                            break;
                        case R.id.bb_menu_past_blasts:
                            mDrawerLayout.closeDrawers();
                            viewPager.setCurrentItem(3);
                            currentTab = 3;
                            mActivityTitle = actionBar.getTitle().toString();
                            break;
                        default:
                            mDrawerLayout.closeDrawers();
                            mBottomBar.setSelected(false);
                            mBottomBar.clearFocus();
                            break;

                    }
                }
            });
            mBottomBar.setOnTabReselectListener(new OnTabReselectListener() {

                @Override
                public void onTabReSelected(@IdRes int menuItemId) {
                    switch (menuItemId) {
                        case R.id.bb_menu_radius:
                            stopStreamMusic();
                            Blast_Radius.refreshPosts(c);
                            break;
                        case R.id.bb_menu_trailing:
                            stopStreamMusic();
                            Trailing.refreshPosts(c);
                            break;
                        case R.id.bb_menu_trending:
                            stopStreamMusic();
                            Trending.refreshPosts(c);
                            break;
                        case R.id.bb_menu_past_blasts:
                            stopStreamMusic();
                            Past_Blasts.refreshPosts(c);
                            break;
                        default:
                            mDrawerLayout.closeDrawers();
                            mBottomBar.setSelected(false);
                            mBottomBar.clearFocus();
                            break;
                    }
                }
            }
            );
        actionBar.setTitle("Blast Radius - 10 miles");
            setTitle();

//        actionBar.setHomeButtonEnabled(false);
//        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bg_post_row)));
//        actionBar.setStackedBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bg_post_row)));
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
 
        // Adding Tabs
        //for (String tab_name : tabs) {
        //    actionBar.addTab(actionBar.newTab().setCustomView(getTabLayout(tab_name))
       //             .setTabListener(this));
       // }
        //setTabsMaxWidth();


        //variables.pd.dismiss();
 
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        	 
            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                //actionBar.setSelectedNavigationItem(position);
                switch (position) {
                    case 0:
                        if (Blast_Radius.adapter != null) Blast_Radius.adapter.notifyDataSetChanged();
                        switch (variables.radius) {
                            case "1":
                                actionBar.setTitle(String.format("Blast Radius - 1 mile"));
                                setTitle();
                                break;
                            case "5":
                                actionBar.setTitle(String.format("Blast Radius - 5 miles"));
                                setTitle();
                                break;
                            case "10":
                                actionBar.setTitle(String.format("Blast Radius - 10 miles"));
                                setTitle();
                                break;
                            case "25":
                                actionBar.setTitle(String.format("Blast Radius - 25 miles"));
                                setTitle();
                                break;
                            default:
                                actionBar.setTitle(String.format("Blast Radius - 10 miles"));
                                setTitle();
                                break;
                        }
                        Blast_Radius.mSwipeRefreshLayout.setRefreshing(false);
                        currentTab = position;
                        mBottomBar.selectTabAtPosition(position);
                        break;
                    case 1:
                        if (Trailing.adapter != null) Trailing.adapter.notifyDataSetChanged();
                        actionBar.setTitle("Trailing");
                        setTitle();
                        Trailing.mSwipeRefreshLayout.setRefreshing(false);
                        currentTab = position;
                        mBottomBar.selectTabAtPosition(position);
                        break;
                    case 2:
                        if (Trending.adapter != null) Trending.adapter.notifyDataSetChanged();
                        actionBar.setTitle("Trending");
                        setTitle();
                        Trending.mSwipeRefreshLayout.setRefreshing(false);
                        currentTab = position;
                        mBottomBar.selectTabAtPosition(position);
                        break;
                    case 3:
                        if (Past_Blasts.adapter != null) Past_Blasts.adapter.notifyDataSetChanged();
                        actionBar.setTitle("Blasts from your Past");
                        setTitle();
                        Past_Blasts.mSwipeRefreshLayout.setRefreshing(false);
                        currentTab = position;
                        mBottomBar.selectTabAtPosition(position);
                        break;
                    default:
                        break;
                }

            }
 
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
//                stopStreamMusic();
//                if (lastPost != null) {
//                    lastPost.isPlaying = false;
//                    lastPost.isLoading = false;
//                }
//                if(BlastAdapter.timer != null){
//                    BlastAdapter.timer.cancel();
//                }
//                BlastAdapter.timer = null;
//                if(second5!=null){
//                    second5.cancel();
//                    second5 = null;
//                }

            }
 
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });


        if(locationUpdater.updateLocation(Blast_Home.this)){
                locationUpdater.quickLoc();
        }
        else{
            AlertDialog.Builder warn = new AlertDialog.Builder(Blast_Home.this,R.style.MyDialogTheme);
            warn.setTitle("Whoops!!");
            warn.setMessage("It seems you don't have location services enabled! \n Please enable to get the best functionality of the app.");
            warn.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    finish();
                }
            });
            warn.show();
        }

            //displayShowcase();



    }

    public void setTitle(){
        mActivityTitle = actionBar.getTitle().toString();
    }

 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //getMenuInflater().inflate(R.menu.blast_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void search(MenuItem item){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        LayoutInflater inflater = this.getLayoutInflater();
        final View v = inflater.inflate(R.layout.search_dialog,null);
        final EditText search = (EditText)v.findViewById(R.id.search_field);
        builder.setView(v).setPositiveButton("Search", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (!search.getText().toString().isEmpty()) {
                    //Toast.makeText(getApplicationContext(), search.getText().toString(), Toast.LENGTH_LONG).show();
                    new searchUsers(search.getText().toString()).execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Nothing to search", Toast.LENGTH_LONG).show();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
       AlertDialog dialog = builder.create();
        dialog.setTitle(Html.fromHtml("<font color='#000000'>Search For Users...</font>"));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int wordsLength = countWords(s.toString());// words.length;
                // count == 0 means a new word is going to start
                if (count == 0 && wordsLength >= 2) {
                    setCharLimit(search, search.getText().length());
                } else {
                    removeFilter(search);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
            private int countWords(String s) {
                String trim = s.trim();
                if (trim.isEmpty())
                    return 0;
                return trim.split("\\s+").length; // separate string around spaces
            }

            private InputFilter filter;

            private void setCharLimit(EditText et, int max) {
                filter = new InputFilter.LengthFilter(max);
                et.setFilters(new InputFilter[] { filter });
            }

            private void removeFilter(EditText et) {
                if (filter != null) {
                    et.setFilters(new InputFilter[0]);
                    filter = null;
                }
            }
        });

    }

    public void radius(MenuItem item){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        LayoutInflater inflater = this.getLayoutInflater();
        final View v = inflater.inflate(R.layout.radius_dialog,null);
        Spinner spinner = (Spinner) v.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                radiusResult = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.radius_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        builder.setView(v).setPositiveButton("Submit", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                switch (radiusResult) {
                    case "1 mile":
                        variables.radius = "1";
                        break;
                    case "5 miles":
                        variables.radius = "5";
                        break;
                    case "10 miles":
                        variables.radius = "10";
                        break;
                    case "25 miles":
                        variables.radius = "25";
                        break;
                    default:
                        variables.radius = "10";
                        break;

                }
                System.out.println(variables.radius);
                Blast_Radius.refreshPosts(getBaseContext());
                Blast_Radius.mSwipeRefreshLayout.setRefreshing(true);
                dialog.dismiss();
                mDrawerLayout.closeDrawers();
                setRadiusTitle(radiusResult);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setTitle(Html.fromHtml("<font color='#000000'>Change Blast Radius</font>"));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    public void setRadiusTitle(String radiusResult){
        System.out.println(mActivityTitle);
        if(mActivityTitle.contains("Radius")){
            System.out.println("Chaning title");
            switch (radiusResult) {
                case "1 mile":
                    actionBar.setTitle(String.format("Blast Radius - 1 mile"));
                    System.out.println(1);
                    setTitle();
                    break;
                case "5 miles":
                    actionBar.setTitle(String.format("Blast Radius - 5 miles"));
                    System.out.println(5);
                    setTitle();
                    break;
                case "10 miles":
                    actionBar.setTitle(String.format("Blast Radius - 10 miles"));
                    System.out.println(10);
                    setTitle();
                    break;
                case "25 miles":
                    actionBar.setTitle(String.format("Blast Radius - 25 miles"));
                    System.out.println(25);
                    setTitle();
                    break;
                default:
                    actionBar.setTitle(String.format("Blast Radius - 10 mile"));
                    System.out.println("default");
                    setTitle();
                    break;
            }
        }
    }

    public void profile(MenuItem item){
        Intent i = new Intent(getBaseContext(),ProfileActivity.class);
        startActivity(i);
        stopStreamMusic();
    }
    
    public void logout(MenuItem item){

    	SharedPreferencesEditor.removeKey(Blast_Home.this, "username");
        SharedPreferencesEditor.removeKey(Blast_Home.this, "login");
        SharedPreferencesEditor.removeKey(Blast_Home.this, "userID");
    	Intent intent = new Intent(Blast_Home.this,Blast_Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
        finish();
        Past_Blasts.adapterSet = false;
        Blast_Radius.adapterSet=false;
        Trailing.adapterSet=false;
        return;
    }

    public void autoplay(MenuItem item){
        if(variables.autoPlay == true){
            variables.autoPlay = false;
            SharedPreferencesEditor.updateKey(getBaseContext(),"autoplay",false);
            Toast.makeText(getBaseContext(),"Autoplay disabled.",Toast.LENGTH_SHORT).show();
        }
        else{
            variables.autoPlay = true;
            SharedPreferencesEditor.updateKey(getBaseContext(),"autoplay",true);
            Toast.makeText(getBaseContext(),"Autoplay enabled.",Toast.LENGTH_SHORT).show();
        }
    }

    public void refresh(){
        locationUpdater.updateLocation(Blast_Home.this);
        Blast_Radius.refreshPosts(Blast_Home.this);
        Past_Blasts.refreshPosts(Blast_Home.this);
        Trending.refreshPosts(Blast_Home.this);
        Trailing.refreshPosts(Blast_Home.this);
    }

    public void refresh(View view){
        stopStreamMusic();
        locationUpdater.updateLocation(Blast_Home.this);
        Blast_Radius.refreshPosts(Blast_Home.this);
        Past_Blasts.refreshPosts(Blast_Home.this);
        Trending.refreshPosts(Blast_Home.this);
        Trailing.refreshPosts(Blast_Home.this);
    }

    public void spotifySearch(MenuItem item){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        LayoutInflater inflater = this.getLayoutInflater();
        final View v = inflater.inflate(R.layout.spotify_serach_dialog,null);
        final EditText artist = (EditText) v.findViewById(R.id.artist);
        final EditText song = (EditText) v.findViewById(R.id.song);
        builder.setView(v).setPositiveButton("Search", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {

                if(!artist.getText().toString().isEmpty() || !song.getText().toString().isEmpty()){
                    new spotifySearchTask(artist.getText().toString(),song.getText().toString()).execute();
                }

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setTitle(Html.fromHtml("<font color='#000000'>Search for music on Spotify</font>"));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        mDrawerLayout.closeDrawers();
    }

    public void spotifyResults(final ArrayList<Objects.spotifyTrack> tracks){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        final LayoutInflater inflater = this.getLayoutInflater();
        final SpotifyTrackAdpater spotifyAdapter = new SpotifyTrackAdpater(this,tracks);
        final View v = inflater.inflate(R.layout.spotify_results_dialog,null);
        final ListView results = (ListView) v.findViewById(R.id.results);
        final TextView noResults = (TextView) v.findViewById(R.id.noResults);
        if(tracks.isEmpty()){
            results.setVisibility(View.INVISIBLE);
            noResults.setVisibility(View.VISIBLE);
        }

        results.setAdapter(spotifyAdapter);
        results.setVisibility(View.VISIBLE);


        results.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Objects.spotifyTrack track = spotifyAdapter.getItem(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(Blast_Home.this, R.style.MyDialogTheme);
                final View dialogView = inflater.inflate(R.layout.repost_dialog, null);
                final EditText message = (EditText) dialogView.findViewById(R.id.message_field);
                builder.setView(dialogView).setPositiveButton("Post", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        new spotifyPost(track,message.getText().toString()).execute();
                        dialog.dismiss();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setTitle(Html.fromHtml("<font color='#000000'>Post message</font>"));
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

            }
        });
        builder.setView(v).setPositiveButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();
                stopStreamMusic();

            }
        }).setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                stopStreamMusic();
                spotifySearch(null);
            }

        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog.dismiss();
                stopStreamMusic();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setTitle(Html.fromHtml("<font color='#000000'>Results for search</font>"));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


	
	//Method for finding and displaying all mp3 files currently in device
	
	public void displayAvailableFiles(MenuItem item){

                        stopStreamMusic();
                if (lastPost != null) {
                    lastPost.isPlaying = false;
                    lastPost.isLoading = false;
                }
                if(BlastAdapter.timer != null){
                    BlastAdapter.timer.cancel();
                }
                BlastAdapter.timer = null;
                if(second5!=null){
                    second5.cancel();
                    second5 = null;
                }

        String path = "";
        String mimeTypes = "audio/AMR,audio/mp3,audio/wav,audio/x-aac";
        path = Environment.getExternalStorageDirectory().getAbsolutePath();
        //Intent intent1= new Intent(path);
        //intent1.setType(mimeTypes);
        //intent1.setAction(Intent.ACTION_GET_CONTENT);

        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i,1);



        //startActivityForResult(Intent.createChooser(intent1, "select music"), 1);
        onResume();

	 }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch(requestCode)
            {
                case 1:
                    Uri selectedAudioUri = data.getData();
                    String[] proj = { MediaStore.Audio.Media._ID,
                            MediaStore.Audio.Media.DATA,
                            MediaStore.Audio.Media.ALBUM_ID};

                    Cursor tempCursor = managedQuery(selectedAudioUri,
                            proj, null, null, null);

                    tempCursor.moveToFirst(); //reset the cursor
                    int col_index=-1;


                    String selectedAudioPath = getPathAudio(selectedAudioUri);
                    Intent intent = new Intent(this,Edit_Song_For_Post.class);
                    col_index = tempCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
                    intent.putExtra("albumID",tempCursor.getInt(col_index));
                    intent.putExtra("path to song", selectedAudioPath);
                    startActivity(intent);
                    finish();

                    break;

            }
        }
    }
    private String getPathAudio(Uri uriAudio) {

        String selectedAudioPath = "";
        String[] projection = {MediaStore.Audio.Media.DATA};

        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uriAudio, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            cursor.moveToFirst();
            selectedAudioPath = cursor.getString(column_index);

        } else {
            selectedAudioPath = null;
        }

        if (selectedAudioPath == null) {

            selectedAudioPath = uriAudio.getPath();
        }

        return selectedAudioPath;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    private class searchUsers extends AsyncTask<String, String, String> {

        String searchString;
        ArrayList<User> users = new ArrayList<User>();

        public searchUsers(String searchString) {

            this.searchString = searchString;

        }

        protected String doInBackground(String... urls) {


            WebRequester web = new WebRequester(getApplicationContext());
            users = web.searchUsers(searchString);



            return "Post Execute";
        }

        protected void onProgressUpdate(String...a) {
        }

        protected void onPostExecute(String result) {

            Blast_Home.searchUsers = users;
            //Intent i = new Intent(this,Search_Users.class);
            //startActivity(i);
            if(!searchUsers.isEmpty()){
                Intent i = new Intent(getBaseContext(),Search_Users.class);
                i.putExtra("searchString",searchString);
                startActivity(i);
            }
            else{
                Toast.makeText(getBaseContext(),"No results for search...",Toast.LENGTH_LONG).show();
            }
        }
    }

    private class spotifySearchTask extends AsyncTask<String, String, String> {

        String artist;
        String song;
        ArrayList<Objects.spotifyTrack> tracks = new ArrayList<Objects.spotifyTrack>();

        public spotifySearchTask(String artist, String song ) {

            this.artist = artist;
            this.song = song;

        }

        protected String doInBackground(String... urls) {


            WebRequester web = new WebRequester(getApplicationContext());
            tracks = web.spotifySearch(artist,song);



            return "Post Execute";
        }

        protected void onProgressUpdate(String...a) {
        }

        protected void onPostExecute(String result) {

            spotifyResults(tracks);
        }
    }

    private class spotifyPost extends AsyncTask<String, String, String> {

        Objects.spotifyTrack track;
        String message;

        public spotifyPost(Objects.spotifyTrack track, String message) {

            this.track = track;
            this.message = message;

        }

        protected String doInBackground(String... urls) {


            WebRequester web = new WebRequester(getApplicationContext());
            int result = web.postSong(SharedPreferencesEditor.recieveKey(c, "userID"),track.track,track.artist,track.album,track.imageURL,message,30,track.previewURL,SharedPreferencesEditor.recieveFloat(c, "latitude"),SharedPreferencesEditor.recieveFloat(c, "longitude"),true);



            return "Post Execute";
        }

        protected void onProgressUpdate(String...a) {
        }

        protected void onPostExecute(String result) {
            locationUpdater.quickLoc();
            refresh();
            AlertDialog.Builder warn = new AlertDialog.Builder(Blast_Home.this);
            warn.setTitle("Posted Successfully!");
            warn.setMessage("Taking you back home now.");
            warn.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            warn.show();
        }
    }

}
