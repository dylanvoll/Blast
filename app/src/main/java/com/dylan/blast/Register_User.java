package com.dylan.blast;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import java.util.regex.Pattern;

import extras.WebRequester;
import extras.location;


public class Register_User extends Activity {
    private static final Pattern rfc2822 = Pattern.compile(
            "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"
    );
    public static final String regexp = "((?=.*[a-z])(?=.*[A-Z]).{6,20})";
    //(?=.*\d)
    Pattern passwordPattern = Pattern.compile(regexp);

	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_register_user);
        location listener = new location(Register_User.this);

	}

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Register_User.this, Blast_Login.class);
        startActivity(intent);
        finish();
    }

    public void toLogin(View view){
        Intent intent = new Intent(Register_User.this, Blast_Login.class);
        startActivity(intent);
        finish();
    }

    public void registerUser(View view){
        String firstName,lastName,email,verifyEmail,username,password,verifyPassword = "";
        String dialogMessage = "";

        EditText ETfirstName = (EditText) findViewById(R.id.register_firstname);
        EditText ETlastName = (EditText) findViewById(R.id.register_lastname);
        EditText ETemail = (EditText) findViewById(R.id.register_email);
        EditText ETVerifyemail = (EditText) findViewById(R.id.register_email_verify);
        EditText ETusername = (EditText) findViewById(R.id.register_username);
        EditText ETpassword = (EditText) findViewById(R.id.register_password);
        EditText ETverifyPassword = (EditText) findViewById(R.id.verifyPassword);
        firstName = ETfirstName.getText().toString();
        lastName = ETlastName.getText().toString();
        email = ETemail.getText().toString();
        verifyEmail = ETVerifyemail.getText().toString();
        username = ETusername.getText().toString();
        password = ETpassword.getText().toString();
        verifyPassword = ETverifyPassword.getText().toString();

        if(firstName.isEmpty()){
            ETfirstName.setError("This field cannot be empty.");
            return;
        }
        if(lastName.isEmpty()){
            ETlastName.setError("This field cannot be empty.");
            return;
        }
        if(email.isEmpty()){
            ETemail.setError("This field cannot be empty.");
            return;
        }
        if(username.isEmpty()){
            ETusername.setError("This field cannot be empty.");
            return;
        }
        if(password.isEmpty()){
            ETpassword.setError("This field cannot be empty.");
            return;
        }
        if(!rfc2822.matcher(email).matches()) {
           dialogMessage = "Invalid email address";
        }

        if(!passwordPattern.matcher(password).matches()){
            dialogMessage = "Please make sure your password contains at least one lowercase letter and one uppercase letter.";
        }
        if(!password.equals(verifyPassword)){
            dialogMessage = "Your passwords do not match, please make sure they are the same.";
        }
        if(!email.equals(verifyEmail)){
            dialogMessage = "Your emails do not match, please make sure they are the same.";
        }

        System.out.println(dialogMessage);
        if(!dialogMessage.isEmpty()) {
            AlertDialog.Builder warn = new AlertDialog.Builder(Register_User.this);
            warn.setTitle("Whoops!");
            warn.setMessage(dialogMessage);
            warn.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {


                }
            });
            warn.show();

        }



        else {
            new verifyLogin(firstName, lastName, email, username, password, Register_User.this).execute();
        }

        }

    class verifyLogin extends AsyncTask<Void, String, String>
    {
        SharedPreferences prefs = getSharedPreferences("blast", MODE_PRIVATE);
        SharedPreferences.Editor edit;
        String firstName,lastName,email,username,password;
        Context c;
        ProgressDialog pd;
        boolean code = false;
        public verifyLogin(String IfirstName,String IlastName,String Iemail,String Iusername,String Ipassword,Context context){
            firstName = IfirstName;
            lastName = IlastName;
            email = Iemail;
            username = Iusername;
            password = Ipassword;
            c = context;
        }
        protected void onPreExecute (){
            pd = new ProgressDialog(c);
            pd.setCanceledOnTouchOutside(false);
            pd.setMessage("Registering User...");
            pd.show();
        }

        protected String doInBackground(Void...arg0) {
            try{
                WebRequester web = new WebRequester(Register_User.this);
                code = web.registerUser(username,password,firstName,lastName,email);
                if (code) {
                    publishProgress("User is valid! Registering now.");
                }
                else{
                    publishProgress("Something went wrong...");
                }
                Thread.sleep(1000);
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return "You are at PostExecute";
        }

        protected void onProgressUpdate(String...a){
            pd.setMessage(a[0]);
        }

        protected void onPostExecute(String result) {
            if(code){
                pd.dismiss();
                AlertDialog.Builder warn = new AlertDialog.Builder(Register_User.this);
                warn.setTitle("Congratulations!");
                warn.setMessage("You're now registered to Blast! \n Please login with your new username: " + username + "\nYou will also receive a welcome email shortly.");
                warn.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Register_User.this, Blast_Login.class);
                        startActivity(intent);
                    }
                });
                warn.show();

            }
            else{
                pd.dismiss();
                AlertDialog.Builder warn = new AlertDialog.Builder(Register_User.this);
                warn.setTitle("Whoops!");
                warn.setMessage("That username/email is already chosen. Please try again with another username/email.");
                warn.setNegativeButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                warn.show();
            }
        }
    }
}

