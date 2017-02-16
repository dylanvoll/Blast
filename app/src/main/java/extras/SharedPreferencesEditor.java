package extras;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Dylan on 10/7/2015.
 */
public class SharedPreferencesEditor {
    static SharedPreferences.Editor edit;
    public static void updateKey(Context c,String key, String value){

        SharedPreferences prefs = c.getSharedPreferences("blast", c.MODE_PRIVATE);
        edit = prefs.edit();
        edit.putString(key,value);
        edit.commit();

    }

    public static void updateKey(Context c,String key, double value){

        SharedPreferences prefs = c.getSharedPreferences("blast", c.MODE_PRIVATE);
        edit = prefs.edit();
        edit.putFloat(key,(float)value);
        edit.commit();

    }

    public static void updateKey(Context c,String key, boolean value){

        SharedPreferences prefs = c.getSharedPreferences("blast", c.MODE_PRIVATE);
        edit = prefs.edit();
        edit.putBoolean(key,value);
        edit.commit();

    }

    public static void updateKey(Context c,String key, int value){

        SharedPreferences prefs = c.getSharedPreferences("blast", c.MODE_PRIVATE);
        edit = prefs.edit();
        edit.putInt(key,value);
        edit.commit();
    }

    public static void removeKey(Context c,String key){

        SharedPreferences prefs = c.getSharedPreferences("blast", c.MODE_PRIVATE);
        edit = prefs.edit();
        edit.remove(key);
        edit.commit();
    }

    public static boolean recieveBoolean(Context c,String key){

        SharedPreferences prefs = c.getSharedPreferences("blast", c.MODE_PRIVATE);
        return prefs.getBoolean(key,false);
    }

    public static String recieveKey(Context c,String key){

        SharedPreferences prefs = c.getSharedPreferences("blast", c.MODE_PRIVATE);
        return prefs.getString(key,null);
    }
    public static int recieveInt(Context c,String key){

        SharedPreferences prefs = c.getSharedPreferences("blast", c.MODE_PRIVATE);
        return prefs.getInt(key,0);
    }
    public static float recieveFloat(Context c,String key){

        SharedPreferences prefs = c.getSharedPreferences("blast", c.MODE_PRIVATE);
        return prefs.getFloat(key,0);
    }
}
