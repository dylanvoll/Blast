package extras;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Base64;

import com.dylan.blast.Blast_Login;
import com.dylan.blast.Comment;
import com.dylan.blast.ListPair;
import com.dylan.blast.Objects;
import com.dylan.blast.Post;
import com.dylan.blast.R;
import com.dylan.blast.User;

public class WebRequester {
	Context c = null;
    String host = null;
    SSLContextBuilder builder;

    SSLConnectionSocketFactory sslsf;

	
	
	public WebRequester(Context context){
		c=context;
        host = c.getResources().getString(R.string.host);
        try{
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            sslsf = new SSLConnectionSocketFactory(
                    builder.build());
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
	}

    public String encode64(String s){
        byte[] data = s.getBytes();
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public boolean ping(){

        BufferedReader in;
        boolean b = false;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/ping.php?", host);
            request.setURI(new URI(url));
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String result = in.readLine();
            if(!result.equals(1)){
                b = true;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return b;
    }

    public boolean getFollowers(String userID){
        BufferedReader in = null;
        boolean b = false;
        String result = null;
        StringBuilder sb = new StringBuilder();
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/getFollowers.php?", host);
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("user_id", userID));
            postParameters.add(new BasicNameValuePair("auth_token", variables.authToken));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
            request.setEntity(form);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            ArrayList<String> lines = new ArrayList<String>();
            int i =0;
            String line = null;
            while ((line = in.readLine()) != null)
            {
                lines.add(line);
                sb.append(line + "\n");
                i++;
            }
            result = sb.toString();

            //System.out.println(lines.get(i-1));
            JSONObject jObject = new JSONObject(lines.get(i-1));
            b = jObject.getBoolean("code");
            variables.followList = new LinkedList(Arrays.asList(jObject.getString("following").split("\\s*,\\s*")));
            variables.bio = jObject.getString("bio");
            in.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }


        return b;
    }

    public boolean recoverPass(String email){
        BufferedReader in = null;
        boolean b = false;
        String result = null;
        StringBuilder sb = new StringBuilder();
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/recoverPass.php?", host);
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("email", email));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
            request.setEntity(form);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            ArrayList<String> lines = new ArrayList<String>();
            int i =0;
            String line = null;
            while ((line = in.readLine()) != null)
            {
                lines.add(line);
                sb.append(line + "\n");
                i++;
            }
            result = sb.toString();

            //System.out.println(lines.get(i-1));
            JSONObject jObject = new JSONObject(lines.get(i-1));
            b = jObject.getBoolean("code");
            Blast_Login.errorMessage = jObject.getString("message");
            in.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }


