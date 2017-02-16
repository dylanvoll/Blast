package com.dylan.blast;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

/**
 * Created by Dylan on 1/7/2017.
 */

public class TrailersFragment extends Fragment {

    public static ListView listview = null;
    public static ArrayList<Post> posts = null;
    static FragmentActivity activity;
    static boolean adapterSet = false;
    public static SwipeRefreshLayout mSwipeRefreshLayout;
    public static Context baseContext;
    public static LinearLayout empty = null;
    UsersRowAdapter trailerAdapter;
    ListPair pair = null;

    public Fragment setPair(ListPair pair){
        this.pair = pair;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        activity = this.getActivity();

        baseContext = this.getActivity().getBaseContext();

        View view = inflater.inflate(R.layout.trailers_fragment, container, false);

        listview = (ListView) view.findViewById(R.id.trailersList);

        listview.setScrollingCacheEnabled(false);

        listview.setNestedScrollingEnabled(true);

        //empty = (LinearLayout)view.findViewById(R.id.noPosts);

        //mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.trailing_swipe_refresh);

        //mSwipeRefreshLayout.setColorSchemeResources(R.color.bg_post_row, R.color.bg_post_row, R.color.bg_post_row);

        //final Button load = (Button) footer.findViewById(R.id.loadPosts);
        //final ProgressBar loading = (ProgressBar)footer.findViewById(R.id.postLoading);
        trailerAdapter = new UsersRowAdapter(activity,pair.trailers,R.layout.users_row_layout);
        listview.setAdapter(trailerAdapter);



        new Trailing.getPosts(getActivity().getBaseContext()).execute();


        return view;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
