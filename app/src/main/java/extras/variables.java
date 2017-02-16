package extras;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Base64;

import com.dylan.blast.Post;
import com.dylan.blast.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class variables {

    public static ProgressDialog pd;
    public static ArrayList<Post> radiusPosts = null;
    public static ArrayList<Post> pastPosts = null;
    public static ArrayList<Post> trendingPosts = null;
    public static Bitmap defaultAlbum;
    public static Bitmap defaultUser;
    public static File postsCache;
    public static File usersCache;
    public static boolean autoPlay = false;
    public static String uid = null;
    public static List<String> followList = new ArrayList();
    public static String bio = null;
    public static User currentUser = null;
    public static String radius = "10";
    public static String server = null;
    public static String authToken = null;


    public static void setPD(Context c){
        pd = new ProgressDialog(c);
    }

    public static Uri stringToUri(String path){
        Uri uri = Uri.parse(path);
        return uri;
    }

    public static String decode64(String s){
        String newString = null;
        try {
            byte[] data = Base64.decode(s, Base64.DEFAULT);
            newString =  new String(data, "UTF-8");
        }
        catch(Exception e){e.printStackTrace();}
        return newString;
    }

}
