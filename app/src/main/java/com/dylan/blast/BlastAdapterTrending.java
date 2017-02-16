package com.dylan.blast;


import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import extras.variables;


public class BlastAdapterTrending extends ArrayAdapter<Post> {

	private LayoutInflater inflater;
    int count = 1;
    public static CountDownTimer timer = null;
    ViewHolder latestView = null;
    public static Post lastPost = null;
    Drawable play = getContext().getResources().getDrawable(R.drawable.play_button);
    Drawable pause = getContext().getResources().getDrawable(R.drawable.pause_outline);

    Drawable album_default = getContext().getResources().getDrawable(R.drawable.default_artwork);


	public BlastAdapterTrending(FragmentActivity activity, ArrayList<Post> posts){

             super(activity, R.layout.blast_row_layout_trending, posts);
             inflater = activity.getWindow().getLayoutInflater();
             //new updatePosts(this,posts).execute();

    }

    public void autoplay(Post post){
        System.out.println("Autoplaying attempted");
        if(post.position < getCount() && variables.autoPlay == true) {
            new playNextSong(BlastAdapterTrending.this, getItem(post.position)).execute();
        }
    }

    static class ViewHolder{
        TextView userName;
        TextView songName;
        TextView artist;
        TextView album;
        TextView date;
        TextView count;
        TextView rank;
        SimpleDraweeView button;
        ImageView alubmOverlay;
        boolean isPlaying;
        ProgressBar progress;
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

    public void stop(Post post){
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        if(BlastAdapter.timer != null){
            BlastAdapter.timer.cancel();
            BlastAdapter.timer = null;
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
                if(post.position < getCount() && variables.autoPlay == true) {
                    new playNextSong(BlastAdapterTrending.this, getItem(post.position)).execute();
                }
            }
        };

        //hold.alubmOverlay.setImageDrawable(pause);
        //hold.isPlaying = true;
        notifyDataSetChanged();
        Blast_Home.startStreamMusic(getContext(),post,view,timer,this);
        latestView = view;
        lastPost = post;
        Blast_Home.lastPost = post;
        Blast_Home.playingTab = 2;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.blast_row_layout_trending, parent, false);
            holder = new ViewHolder();
            holder.album = (TextView) convertView.findViewById(R.id.post_album);
            holder.artist = (TextView) convertView.findViewById(R.id.post_artist);
            holder.button = (SimpleDraweeView) convertView.findViewById(R.id.trendPlayButton);
            holder.songName = (TextView) convertView.findViewById(R.id.post_songName);
            holder.userName = (TextView) convertView.findViewById(R.id.post_username);
            holder.alubmOverlay = (ImageView) convertView.findViewById(R.id.album_overlay);
            holder.count = (TextView) convertView.findViewById(R.id.trendingCount);
            holder.rank = (TextView) convertView.findViewById(R.id.trendingSpot);
            holder.isPlaying = false;
            holder.progress = (ProgressBar) convertView.findViewById(R.id.progressBar);
            holder.progress.setVisibility(View.GONE);
            convertView.setTag(holder);
            getItem(position).trendView = holder;
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        final Post post = getItem(position);

        holder.songName.setText(post.song);

        holder.artist.setText(post.artist);

        holder.album.setText(post.album);

        holder.rank.setText(String.valueOf(post.position));

        holder.count.setText(String.valueOf(post.count) + " Posts This Week");

        holder.button.setFocusable(false);

        holder.button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final ViewHolder hold = (ViewHolder)v.getTag();
                if(post.isLoading){
                    stop(post);
                    post.isLoading =false;
                }
                else if (post.isPaused){
                    resume(post);
                }
                else if (post.isPlaying) {
                    pause(post);
                }
                else {
                    if(lastPost == null){
                        play(hold,post);
                    }
                    else if(latestView != null && ((lastPost.isPlaying == true)|| (lastPost.isPaused == true))){
                        stop(lastPost);
                        play(hold,post);
                    }
                    else{
                        play(hold,post);
                    }
                }
                latestView = holder;
                lastPost = post;
                Blast_Home.lastPost = post;
            }


        });

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

    private class playNextSong extends AsyncTask<String, String, String> {

        Post post;
        BlastAdapterTrending adapter;
        //CountDownTimer timer = Blast_Home.second5;
        //int nextPosition;
        int left = 3;


        public playNextSong(BlastAdapterTrending adapter,final Post post) {

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
                    play(post.trendView,post);
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

    private Uri stringToUri(String path){
        Uri uri = Uri.parse(path);
        return uri;
    }

}
