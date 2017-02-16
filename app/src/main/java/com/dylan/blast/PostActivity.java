package com.dylan.blast;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
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


public class PostActivity extends AppCompatActivity {

    static ListView commentView;
    static CommentAdapter adapter;
    static ArrayList<Comment> comments = null;
    static Activity activity;
    static boolean adapterSet = false;
    TextView userName;
    TextView songName;
    TextView artist;
    TextView album;
    TextView date;
    TextView message;
    SimpleDraweeView button;
    SimpleDraweeView userImage;
    ImageView alubmOverlay;
    ProgressBar progress;
    ImageButton repostButton;
    final Post post = Blast_Home.postForDetail;
    Drawable play;
    Drawable pause;
    Drawable profile_default;
    Drawable album_default;
    CountDownTimer timer = null;
    ImageView star;
    TextView numLikes;
    TextView reblastCount;
    View repost;
    boolean isFvorited = false;
    int numberLikes = 0;
    Drawable starOn;
    Drawable starOff;
    private static BlastMediaPlayer player;
    PopupWindow popWindow;


    @Override
    public void onBackPressed() {

        {
            super.onBackPressed();
            Blast_Home.postForDetail = null;
            Blast_Home.lastPost = post;
            Blast_Home.playingTab = Blast_Home.mBottomBar.getCurrentTabPosition();
            if (Blast_Radius.adapter != null) Blast_Radius.adapter.notifyDataSetChanged();
            if (Past_Blasts.adapter != null) Past_Blasts.adapter.notifyDataSetChanged();
            if (Trending.adapter != null) Trending.adapter.notifyDataSetChanged();
            if (Trailing.adapter != null) Trailing.adapter.notifyDataSetChanged();
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.post_activity);
        new getComments(post).execute();
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
        if(getIntent().getStringExtra("trend_user")!= null){
            getSupportActionBar().setTitle("Latest Trender");
        }
        else{
            getSupportActionBar().setTitle("Post by: " + StringUtils.capitalise(post.username));
        }
        starOn = getResources().getDrawable(R.drawable.favorite_on);
        starOff = getResources().getDrawable(R.drawable.favorite_off);
         play = getBaseContext().getResources().getDrawable(R.drawable.play_button);
         pause = getBaseContext().getResources().getDrawable(R.drawable.pause_outline);
         profile_default = getBaseContext().getResources().getDrawable(R.drawable.profile_default);
         album_default = getBaseContext().getResources().getDrawable(R.drawable.default_artwork);
        activity = this;
        //commentView = (ListView) findViewById(R.id.commentsView);
        //commentView.setEmptyView(findViewById(R.id.noComments));
        album = (TextView) findViewById(R.id.post_album);
        artist = (TextView) findViewById(R.id.post_artist);
        button = (SimpleDraweeView) findViewById(R.id.playButton);
        date = (TextView) findViewById(R.id.post_date);
        songName = (TextView) findViewById(R.id.post_songName);
        message = (TextView) findViewById(R.id.post_message);
        userImage = (SimpleDraweeView) findViewById(R.id.user_image);
        userName = (TextView) findViewById(R.id.post_username);
        alubmOverlay = (ImageView) findViewById(R.id.album_overlay);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        star = (ImageView) findViewById(R.id.favoriteStar);
        numLikes = (TextView) findViewById(R.id.numLikes);
        repost = findViewById(R.id.repost);
        progress.setVisibility(View.GONE);
        numberLikes = post.likeList.size();
        repostButton = (ImageButton) findViewById(R.id.repost_button);
        reblastCount = (TextView) findViewById(R.id.reblastCount);

        reblastCount.setText(""+ post.reblastCount);

        if(post.likeList.contains(variables.uid)){
            star.setImageDrawable(starOn);
            isFvorited = true;
        }

        else{
            star.setImageDrawable(starOff);
            isFvorited = false;
        }



        repostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                final View view = inflater.inflate(R.layout.repost_dialog,null);
                final EditText message = (EditText)view.findViewById(R.id.message_field);
                builder.setView(view).setPositiveButton("Repost", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        new repostSong(message.getText().toString(),post.songID).execute();
                        post.reblastCount++;
                        reblastCount.setText(""+post.reblastCount);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setTitle(Html.fromHtml("<font color='#000000'>Repost Song</font>"));
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });

