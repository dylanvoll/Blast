package com.dylan.blast;

/**
 * Created by Dylan on 1/22/2016.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.util.ArrayList;

import extras.WebRequester;

public class Trailing extends Fragment {

    public static BlastAdapter adapter;
    public static ListView listview = null;
    public static ArrayList<Post> posts = null;
    static FragmentActivity activity;
    static boolean adapterSet = false;
    public static SwipeRefreshLayout mSwipeRefreshLayout;
    public static Context baseContext;
    public static LinearLayout empty = null;
    static View footer = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        if (posts != null) {
            refreshPosts(getActivity().getBaseContext());
        }

        activity = this.getActivity();

        baseContext = this.getActivity().getBaseContext();

        View view = inflater.inflate(R.layout.trailing, container, false);

        listview = (ListView) view.findViewById(R.id.trailing_list_view);

        listview.setScrollingCacheEnabled(false);

        listview.setNestedScrollingEnabled(true);

        empty = (LinearLayout)view.findViewById(R.id.noPosts);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.trailing_swipe_refresh);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.bg_post_row, R.color.bg_post_row, R.color.bg_post_row);

        footer = activity.getLayoutInflater().inflate(R.layout.footer_view,null,false);
        final Button load = (Button) footer.findViewById(R.id.loadPosts);
        final ProgressBar loading = (ProgressBar)footer.findViewById(R.id.postLoading);
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                load.setVisibility(View.INVISIBLE);
                new addMorePosts(activity.getBaseContext(),loading,load,adapter.getItem(posts.size()-1)).execute();
            }
        });


        new getPosts(getActivity().getBaseContext()).execute();


        return view;
    }


    public static void addAdapter() {
        if(posts.isEmpty()){
            listview.setVisibility(View.INVISIBLE);
            empty.setVisibility(View.VISIBLE);
            listview.removeFooterView(footer);
        }

        else {
            listview.removeFooterView(footer);
            adapter = new BlastAdapter(activity, posts);
            listview.setVisibility(View.VISIBLE);
            empty.setVisibility(View.INVISIBLE);
            if(posts.size()>5) {
                listview.addFooterView(footer);
            }
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    Post post = adapter.getItem(position);
                    Blast_Home.postForDetail = post;
                    if (BlastAdapter.timer != null) BlastAdapter.timer.cancel();
                    System.out.println("Clicked #" + position + " Post Song is:" + post.song + " ");
                    Intent i = new Intent(activity.getBaseContext(), PostActivity.class);
                    activity.startActivity(i);
                    activity.overridePendingTransition(R.anim.right_in,R.anim.right_out);
                    //Blast_Home.stopStreamMusic();
                }
            });

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                @Override
                public void onRefresh() {
                    refreshContent();
                }
            });
            adapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
            //adapterSet = true;
        }
    }

    public static void refreshContent(){

        Blast_Home.stopStreamMusic();
        if(Blast_Home.second5 != null){Blast_Home.second5.cancel();}
        if(BlastAdapter.timer != null){BlastAdapter.timer.cancel();}
        //if(Blast_Home.lastPost != null){Blast_Home.lastPost.isPlaying = false;}
        refreshPosts(baseContext);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public static void refreshPosts(Context c) {
        listview.smoothScrollToPosition(0);
        mSwipeRefreshLayout.setRefreshing(true);
        new getPosts(c).execute();
    }

    public static void setPositions() {
        for (int i = 0; i < posts.size(); i++) {
            posts.get(i).position = i;
        }
    }

    public static void setPosts(ArrayList<Post> posts) {
        Trailing.posts = posts;
        setPositions();
        if(posts.size()<4){
            //listview.setNestedScrollingEnabled(false);
        }
    }

    static class getPosts extends AsyncTask<Void, String, String> {

        Context c;
        ProgressDialog pd;

        public getPosts(Context c) {
            this.c = c;
        }

        protected void onPreExecute() {
            pd = new ProgressDialog(c);
            pd.setMessage("Loading...");
            //pd.show();
        }

        protected String doInBackground(Void... arg0) {
            if(!Blast_Home.followList.isEmpty()) {
                //System.out.println(Blast_Home.followList.toString());
                try {
                    WebRequester web = new WebRequester(c);
                    ArrayList<Post> posts = (web.getTrailingPosts(Blast_Home.followList));
                    setPosts(posts);
                    //variables.pastPosts = posts;

                } catch (Exception e) {
                    e.printStackTrace();
                    pd.setMessage("Connection Error...");
                }
            }
            else posts = new ArrayList<Post>();
            return "You are at PostExecute";
        }

        protected void onProgressUpdate(String... a) {
            pd.setMessage(a[0]);
        }

        protected void onPostExecute(String result) {
            if(!adapterSet) {
                addAdapter();
            }
            else {
                listview.removeFooterView(footer);
                adapter = new BlastAdapter(activity,posts);
                if(posts.size()>5) {
                    listview.addFooterView(footer);
                }
                listview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
                listview.setVisibility(View.VISIBLE);
                empty.setVisibility(View.INVISIBLE);
            }
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    static class addMorePosts extends AsyncTask<Void, String, String> {

        ProgressBar loading = null;
        Button load = null;
        ArrayList<Post> postsAdd = null;
        Post post = null;
        Context context = null;

        public addMorePosts(Context context,ProgressBar loading, Button load,Post post) {
            this.loading = loading;
            this.load = load;
            this.post = post;
            this.context = context;
        }

        protected void onPreExecute() {

        }

        protected String doInBackground(Void... arg0) {
            if(!Blast_Home.followList.isEmpty()) {
                try {
                    WebRequester web = new WebRequester(context);
                    postsAdd = (web.getTrailingPostsAdd(Blast_Home.followList,post.timestamp));
                    for (int i = postsAdd.size()-1; i>=0; i--) {
                        for (Post p2 : posts) {
                            //System.out.println(p2.postID + " and " + postsAdd.get(i));
                            if (p2.postID.equals(postsAdd.get(i).postID)) {
                                    postsAdd.remove(i);
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else postsAdd = new ArrayList<Post>();
            return "You are at PostExecute";
        }

        protected void onProgressUpdate(String... a) {

        }

        protected void onPostExecute(String result) {

            listview.removeFooterView(footer);

            int position = posts.size()-1;
            if(!postsAdd.isEmpty()) {

                adapter.addAll(postsAdd);
                setPositions();
                listview.addFooterView(footer);
            }

            else{
                Toast.makeText(context, "No More Blasts to Load", Toast.LENGTH_LONG).show();
                listview.removeFooterView(footer);
                listview.setSelection(adapter.getCount() - 1);
            }
            //adapter = new BlastAdapter(activity,posts);
            // listview.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            loading.setVisibility(View.INVISIBLE);
            load.setVisibility(View.VISIBLE);



        }
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
