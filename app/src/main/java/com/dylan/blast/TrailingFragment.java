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

public class TrailingFragment extends Fragment {

    public static ListView listview = null;
    public static ArrayList<Post> posts = null;
    static FragmentActivity activity;
    static boolean adapterSet = false;
    public static SwipeRefreshLayout mSwipeRefreshLayout;
    public static Context baseContext;
    public static LinearLayout empty = null;
    UsersRowAdapter trailingAdapter;
    ListPair pair = null;

    public Fragment setPair(ListPair pair){
        this.pair = pair;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        activity = this.getActivity();

        baseContext = this.getActivity().getBaseContext();

        View view = inflater.inflate(R.layout.trailing_fragment, container, false);

        listview = (ListView) view.findViewById(R.id.trailingList);

        listview.setScrollingCacheEnabled(false);

        listview.setNestedScrollingEnabled(true);

        empty = (LinearLayout)view.findViewById(R.id.noPosts);

        //mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.trailing_swipe_refresh);

        //mSwipeRefreshLayout.setColorSchemeResources(R.color.bg_post_row, R.color.bg_post_row, R.color.bg_post_row);


        trailingAdapter = new UsersRowAdapter(activity,pair.trailing,R.layout.users_row_layout);
        listview.setAdapter(trailingAdapter);


        return view;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
