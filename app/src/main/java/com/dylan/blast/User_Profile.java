package com.dylan.blast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

import extras.BlastMediaPlayer;
import extras.SharedPreferencesEditor;
import extras.WebRequester;
import extras.variables;


public class User_Profile extends AppCompatActivity {

    static ListView postList = null;
    String userID = null;
    SimpleDraweeView userImage;
    TextView username;
    TextView fullName;
    TextView bio;
    TextView numPosts;
    Button follow;
    LinearLayout userInfo;
    FrameLayout loading;
    private static BlastMediaPlayer player = BlastMediaPlayer.getInstance();
    static ArrayList<Post> posts = null;
    static BlastAdapter adapter;
    static View footer = null;
    static Activity activity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_user__profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(6);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setTitle("User Profile");
        activity = this;
        userInfo = (LinearLayout) findViewById(R.id.userInfo);
        loading = (FrameLayout) findViewById(R.id.loading);
        postList = (ListView) findViewById(R.id.userProfilePosts);
        postList.setVisibility(View.INVISIBLE);
        userInfo.setVisibility(View.INVISIBLE);
        userInfo.setElevation(4);
        loading.setVisibility(View.VISIBLE);
        userID = getIntent().getStringExtra("userID");
        userImage = (SimpleDraweeView) findViewById(R.id.userProfileImage);
        username = (TextView) findViewById(R.id.userProfileUsername);
        fullName = (TextView) findViewById(R.id.userProfileFullName);
        bio = (TextView) findViewById(R.id.userProfileBio);
        numPosts = (TextView) findViewById(R.id.userProfileNumPosts);
        follow = (Button) findViewById(R.id.userProfileFollowButton);
        loading = (FrameLayout) findViewById(R.id.loading);
        postList.setVisibility(View.INVISIBLE);
        userInfo.setVisibility(View.INVISIBLE);
        footer = this.getLayoutInflater().inflate(R.layout.footer_view,null,false);
        final Button load = (Button) footer.findViewById(R.id.loadPosts);
        final ProgressBar loading = (ProgressBar)footer.findViewById(R.id.postLoading);
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                load.setVisibility(View.INVISIBLE);
                new addMorePosts(getBaseContext(),loading,load,adapter.getItem(posts.size()-1),userID).execute();
            }
        });
        new getUserInfo(userID).execute();

    }
    @Override
    protected void onResume(){
        super.onResume();
        if(adapter!=null)adapter.notifyDataSetChanged();
    }

    private Uri stringToUri(String path){
        Uri uri = Uri.parse(path);
        return uri;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user__profile, menu);
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

        return super.onOptionsItemSelected(item);
    }

    public void home(MenuItem item){
        Intent i = new Intent(getBaseContext(),Blast_Home.class);
        startActivity(i);
        finish();
    }

    public void trailList(View view){
        Intent i = new Intent(getBaseContext(),TrailingView.class);
        i.putExtra("uid",userID);
        i.putExtra("username",username.getText().toString());
        startActivity(i);
        //finish();
    }

    public void loadUI(ArrayList<String> info){
        userImage.setImageURI(stringToUri(String.format("%s/blast/users/%s/profile.png",variables.server, userID)));
        username.setText(StringUtils.capitalise(info.get(0)));
        if(!info.get(0).isEmpty()){
            getSupportActionBar().setTitle(StringUtils.capitalise(info.get(0)) + "'s Profile");
        }
        fullName.setText(info.get(1) + " " + info.get(2));
        bio.setText(variables.decode64(info.get(3)));
        bio.setMovementMethod(new ScrollingMovementMethod());
        numPosts.setText(info.get(4) + " Total Blasts");
        adapter = new BlastAdapter(this,posts);
        adapter.homeView = false;
        if(posts.size()>5) {
            postList.addFooterView(footer);
        }
        postList.setAdapter(adapter);
        postList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Post post = adapter.getItem(position);
                Blast_Home.postForDetail = post;
                if (BlastAdapter.timer != null) BlastAdapter.timer.cancel();
                System.out.println("Clicked #" + position + " Post Song is:" + post.song + " ");
                Intent i = new Intent(getBaseContext(), PostActivity.class);
                startActivity(i);
                activity.overridePendingTransition(R.anim.right_in,R.anim.right_out);
                adapter.notifyDataSetChanged();
            }
        });
        if(Blast_Home.followList.contains(userID)){
            follow.setText("Un-trail " + StringUtils.capitaliseAllWords(username.getText().toString()));
        }
        else{
            follow.setText("Trail " + StringUtils.capitaliseAllWords(username.getText().toString()));
        }
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (follow.getText().toString().equals("Trail " +  StringUtils.capitaliseAllWords(username.getText().toString()))){
                    new trail(SharedPreferencesEditor.recieveKey(getBaseContext(),"userID"),userID).execute();
                    follow.setText("Un-trail " + StringUtils.capitaliseAllWords(username.getText().toString()));
                    Blast_Home.followList.add(userID);
                }
                else{
                    new untrail(SharedPreferencesEditor.recieveKey(getBaseContext(),"userID"),userID).execute();
                    follow.setText("Trail " + StringUtils.capitaliseAllWords(username.getText().toString()));
                    Blast_Home.followList.remove(userID);
                }
            }
        });
        userInfo.setVisibility(View.VISIBLE);
        postList.setVisibility(View.VISIBLE);
        loading.setVisibility(View.INVISIBLE);
    }

    private class getUserInfo extends AsyncTask<String, String, String> {


        String userID;
        ArrayList<String> info;


        public getUserInfo(String userID) {

            this.userID = userID;

        }

        protected String doInBackground(String... urls) {


            WebRequester web = new WebRequester(getBaseContext());
            info = web.getUserInfo(userID);
            posts = web.getPostsByUserID(userID);


            return "Post Execute";
        }

        protected void onProgressUpdate(String...a){

        }

        protected void onPostExecute(String result) {

                loadUI(info);

        }
    }

    private class trail extends AsyncTask<String, String, String> {


        String userID;
        String followID;



        public trail(String userID,String followID) {

            this.userID = userID;
            this.followID = followID;

        }

        protected String doInBackground(String... urls) {


            WebRequester web = new WebRequester(getBaseContext());
            web.followUser(userID,followID);


            return "Post Execute";
        }

        protected void onProgressUpdate(String...a){

        }

        protected void onPostExecute(String result) {




        }
    }

    private class untrail extends AsyncTask<String, String, String> {


        String userID;
        String followID;



        public untrail(String userID,String followID) {

            this.userID = userID;
            this.followID = followID;

        }

        protected String doInBackground(String... urls) {


            WebRequester web = new WebRequester(getBaseContext());
            web.unfollowUser(userID,followID);


            return "Post Execute";
        }

        protected void onProgressUpdate(String...a){

        }

        protected void onPostExecute(String result) {




        }
    }

    static class addMorePosts extends AsyncTask<Void, String, String> {

        ProgressBar loading = null;
        Button load = null;
        ArrayList<Post> postsAdd = null;
        Post post = null;
        Context context = null;
        String userID = null;

        public addMorePosts(Context context,ProgressBar loading, Button load,Post post, String userID) {
            this.loading = loading;
            this.load = load;
            this.post = post;
            this.context = context;
            this.userID = userID;
        }

        protected void onPreExecute() {

        }

        protected String doInBackground(Void... arg0) {

            try {
                WebRequester web = new WebRequester(context);
                postsAdd = (web.getPostsByUserIDAdd(userID,post.timestamp));
                for (int i = postsAdd.size()-1; i>=0; i--) {
                    for (Post p2 : posts) {
                        //System.out.println(p2.postID + " and " + postsAdd.get(i));
                        if (p2.postID.equals(postsAdd.get(i).postID)) {
                            postsAdd.remove(i);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return "You are at PostExecute";
        }

        protected void onProgressUpdate(String... a) {

        }

        protected void onPostExecute(String result) {


            if(!postsAdd.isEmpty()) {

                adapter.addAll(postsAdd);
                postList.removeFooterView(footer);
                postList.addFooterView(footer);

            }


            else{
                Toast.makeText(context, "No More Blasts to Load", Toast.LENGTH_LONG).show();
                postList.removeFooterView(footer);
                postList.setSelection(adapter.getCount() - 1);
            }
            adapter.notifyDataSetChanged();
            loading.setVisibility(View.INVISIBLE);
            load.setVisibility(View.VISIBLE);



        }
    }
}


