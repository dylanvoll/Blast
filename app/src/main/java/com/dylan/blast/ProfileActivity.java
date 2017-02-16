package com.dylan.blast;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.KeyListener;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Pattern;

import extras.SharedPreferencesEditor;
import extras.WebRequester;
import extras.variables;


public class ProfileActivity extends AppCompatActivity {

    ListView userList = null;
    AlertDialog dialog = null;
    SimpleDraweeView userImage;
    String username = null;
    TextView changeProfile;
    TextView fullName;
    EditText bio;
    Button editBio;
    LinearLayout userInfo;
    FrameLayout loading;
    TextView empty;
    ArrayList<User> users = null;
    UsersRowAdapter adapter;
    final String TEMP_PHOTO_FILE = "temp.png";
    protected static final int REQ_CODE_PICK_IMAGE = 1;
    protected static final int REQ_CODE_CROP_IMAGE = 2;
    String bioCheck = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profile_activity);
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
        getSupportActionBar().setTitle(SharedPreferencesEditor.recieveKey(getBaseContext(),"username"));
        userInfo = (LinearLayout) findViewById(R.id.userInfo);
        loading = (FrameLayout) findViewById(R.id.loading);
        //userList = (ListView) findViewById(R.id.userProfileTrailers);
        //userList.setVisibility(View.INVISIBLE);
        userInfo.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);
        userImage = (SimpleDraweeView) findViewById(R.id.userProfileImage);
        changeProfile = (TextView) findViewById(R.id.changePicture);
        fullName = (TextView) findViewById(R.id.userProfileFullName);
        bio = (EditText) findViewById(R.id.userProfileBio);
        editBio = (Button) findViewById(R.id.editBio);
        bio.setBackground(null);
        loading = (FrameLayout) findViewById(R.id.loading);
        //empty = (TextView) findViewById(R.id.emptyTrailers);
        //userList.setVisibility(View.INVISIBLE);
        userInfo.setVisibility(View.INVISIBLE);

        new getTrailers().execute();
    }

    public void test(View view){
        Intent i = new Intent(getBaseContext(),TrailingView.class);
        i.putExtra("uid",variables.uid);
        i.putExtra("username",username);
        startActivity(i);
        //finish();
    }

    public void loadUI(ArrayList<String> info){
        if(info!=null) {
            userImage.setImageURI(stringToUri(String.format("%s/blast/users/%s/profile.png", variables.server, variables.currentUser.uid)));
            changeProfile.setText("Edit Picture");
            if (!info.get(0).isEmpty()) {
                getSupportActionBar().setTitle(StringUtils.capitalise(info.get(0)) + "'s Profile");
                username = info.get(0);
            }
            fullName.setText(info.get(1) + " " + info.get(2));
            bio.setText(decode64(info.get(3)));
            bio.setTag(bio.getKeyListener());
            bio.setKeyListener(null);
            editBio.setText("Edit Bio");
            editBio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editBio.getText().equals("Edit Bio")) {
                        bioCheck = bio.getText().toString();
                        bio.setKeyListener((KeyListener) bio.getTag());
                        editBio.setText("Submit");
                        bio.requestFocus();
                        bio.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.text_border,null));
                        bio.setText(bio.getText().toString().trim());
                    } else {
                        bio.setKeyListener(null);
                        editBio.setText("Edit Bio");
                        editBio.requestFocus();
                        bio.setBackgroundColor(getResources().getColor(R.color.transparent));
                        if (!bioCheck.equals(bio.getText().toString().trim())) {
                            bio.setText(bio.getText().toString().trim());
                            new updateBio(bio.getText().toString().trim()).execute();
                        }
                    }
                }
            });
            editBio.setBackgroundColor(getResources().getColor(R.color.bg_post_row));
            userInfo.setVisibility(View.VISIBLE);
            loading.setVisibility(View.INVISIBLE);
        }
        else{
            loading.setVisibility(View.INVISIBLE);
            empty.setVisibility(View.VISIBLE);
        }
    }


    public String decode64(String s){
        String newString = null;
        try {
            byte[] data = Base64.decode(s, Base64.DEFAULT);
            newString =  new String(data, "UTF-8");
        }
        catch(Exception e){e.printStackTrace();}
        return newString;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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

    public void home(MenuItem item){
        Intent i = new Intent(getBaseContext(),Blast_Home.class);
        startActivity(i);
        finish();
    }

    public void changePassword(MenuItem item){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View v = inflater.inflate(R.layout.change_password_dialog,null);
        final EditText cp = (EditText)v.findViewById(R.id.cp);
        final EditText np = (EditText)v.findViewById(R.id.np);
        final EditText vp = (EditText)v.findViewById(R.id.vp);
        builder.setView(v).setPositiveButton("Submit", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                if ((!cp.getText().toString().isEmpty()) && (!np.getText().toString().isEmpty()) && (!vp.getText().toString().isEmpty())) {
                    new changePassword(cp.getText().toString(), np.getText().toString(), vp.getText().toString()).execute();
                } else {
                    Toast.makeText(getBaseContext(), "Some fields were empty.", Toast.LENGTH_LONG).show();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.setTitle(Html.fromHtml("<font color='#000000'>Change Password</font>"));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private Uri stringToUri(String path){
        Uri uri = Uri.parse(path);
        return uri;
    }

    private class getTrailers extends AsyncTask<String, String, String> {



        ArrayList<String> info;

        public getTrailers() {


        }

        protected String doInBackground(String... urls) {


            WebRequester web = new WebRequester(getApplicationContext());
            users = web.getTrailers(variables.currentUser.uid);
            info = web.getUserInfo(variables.currentUser.uid);



            return "Post Execute";
        }

        protected void onProgressUpdate(String...a) {
        }

        protected void onPostExecute(String result) {

           loadUI(info);

        }
    }

    private class changePassword extends AsyncTask<String, String, String> {



        String cp,np,vp = null;
        boolean response = false;
        String result = "";

        public changePassword(String cp, String np, String vp) {
            ProgressDialog pd = new ProgressDialog(getBaseContext());
            this.cp = cp;
            this.np = np;
            this.vp = vp;
            final String regexp = "((?=.*[a-z])(?=.*[A-Z]).{6,20})";
            Pattern passwordPattern = Pattern.compile(regexp);
            if(!passwordPattern.matcher(np).matches()){
                //dialog.setMessage("Password must have one uppercase and one lowercase letter.");
                result = "Password must contain at least one uppercase and one lowercase letter";
            }
            if(np.length()<6 || vp.length()<6){
                result = "Password must be at least 6 characters";
            }
            if(np.equals(cp)){
                result = "Passwords are the same";
            }
            if(!np.equals(vp)){
                result = "New passwords dont match";
            }

        }

        protected String doInBackground(String... urls) {

            if(result.isEmpty()) {
                WebRequester web = new WebRequester(getApplicationContext());
                response = web.changePassword(variables.uid,cp,np);
            }



            return result;
        }

        protected void onProgressUpdate(String...a) {
        }

        protected void onPostExecute(String result) {
            dialog.dismiss();
            AlertDialog.Builder build = new AlertDialog.Builder(ProfileActivity.this);
            build.setTitle("Change Password");

                    build.setPositiveButton(
                            "Okay", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });


            if (!response){
                    if(!result.isEmpty()) {
                        build.setMessage(result);
                    }
                else{
                        build.setMessage("Something went wrong when changing your password. Please try again.");
                    }
                }

            else{
                build.setMessage("Success");
            }
            AlertDialog d = build.create();
            d.show();

        }
    }

    public void startPictureActivity(View view) {

        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.setType("image/*")
                .putExtra(MediaStore.EXTRA_OUTPUT, getTempUri())
                .putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        startActivityForResult(photoPickerIntent, REQ_CODE_PICK_IMAGE);
    }

   /* public void startPictureActivity(View view) {

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.setType("image/*")
                .putExtra("crop", "true")
                .putExtra("aspectX", 400)
                .putExtra("aspectY", 400)
                .putExtra("outputX", 400)
                .putExtra("outputY", 400)
                .putExtra("scale", true)
                .putExtra("scaleUpIfNeeded", true)
                .putExtra(MediaStore.EXTRA_OUTPUT, getTempUri())
                .putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        startActivityForResult(photoPickerIntent, REQ_CODE_PICK_IMAGE);
    }*/

    private Uri getTempUri() {
        return Uri.fromFile(getTempFile());
    }

    private File getTempFile() {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            File file = new File(Environment.getExternalStorageDirectory(),TEMP_PHOTO_FILE);
            try {
                file.createNewFile();
            } catch (IOException e) {}

            return file;
        } else {

            return null;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {

        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case REQ_CODE_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    if (imageReturnedIntent != null) {
                        try {
                            Intent intent = new Intent("com.android.camera.action.CROP");
                            intent.setDataAndType(imageReturnedIntent.getData(), "image/*");
                            intent.putExtra("crop", "true");
                            intent.putExtra("aspectX", 400);
                            intent.putExtra("aspectY", 400);
                            intent.putExtra("outputX", 400);
                            intent.putExtra("outputY", 400);
                            intent.putExtra("return-data", false);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
                            intent.putExtra("scale", true);
                            intent.putExtra("scaleUpIfNeeded", true);
                            intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
                            startActivityForResult(intent, REQ_CODE_CROP_IMAGE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case REQ_CODE_CROP_IMAGE:
                if (resultCode == RESULT_OK) {
                    if (imageReturnedIntent != null) {
                        try {
                            File tempFile = getTempFile();
                            InputStream is = new BufferedInputStream(new FileInputStream(tempFile));
                            byte[] bytes = IOUtils.toByteArray(is);
                            String byteString = Base64.encodeToString(bytes, Base64.DEFAULT);
                            new changePicture(byteString, tempFile).execute();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;

            default:


                    File tempFile = getTempFile();
                if(tempFile!=null) {
                    try {
                        InputStream is = new BufferedInputStream(new FileInputStream(tempFile));
                        byte[] bytes = IOUtils.toByteArray(is);
                        String byteString = Base64.encodeToString(bytes, Base64.DEFAULT);
                        new changePicture(byteString, tempFile).execute();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

        }
    }

    private class changePicture extends AsyncTask<String, String, String> {



        String byteString = null;
        File temp = null;

        public changePicture(String byteString, File temp) {

            this.byteString = byteString;
            this.temp = temp;

        }

        protected String doInBackground(String... urls) {


            WebRequester web = new WebRequester(getApplicationContext());
             web.updateProfilePicture(variables.currentUser.uid, byteString);

            return "Post Execute";
        }

        protected void onProgressUpdate(String...a) {
        }

        protected void onPostExecute(String result) {
            Fresco.getImagePipeline().evictFromCache(stringToUri(String.format("%s/blast/users/%s/profile.png",variables.server, variables.currentUser.uid)));
            if (temp.exists()) temp.delete();
            userImage.setImageURI(stringToUri(String.format("%s/blast/users/%s/profile.png", variables.server, variables.currentUser.uid)));


        }
    }

    private class updateBio extends AsyncTask<String, String, String> {



        String bioString = null;

        public updateBio(String bioString) {
            this.bioString = bioString;

        }

        protected String doInBackground(String... urls) {


            WebRequester web = new WebRequester(getApplicationContext());
            web.updateBio(variables.currentUser.uid, bioString);




            return "Post Execute";
        }

        protected void onProgressUpdate(String...a) {
        }

        protected void onPostExecute(String result) {



        }
    }
}
