package com.dylan.blast;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import extras.SharedPreferencesEditor;
import extras.WebRequester;
import extras.variables;

/**
 * Created by Dylan on 2/24/2016.
 */
public class UsersRowAdapter extends ArrayAdapter<User> {

    private LayoutInflater inflater;
    public ArrayList<User> users = null;
    private Activity activity = null;
    private int layoutID;

    public UsersRowAdapter(Activity activity, ArrayList<User> users, int layoutID) {

        super(activity, R.layout.blast_row_layout, users);
        inflater = activity.getWindow().getLayoutInflater();
        this.users = users;
        this.activity = activity;
        this.layoutID = layoutID;
    }


    static class ViewHolder{
        TextView userName;
        TextView fullName;
        SimpleDraweeView userImage;
        Button trail;
        TextView multiplier;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        final ViewHolder holder;
        final User user = getItem(position);
        switch (layoutID) {

            case R.layout.users_row_layout:

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.users_row_layout, parent, false);
                holder = new ViewHolder();
                holder.fullName = (TextView) convertView.findViewById(R.id.full_name);
                holder.userImage = (SimpleDraweeView) convertView.findViewById(R.id.user_image);
                holder.userName = (TextView) convertView.findViewById(R.id.username);
                holder.trail = (Button) convertView.findViewById(R.id.trail_button);
                convertView.setTag(holder);
                getItem(position).view = holder;
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (user.uid.equals(variables.uid)) {
                holder.trail.setVisibility(View.INVISIBLE);
            } else {
                holder.trail.setVisibility(View.VISIBLE);
            }

            holder.fullName.setText(user.firstName + " " + user.lastName);
            holder.userImage.setImageURI(stringToUri(String.format("%s/blast/users/%s/profile.png", variables.server, user.uid)));
            if (!user.uid.equals(variables.uid)) {
                holder.userImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(activity.getBaseContext(), User_Profile.class);
                        i.putExtra("userID", user.uid);
                        activity.startActivity(i);
                        activity.overridePendingTransition(R.anim.right_in,R.anim.right_out);
                    }
                });
            }
            holder.userName.setText(user.username);

            holder.trail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.trail.getText().toString().equals("Trail")) {
                        new trail(SharedPreferencesEditor.recieveKey(getContext(), "userID"), user.uid).execute();
                        holder.trail.setText("Un-trail");
                        Blast_Home.followList.add(user.uid);
                    } else {
                        new untrail(SharedPreferencesEditor.recieveKey(getContext(), "userID"), user.uid).execute();
                        holder.trail.setText("Trail");
                        Blast_Home.followList.remove(user.uid);
                    }
                }
            });

            if (Blast_Home.followList.contains(user.uid)) {

                holder.trail.setText("Un-trail");
            } else {
                holder.trail.setText("Trail");
            }

            notifyDataSetChanged();

            break;

            case R.layout.users_row_layout_dialog:



                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.users_row_layout_dialog, parent, false);
                    holder = new ViewHolder();
                    holder.fullName = (TextView) convertView.findViewById(R.id.full_name);
                    holder.userImage = (SimpleDraweeView) convertView.findViewById(R.id.user_image);
                    holder.userName = (TextView) convertView.findViewById(R.id.username);
                    holder.trail = (Button) convertView.findViewById(R.id.trail_button);
                    holder.multiplier = (TextView)convertView.findViewById(R.id.multiplier);
                    convertView.setTag(holder);
                    getItem(position).view = holder;
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                if(user.multiplier <= 1){
                    parent.removeViewInLayout(holder.multiplier);
                }
                else{
                    holder.multiplier.setText("x"+user.multiplier);
                }

                if (user.uid.equals(variables.uid)) {
                    holder.trail.setVisibility(View.INVISIBLE);
                } else {
                    holder.trail.setVisibility(View.VISIBLE);
                }

                holder.fullName.setText(user.firstName + " " + user.lastName);
                holder.userImage.setImageURI(stringToUri(String.format("%s/blast/users/%s/profile.png", variables.server, user.uid)));
                if (!user.uid.equals(variables.uid)) {
                    holder.userImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(activity.getBaseContext(), User_Profile.class);
                            i.putExtra("userID", user.uid);
                            activity.startActivity(i);
                            activity.overridePendingTransition(R.anim.right_in,R.anim.right_out);
                        }
                    });
                }
                holder.userName.setText(user.username);

                holder.trail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.trail.getText().toString().equals("Trail")) {
                            new trail(SharedPreferencesEditor.recieveKey(getContext(), "userID"), user.uid).execute();
                            holder.trail.setText("Un-trail");
                            Blast_Home.followList.add(user.uid);
                        } else {
                            new untrail(SharedPreferencesEditor.recieveKey(getContext(), "userID"), user.uid).execute();
                            holder.trail.setText("Trail");
                            Blast_Home.followList.remove(user.uid);
                        }
                    }
                });

                if (Blast_Home.followList.contains(user.uid)) {

                    holder.trail.setText("Un-trail");
                } else {
                    holder.trail.setText("Trail");
                }

                notifyDataSetChanged();


        }
        return convertView;
    }
    private Uri stringToUri(String path){
        Uri uri = Uri.parse(path);
        return uri;
    }

    private class trail extends AsyncTask<String, String, String> {


        String userID;
        String followID;



        public trail(String userID,String followID) {

            this.userID = userID;
            this.followID = followID;

        }

        protected String doInBackground(String... urls) {


            WebRequester web = new WebRequester(getContext());
            web.followUser(userID,followID);


            return "Post Execute";
        }

        protected void onProgressUpdate(String...a){

        }

        protected void onPostExecute(String result) {




        }
    }

    private class untrail extends AsyncTask<String, String, String> {


        String userID;
        String followID;



        public untrail(String userID,String followID) {

            this.userID = userID;
            this.followID = followID;

        }

        protected String doInBackground(String... urls) {


            WebRequester web = new WebRequester(getContext());
            web.unfollowUser(userID,followID);


            return "Post Execute";
        }

        protected void onProgressUpdate(String...a){

        }

        protected void onPostExecute(String result) {




        }
    }
}
