package com.dylan.blast;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.util.Base64;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import extras.SharedPreferencesEditor;

/**
 * Created by Dylan on 10/10/2015.
 */
public class Post {
    Context context;
    String baseUsername;
    String baseUID;
    public String postID;
    public String username;
    public String userID;
    public String time;
    public String message;
    public String song;
    public String artist;
    public String album;
    public String path_to_song;
    public String path_to_album;
    public String songID;
    public int reblastCount;
    public int duration;
    public int isRepost;
    String likes = null;
    List<String> likeList= null;
    public int count = 0;
    public int position;
    public Bitmap albumImageBit = null;
    public boolean isPlaying = false;
    public BlastAdapter.ViewHolder view = null;
    public BlastAdapterTrending.ViewHolder trendView = null;
    public boolean isLoading = false;
    public boolean isPaused = false;
    public String timestamp = null;
    public String miles = "";
    public int numComments = 0;

    //General Post Constructor
    public Post(
            int isRepost,
            String baseUsername,
            String baseUID,
            String postID,
            int position,
            String username,
            String userID,
            String time,
            String message,
            String song,
            String artist,
            String album,
            String path_to_song,
            String path_to_album,
            String songID,
            String likes,
            int duration,
            int reblastCount,
            Context context
    )
    {
        this.isRepost = isRepost;
        this.baseUsername = baseUsername;
        this.baseUID = baseUID;
        this.postID = postID;
        this.position = position;
        this.username = username;
        this.userID = userID;
        this.timestamp = getTime(time);
        this.time = timeToString(this.timestamp);
        this.message = decode64(message);
        this.song = song;
        this.artist = artist;
        this.album = album;
        this.path_to_song = path_to_song;
        this.path_to_album = path_to_album;
        this.songID = songID;
        this.duration = duration;
        this.reblastCount = setReblastCount(reblastCount);
        this.likes = likes;
        likeList = new LinkedList(Arrays.asList(likes.split("\\s*,\\s*")));
        if(likes.equals("null"))likeList.clear();
        this.context = context;


    }

    //Radius Post Constructor
    public Post(
            int isRepost,
            String baseUsername,
            String baseUID,
            String postID,
            int position,
            String username,
            String userID,
            String time,
            String message,
            String song,
            String artist,
            String album,
            String path_to_song,
            String path_to_album,
            String songID,
            String likes,
            int duration,
            int reblastCount,
            double postLong,
            double postLat,
            Context context
    )
    {
        this.isRepost = isRepost;
        this.baseUsername = baseUsername;
        this.baseUID = baseUID;
        this.postID = postID;
        this.position = position;
        this.username = username;
        this.userID = userID;
        this.timestamp = getTime(time);
        this.time = timeToString(this.timestamp);
        this.message = decode64(message);
        this.song = song;
        this.artist = artist;
        this.album = album;
        this.path_to_song = path_to_song;
        this.path_to_album = path_to_album;
        this.songID = songID;
        this.duration = duration;
        this.reblastCount = setReblastCount(reblastCount);
        this.likes = likes;
        likeList = new LinkedList(Arrays.asList(likes.split("\\s*,\\s*")));
        if(likes.equals("null"))likeList.clear();
        this.context = context;
        this.miles = calcMilesAway(postLat,postLong);


    }
    //Trending post constructor
    public Post(
            int isRepost,
            String baseUsername,
            String baseUID,
            String postID,
            String likes,
            int count,
            int position,
            String username,
            String userID,
            String time,
            String message,
            String song,
            String artist,
            String album,
            String path_to_song,
            String path_to_album,
            String songID,
            int duration,
            int reblastCount,
            Context context)
    {
        this.isRepost = isRepost;
        this.baseUsername = baseUsername;
        this.baseUID = baseUID;
        this.postID = postID;
        this.likes = likes;
        this.username = username;
        this.userID = userID;
        this.timestamp = getTime(time);
        this.time = timeToString(this.timestamp);
        this.message = decode64(message);
        this.song = song;
        this.artist = artist;
        this.album = album;
        this.path_to_song = path_to_song;
        this.path_to_album = path_to_album;
        this.songID = songID;
        this.duration = duration;
        this.reblastCount = setReblastCount(reblastCount);
        this.count = count;
        this.position = position;
        this.context = context;
        likeList = new LinkedList(Arrays.asList(likes.split("\\s*,\\s*")));
        if(likes.equals("null"))likeList.clear();
    }


    private int setReblastCount(int reblastCount){
        if(reblastCount <= 1)return 0;
        else return reblastCount-1;
    }


    private String timeToString(String time){
        String newTime = null;
        if(time != null) {
            try {
                String[] parts = time.split(" ");
                String[] yearParts = parts[0].split("-");
                final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                final Date dateObj = sdf.parse(parts[1]);
                String regTime = new SimpleDateFormat("K:mm aa").format(dateObj);
                newTime = String.format("%s %s %s ",yearParts[2],getMonth(yearParts[1]),yearParts[0]);

            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        return newTime;
    }

    private String getMonth(String value){
        String month = null;
        if(value.equals("01")) month = "Jan";
        if(value.equals("02")) month = "Feb";
        if(value.equals("03")) month = "Mar";
        if(value.equals("04")) month = "Apr";
        if(value.equals("05")) month = "May";
        if(value.equals("06")) month = "Jun";
        if(value.equals("07")) month = "Jul";
        if(value.equals("08")) month = "Aug";
        if(value.equals("09")) month = "Sep";
        if(value.equals("10")) month = "Oct";
        if(value.equals("11")) month = "Nov";
        if(value.equals("12")) month = "Dec";
        return month;
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

    private String getTime(String OurDate)
    {
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = formatter.parse(OurDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //this format changeable
            dateFormatter.setTimeZone(TimeZone.getDefault());
            OurDate = dateFormatter.format(value);

            //Log.d("OurDate", OurDate);
        }
        catch (Exception e)
        {
            OurDate = "00-00-0000 00:00";
        }
        return OurDate;
    }

    private String calcMilesAway(double latitude,double longitude){
        Location loc1 = new Location("");
        loc1.setLatitude(latitude);
        loc1.setLongitude(longitude);
        //System.out.println("#@#@#@#@    " + latitude + "," + longitude);
        Location loc2 = new Location("");
        loc2.setLongitude(SharedPreferencesEditor.recieveFloat(context, "latitude"));
        loc2.setLatitude(SharedPreferencesEditor.recieveFloat(context, "longitude"));
        //System.out.println("#@#@#@#@    " + SharedPreferencesEditor.recieveFloat(context, "latitude") + "," + SharedPreferencesEditor.recieveFloat(context, "longitude"));
        double miles = 0.000621371192*loc1.distanceTo(loc2);
        if(miles<1){
            return "< 1 mi";
        }
        else {
            DecimalFormat df = new DecimalFormat("##.#");

            return df.format(miles) + " mi";
        }
    }


}
