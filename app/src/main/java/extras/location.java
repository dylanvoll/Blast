package extras;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by Dylan on 9/13/2015.
 */
public class location implements LocationListener {

    Context context;

    public location(Context c){
        this.context = c;
    }

    @Override
    public void onLocationChanged(Location location) {
        SharedPreferencesEditor.updateKey(context,"latitude",location.getLatitude());
        SharedPreferencesEditor.updateKey(context,"longitude",location.getLongitude());

    }

    @Override
    public void onProviderDisabled(String provider) {

        System.out.println("Location is disabled");
    }

    @Override
    public void onProviderEnabled(String provider) {
        System.out.println("Location is enabled");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
