package extras;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.dylan.blast.Blast_Radius;


/**
 * Created by Dylan on 9/13/2015.
 */
public class locationUpdater {


    static boolean enabled = false;
    static String provider = null;
    static LocationManager locationManager = null;
    static Context context;

    public static boolean updateLocation(Context c) {
        context = c;
        locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        location listener = new location(c);

        if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)) {
            enabled = true;
            provider = locationManager.GPS_PROVIDER;
        }
        if (locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)) {
            enabled = true;
            provider = locationManager.NETWORK_PROVIDER;
        }
        if (enabled) {

            Location loc = locationManager.getLastKnownLocation(provider);
            if (loc != null) {
                SharedPreferencesEditor.updateKey(c, "latitude", loc.getLatitude());
                SharedPreferencesEditor.updateKey(c, "longitude", loc.getLongitude());

                locationManager.requestSingleUpdate(provider, listener, null);


            }
        }
        return enabled;
    }

    public static void quickLoc() {
        Location loc = locationManager.getLastKnownLocation(provider);
        if (loc != null) {
            SharedPreferencesEditor.updateKey(context, "latitude", loc.getLatitude());
            SharedPreferencesEditor.updateKey(context, "longitude", loc.getLongitude());
        }
        if(Blast_Radius.adapter != null)Blast_Radius.refreshPosts(context);

    }
}
