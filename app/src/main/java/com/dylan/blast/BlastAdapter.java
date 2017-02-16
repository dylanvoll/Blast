package com.dylan.blast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.drawee.view.SimpleDraweeView;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import extras.SharedPreferencesEditor;
import extras.WebRequester;
import extras.variables;


public class BlastAdapter extends ArrayAdapter<Post> {
	
	private LayoutInflater inflater = null;
    boolean isPlaying =false;
    ViewHolder latestView = null;
    Post lastPost = null;
    Drawable play = getContext().getResources().getDrawable(R.drawable.play_button);
    Drawable pause = getContext().getResources().getDrawable(R.drawable.pause_outline);
    Drawable starOn = getContext().getResources().getDrawable(R.drawable.favorite_on);
    Drawable starOff = getContext().getResources().getDrawable(R.drawable.favorite_off);
    public static CountDownTimer timer = null;
    int startPost = 0;
    FragmentActivity factivity;
    AppCompatActivity activity;
    boolean homeView = true;
    boolean pastBlast = false;
    ListView commentView = null;
    CommentAdapter commentAdapter = null;
    PopupWindow popWindow = null;
    public BlastAdapter adapter = this;

	
	public BlastAdapter(FragmentActivity activity, ArrayList<Post> posts){

        super(activity, R.layout.blast_row_layout, posts);
        this.factivity = activity;
        inflater = activity.getWindow().getLayoutInflater();
        //Fresco.getImagePipeline().clearCaches();
        //new updatePosts(this,posts).execute();
    }

    public BlastAdapter(Fragment fragment, ArrayList<Post> posts){

        super(fragment.getActivity(), R.layout.blast_row_layout, posts);
        this.factivity = fragment.getActivity();
        inflater = factivity.getWindow().getLayoutInflater();
        //Fresco.getImagePipeline().clearCaches();
        //new updatePosts(this,posts).execute();
    }

    public BlastAdapter(AppCompatActivity activity, ArrayList<Post> posts){

        super(activity, R.layout.blast_row_layout, posts);
        this.activity = activity;
        inflater = activity.getWindow().getLayoutInflater();
        //Fresco.getImagePipeline().clearCaches();
        //new updatePosts(this,posts).execute();
    }

    public void autoplay(Post post){
        if(post.position+1 < getCount() && Blast_Home.lastPost.count == 0 && variables.autoPlay == true) {
            new playNextSong(BlastAdapter.this, getItem(post.position + 1)).execute();
        }
    }

    public void stop(Post post){
        System.out.println("Adapter Stop");
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        if(BlastAdapterTrending.timer != null){
            BlastAdapterTrending.timer.cancel();
            BlastAdapterTrending.timer = null;
        }
        try {
            Thread.sleep(250);
            Blast_Home.stopStreamMusic();
        } catch (Exception e) {
            e.printStackTrace();
        }
        post.isPlaying = false;
        post.isLoading = false;
        post.isPaused = false;
        notifyDataSetChanged();
        //hold.alubmOverlay.setImageDrawable(play);
    }

    public void pause(Post post){
        Blast_Home.pauseStreamMusic();
        post.isPaused = true;
        post.isPlaying = false;
        post.isLoading = false;
        notifyDataSetChanged();
        //hold.alubmOverlay.setImageDrawable(play);
    }

    public void resume(Post post){
        Blast_Home.resumeStreamMusic();
        post.isPaused = false;
        post.isPlaying = true;
        post.isLoading = false;
        notifyDataSetChanged();
        //hold.alubmOverlay.setImageDrawable(play);
    }

    public void play(final ViewHolder view,final Post post){

        if(Blast_Home.lastPost != null && Blast_Home.lastPost.isPlaying){
            stop(Blast_Home.lastPost);
        }
        post.isLoading = true;
        notifyDataSetChanged();

        timer = new CountDownTimer(post.duration*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                stop(post);
                //System.out.println(getCount());
                //System.out.println(post.position+1);
                if(post.position+1 < getCount() && Blast_Home.lastPost.count == 0 && variables.autoPlay == true) {
                    new playNextSong(BlastAdapter.this, getItem(post.position + 1)).execute();
                }
            }
        };

