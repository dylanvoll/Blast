package com.dylan.blast;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;

import extras.NetworkCheck;
import extras.SharedPreferencesEditor;
import extras.WebRequester;
import extras.variables;


public class Launcher_Activity extends Activity {

    public static boolean bool = false;
    ImageView logo;
    ProgressBar loading;
    TextView message;
    CountDownTimer timer;
    String [] appPermissions;
    int grantedCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(getBaseContext());
        super.onCreate(savedInstanceState);
        appPermissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
                };
        ActivityCompat.requestPermissions(this,appPermissions,
                0);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_launcher_activity);
        variables.server = getResources().getString(R.string.server);
        logo = (ImageView) findViewById(R.id.launchLogo);
        loading = (ProgressBar) findViewById(R.id.launchProgress);
        message = (TextView) findViewById(R.id.noConnection);
        final Launcher_Activity l = this;
        timer = new CountDownTimer(2500,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                new NetworkCheck(getBaseContext(),bool,l).execute();
            }
        };

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0) {
            grantedCount = 0;
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(appPermissions[i])) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        grantedCount++;
                    } else {
                        final String perm = appPermissions[i];
                        AlertDialog.Builder warn = new AlertDialog.Builder(this,R.style.MyDialogTheme);
                        warn.setTitle("Whoops!!");
                        warn.setMessage("Blast! needs access to your music and location to provide the best music sharing experience possible. Please reconsider this choice :)");
                        warn.setPositiveButton("Okay", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(Launcher_Activity.this,new String[]{perm}, 0);
                            }
                        });
                        warn.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(Launcher_Activity.this,new String[]{perm}, 0);
                            }
                        });
                        warn.setCancelable(false);
                        warn.show();

                    }
                }
            }
        }

        else{
            AlertDialog.Builder warn = new AlertDialog.Builder(this,R.style.MyDialogTheme);
            warn.setTitle("Whoops!!");
            warn.setMessage("Blast! needs access to your music and location to provide the best music sharing experience possible. Please enable these permissions in Settings under Application/Application Manager.");
            warn.setPositiveButton("Okay", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            warn.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            warn.setCancelable(false);
            warn.show();
        }

        if(grantedCount == 3)timer.start();

    }

    public void startLogin(){
        if(SharedPreferencesEditor.recieveKey(this,"authToken") != null){variables.authToken = SharedPreferencesEditor.recieveKey(this,"authToken");}
        if((SharedPreferencesEditor.recieveKey(this, "login") != null) && (SharedPreferencesEditor.recieveKey(this,"login").equals("true"))){
            variables.currentUser = new User(SharedPreferencesEditor.recieveKey(getBaseContext(),"username"),SharedPreferencesEditor.recieveKey(getBaseContext(),"userID"),null,null);
            new getFollowers(SharedPreferencesEditor.recieveKey(getBaseContext(),"userID")).execute();
        }
        else {
            Intent i = new Intent(getBaseContext(), Blast_Login.class);
            startActivity(i);
            finish();
        }
    }

    public void noConnection(){
        loading.setVisibility(View.INVISIBLE);
        message.setText("No network connection or server is down. Please try again later.");
        message.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_launcher, menu);
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

    class getFollowers extends AsyncTask<Void, String, String>
    {
        String userID;
        public getFollowers(String userID){
            this.userID = userID;

        }
        protected void onPreExecute (){

        }

        protected String doInBackground(Void...arg0) {
            WebRequester web = new WebRequester(getApplicationContext());
            web.getFollowers(userID);
            for(String i: variables.followList){
                Fresco.getImagePipeline().evictFromCache(variables.stringToUri(String.format("%s/blast/users/%s/profile.png",variables.server,i)));
            }
            return "You are at PostExecute";
        }

        protected void onProgressUpdate(String...a){

        }

        protected void onPostExecute(String result) {
            variables.uid = userID;
            Intent intent = new Intent(getBaseContext(),Blast_Home.class);
            startActivity(intent);
            finish();

        }
    }
}
