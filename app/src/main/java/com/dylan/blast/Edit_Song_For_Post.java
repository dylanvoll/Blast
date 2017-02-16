package com.dylan.blast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import extras.RangeSeekBar;
import extras.SharedPreferencesEditor;
import extras.WebRequester;
import extras.locationUpdater;
import extras.variables;
import soundfile.CheapSoundFile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;


import org.apache.commons.io.IOUtils;

public class Edit_Song_For_Post extends AppCompatActivity {
	Uri uri;
	String length;
    MediaPlayer player = new MediaPlayer();
	RangeSeekBar<Integer> rangeSeekBar;
	boolean playerReady = false;
	String songTitle = null;
	String songArtist = null;
    String songAlbum = null;
    String albumPath = null;
    TextView numSecView;
    int albumID;
    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
    CountDownTimer timer = null;
	RelativeLayout rellayout;
    String postMessage = null;
	int seconds;
    int kbps;
    String mime;
    CheapSoundFile csf = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_edit__song__for__post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(6);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(),Blast_Home.class);
                startActivity(i);
                finish();
            }
        });
        getSupportActionBar().setTitle("Edit Song");
        variables.setPD(Edit_Song_For_Post.this);
		String path = getIntent().getStringExtra("path to song");
		 uri = Uri.parse(path);
        try {
            final CheapSoundFile.ProgressListener listener =
                    new CheapSoundFile.ProgressListener() {
                        public boolean reportProgress(double frac) {
                            // Do nothing - we're not going to try to
                            // estimate when reloading a saved sound
                            // since it's usually fast, but hard to
                            // estimate anyway.
                            return true;  // Keep going
                        }
                    };
            csf = CheapSoundFile.create(uri.getPath(), listener);
            if(csf == null){
                AlertDialog.Builder warn = new AlertDialog.Builder(Edit_Song_For_Post.this);
                warn.setTitle("Sorry!");
                warn.setMessage("This file type is unfortunately not supported.");
                warn.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Edit_Song_For_Post.this,Blast_Home.class);
                        startActivity(intent);

                    }
                });
                warn.show();

            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
		long songLength = 0;
		MediaMetadataRetriever data = new MediaMetadataRetriever();
		data.setDataSource(this, uri);
		songTitle = data.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
		songArtist = data.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        songAlbum = data.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
		length = data.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        albumID = getIntent().getIntExtra("albumID",0);
        ContentResolver res = this.getContentResolver();
        Uri uri = ContentUris.withAppendedId(sArtworkUri, albumID);
            if(albumID == 0) albumPath = "";
            else{
                try {
                    String[] proj = {MediaStore.Images.Media.DATA};
                    Cursor cursor = Edit_Song_For_Post.this.getContentResolver().query(uri, proj, null, null, null);
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if(cursor.getCount() == 0) albumPath = "";
                    else {
                        cursor.moveToFirst();
                        albumPath = cursor.getString(column_index);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }

            }
            //System.out.println(albumPath);



        //kbps = Integer.parseInt(data.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE))/1000;
       mime = data.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
        //Toast.makeText(Edit_Song_For_Post.this,mime+ "",Toast.LENGTH_LONG).show();
		if (length != null) {
		    songLength = Long.parseLong(length);
		}
		seconds = (int) (songLength/1000);
		 EditText artistText = (EditText) findViewById(R.id.post_artist);
	     artistText.setText(songArtist);
	     EditText titleText = (EditText) findViewById(R.id.post_title);
	     titleText.setText(songTitle);
         EditText albumText = (EditText) findViewById(R.id.post_album);
         albumText.setText(songAlbum);
        rellayout = (RelativeLayout) findViewById(R.id.edit_song_layout);
        numSecView = (TextView) findViewById(R.id.numSecView);
		rangeSeekBar = new RangeSeekBar<Integer>(this);
        rangeSeekBar.setPlayerAndTimerAndText(player,timer,numSecView);
        // Set the range
        rangeSeekBar.setRangeValues(0, seconds);
        rangeSeekBar.setSelectedMinValue(0);
        rangeSeekBar.setSelectedMaxValue(seconds);
        Button listen = (Button)findViewById(R.id.edit_song_listen);
        rangeSeekBar.setButton(listen);
        rellayout.addView(rangeSeekBar);
       
        
        
	}
	public void resizeSeekBar(View view){
        Button listen = (Button)findViewById(R.id.edit_song_listen);
        if(timer!=null)timer.cancel();
        if(player!=null)player.reset();
        listen.setText("Listen to song");
		int currentMin = rangeSeekBar.getSelectedMinValue();
		int currentMax = rangeSeekBar.getSelectedMaxValue();
		if((currentMax-currentMin > 1) && (currentMax-currentMin <= currentMax)){
			rellayout.removeView(rangeSeekBar);
			rangeSeekBar.setRangeValues(currentMin, currentMax);
			rangeSeekBar.setSelectedMinValue(currentMin);
			rangeSeekBar.setSelectedMaxValue(currentMax);
			
			rellayout.addView(rangeSeekBar);
		}
		else{
			AlertDialog.Builder warn = new AlertDialog.Builder(Edit_Song_For_Post.this);
			warn.setTitle("Whoops!");
			warn.setMessage("You're already zoomed in as much as possible.");
			warn.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					
				}
			});
			warn.show();
		}
	}
	
	public void resetSeekBar(View view){
        Button listen = (Button)findViewById(R.id.edit_song_listen);
        if(timer!=null)timer.cancel();
        if(player!=null)player.reset();
        listen.setText("Listen to song");
		rellayout.removeView(rangeSeekBar);
		rangeSeekBar.setRangeValues(0, seconds);
		rangeSeekBar.setSelectedMinValue(0);
		rangeSeekBar.setSelectedMaxValue(seconds);
		rellayout.addView(rangeSeekBar);
	}
	
	@Override
	public void onBackPressed() {
        if(player!=null) {
            player.stop();
            player.release();
        }
		Intent intent = new Intent(Edit_Song_For_Post.this,Blast_Home.class);
		startActivity(intent);
        finish();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.edit__song__for__post, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public void playSongSelected(View view){
        Button listen = (Button)findViewById(R.id.edit_song_listen);
        if(listen.getText().equals("Stop")){

            if(timer!=null)timer.cancel();
            player.stop();
            player.reset();
            listen.setText("Listen to song");
        }
        else {

            int length;
            length = rangeSeekBar.getAbsoluteMaxValue() - rangeSeekBar.getSelectedMinValue();

            try {

                    final int startTime = rangeSeekBar.getSelectedMinValue();
                    player.setDataSource(this, uri);
                    player.prepareAsync();
                    player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(final MediaPlayer mp) {
                            mp.seekTo(startTime * 1000);
                            rangeSeekBar.setSelectedMaxValue(rangeSeekBar.getSelectedMinValue());
                            mp.start();

                        }
                    });

                    //System.out.println(startTime);
                    listen.setText("Stop");
                    timer = new CountDownTimer((length+2)*1000, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {

                            rangeSeekBar.setSelectedMaxValue(rangeSeekBar.getSelectedMaxValue() + 1);
                        }

                        @Override
                        public void onFinish() {
                         rangeSeekBar.setSelectedMaxValue(rangeSeekBar.getSelectedMaxValue() + 1);
                         Button listen = (Button)findViewById(R.id.edit_song_listen);
                         listen.setText("Listen to song");
                         if(player != null) {
                             rangeSeekBar.setSelectedMaxValue(rangeSeekBar.getAbsoluteMaxValue());
                             player.stop();
                             player.reset();
                         }
                        }
                    };
                    rangeSeekBar.setTimer(timer);
                    player.start();
                    timer.start();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
	public void postSongForUser(View view){
        if(timer!=null){
            timer.cancel();
        }
        int length;
        length = rangeSeekBar.getSelectedMaxValue() - rangeSeekBar.getSelectedMinValue();
        player.reset();
        if(length<=30) {
            EditText messageText = (EditText) findViewById(R.id.post_message);
            messageText.setFocusable(true);
            String message = messageText.getText().toString();
            EditText songText = (EditText) findViewById(R.id.post_title);
            songText.setFocusable(true);
            songTitle = songText.getText().toString();
            EditText artistText = (EditText) findViewById(R.id.post_artist);
            artistText.setFocusable(true);
            songArtist = artistText.getText().toString();
            EditText albumText = (EditText) findViewById(R.id.post_album);
            albumText.setFocusable(true);
            songAlbum = albumText.getText().toString();
            try {
                long startMili = rangeSeekBar.getSelectedMinValue() * 1000;
                long endMili = rangeSeekBar.getSelectedMaxValue() * 1000;
                new postSong(songTitle, songArtist, songAlbum, albumPath, message, startMili, endMili, kbps, Edit_Song_For_Post.this, seconds, csf).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            AlertDialog.Builder warn = new AlertDialog.Builder(Edit_Song_For_Post.this);
            warn.setTitle("Warning!");
            warn.setMessage("Your clip can only be up to 30 seconds long!");
            warn.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {


                }
            });
            warn.show();
        }
	}
    class postSong extends AsyncTask<Void, String, String>
    {
        ProgressDialog pd;
        Context c;
        int code = 0;
        int kbps = 0;
        int songLength;
        long startmili;
        long endmili;
        String songTitle = null;
        String songArtist = null;
        String songAlbum = null;
        String postMessage = null;
        String albumPath = null;
        CheapSoundFile csf;

        public postSong(String post_title, String post_artist,String post_album,String album_path,String post_messsage,long start,long end,int song_kbps,Context context,int length,CheapSoundFile cheap){
            startmili = start;
            endmili = end;
            songTitle = post_title;
            songArtist = post_artist;
            songAlbum = post_album;
            postMessage = post_messsage;
            kbps = song_kbps;
            c = context;
            songLength = length;
            csf = cheap;
            albumPath = album_path;

        }
        protected void onPreExecute (){
            pd = new ProgressDialog(Edit_Song_For_Post.this);
            pd.setCanceledOnTouchOutside(false);
            pd.setMessage("Connecting...");
            pd.show();
        }

        protected String doInBackground(Void...arg0) {
            try{
                String userID = SharedPreferencesEditor.recieveKey(c, "userID");
                float latitude = SharedPreferencesEditor.recieveFloat(c, "latitude");
                float longitude = SharedPreferencesEditor.recieveFloat(c, "longitude");
                WebRequester web = new WebRequester(Edit_Song_For_Post.this);
                String path = c.getExternalCacheDir().getPath()+"/tempSong.mp3";
                String localPath = String.format("/var/blast/users/%s/temp/tempSong.mp3",userID);
                //System.out.println(path);
                //System.out.println(uri.getPath());
                final int startFrame = secondsToFrames(miliToSeconds(startmili), csf.getSampleRate(), csf.getSamplesPerFrame());
                final int endFrame = secondsToFrames(miliToSeconds(endmili),csf.getSampleRate(),csf.getSamplesPerFrame());
                final int duration = (int)(miliToSeconds(endmili) - miliToSeconds(startmili) + 0.5);
                final File outFile = new File(path);
                csf.WriteFile(outFile,startFrame,endFrame-startFrame);
                InputStream is = new BufferedInputStream(new FileInputStream(outFile));
                byte [] trimmed = IOUtils.toByteArray(is);
                String byteString= Base64.encodeToString(trimmed, Base64.DEFAULT);
                String albumByteString = "";
                if((albumPath != null) && (albumPath != "")) {
                    //final File albumFile = new File(albumPath);
                    Bitmap bmp = BitmapFactory.decodeFile(albumPath);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    Bitmap scaled = Bitmap.createScaledBitmap(bmp,350,350,false);
                    scaled.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                    byte[] albumPic = bos.toByteArray();
                    scaled.recycle();
                    bmp.recycle();
                    albumByteString = Base64.encodeToString(albumPic, Base64.DEFAULT);
                }
                is.close();
                //System.out.println(albumPath);
                publishProgress("Posting now...");
                web.postSong(userID, songTitle, songArtist, songAlbum, albumByteString, postMessage, duration, byteString, latitude, longitude,false);
//                new Thread() {
//                    public void run() {
//                        try {
//                            sleep(2000);
//
//
//                        } catch(Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }.start();
                outFile.delete();

            }
            catch(Exception e){
                e.printStackTrace();
                //pd.setMessage("Connection Error...");
            }

            pd.dismiss();
            return "You are at PostExecute";

        }

        protected void onProgressUpdate(String...a){
            pd.setMessage(a[0]);
        }

        protected void onPostExecute(String result) {

                locationUpdater.quickLoc();
                AlertDialog.Builder warn = new AlertDialog.Builder(Edit_Song_For_Post.this);
                warn.setTitle("Posted Successfully!");
                warn.setMessage("Taking you back home now.");
                warn.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Edit_Song_For_Post.this, Blast_Home.class);
                        startActivity(intent);
                        finish();
                    }
                });
                warn.show();


        }

    }
    private static int secondsToFrames(double seconds,int mSampleRate, int mSamplesPerFrame) {
        return (int)(1.0 * seconds * mSampleRate / mSamplesPerFrame + 0.5);
    }
    private static int miliToSeconds(long mili){
        return (int) mili/1000;
    }

}

