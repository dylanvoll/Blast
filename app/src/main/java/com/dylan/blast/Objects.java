package com.dylan.blast;

/**
 * Created by Dylan on 12/26/2016.
 */

public class Objects {

    public static class spotifyTrack{
        String artist;
        String album;
        String track;
        String previewURL;
        String imageURL;
        SpotifyTrackAdpater.ViewHolder view;
        boolean isPlaying = false;

        public spotifyTrack(String artist, String album, String track, String previewURL, String imageURL){
            this.artist = artist;
            this.album = album;
            this.track = track;
            this.previewURL = previewURL;
            this.imageURL = imageURL;
        }
    }
}
