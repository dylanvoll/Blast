package com.dylan.blast;

import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.concurrent.Exchanger;

import extras.BlastMediaPlayer;

/**
 * Created by Dylan on 12/26/2016.
 */

public class SpotifyTrackAdpater extends ArrayAdapter<Objects.spotifyTrack> {

    Drawable play = getContext().getResources().getDrawable(R.drawable.play_button);
    Drawable pause = getContext().getResources().getDrawable(R.drawable.pause_outline);

    Objects.spotifyTrack lastTrack = null;

    public SpotifyTrackAdpater(Blast_Home activity, ArrayList<Objects.spotifyTrack> tracks) {
        super(activity, R.layout.spotify_track_layout, tracks);
    }

    static class ViewHolder {
        TextView track;
        TextView artist;
        SimpleDraweeView albumImage;
        ImageView alubmOverlay;
        ProgressBar progress;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spotify_track_layout, parent, false);
            holder = new SpotifyTrackAdpater.ViewHolder();
            holder.track = (TextView) convertView.findViewById(R.id.track);
            holder.artist = (TextView) convertView.findViewById(R.id.artist);
            holder.albumImage = (SimpleDraweeView) convertView.findViewById(R.id.albumImage);
            holder.alubmOverlay = (ImageView) convertView.findViewById(R.id.albumOverlay);
            holder.progress = (ProgressBar) convertView.findViewById(R.id.progressBar);
            holder.progress.setVisibility(View.GONE);
            convertView.setTag(holder);
            getItem(position).view = holder;
        } else {
            holder = (SpotifyTrackAdpater.ViewHolder) convertView.getTag();
        }
        final Objects.spotifyTrack track = getItem(position);
        if(track.isPlaying){
            holder.alubmOverlay.setMaxHeight(10);
            holder.alubmOverlay.setMaxWidth(10);
            holder.alubmOverlay.setImageDrawable(pause);

        }
        else{
            holder.alubmOverlay.setImageDrawable(play);
        }
        holder.track.setText(track.track);
        holder.artist.setText(track.artist);
        if(track.imageURL!=null)holder.albumImage.setImageURI(stringToUri(track.imageURL));
        holder.alubmOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlastMediaPlayer player = BlastMediaPlayer.getInstance();
                if(!track.isPlaying) {
                    Blast_Home.stopStreamMusic();
                    holder.alubmOverlay.setVisibility(View.INVISIBLE);
                    holder.progress.setVisibility(View.VISIBLE);
                    try {
                        player.mp = new MediaPlayer();
                        player.mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        player.mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(final MediaPlayer mp) {
                                track.isPlaying = true;
                                notifyDataSetChanged();
                                holder.alubmOverlay.setVisibility(View.VISIBLE);
                                holder.progress.setVisibility(View.INVISIBLE);
                                mp.start();
                            }
                        });
                        player.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                track.isPlaying = false;
                                mp.stop();
                                notifyDataSetChanged();
                            }
                        });
                        player.mp.setDataSource(track.previewURL);
                        player.mp.prepareAsync();

                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
                else{
                    track.isPlaying = false;
                    Blast_Home.stopStreamMusic();
                    notifyDataSetChanged();
                }
                if(lastTrack!= null){
                    lastTrack.isPlaying = false;
                }
                lastTrack = track;
            }
        });

        notifyDataSetChanged();
        return  convertView;
    }

    private Uri stringToUri(String path){
        Uri uri = Uri.parse(path);
        return uri;
    }
}