        star.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {

                if(isFvorited){
                    new updateLike(post.postID,false).execute();
                    star.setImageDrawable(starOff);
                    isFvorited = false;
                    numberLikes--;
                    post.likeList.remove(SharedPreferencesEditor.recieveKey(getBaseContext(),"userID"));
                    post.likes = "" + numberLikes;
                }
                else{
                    new updateLike(post.postID,true).execute();
                    star.setImageDrawable(starOn);
                    isFvorited = true;
                    numberLikes++;
                    post.likeList.add(SharedPreferencesEditor.recieveKey(getBaseContext(),"userID"));
                    post.likes = "" + numberLikes;
                }
                numLikes.setText(numberLikes + "");
            }
        });

        numLikes.setText(numberLikes+ "");

        userName.setText(StringUtils.capitalise(post.username));

        songName.setText(post.song);

        artist.setText(post.artist);

        album.setText(post.album);

        date.setText(post.time);

        message.setText(post.message);
        //message.setMovementMethod(LinkMovementMethod.getInstance());

        button.setFocusable(false);

        button.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {
                System.out.println(post.isPlaying + " " + post.isLoading + " " + post.isPaused);
                if(post.isLoading){
                    stop(post);
                    post.isLoading =false;
                }
                else if (post.isPlaying) {
                    pause(post);
                }
                else if(post.isPaused){
                    resume(post);
                }
                else {
                        play(post);
                    }
                }

            });

        userImage.setImageURI(stringToUri(String.format("%s/blast/users/%s/profile.png",variables.server, post.userID)));
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!post.userID.equals(variables.uid)){
                    Intent i = new Intent(getBaseContext(),User_Profile.class);
                    i.putExtra("userID",post.userID);
                    startActivity(i);
                    activity.overridePendingTransition(R.anim.right_in,R.anim.right_out);
                }
            }
        });
        if(post.path_to_album.isEmpty()) {

            button.setImageURI(stringToUri(String.format("%s/blast/users/%s/album.png", variables.server, post.path_to_song)));
        }
        else{
            button.setImageURI(stringToUri(post.path_to_album));
        }
        alubmOverlay.setImageDrawable(play);
        alubmOverlay.setVisibility(View.VISIBLE);
        if(post.isPlaying){
            alubmOverlay.setImageDrawable(pause);
        }
        if(!post.isLoading) {
            alubmOverlay.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
        }
        else{
            alubmOverlay.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
        }
        if(post.userID.equals(variables.uid) || post.baseUID.equals(variables.uid)){
            if (repost != null) {
                //View postItems = findViewById(R.id.postItems);
                //((ViewGroup) postItems).removeView(repost);
                repostButton.setEnabled(false);
            }
        }
        player = BlastMediaPlayer.getInstance();

        player.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop(post);
                alubmOverlay.setImageDrawable(play);
            }
        });


    }

    public void showPopUp(View v){

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        final View view = getLayoutInflater().inflate(R.layout.comment_layout,null,false);
        commentView = (ListView) view.findViewById(R.id.commentsView);
        commentView.setAdapter(adapter);
        commentView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                adapter.getItem(position);
                System.out.println(adapter.getItem(position).created);
            }
        });
        commentView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        Button button = (Button) view.findViewById(R.id.sendComment);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment(view);
            }
        });
        popWindow = new PopupWindow();
        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        popWindow.setContentView(view);
        popWindow.setWidth(width-20);
        popWindow.setHeight(height-50);
        // set a background drawable with rounders corners
        popWindow.setBackgroundDrawable(getDrawable(R.drawable.comment_background));

        popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);


        popWindow.setAnimationStyle(R.style.PopupAnimation);

        popWindow.setFocusable(true);

        // show the popup at bottom of the screen and set some margin at bottom ie,
        popWindow.showAtLocation(v, Gravity.BOTTOM, 0,100);
    }

    public void sendComment(View view){
        EditText commentBox = (EditText) view.findViewById(R.id.comment_box);
        if(!commentBox.getText().toString().equals(null)){
            if(!commentBox.getText().toString().equals("")) {
                new sendComment(commentBox.getText().toString()).execute();
                commentBox.setText("");
            }
        }
    }

    public String decode64(String s){
        String newString = null;
        try {
            byte[] data = Base64.decode(s, Base64.DEFAULT);
            newString =  new String(data, "UTF-8");
        }
        catch(Exception e){e.printStackTrace();}
        return newString;
    }

    public String encode64(String s){
        byte[] data = s.getBytes();
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public void addCommentToList(String comment, String postID){
        Comment c = new Comment(null,postID,SharedPreferencesEditor.recieveKey(getBaseContext(), "userID"),encode64(comment),SharedPreferencesEditor.recieveKey(getBaseContext(), "username"),"Just Now");
        adapter.add(c);
        adapter.notifyDataSetChanged();
        commentView.setSelection(adapter.getCount()-1);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post, menu);
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

    public static void setComments(ArrayList<Comment> comments){

        PostActivity.comments = comments;
    }

    public void addAdapter() {

        adapter = new CommentAdapter(activity,comments,post);
        adapter.notifyDataSetChanged();
        adapterSet = true;

    }

    public void stop(Post post){

        Blast_Home.stopStreamMusic();
        post.isPlaying = false;
        post.isLoading = false;
        post.isPaused = false;
        alubmOverlay.setImageDrawable(play);
    }

    public void play(final Post post){


        post.isLoading = true;
        this.progress.setVisibility(View.VISIBLE);
        this.alubmOverlay.setVisibility(View.GONE);
        post.isPlaying = true;
        startStreamMusic(this);

    }

    public void pause(final Post post){
        post.isPaused = true;
        post.isPlaying = false;
        post.isLoading = false;
        player.mp.pause();
        alubmOverlay.setImageDrawable(play);

    }

    public void resume(final Post post){
        post.isPaused = false;
        post.isPlaying = true;
        post.isLoading = false;
        player.mp.start();
        alubmOverlay.setImageDrawable(pause);
    }

    public void startStreamMusic(final PostActivity activity){

        try {
            Blast_Home.stopStreamMusic(true);
            player.mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mp) {
                    post.isLoading = false;
                    activity.progress.setVisibility(View.GONE);
                    activity.alubmOverlay.setImageDrawable(pause);
                    activity.alubmOverlay.setVisibility(View.VISIBLE);
                    mp.start();
                    post.isPlaying = true;

                }
            });

            player.mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

            if(post.path_to_song.split("/").length == 2) {

                player.mp.setDataSource(Uri.parse(String.format("%s/blast/users/%s/%s.mp3", variables.server, post.path_to_song, post.songID)).toString());
            }
            else{
                player.mp.setDataSource(Uri.parse(post.path_to_song).toString());
            }

            player.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stop(post);
                    alubmOverlay.setImageDrawable(play);
                }
            });

            player.mp.prepareAsync();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error connecting to server");
        }


    }

    public static void stopStreamMusic(){
        Blast_Home.stopStreamMusic(true);
    }

    private Uri stringToUri(String path){
        Uri uri = Uri.parse(path);
        return uri;
    }

    private class repostSong extends AsyncTask<String, String, String> {

        String message;
        String songID;
        boolean b = false;

        public repostSong(String message,String songID) {


            this.message = message;
            this.songID = songID;

        }

        protected String doInBackground(String... urls) {

            float latitude = SharedPreferencesEditor.recieveFloat(getBaseContext(), "latitude");
            float longitude = SharedPreferencesEditor.recieveFloat(getBaseContext(), "longitude");
            WebRequester web = new WebRequester(getBaseContext());
            b = web.repostSong(latitude,longitude,variables.uid,songID,message);

            return "Post Execute";
        }

        protected void onProgressUpdate(String...a){

        }

        protected void onPostExecute(String result) {

            if(b){Toast.makeText(getBaseContext(), "Posted Successfully!", Toast.LENGTH_LONG).show();
                Past_Blasts.refreshPosts(getBaseContext());}
            else{Toast.makeText(getBaseContext(), "Something went wrong, please try again.", Toast.LENGTH_LONG).show();}


        }
    }

    private class getComments extends AsyncTask<String, String, String> {

        Post post;

        int left = 3;

        ArrayList<Comment> comments;


        public getComments(final Post post) {

            this.post = post;

        }

        protected String doInBackground(String... urls) {


            WebRequester web = new WebRequester(getBaseContext());
            comments = web.getPostComments(post);
            setComments(comments);

            return "Post Execute";
        }

        protected void onProgressUpdate(String...a){

        }

        protected void onPostExecute(String result) {

        addAdapter();


        }
}
    private class sendComment extends AsyncTask<String, String, String> {

        String comment = null;
        boolean response = false;

        public sendComment(final String comment) {

            this.comment = comment;

        }

        protected String doInBackground(String... urls) {


            WebRequester web = new WebRequester(getBaseContext());
            response = web.postComment(comment, post.postID, SharedPreferencesEditor.recieveKey(getBaseContext(), "userID"));


            return "Post Execute";
        }

        protected void onProgressUpdate(String...a){

        }

        protected void onPostExecute(String result) {

            if(response){
                addCommentToList(comment,post.postID);
            }


        }
    }

    private class updateLike extends AsyncTask<String, String, String> {

        String postID = null;
        boolean likeFlag = false;

        public updateLike(String postID, boolean likeFlag) {

            this.likeFlag = likeFlag;
            this.postID = postID;



        }

        protected String doInBackground(String... urls) {


            WebRequester web = new WebRequester(getBaseContext());
            if (likeFlag){
                web.likePost(postID,variables.uid);
            }
            else{
                web.unlikePost(postID,variables.uid);
            }



            return "Post Execute";
        }

        protected void onProgressUpdate(String...a){

        }

        protected void onPostExecute(String result) {




        }
    }
}
