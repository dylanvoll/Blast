package com.dylan.blast;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;

import extras.CustomViewPager;
import extras.WebRequester;
import tabswipe.adapter.TrailPagerAdapter;


public class TrailingView extends AppCompatActivity {

    TextView trailersButton;
    TextView trailingButton;
    ListView trailersList;
    ListView trailingList;
    ProgressBar loading;
    LinearLayout mainUI;
    TextView noUsers;
    String uid = null;
    String username = null;
    boolean trailerEmpty = false;
    boolean trailingEmpty = false;
    ListPair pair = new ListPair();
    CustomViewPager pager = null;
    View traiingLine;
    View trailersLine;

    public void back(MenuItem item){
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_trailing_view);
        uid = getIntent().getStringExtra("uid");
        username = getIntent().getStringExtra("username");
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(6);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setTitle("Trailers");
         loading = (ProgressBar) findViewById(R.id.loading);
         mainUI = (LinearLayout) findViewById(R.id.main_ui);
        noUsers = (TextView) findViewById(R.id.noUsers);
        noUsers.setVisibility(View.VISIBLE);
         trailersButton = (TextView) findViewById(R.id.trailers);
        trailersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(0);
                trailersButton.setTextColor(getResources().getColor(R.color.bg_post_row));
                trailingButton.setTextColor(getResources().getColor(R.color.input_register));
                trailersLine.setBackground(new ColorDrawable(getResources().getColor(R.color.bg_main)));
                traiingLine.setBackground(new ColorDrawable(getResources().getColor(R.color.white)));
                getSupportActionBar().setTitle("Trailers");
                if (trailerEmpty){
                    noUsers.setText(String.format("%s doesn't have any Trailers", StringUtils.capitalise(username)));
                    noUsers.setVisibility(View.VISIBLE);
                    pager.setVisibility(View.INVISIBLE);
                    pager.setEnabled(false);
                }
                else{
                    noUsers.setVisibility(View.INVISIBLE);
                    pager.setVisibility(View.VISIBLE);
                    pager.setEnabled(true);
                }
            }
        });
        trailersLine = (View) findViewById(R.id.trailersLine);
        trailingButton = (TextView) findViewById(R.id.trailing);
        trailingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(1);
                trailingButton.setTextColor(getResources().getColor(R.color.bg_post_row));
                trailersButton.setTextColor(getResources().getColor(R.color.input_register));
                traiingLine.setBackground(new ColorDrawable(getResources().getColor(R.color.bg_main)));
                trailersLine.setBackground(new ColorDrawable(getResources().getColor(R.color.white)));
                getSupportActionBar().setTitle("Trailing");
                if (trailingEmpty){
                    noUsers.setText(String.format("%s isn't Trailing anyone.", StringUtils.capitalise(username)));
                    noUsers.setVisibility(View.VISIBLE);
                    pager.setVisibility(View.INVISIBLE);
                    pager.setEnabled(false);
                }
                else{
                    noUsers.setVisibility(View.INVISIBLE);
                    pager.setVisibility(View.VISIBLE);
                    pager.setEnabled(true);
                }
            }
        });
        traiingLine = (View) findViewById(R.id.trailingLine);


         new getLists(uid).execute();
    }

    public void loadUI(){
        pager = (CustomViewPager) findViewById(R.id.pager);
        pager.setOffscreenPageLimit(1);
        pager.setAdapter(new TrailPagerAdapter(getSupportFragmentManager(),pair));
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch(position){
                    case 0:
                        trailersLine.setBackground(new ColorDrawable(getResources().getColor(R.color.bg_main)));
                        traiingLine.setBackground(new ColorDrawable(getResources().getColor(R.color.white)));
                        break;
                    case 1:
                        traiingLine.setBackground(new ColorDrawable(getResources().getColor(R.color.bg_main)));
                        trailersLine.setBackground(new ColorDrawable(getResources().getColor(R.color.white)));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        noUsers.setVisibility(View.VISIBLE);
        trailersButton.setText(pair.trailers.size() + " Trailers");
        trailingButton.setText(pair.trailing.size() + " Trailing");
        if(pair.trailers.isEmpty()){
            trailerEmpty = true;
            noUsers.setVisibility(View.VISIBLE);
            pager.setVisibility(View.INVISIBLE);
            pager.setEnabled(false);
        }
        else{
            noUsers.setVisibility(View.INVISIBLE);
            pager.setVisibility(View.VISIBLE);
            pager.setEnabled(true);
        }
        if(pair.trailing.isEmpty()){
            trailingEmpty = true;
        }

        loading.setVisibility(View.INVISIBLE);
        mainUI.setVisibility(View.VISIBLE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trailing_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class getLists extends AsyncTask<String, String, String> {


        String userID;


        public getLists(String userID) {

            this.userID = userID;

        }

        protected String doInBackground(String... urls) {


            WebRequester web = new WebRequester(getBaseContext());
            pair = web.getTrailLists(pair, uid);



            return "Post Execute";
        }

        protected void onProgressUpdate(String...a){

        }

        protected void onPostExecute(String result) {

            loadUI();

        }
    }
}
