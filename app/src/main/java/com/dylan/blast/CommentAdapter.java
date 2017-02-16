package com.dylan.blast;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import extras.SharedPreferencesEditor;
import extras.variables;

/**
 * Created by Dylan on 11/19/2015.
 */
public class CommentAdapter extends ArrayAdapter<Comment> {

    private LayoutInflater inflater;
    Activity activity;
    public static Post post;

    public CommentAdapter(Activity activity, ArrayList<Comment> comments, Post post){

        super(activity, R.layout.comment_row_layout, comments);
        inflater = activity.getWindow().getLayoutInflater();
        this.activity = activity;
        this.post = post;

    }

    static class ViewHolder{

        SimpleDraweeView userImage;
        TextView username;
        TextView comment;
        TextView date;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment_row_layout, parent, false);
            holder = new ViewHolder();
            holder.userImage = (SimpleDraweeView) convertView.findViewById(R.id.comment_userImage);
            holder.username = (TextView) convertView.findViewById(R.id.comment_username);
            holder.comment = (TextView) convertView.findViewById(R.id.comment_message);
            holder.date = (TextView) convertView.findViewById(R.id.comment_date);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        final Comment comment = getItem(position);

        holder.userImage.setImageURI(stringToUri(String.format("%s/blast/users/%s/profile.png",variables.server, comment.userID)));
        if(comment.userID != variables.currentUser.uid) {
            holder.userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!comment.userID.equals(SharedPreferencesEditor.recieveKey(getContext(), "userID"))) {
                        Intent i = new Intent(activity.getBaseContext(), User_Profile.class);
                        i.putExtra("userID", comment.userID);
                        activity.startActivity(i);
                        activity.overridePendingTransition(R.anim.right_in,R.anim.right_out);
                        Blast_Home.stopStreamMusic();
                    }
                }
            });
        }
        holder.username.setText(comment.username);
        holder.comment.setText(comment.comment);
        holder.date.setText(comment.created);

        return convertView;
    }

    private Uri stringToUri(String path){
        Uri uri = Uri.parse(path);
        return uri;
    }
}
