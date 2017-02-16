package com.dylan.blast;

import extras.WebRequester;
import extras.SharedPreferencesEditor;
import extras.variables;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

public class Blast_Login extends AppCompatActivity {
	public static boolean allowLogin = false;
    public static String errorMessage = null;
    public AlertDialog initd = null;
	Context c;
    public static String userID = "";
    public static String authToken = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(SharedPreferencesEditor.recieveKey(this,"authToken") != null){variables.authToken = SharedPreferencesEditor.recieveKey(this,"authToken");}
        if((SharedPreferencesEditor.recieveKey(this,"login") != null) && (SharedPreferencesEditor.recieveKey(this,"login").equals("true"))){
           variables.currentUser = new User(SharedPreferencesEditor.recieveKey(getBaseContext(),"username"),SharedPreferencesEditor.recieveKey(getBaseContext(),"userID"),null,null);
            new getFollowers(SharedPreferencesEditor.recieveKey(getBaseContext(),"userID")).execute();

        }
        else {
            setContentView(R.layout.layout_blast_login);

            c = Blast_Login.this;
            variables.setPD(c);
        }
	}

    public void registerUser(View view){
        Intent intent = new Intent(c,Register_User.class);
        startActivity(intent);
        finish();

    }

    @Override
    public void onBackPressed() {

    }
	

	public void loginClicked(View view){

		EditText user = (EditText)findViewById(R.id.username);
		String username = user.getText().toString();
		EditText pass = (EditText)findViewById(R.id.password);
		String password = pass.getText().toString();
		Context context = Blast_Login.this;
        new verifyLogin(username,password,context).execute();	


    }

    public void forgotPass(View view){
        AlertDialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View v = inflater.inflate(R.layout.recover_pass_dialog,null);
        final EditText email = (EditText)v.findViewById(R.id.email);
        builder.setView(v).setPositiveButton("Send", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (!email.getText().toString().isEmpty()) {
                    //Toast.makeText(getApplicationContext(), search.getText().toString(), Toast.LENGTH_LONG).show();
                    new recoverPass(email.getText().toString(),initd).execute();
                } else {
                    //Toast.makeText(getApplicationContext(), "Nothing to search", Toast.LENGTH_LONG).show();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        initd = builder.create();
        initd.setTitle(Html.fromHtml("<font color='#000000'>Recover Password...</font>"));
        initd.setCanceledOnTouchOutside(false);
        initd.show();

    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.blast__login, menu);
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
	class verifyLogin extends AsyncTask<Void, String, String>
	{
		String username;
		String password;
		Context c;
		ProgressDialog pd;
		Intent i;
		boolean login = false;
		public verifyLogin(String user,String pass,Context context){
			 username = user;
			 password = pass;
			 c = context;
            pd = new ProgressDialog(Blast_Login.this);
            pd.setMessage("connecting");
            pd.show();

            if(username.isEmpty() || password.isEmpty()){
                return;
            }
			 
		}
	    protected void onPreExecute (){

	    }

	    protected String doInBackground(Void...arg0) {
	    	try{
			WebRequester web = new WebRequester(Blast_Login.this);	
			
			int code = web.login(username, password);
			switch (code){
			default: login = false;
					publishProgress("Cant connect to server!");
					
					break;
        	case 0: login = false;
        			publishProgress("Password is incorrect");
        			
        			break;
        	case 1: login = true;
        			publishProgress("Success! Please Wait...");

        			break;
        	case 2: login = false;
        			publishProgress("Username does not exist");
        			
        			break;
            case 5: login = false;
                    publishProgress("Login Correct but couldn't authenticate");

                    break;
        	}
	    	}
	    	catch(Exception e){
	    		publishProgress("Couldn't connect to server");
	    	
	    		e.printStackTrace();
	    	}
	        return "You are at PostExecute";
	    }

	    protected void onProgressUpdate(String...a){
	        pd.setMessage(a[0]);
	    }

	    protected void onPostExecute(String result) {
            CountDownTimer timer = new CountDownTimer(2000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    finishLogin(login,username,pd);
                }
            };
            timer.start();

	    }
	}

    public void finishLogin(boolean login, String username, ProgressDialog pd){
        if(login == true) {
            new getFollowers(userID).execute();
            variables.currentUser = new User(username,userID,null,null);
            SharedPreferencesEditor.updateKey(c,"login", "true");
            SharedPreferencesEditor.updateKey(c,"username",username);
            SharedPreferencesEditor.updateKey(c,"userID",this.userID);
            SharedPreferencesEditor.updateKey(c,"authToken",this.authToken);
            SharedPreferencesEditor.updateKey(c,"latitude",0.0);
            SharedPreferencesEditor.updateKey(c,"longitude",0.0);
            variables.uid = this.userID;
            variables.authToken = this.authToken;
            Intent intent = new Intent(c,Blast_Home.class);
            startActivity(intent);
            pd.dismiss();
            finish();
        }
        else{
            Intent intent = new Intent(c,Blast_Login.class);
            startActivity(intent);
            finish();
        }
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

            return "You are at PostExecute";
        }

        protected void onProgressUpdate(String...a){

        }

        protected void onPostExecute(String result) {
            Intent intent = new Intent(c,Blast_Home.class);
            startActivity(intent);
            finish();


        }
    }

    private class recoverPass extends AsyncTask<String, String, String> {

        String email = null;
        boolean response = false;
        AlertDialog initd = null;

        public recoverPass(String email, AlertDialog d) {

            this.email = email;
            this.initd = d;

        }

        protected String doInBackground(String... urls) {


            WebRequester web = new WebRequester(getApplicationContext());
            response = web.recoverPass(email);



            return "Post Execute";
        }

        protected void onProgressUpdate(String...a) {
        }

        protected void onPostExecute(String result) {

        if(response){
            initd.dismiss();
        AlertDialog dialog = new AlertDialog.Builder(
                Blast_Login.this
        ).setTitle("Recover Password")
         .setMessage("Recovery E-mail successfully sent. Please check your inbox and spam folders for the request.")
         .setPositiveButton(
                "Okay",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
         }).show();


        }
            else{
            AlertDialog dialog = new AlertDialog.Builder(
                    Blast_Login.this
            ).setTitle("Recover Password")
                    .setMessage(errorMessage)
                    .setPositiveButton(
                            "Okay",new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                    .show();
        }

        }
    }

}
