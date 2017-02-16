package com.dylan.blast;

import android.util.Base64;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Dylan on 11/19/2015.
 */
public class Comment {
    public String userID = null;
    public String postID = null;
    public String commentID = null;
    public String comment = null;
    public String username = null;
    public String created = null;

    public Comment(String commentID, String postID, String userID, String comment, String username, String created){
        this.commentID = commentID;
        this.postID = postID;
        this.userID = userID;
        this.comment = decode64(comment);
        this.username = username;
        if(created.equals("Just Now")) {
            this.created = created;
        }
        else{
            this.created = timeToString(getTime(created));
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

    private String timeToString(String time){
        String newTime = null;
        if(time != null) {
                try {
                    String[] parts = time.split(" ");
                    String[] yearParts = parts[0].split("-");
                    final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                    final Date dateObj = sdf.parse(parts[1]);
                    String regTime = new SimpleDateFormat("K:mm aa").format(dateObj);
                    newTime = String.format("%s %s %s ", yearParts[2], getMonth(yearParts[1]), yearParts[0]);

                } catch (Exception e) {
                    e.printStackTrace();
                }
        }

        return newTime;
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
}
