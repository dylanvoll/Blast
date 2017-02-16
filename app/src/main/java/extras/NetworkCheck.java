package extras;

import android.content.Context;
import android.os.AsyncTask;

import com.dylan.blast.Launcher_Activity;

/**
 * Created by Dylan on 2/22/2016.
 */
public class NetworkCheck extends AsyncTask<String, String, String> {

    Context c = null;
    boolean b = false;
    Launcher_Activity l;

    public NetworkCheck(Context c, boolean b, Launcher_Activity l){
    this.c = c;
    this.b = b;
    this.l = l;
    }

    @Override
    protected String doInBackground(String... params) {
        WebRequester web = new WebRequester(c);
        b = web.ping();
        return "Finished";
    }

    protected void onPostExecute(String result) {

        if (b == true){
            l.startLogin();
        }
        else{
            l.noConnection();
        }

    }
}