        return b;
    }

    public ArrayList<Post> getTrailingPosts(List<String> following){
        ArrayList<Post> posts = new ArrayList<Post>();
        BufferedReader in = null;
        try{
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/getTrailingPosts.php?", host);
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("following", "'" + StringUtils.join(following.iterator() , "','") + "'"));
            postParameters.add(new BasicNameValuePair("user_id", variables.uid));
            postParameters.add(new BasicNameValuePair("auth_token", variables.authToken));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
            request.setEntity(form);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String jsonString = in.readLine();
            //System.out.println(jsonString);
            JSONArray postsArray = new JSONArray(jsonString);
            for (int i = 0; i < postsArray.length(); i++) {
                JSONObject jsonobject = postsArray.getJSONObject(i);
                String postID = jsonobject.getString("postID");
                String username = jsonobject.getString("username");
                String userID = jsonobject.getString("userID");
                String time = jsonobject.getString("created");
                String message = jsonobject.getString("message");
                String song = jsonobject.getString("song");
                String artist = jsonobject.getString("artist");
                String album = jsonobject.getString("album");
                String path_to_song = jsonobject.getString("path_to_song");
                String path_to_album = jsonobject.getString("path_to_album");
                String songID = jsonobject.getString("songID");
                String likes = jsonobject.getString("likes");
                String baseUsername = jsonobject.getString("baseUsername");
                String baseUID = jsonobject.getString("baseUID");
                int duration = jsonobject.getInt("duration");
                int reblastCount = jsonobject.getInt("reblast");
                int isRepost = jsonobject.getInt("isRepost");
                Post post = new Post(isRepost,baseUsername,baseUID,postID,i,username,userID,time,message,song,artist,album,path_to_song,path_to_album,songID,likes,duration,reblastCount,c);
                posts.add(post);
            }
            in.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return posts;
    }

    public ArrayList<Post> getTrailingPostsAdd(List<String> following,String timestamp){
        ArrayList<Post> posts = new ArrayList<Post>();
        BufferedReader in = null;
        try{
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/getTrailingPostsAdd.php?", host);
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("user_id", variables.uid));
            postParameters.add(new BasicNameValuePair("auth_token", variables.authToken));
            postParameters.add(new BasicNameValuePair("following", "'" + StringUtils.join(following.iterator() , "','") + "'"));
            postParameters.add(new BasicNameValuePair("timestamp", timestamp));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
            request.setEntity(form);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String jsonString = in.readLine();
            //System.out.println(jsonString);
            JSONArray postsArray = new JSONArray(jsonString);
            for (int i = 0; i < postsArray.length(); i++) {
                JSONObject jsonobject = postsArray.getJSONObject(i);
                String postID = jsonobject.getString("postID");
                String username = jsonobject.getString("username");
                String userID = jsonobject.getString("userID");
                String time = jsonobject.getString("created");
                String message = jsonobject.getString("message");
                String song = jsonobject.getString("song");
                String artist = jsonobject.getString("artist");
                String album = jsonobject.getString("album");
                String path_to_song = jsonobject.getString("path_to_song");
                String path_to_album = jsonobject.getString("path_to_album");
                String songID = jsonobject.getString("songID");
                String likes = jsonobject.getString("likes");
                String baseUsername = jsonobject.getString("baseUsername");
                String baseUID = jsonobject.getString("baseUID");
                int duration = jsonobject.getInt("duration");
                int reblastCount = jsonobject.getInt("reblast");
                int isRepost = jsonobject.getInt("isRepost");
                Post post = new Post(isRepost,baseUsername,baseUID,postID,i,username,userID,time,message,song,artist,album,path_to_song,path_to_album,songID,likes,duration,reblastCount,c);
                posts.add(post);
            }
            in.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return posts;
    }

    public boolean postComment(String comment, String postID, String userID){

        BufferedReader in = null;
        boolean b = false;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/postComment.php?", host);
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("post_id", postID));
            postParameters.add(new BasicNameValuePair("user_id", userID));
            postParameters.add(new BasicNameValuePair("auth_token", variables.authToken));
            postParameters.add(new BasicNameValuePair("comment", encode64(comment)));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
            request.setEntity(form);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String jsonString = in.readLine();
            //System.out.println(jsonString);
            JSONObject responseArray = new JSONObject(jsonString);
            b = responseArray.getBoolean("code");
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return b;
    }

    public boolean likePost(String postID, String userID){

        BufferedReader in = null;
        boolean b = false;
        String result = null;
        StringBuilder sb = new StringBuilder();
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/likePost.php?", host);
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("post_id", postID));
            postParameters.add(new BasicNameValuePair("auth_token", variables.authToken));
            postParameters.add(new BasicNameValuePair("user_id", userID));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
            request.setEntity(form);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            ArrayList<String> lines = new ArrayList<String>();
            int i =0;
            String line = null;
            while ((line = in.readLine()) != null)
            {
                lines.add(line);
                sb.append(line + "\n");
                i++;
            }
            result = sb.toString();

            //System.out.println(lines.get(i-1));
            JSONObject jObject = new JSONObject(lines.get(i-1));
            b = jObject.getBoolean("code");
            in.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return b;
    }

    public boolean followUser(String userID, String followID){

        BufferedReader in = null;
        boolean b = false;
        String result = null;
        StringBuilder sb = new StringBuilder();
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/followUser.php?", host);
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("follow_id", followID));
            postParameters.add(new BasicNameValuePair("auth_token", variables.authToken));
            postParameters.add(new BasicNameValuePair("user_id", userID));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
            request.setEntity(form);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            ArrayList<String> lines = new ArrayList<String>();
            int i =0;
            String line = null;
            while ((line = in.readLine()) != null)
            {
                lines.add(line);
                sb.append(line + "\n");
                i++;
            }
            result = sb.toString();

            //System.out.println(lines.get(i-1));
            JSONObject jObject = new JSONObject(lines.get(i-1));
            b = jObject.getBoolean("code");
            in.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return b;
    }

    public boolean unfollowUser(String userID, String followID){

        BufferedReader in = null;
        boolean b = false;
        String result = null;
        StringBuilder sb = new StringBuilder();
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/unfollowUser.php?", host);
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("follow_id", followID));
            postParameters.add(new BasicNameValuePair("auth_token", variables.authToken));
            postParameters.add(new BasicNameValuePair("user_id", userID));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
            request.setEntity(form);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            ArrayList<String> lines = new ArrayList<String>();
            int i =0;
            String line = null;
            while ((line = in.readLine()) != null)
            {
                lines.add(line);
                sb.append(line + "\n");
                i++;
            }
            result = sb.toString();

            System.out.println(lines.get(i-1));
            JSONObject jObject = new JSONObject(lines.get(i-1));
            b = jObject.getBoolean("code");
            in.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return b;
    }

    public boolean unlikePost(String postID, String userID){

        BufferedReader in = null;
        boolean b = false;
        String result = null;
        StringBuilder sb = new StringBuilder();
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/unlikePost.php?", host);
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("post_id", postID));
            postParameters.add(new BasicNameValuePair("auth_token", variables.authToken));
            postParameters.add(new BasicNameValuePair("user_id", userID));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
            request.setEntity(form);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            ArrayList<String> lines = new ArrayList<String>();
            int i =0;
            String line = null;
            while ((line = in.readLine()) != null)
            {
                lines.add(line);
                sb.append(line + "\n");
                i++;
            }
            result = sb.toString();

            //System.out.println(lines.get(i-1));
            JSONObject jObject = new JSONObject(lines.get(i-1));
            b = jObject.getBoolean("code");
            in.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }


        return b;
    }

    public ArrayList<Comment> getPostComments(Post post){
        ArrayList<Comment> comments = new ArrayList<Comment>();
        BufferedReader in = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/getPostComments.php?", host);
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("post_id",post.postID));
            postParameters.add(new BasicNameValuePair("auth_token", variables.authToken));
            postParameters.add(new BasicNameValuePair("user_id", variables.uid));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
            request.setEntity(form);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String jsonString = in.readLine();
            //System.out.println(jsonString);
            if(jsonString!= null) {
                JSONArray postsArray = new JSONArray(jsonString);
                for (int i = 0; i < postsArray.length(); i++) {
                    JSONObject jsonobject = postsArray.getJSONObject(i);
                    String commentID = jsonobject.getString("commentID");
                    String comment = jsonobject.getString("comment");
                    String postID = jsonobject.getString("postID");
                    String userID = jsonobject.getString("userID");
                    String username = jsonobject.getString("username");
                    String created = jsonobject.getString("created");
                    Comment c = new Comment(commentID, postID, userID, comment, username, created);
                    comments.add(c);
                }
            }
            in.close();
        }
            catch(Exception e){
                e.printStackTrace();
            }

            return comments;
        }

    public ArrayList<Post> getTrendingPosts(){
        ArrayList<Post> posts = new ArrayList<Post>();
        BufferedReader in = null;
        try{
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/getTrendingPosts.php?", host);
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("auth_token", variables.authToken));
            postParameters.add(new BasicNameValuePair("user_id", variables.uid));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
            request.setEntity(form);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String jsonString = in.readLine();
            //System.out.println(jsonString);
            JSONArray postsArray = new JSONArray(jsonString);
            for (int i = 0; i < postsArray.length(); i++) {
                JSONObject jsonobject = postsArray.getJSONObject(i);
                String postID = jsonobject.getString("postID");
                String username = jsonobject.getString("username");
                String userID = jsonobject.getString("userID");
                String time = jsonobject.getString("created");
                String message = jsonobject.getString("message");
                String song = jsonobject.getString("song");
                String artist = jsonobject.getString("artist");
                String album = jsonobject.getString("album");
                String path_to_song = jsonobject.getString("path_to_song");
                String path_to_album = jsonobject.getString("path_to_album");
                String songID = jsonobject.getString("songID");
                int duration = jsonobject.getInt("duration");
                int count = jsonobject.getInt("count");
                String likes = jsonobject.getString("likeList");
                String baseUsername = jsonobject.getString("baseUsername");
                String baseUID = jsonobject.getString("baseUID");
                int isRepost = jsonobject.getInt("isRepost");
                int reblastCount = jsonobject.getInt("reblast");
                Post post = new Post(isRepost,baseUsername,baseUID,postID,likes,count,i+1,username,userID,time,message,song,artist,album,path_to_song,path_to_album,songID,duration,reblastCount,c);
                posts.add(post);
            }
            in.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return posts;
    }

    public ArrayList<String> getUserInfo(String user){
        ArrayList<String> info = new ArrayList<String>();
        BufferedReader in = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/getUserInfo.php?", host);
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("auth_token", variables.authToken));
            postParameters.add(new BasicNameValuePair("user_id", variables.uid));
            postParameters.add(new BasicNameValuePair("user",user));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
            request.setEntity(form);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String jsonString = in.readLine();
            //System.out.println(jsonString);
            if (jsonString != null) {
                JSONArray postsArray = new JSONArray(jsonString);
                JSONObject jsonobject = postsArray.getJSONObject(0);
                info.add(jsonobject.getString("username"));
                info.add(jsonobject.getString("first_name"));
                info.add(jsonobject.getString("last_name"));
                info.add(jsonobject.getString("bio"));
                info.add(jsonobject.getString("numPosts"));
                in.close();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return info;
    }

    public ListPair getTrailLists(ListPair pair,String uid){
         ArrayList<User> trailers = new ArrayList<>();
         ArrayList<User> trailing = new ArrayList<>();
        BufferedReader in = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/getTrailLists.php?", host);
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("auth_token", variables.authToken));
            postParameters.add(new BasicNameValuePair("user_id", variables.uid));
            postParameters.add(new BasicNameValuePair("user", uid));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
            request.setEntity(form);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String jsonString = in.readLine();
            //System.out.println(jsonString);
            if (jsonString != null) {
                JSONObject postsArray = new JSONObject(jsonString);
                JSONArray trailerRows = postsArray.getJSONArray("trailers");
                for (int i = 0; i < trailerRows.length(); i++) {
                    JSONObject jsonobject = trailerRows.getJSONObject(i);
                    String username = jsonobject.getString("username");
                    String firstName = jsonobject.getString("first_name");
                    String lastName = jsonobject.getString("last_name");
                    String user_id = jsonobject.getString("user_id");
                    User user = new User(username, user_id, firstName, lastName);
                    trailers.add(user);
                }
                JSONArray trailingRows = postsArray.getJSONArray("trailing");
                for (int i = 0; i < trailingRows.length(); i++) {
                    JSONObject jsonobject = trailingRows.getJSONObject(i);
                    String username = jsonobject.getString("username");
                    String firstName = jsonobject.getString("first_name");
                    String lastName = jsonobject.getString("last_name");
                    String user_id = jsonobject.getString("user_id");
                    User user = new User(username, user_id, firstName, lastName);
                    trailing.add(user);

                }
                pair.setTrailers(trailers);
                pair.setTrailing(trailing);
                in.close();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return pair;
    }

    public ArrayList<User> searchUsers(String searchString){
        ArrayList<User> users = new ArrayList<User>();
        BufferedReader in = null;
        try{
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/searchUsers.php?", host);
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("auth_token", variables.authToken));
            postParameters.add(new BasicNameValuePair("user_id", variables.uid));
            postParameters.add(new BasicNameValuePair("search_string", searchString));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
            request.setEntity(form);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String jsonString = in.readLine();
            //System.out.println(jsonString);
            if(jsonString!=null) {
                JSONArray postsArray = new JSONArray(jsonString);
                for (int i = 0; i < postsArray.length(); i++) {
                    JSONObject jsonobject = postsArray.getJSONObject(i);
                    String username = jsonobject.getString("username");
                    String firstName = jsonobject.getString("first_name");
                    String lastName = jsonobject.getString("last_name");
                    String uid = jsonobject.getString("user_id");
                    User user = new User(username, uid, firstName, lastName);
                    if (!uid.equals(SharedPreferencesEditor.recieveKey(c, "userID"))) {
                        users.add(user);
                    }
                }
            }
            in.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return users;
    }

    public ArrayList<User> getPostReblastList(String songID, String baseUID){
        ArrayList<User> users = new ArrayList<User>();
        BufferedReader in = null;
        try{
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/getReblastList.php?", host);
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("auth_token", variables.authToken));
            postParameters.add(new BasicNameValuePair("user_id", variables.uid));
            postParameters.add(new BasicNameValuePair("song_id", songID));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
            request.setEntity(form);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String jsonString = in.readLine();
            //System.out.println(jsonString);
            if(jsonString!=null) {
                JSONArray postsArray = new JSONArray(jsonString);
                for (int i = 0; i < postsArray.length(); i++) {
                    JSONObject jsonobject = postsArray.getJSONObject(i);
                    String username = jsonobject.getString("username");
                    String firstName = jsonobject.getString("first_name");
                    String lastName = jsonobject.getString("last_name");
                    String uid = jsonobject.getString("user_id");
                    int multiplier = jsonobject.getInt("multiplier");
                    User user = new User(username, uid, firstName, lastName,multiplier);
                    if(!user.uid.equals(baseUID)) {
                        users.add(user);
                    }
                }
            }
            in.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return users;
    }

    public ArrayList<User> getPostFavoriteList(String postID){
        ArrayList<User> users = new ArrayList<User>();
        BufferedReader in = null;
        try{
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/getFavoriteList.php?", host);
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("auth_token", variables.authToken));
            postParameters.add(new BasicNameValuePair("user_id", variables.uid));
            postParameters.add(new BasicNameValuePair("post_id", postID));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
            request.setEntity(form);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String jsonString = in.readLine();
            //System.out.println(jsonString);
            if(jsonString!=null) {
                JSONArray postsArray = new JSONArray(jsonString);
                for (int i = 0; i < postsArray.length(); i++) {
                    JSONObject jsonobject = postsArray.getJSONObject(i);
                    String username = jsonobject.getString("username");
                    String firstName = jsonobject.getString("first_name");
                    String lastName = jsonobject.getString("last_name");
                    String uid = jsonobject.getString("user_id");
                    User user = new User(username, uid, firstName, lastName);
                    users.add(user);
                }
            }
            in.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return users;
    }

    public ArrayList<User> getTrailers(String user_id){
        ArrayList<User> users = new ArrayList<User>();
        BufferedReader in = null;
        try{
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/getTrailers.php?", host);
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("auth_token", variables.authToken));
            postParameters.add(new BasicNameValuePair("user_id", user_id));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
            request.setEntity(form);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String jsonString = in.readLine();
            //System.out.println(jsonString);
            if(jsonString!=null) {
                JSONArray postsArray = new JSONArray(jsonString);
                for (int i = 0; i < postsArray.length(); i++) {
                    JSONObject jsonobject = postsArray.getJSONObject(i);
                    String username = jsonobject.getString("username");
                    String firstName = jsonobject.getString("first_name");
                    String lastName = jsonobject.getString("last_name");
                    String uid = jsonobject.getString("user_id");
                    User user = new User(username, uid, firstName, lastName);
                    if (!uid.equals(SharedPreferencesEditor.recieveKey(c, "userID"))) {
                        users.add(user);
                    }
                }
            }
            in.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return users;
    }

    public boolean updateProfilePicture(String uid,String byteString){
        StringBuilder sb = new StringBuilder();
        String result = null;
        BufferedReader in = null;
        boolean b = false;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/updateProfilePicture.php?", host);
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("auth_token", variables.authToken));
            postParameters.add(new BasicNameValuePair("user_id", uid));
            postParameters.add(new BasicNameValuePair("byte_string", byteString));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
            request.setEntity(form);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            ArrayList<String> lines = new ArrayList<String>();
            int i = 0;
            String line = null;
            while ((line = in.readLine()) != null) {
                lines.add(line);
                sb.append(line + "\n");
                i++;
            }
            result = sb.toString();

            //System.out.println(lines.get(i - 1));
            JSONObject jObject = new JSONObject(lines.get(i - 1));
            b = jObject.getBoolean("state");
            in.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return b;
        }

    public boolean updateBio(String uid,String bio){
        StringBuilder sb = new StringBuilder();
        String result = null;
        BufferedReader in = null;
        boolean b = false;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/updateBio.php?", host);
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("auth_token", variables.authToken));
            postParameters.add(new BasicNameValuePair("user_id", uid));
            postParameters.add(new BasicNameValuePair("bio", encode64(bio)));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
            request.setEntity(form);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            ArrayList<String> lines = new ArrayList<String>();
            int i = 0;
            String line = null;
            while ((line = in.readLine()) != null) {
                lines.add(line);
                //System.out.println(line);
                sb.append(line + "\n");
                i++;
            }
            result = sb.toString();

            //System.out.println(lines.get(i - 1));
            JSONObject jObject = new JSONObject(lines.get(i - 1));
            b = jObject.getBoolean("code");
            in.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return b;
    }

    public ArrayList<Post> getPostsByUserID(String user){
        ArrayList<Post> posts = new ArrayList<Post>();
        BufferedReader in = null;
        try{
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/getPostsByUserID.php?", host);
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("auth_token", variables.authToken));
            postParameters.add(new BasicNameValuePair("user_id", variables.uid));
            postParameters.add(new BasicNameValuePair("user", user));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
            request.setEntity(form);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String jsonString = in.readLine();
            //System.out.println(jsonString);
            if(jsonString!=null) {
                JSONArray postsArray = new JSONArray(jsonString);
                for (int i = 0; i < postsArray.length(); i++) {
                    JSONObject jsonobject = postsArray.getJSONObject(i);
                    String postID = jsonobject.getString("postID");
                    String username = jsonobject.getString("username");
                    String userID = jsonobject.getString("userID");
                    String time = jsonobject.getString("created");
                    String message = jsonobject.getString("message");
                    String song = jsonobject.getString("song");
                    String artist = jsonobject.getString("artist");
                    String album = jsonobject.getString("album");
                    String path_to_song = jsonobject.getString("path_to_song");
                    String path_to_album = jsonobject.getString("path_to_album");
                    String songID = jsonobject.getString("songID");
                    String likes = jsonobject.getString("likes");
                    int duration = jsonobject.getInt("duration");
                    String baseUsername = jsonobject.getString("baseUsername");
                    String baseUID = jsonobject.getString("baseUID");
                    int isRepost = jsonobject.getInt("isRepost");
                    int reblastCount = jsonobject.getInt("reblast");
                    Post post = new Post(isRepost, baseUsername, baseUID, postID, i, username, userID, time, message, song, artist, album, path_to_song, path_to_album, songID, likes, duration,reblastCount, c);
                    posts.add(post);
                }
            }
            in.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return posts;
    }

    public ArrayList<Post> getPostsByUserIDAdd(String user,String timestamp){
        ArrayList<Post> posts = new ArrayList<Post>();
        BufferedReader in = null;
        try{
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/getPostsByUserIDAdd.php?", host);
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("auth_token", variables.authToken));
            postParameters.add(new BasicNameValuePair("user_id", variables.uid));
            postParameters.add(new BasicNameValuePair("user", user));
            postParameters.add(new BasicNameValuePair("timestamp", timestamp));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
            request.setEntity(form);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String jsonString = in.readLine();
            //System.out.println(jsonString);
            if(jsonString!=null) {
                JSONArray postsArray = new JSONArray(jsonString);
                for (int i = 0; i < postsArray.length(); i++) {
                    JSONObject jsonobject = postsArray.getJSONObject(i);
                    String postID = jsonobject.getString("postID");
                    String username = jsonobject.getString("username");
                    String userID = jsonobject.getString("userID");
                    String time = jsonobject.getString("created");
                    String message = jsonobject.getString("message");
                    String song = jsonobject.getString("song");
                    String artist = jsonobject.getString("artist");
                    String album = jsonobject.getString("album");
                    String path_to_song = jsonobject.getString("path_to_song");
                    String path_to_album = jsonobject.getString("path_to_album");
                    String songID = jsonobject.getString("songID");
                    String likes = jsonobject.getString("likes");
                    int duration = jsonobject.getInt("duration");
                    String baseUsername = jsonobject.getString("baseUsername");
                    String baseUID = jsonobject.getString("baseUID");
                    int isRepost = jsonobject.getInt("isRepost");
                    int reblastCount = jsonobject.getInt("reblast");
                    Post post = new Post(isRepost, baseUsername, baseUID, postID, i, username, userID, time, message, song, artist, album, path_to_song, path_to_album, songID, likes, duration,reblastCount, c);
                    posts.add(post);
                }
            }
            in.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return posts;
    }

    public ArrayList<Post> getPostsByLocation(float latitude, float longitude, String user){
        ArrayList<Post> posts = new ArrayList<Post>();
        BufferedReader in = null;
        try{
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/getPostsByLocation.php?", host);
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("latitude", latitude+""));
            postParameters.add(new BasicNameValuePair("longitude", longitude+""));
            postParameters.add(new BasicNameValuePair("auth_token", variables.authToken));
            postParameters.add(new BasicNameValuePair("user_id", user));
            postParameters.add(new BasicNameValuePair("radius", variables.radius));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
            request.setEntity(form);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String jsonString = in.readLine();
            //System.out.println(jsonString);
            if(jsonString!=null) {
                JSONArray postsArray = new JSONArray(jsonString);
                for (int i = 0; i < postsArray.length(); i++) {
                    JSONObject jsonobject = postsArray.getJSONObject(i);
                    String postID = jsonobject.getString("postID");
                    String username = jsonobject.getString("username");
                    String userID = jsonobject.getString("userID");
                    String time = jsonobject.getString("created");
                    String message = jsonobject.getString("message");
                    String song = jsonobject.getString("song");
                    String artist = jsonobject.getString("artist");
                    String album = jsonobject.getString("album");
                    String path_to_song = jsonobject.getString("path_to_song");
                    String path_to_album = jsonobject.getString("path_to_album");
                    String songID = jsonobject.getString("songID");
                    String likes = jsonobject.getString("likes");
                    int duration = jsonobject.getInt("duration");
                    String baseUsername = jsonobject.getString("baseUsername");
                    String baseUID = jsonobject.getString("baseUID");
                    int isRepost = jsonobject.getInt("isRepost");
                    int reblastCount = jsonobject.getInt("reblast");
                    double postLat = jsonobject.getDouble("postLat");
                    double postLong = jsonobject.getDouble("postLong");
                    Post post = new Post(isRepost, baseUsername, baseUID, postID, i, username, userID, time, message, song, artist, album, path_to_song, path_to_album, songID, likes, duration,reblastCount,postLat,postLong, c);
                    posts.add(post);
                }
            }
            in.close();

        }
        catch(Exception e){
            e.printStackTrace();
        }
        return posts;
    }

    public ArrayList<Post> getPostsByLocationAdd(float latitude, float longitude, String user, String timestamp){
        ArrayList<Post> posts = new ArrayList<Post>();
        BufferedReader in = null;
        try{
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/getPostsByLocationAdd.php?", host);
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("latitude", latitude+""));
            postParameters.add(new BasicNameValuePair("longitude", longitude+""));
            postParameters.add(new BasicNameValuePair("auth_token", variables.authToken));
            postParameters.add(new BasicNameValuePair("user_id", user));
            postParameters.add(new BasicNameValuePair("timestamp", timestamp));
            postParameters.add(new BasicNameValuePair("radius", variables.radius));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
            request.setEntity(form);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String jsonString = in.readLine();
            //System.out.println(jsonString);
            if(jsonString!=null) {
                JSONArray postsArray = new JSONArray(jsonString);
                for (int i = 0; i < postsArray.length(); i++) {
                    JSONObject jsonobject = postsArray.getJSONObject(i);
                    String postID = jsonobject.getString("postID");
                    String username = jsonobject.getString("username");
                    String userID = jsonobject.getString("userID");
                    String time = jsonobject.getString("created");
                    String message = jsonobject.getString("message");
                    String song = jsonobject.getString("song");
                    String artist = jsonobject.getString("artist");
                    String album = jsonobject.getString("album");
                    String path_to_song = jsonobject.getString("path_to_song");
                    String path_to_album = jsonobject.getString("path_to_album");
                    String songID = jsonobject.getString("songID");
                    String likes = jsonobject.getString("likes");
                    int duration = jsonobject.getInt("duration");
                    String baseUsername = jsonobject.getString("baseUsername");
                    String baseUID = jsonobject.getString("baseUID");
                    int isRepost = jsonobject.getInt("isRepost");
                    int reblastCount = jsonobject.getInt("reblast");
                    double postLat = jsonobject.getDouble("postLat");
                    double postLong = jsonobject.getDouble("postLong");
                    Post post = new Post(isRepost, baseUsername, baseUID, postID, i, username, userID, time, message, song, artist, album, path_to_song, path_to_album, songID, likes, duration,reblastCount,postLat,postLong, c);
                    posts.add(post);
                }
            }
            in.close();

        }
        catch(Exception e){
            e.printStackTrace();
        }
        return posts;
    }

    public boolean repostSong(float latitude, float longitude, String user, String songID, String message){
        BufferedReader in = null;
        boolean b = false;
        try{
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/repostSong.php?", host);
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("post_latitude", latitude+""));
            postParameters.add(new BasicNameValuePair("post_longitude", longitude+""));
            postParameters.add(new BasicNameValuePair("auth_token", variables.authToken));
            postParameters.add(new BasicNameValuePair("user_id", user));
            postParameters.add(new BasicNameValuePair("song_id", songID));
            postParameters.add(new BasicNameValuePair("post_message", encode64(message)));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
            request.setEntity(form);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder sb = new StringBuilder();
            ArrayList<String> lines = new ArrayList<String>();
            int i =0;
            String line = null;
            while ((line = in.readLine()) != null)
            {
                //System.out.println(line);
                lines.add(line);
                sb.append(line + "\n");
                i++;
            }
            JSONObject jsonobject = new JSONObject(lines.get(i-1));
            b = jsonobject.getBoolean("code");
            //System.out.println(b);
            in.close();

        }
        catch(Exception e){
            e.printStackTrace();
        }
        return b;
    }


    public boolean registerUser(String username,String password,String firstName,String lastName,String email){

        BufferedReader in = null;
        boolean code = false;
        String result = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/registerUser.php?", host);
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("potentialUser", username));
            postParameters.add(new BasicNameValuePair("potentialPassword", encode64(password)));
            postParameters.add(new BasicNameValuePair("potentialEmail", email));
            postParameters.add(new BasicNameValuePair("potentialFirstName", firstName));
            postParameters.add(new BasicNameValuePair("potentialLastName", lastName));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
            request.setEntity(form);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder sb = new StringBuilder();
            ArrayList<String> lines = new ArrayList<String>();
            int i =0;
            String line = null;
            while ((line = in.readLine()) != null)
            {
                //System.out.println(line);
                lines.add(line);
                sb.append(line + "\n");
                i++;
            }
            result = sb.toString();
            //System.out.println(lines.get(i-1));
            JSONObject jObject = new JSONObject(lines.get(i-1));
            code = jObject.getBoolean("code");
            in.close();

        }
        catch(Exception e){
            e.printStackTrace();
        }
        return code;
    }

    public boolean changePassword(String uid,String cp, String np){

        BufferedReader in = null;
        boolean code = false;
        String result = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/changePassword.php?", host);
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("cp", encode64(cp)));
            postParameters.add(new BasicNameValuePair("np", encode64(np)));
            postParameters.add(new BasicNameValuePair("auth_token", variables.authToken));
            postParameters.add(new BasicNameValuePair("user_id", uid));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
            request.setEntity(form);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder sb = new StringBuilder();
            ArrayList<String> lines = new ArrayList<String>();
            int i =0;
            String line = null;
            while ((line = in.readLine()) != null)
            {
                //System.out.println(line);
                lines.add(line);
                sb.append(line + "\n");
                i++;
            }
            result = sb.toString();
            //System.out.println(lines.get(i-1));
            JSONObject jObject = new JSONObject(lines.get(i-1));
            code = jObject.getBoolean("code");
            in.close();

        }
        catch(Exception e){
            e.printStackTrace();
        }
        return code;
    }
	
	public int login(String username,String password){
	
		BufferedReader in = null;
		int code = 0;
		String result = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/login.php?", host);
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("userinput", username));
            postParameters.add(new BasicNameValuePair("passinput", encode64(password)));
            //System.out.println(encode64(password));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
            request.setEntity(form);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));             
            StringBuilder sb = new StringBuilder();
            ArrayList<String> lines = new ArrayList<String>();
            int i =0;
            String line = null;
            while ((line = in.readLine()) != null)
            {
            	lines.add(line);
                sb.append(line + "\n");
                i++;
            }
            result = sb.toString();
            JSONObject jObject = new JSONObject(lines.get(i-1));
            code = jObject.getInt("code");
            if(code==1) {
            	Blast_Login.userID = jObject.getString("userID");
                Blast_Login.authToken = jObject.getString("authToken");
                variables.followList = new LinkedList(Arrays.asList(jObject.getString("following").split("\\s*,\\s*")));
                variables.bio = jObject.getString("bio");
            	}
            in.close();
            
        	}
        catch(Exception e){
        	e.printStackTrace();
        }
        return code;
	}
	public int postSong(String userID, String songTitle,String songArtist,String songAlbum,String albumByteString,String postMessage,int duration,String byteString,float latitude, float longitude,boolean spotify) {
		
		BufferedReader in = null;
		int code = 0;
		String result = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            String url = String.format("http://%s:80/postSong.php?", host);
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("song_artist", songArtist));
            postParameters.add(new BasicNameValuePair("song_album", songAlbum));
            postParameters.add(new BasicNameValuePair("auth_token", variables.authToken));
            postParameters.add(new BasicNameValuePair("user_id", userID));
            postParameters.add(new BasicNameValuePair("post_message", encode64(postMessage)));
            postParameters.add(new BasicNameValuePair("post_latitude", latitude + "" ));
            postParameters.add(new BasicNameValuePair("post_longitude", longitude + ""));
            postParameters.add(new BasicNameValuePair("song_title", songTitle));
            postParameters.add(new BasicNameValuePair("duration", duration+""));
            postParameters.add(new BasicNameValuePair("album_byte_string", albumByteString));
            postParameters.add(new BasicNameValuePair("byte_string", byteString));
            postParameters.add(new BasicNameValuePair("spotify",String.valueOf(spotify)));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
            request.setEntity(form);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder sb = new StringBuilder();
           ArrayList<String> lines = new ArrayList<String>();
            int i =0;
           String line = null;
            while ((line = in.readLine()) != null)
           {
           	lines.add(line);
                sb.append(line + "\n");
                i++;
            }
            result = sb.toString();
            //System.out.println(lines.get(i-1));
            for(String line1: lines){
                //System.out.println(line1);
            }
            JSONObject jObject = new JSONObject(lines.get(i-1));
            code = jObject.getInt("code");

            in.close();
            
        	}
        catch(Exception e){
        	e.printStackTrace();
        }
        return code;
		
	}

    public ArrayList<Objects.spotifyTrack> spotifySearch(String artist, String song){

        BufferedReader in = null;
        String result = null;
        ArrayList<Objects.spotifyTrack> spotifyTracks = new ArrayList<>();
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            StringBuilder url = new StringBuilder("https://api.spotify.com/v1/search?q=");
            if(!artist.isEmpty() && !song.isEmpty()){
                url.append("track:%22" + song.replace(" ", "%20") + "%22" );
                url.append("OR");
                url.append("artist:%22" + artist.replace(" ", "%20") + "%22" );
            }
            else if(!song.isEmpty()){
                url.append("track:%22" + song.replace(" ", "%20") + "%22" );
            }
            else if(!artist.isEmpty()){
                url.append("artist:%22" + artist.replace(" ", "%20") + "%22" );
            }
            url.append("&type=track");
            request.setURI(new URI(url.toString()));
            //System.out.println(encode64(password));

            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder sb = new StringBuilder();
            ArrayList<String> lines = new ArrayList<String>();
            int i =0;
            String line = null;
            while ((line = in.readLine()) != null)
            {
                lines.add(line);
                sb.append(line + "\n");
                i++;
            }
            result = sb.toString();
            JSONObject jObject = new JSONObject(result);
            JSONArray tracks = jObject.getJSONObject("tracks").getJSONArray("items");
            for (int j = 0; j < tracks.length(); j++) {
                JSONObject track = tracks.getJSONObject(j);
                String trackArtist = track.getJSONArray("artists").getJSONObject(0).getString("name");
                String title = track.getString("name");
                String imageUrl = track.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
                String album = track.getJSONObject("album").getString("name");
                String previewUrl = track.getString("preview_url");
                spotifyTracks.add(new Objects.spotifyTrack(trackArtist,album,title,previewUrl,imageUrl));
            }
            in.close();

        }
        catch(Exception e){
            e.printStackTrace();
        }
        return spotifyTracks;
    }
}