        //hold.alubmOverlay.setImageDrawable(pause);
        //hold.isPlaying = true;
        notifyDataSetChanged();
        Blast_Home.startStreamMusic(getContext(),post,view,timer,this);
        latestView = view;
        Blast_Home.latestView = view;
        lastPost = post;
        Blast_Home.lastPost = post;
        if(Blast_Home.playingTab!=Blast_Home.mBottomBar.getCurrentTabPosition())Blast_Home.playingTab = Blast_Home.mBottomBar.getCurrentTabPosition();

    }

    public void showPopUp(View v){
        View view = null;
        Drawable background = null;
        DisplayMetrics displayMetrics = null;
        if(factivity!= null){
            displayMetrics =  factivity.getResources().getDisplayMetrics();
            view = factivity.getLayoutInflater().inflate(R.layout.comment_layout,null,false);
            background = factivity.getDrawable(R.drawable.comment_background);
        }
        else{
           displayMetrics =  activity.getResources().getDisplayMetrics();
            view = activity.getLayoutInflater().inflate(R.layout.comment_layout,null,false);
            background = activity.getDrawable(R.drawable.comment_background);
        }
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        commentView = (ListView) view.findViewById(R.id.commentsView);
        commentView.setAdapter(commentAdapter);
        commentView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                commentAdapter.getItem(position);
            }
        });
        commentView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        Button button = (Button) view.findViewById(R.id.sendComment);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment(v);
            }
        });
        popWindow = new PopupWindow();
        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        popWindow.setContentView(view);
        popWindow.setWidth(width-20);
        popWindow.setHeight(height-50);
        // set a background drawable with rounders corners
        popWindow.setBackgroundDrawable(background);

        popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);


        popWindow.setAnimationStyle(R.style.PopupAnimation);

        popWindow.setFocusable(true);
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popWindow.dismiss();
                popWindow = null;
                commentAdapter = null;
            }
        });

        // show the popup at bottom of the screen and set some margin at bottom ie,
        popWindow.showAtLocation(v, Gravity.BOTTOM, 0,100);
    }

    public String encode64(String s){
        byte[] data = s.getBytes();
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public void addCommentAdapter(ArrayList<Comment> comments, Post post) {
        System.out.println("made it to adapter");
        if(factivity!= null){
            commentAdapter = new CommentAdapter(factivity,comments,post);
            showPopUp(factivity.findViewById(android.R.id.content));
        }
        else{
            commentAdapter = new CommentAdapter(activity,comments,post);
            showPopUp(activity.findViewById(android.R.id.content));
        }
        commentAdapter.notifyDataSetChanged();


    }

    public void sendComment(View view){
        EditText commentBox = (EditText) view.getRootView().findViewById(R.id.comment_box);
        if(!commentBox.getText().toString().equals(null)){
            if(!commentBox.getText().toString().equals("")) {
                new sendComment(commentBox.getText().toString()).execute();
                commentBox.setText("");
            }
        }
    }

    public void addCommentToList(String comment, String postID){
        Comment c = new Comment(null,postID,SharedPreferencesEditor.recieveKey(getContext(), "userID"),encode64(comment),SharedPreferencesEditor.recieveKey(getContext(), "username"),"Just Now");
        commentAdapter.add(c);
        commentAdapter.notifyDataSetChanged();
        commentView.setSelection(commentAdapter.getCount()-1);

    }

    static class ViewHolder{
        TextView userName;
        TextView songName;
        TextView artist;
        TextView album;
        TextView date;
        TextView message;
        TextView original;
        TextView milesAway;
        ImageButton repostButton;
        LinearLayout originalLayout;
        SimpleDraweeView button;
        SimpleDraweeView userImage;
        ImageView alubmOverlay;
        ProgressBar progress;
        ImageView star;
        TextView numLikes;
        TextView reblastCount;
        LinearLayout reblastList;
        LinearLayout favoriteList;
        ImageView commentsButton;
        LinearLayout commentsList;
        TextView numComments;
        boolean checkComments = true;

        Post post = null;
        //View repost;

        boolean isFvorited = false;
        int numberLikes = 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.blast_row_layout, parent, false);
            holder = new ViewHolder();
            holder.numLikes = (TextView) convertView.findViewById(R.id.numLikes);
            holder.milesAway = (TextView) convertView.findViewById(R.id.miles);
            holder.reblastCount = (TextView) convertView.findViewById(R.id.reblastCount);
            holder.original = (TextView) convertView.findViewById(R.id.original);
            holder.originalLayout = (LinearLayout) convertView.findViewById(R.id.originalLayout);
            holder.album = (TextView) convertView.findViewById(R.id.post_album);
            holder.artist = (TextView) convertView.findViewById(R.id.post_artist);
            holder.button = (SimpleDraweeView) convertView.findViewById(R.id.playButton);
            holder.date = (TextView) convertView.findViewById(R.id.post_date);
            holder.songName = (TextView) convertView.findViewById(R.id.post_songName);
            holder.message = (TextView) convertView.findViewById(R.id.post_message);
            holder.userImage = (SimpleDraweeView) convertView.findViewById(R.id.user_image);
            holder.userName = (TextView) convertView.findViewById(R.id.post_username);
            holder.alubmOverlay = (ImageView) convertView.findViewById(R.id.album_overlay);
            holder.progress = (ProgressBar) convertView.findViewById(R.id.progressBar);
            holder.star = (ImageView) convertView.findViewById(R.id.favoriteStar);
            holder.progress.setVisibility(View.GONE);
            holder.repostButton = (ImageButton) convertView.findViewById(R.id.repost_button);
            holder.reblastList = (LinearLayout) convertView.findViewById(R.id.repostList);
            holder.favoriteList = (LinearLayout) convertView.findViewById(R.id.favoriteList);
            holder.commentsButton = (ImageView) convertView.findViewById(R.id.comment_bubble);
            holder.commentsList = (LinearLayout) convertView.findViewById(R.id.commentsList);
            holder.numComments = (TextView) convertView.findViewById(R.id.numComments);
            convertView.setTag(holder);
            getItem(position).view = holder;
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Post post = getItem(position);

        holder.numComments.setText(post.numComments + "");

        holder.numberLikes = post.likeList.size();

        holder.milesAway.setText(post.miles);

        holder.reblastCount.setText("" + post.reblastCount);

        if (post.userID.equals(variables.uid) || post.baseUID.equals(variables.uid)) {
            //if (holder.repost!= null) {
            //View postItems = convertView.findViewById(R.id.postItems);
            //((ViewGroup) postItems).removeView(holder.repost);
            //((ViewGroup) postItems).setEnabled(false);
            holder.repostButton.setEnabled(false);
            //}
        } else {
            //holder.repost.setVisibility(View.VISIBLE);
            holder.repostButton.setEnabled(true);
        }

        if (post.isRepost == 1) {
            holder.originalLayout.setVisibility(View.VISIBLE);
            if (post.baseUID.equals(variables.uid)) {
                holder.original.setText("You");
            } else {
                holder.original.setText(post.baseUsername);
                holder.original.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getContext(), User_Profile.class);
                        i.putExtra("userID", post.baseUID);
                        getContext().startActivity(i);
                    }
                });
            }
        } else {
            holder.originalLayout.setVisibility(View.INVISIBLE);
        }

        holder.reblastList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                if (activity == null) {
                    builder = new AlertDialog.Builder(factivity);
                } else {
                    builder = new AlertDialog.Builder(activity);
                }
                final View view = inflater.inflate(R.layout.user_list, null);
                ListView list = (ListView) view.findViewById(R.id.dialogList);
                if (post.reblastCount == 0) {
                } else {
                    new loadReblastList(post.songID, post.baseUID, list).execute();
                }
                builder.setView(view).setPositiveButton("Okay", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setTitle(Html.fromHtml("<font color='#000000'>Re-Blasts</font>"));
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });

        holder.favoriteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                if (activity == null) {
                    builder = new AlertDialog.Builder(factivity);
                } else {
                    builder = new AlertDialog.Builder(activity);
                }
                final View view = inflater.inflate(R.layout.user_list, null);
                ListView list = (ListView) view.findViewById(R.id.dialogList);
                if (post.likeList.size() == 0) {
                } else {
                    new loadFavoriteList(post.postID, list).execute();
                }
                builder.setView(view).setPositiveButton("Okay", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setTitle(Html.fromHtml("<font color='#000000'>Favorites</font>"));
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });

        //holder.commentsList

        holder.repostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                if (activity == null) {
                    builder = new AlertDialog.Builder(factivity);
                } else {
                    builder = new AlertDialog.Builder(activity);
                }
                final View view = inflater.inflate(R.layout.repost_dialog, null);
                final EditText message = (EditText) view.findViewById(R.id.message_field);
                builder.setView(view).setPositiveButton("Repost", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        new repostSong(message.getText().toString(), post.songID).execute();
                        post.reblastCount++;
                        holder.reblastCount.setText("" + post.reblastCount);
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

        holder.commentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new getComments(post).execute();
            }
        });

        if (homeView) {
            holder.userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!post.userID.equals(SharedPreferencesEditor.recieveKey(getContext(), "userID"))) {

                        if (activity == null) {

                            Intent i = new Intent(getContext(), User_Profile.class);
                            i.putExtra("userID", post.userID);
                            factivity.startActivity(i);
                            factivity.overridePendingTransition(R.anim.right_in, R.anim.right_out);

                        } else {
                            Intent i = new Intent(getContext(), User_Profile.class);
                            i.putExtra("userID", post.userID);
                            activity.startActivity(i);
                            activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                        }
                        Blast_Home.stopStreamMusic();
                    }
                }
            });
        }

        holder.star.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                if (holder.isFvorited) {
                    new updateLike(post.postID, false).execute();
                    holder.star.setImageDrawable(starOff);
                    holder.isFvorited = false;
                    holder.numberLikes--;
                    post.likeList.remove(SharedPreferencesEditor.recieveKey(getContext(), "userID"));
                    post.likes = "" + holder.numberLikes;
                    notifyDataSetChanged();
                } else {
                    new updateLike(post.postID, true).execute();
                    holder.star.setImageDrawable(starOn);
                    holder.isFvorited = true;
                    holder.numberLikes++;
                    post.likeList.add(SharedPreferencesEditor.recieveKey(getContext(), "userID"));
                    post.likes = "" + holder.numberLikes;
                    notifyDataSetChanged();
                }
            }
        });

        holder.button.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                startPost = post.position;
                final ViewHolder hold = (ViewHolder) v.getTag();
                if (post.isLoading) {
                    stop(post);
                    post.isLoading = false;
                } else if (post.isPaused) {
                    resume(post);
                } else if (post.isPlaying) {
                    pause(post);
                } else {
                    if (lastPost == null) {
                        play(hold, post);
                    } else if (latestView != null && ((lastPost.isPlaying == true) || (lastPost.isPaused == true))) {
                        stop(lastPost);
                        play(hold, post);
                    } else {
                        play(hold, post);
                    }
                }
                latestView = holder;
                Blast_Home.latestView = holder;
                lastPost = post;
                Blast_Home.lastPost = post;
            }


        });

        if(post.likeList.contains(SharedPreferencesEditor.recieveKey(getContext(),"userID"))){
            holder.star.setImageDrawable(starOn);
            holder.isFvorited = true;
        }

        else{
            holder.star.setImageDrawable(starOff);
            holder.isFvorited = false;
        }
        holder.numLikes.setText(holder.numberLikes+ "");

        holder.userName.setText(StringUtils.capitalise(post.username));

        if(post.song.equals(""))holder.songName.setText(getContext().getResources().getString(R.string.noSong));
        else holder.songName.setText(post.song);

        if(post.artist.equals(""))holder.artist.setText(getContext().getResources().getString(R.string.noArtist));
        else holder.artist.setText(post.artist);

        if(post.album.equals(""))holder.album.setText(getContext().getResources().getString(R.string.noAlbum));
        else holder.album.setText(post.album);

        holder.date.setText(post.time);

        holder.message.setText(post.message);

        holder.button.setFocusable(false);


        holder.userImage.setImageURI(stringToUri(String.format("%s/blast/users/%s/profile.png",variables.server, post.userID)));

        if(post.path_to_album.isEmpty()) {

            holder.button.setImageURI(stringToUri(String.format("%s/blast/users/%s/album.png", variables.server, post.path_to_song)));
        }
        else{
            holder.button.setImageURI(stringToUri(post.path_to_album));
        }

        holder.alubmOverlay.setImageDrawable(play);

        if(post.isPlaying){
            holder.alubmOverlay.setImageDrawable(pause);
            }
        if(!post.isLoading) {
            holder.alubmOverlay.setVisibility(View.VISIBLE);
            holder.progress.setVisibility(View.GONE);
        }
        else{
            holder.alubmOverlay.setVisibility(View.GONE);
            holder.progress.setVisibility(View.VISIBLE);
        }

        holder.button.setTag(holder);

        notifyDataSetChanged();

    	return convertView;
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

            float latitude = SharedPreferencesEditor.recieveFloat(getContext(), "latitude");
            float longitude = SharedPreferencesEditor.recieveFloat(getContext(), "longitude");
            WebRequester web = new WebRequester(getContext());
            b = web.repostSong(latitude,longitude,variables.uid,songID,message);

            return "Post Execute";
        }

        protected void onProgressUpdate(String...a){

        }

        protected void onPostExecute(String result) {

            if(b){Toast.makeText(getContext(), "Posted Successfully!", Toast.LENGTH_LONG).show();
                    Past_Blasts.refreshPosts(getContext());}
            else{Toast.makeText(getContext(), "Something went wrong, please try again.", Toast.LENGTH_LONG).show();}


        }
    }

    private class playNextSong extends AsyncTask<String, String, String> {

        Post post;
        BlastAdapter adapter;
        //CountDownTimer timer = Blast_Home.second5;
        //int nextPosition;
        int left = 3;


        public playNextSong(BlastAdapter adapter,final Post post) {

            this.post = post;

            this.adapter = adapter;

            //nextPosition = startPost + 1;



            Blast_Home.second5 = new CountDownTimer(8 * 1000, 2000) {
                @Override
                public void onTick(long millisUntilFinished) {

                    publishProgress(left + "");
                    left--;
                }

                @Override
                public void onFinish() {
                    play(post.view,post);
                }
            };

        }

        protected String doInBackground(String... urls) {



                Blast_Home.second5.start();


            return "Post Execute";
        }

        protected void onProgressUpdate(String...a){
            Toast.makeText(getContext(),"Next post playing in: " + a[0],Toast.LENGTH_SHORT).show();
        }

        protected void onPostExecute(String result) {




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


            WebRequester web = new WebRequester(getContext());
            if (likeFlag){
                web.likePost(postID,SharedPreferencesEditor.recieveKey(getContext(),"userID"));
            }
            else{
                web.unlikePost(postID,SharedPreferencesEditor.recieveKey(getContext(),"userID"));
            }



            return "Post Execute";
        }

        protected void onProgressUpdate(String...a){

        }

        protected void onPostExecute(String result) {




        }
    }

    private class loadReblastList extends AsyncTask<String, String, String> {

        String songID = null;
        ListView list = null;
        ArrayList<User> users;
        UsersRowAdapter adapter;
        String baseUID = null;

        public loadReblastList(String songID, String baseUID, ListView list) {

            this.list = list;
            this.songID = songID;
            this.baseUID = baseUID;



        }

        protected String doInBackground(String... urls) {


            WebRequester web = new WebRequester(getContext());
            users = web.getPostReblastList(songID,baseUID);



            return "Post Execute";
        }

        protected void onProgressUpdate(String...a){

        }

        protected void onPostExecute(String result) {
        if(activity!=null) {
            adapter = new UsersRowAdapter(activity,users,R.layout.users_row_layout_dialog);
        }
        else{
            adapter = new UsersRowAdapter(factivity,users,R.layout.users_row_layout_dialog);
        }
            list.setAdapter(adapter);
        }
    }

    private class loadFavoriteList extends AsyncTask<String, String, String> {

        String postID = null;
        ListView list = null;
        ArrayList<User> users;
        UsersRowAdapter adapter;

        public loadFavoriteList(String postID, ListView list) {

            this.list = list;
            this.postID = postID;



        }

        protected String doInBackground(String... urls) {


            WebRequester web = new WebRequester(getContext());
            users = web.getPostFavoriteList(postID);



            return "Post Execute";
        }

        protected void onProgressUpdate(String...a){

        }

        protected void onPostExecute(String result) {
            if(activity!=null) {
                adapter = new UsersRowAdapter(activity,users,R.layout.users_row_layout);
            }
            else{
                adapter = new UsersRowAdapter(factivity,users,R.layout.users_row_layout);
            }
            list.setAdapter(adapter);
        }
    }

    private class getComments extends AsyncTask<String, String, String> {

        Post post;

        int left = 3;

        ArrayList<Comment> comments;


        public getComments(final Post post) {

            this.post = post;
            System.out.println(post.postID);

        }

        protected String doInBackground(String... urls) {


            WebRequester web = new WebRequester(getContext());
            comments = web.getPostComments(post);

            return "Post Execute";
        }

        protected void onProgressUpdate(String...a){

        }

        protected void onPostExecute(String result) {
            post.numComments = comments.size();
            if(commentAdapter==null)addCommentAdapter(comments,post);
            notifyDataSetChanged();


        }
    }

    private class sendComment extends AsyncTask<String, String, String> {

        String comment = null;
        boolean response = false;

        public sendComment(final String comment) {

            this.comment = comment;

        }

        protected String doInBackground(String... urls) {


            WebRequester web = new WebRequester(getContext());
            response = web.postComment(comment, commentAdapter.post.postID, SharedPreferencesEditor.recieveKey(getContext(), "userID"));


            return "Post Execute";
        }

        protected void onProgressUpdate(String...a){

        }

        protected void onPostExecute(String result) {

            if(response){
                addCommentToList(comment,commentAdapter.post.postID);
            }


        }
    }

    private Uri stringToUri(String path){
        Uri uri = Uri.parse(path);
        return uri;
    }


}
