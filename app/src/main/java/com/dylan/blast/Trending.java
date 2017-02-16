package com.dylan.blast;

import java.util.ArrayList;

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
import android.widget.LinearLayout;
import android.widget.ListView;

import extras.WebRequester;

public class Trending extends Fragment {
	
	public static BlastAdapterTrending adapter;
    public static ArrayList<Post> posts = null;
    public static boolean adapterSet = false;
    static FragmentActivity activity;
    public static ListView listview = null;
    public static SwipeRefreshLayout mSwipeRefreshLayout;
    public static Context baseContext;
    public static LinearLayout empty = null;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        activity = this.getActivity();

        baseContext = this.getActivity().getBaseContext();

        if(posts != null){refreshPosts(getActivity().getBaseContext());}

        View view = inflater.inflate(R.layout.trending, container, false);

        listview = (ListView) view.findViewById(R.id.trending_list_view);

        listview.setScrollingCacheEnabled(false);

        listview.setNestedScrollingEnabled(true);

        empty = (LinearLayout)view.findViewById(R.id.noPosts);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.trending_swipe_refresh);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.bg_post_row, R.color.bg_post_row, R.color.bg_post_row);

        new getPosts(getActivity().getBaseContext()).execute();

        return view;
		                     
        
    }

    public static void addAdapter() {

        if(posts.isEmpty()){
            listview.setVisibility(View.INVISIBLE);
            empty.setVisibility(View.VISIBLE);
        }

        else {
            adapter = new BlastAdapterTrending(activity, posts);

            listview.setAdapter(adapter);

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    Post post = adapter.getItem(position);
                    Blast_Home.postForDetail = post;
                    if (adapter.lastPost != null) {
                        //adapter.lastPost.isPlaying = false;
                        //adapter.lastPost.isLoading = false;
                    }
                    System.out.println("Clicked #" + position + " Post Song is:" + post.song + " ");
                    Intent i = new Intent(activity.getBaseContext(), PostActivity.class);
                    i.putExtra("trend_user",post.username);
                    activity.startActivity(i);
                    activity.overridePendingTransition(R.anim.right_in,R.anim.right_out);
                    //Blast_Home.stopStreamMusic();
                    adapter.notifyDataSetChanged();
                }
            });

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                @Override
                public void onRefresh() {
                    refreshContent();
                }
            });

            adapter.notifyDataSetChanged();
        }
    }

    public static void refreshContent(){

        Blast_Home.stopStreamMusic();
        if(Blast_Home.second5 != null){Blast_Home.second5.cancel();}
        if(BlastAdapterTrending.timer != null){BlastAdapterTrending.timer.cancel();}
        //if(Blast_Home.lastPost != null){Blast_Home.lastPost.isPlaying = false;}
        refreshPosts(baseContext);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public static void refreshPosts(Context c){
        listview.smoothScrollToPosition(0);
        mSwipeRefreshLayout.setRefreshing(true);
        new getPosts(c).execute();

    }

    public static void setPosts(ArrayList<Post> posts){
        Trending.posts = posts;
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
            try {

            WebRequester web = new WebRequester(c);
            ArrayList<Post> posts = web.getTrendingPosts();
            Trending.setPosts(posts);

            //variables.trendingPosts = posts;

            } catch (Exception e) {
                e.printStackTrace();
                pd.setMessage("Connection Error...");
            }
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
                adapter.clear();
                for (Post p : posts) {
                    adapter.add(p);
                }
                adapter.notifyDataSetChanged();

            }

            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
