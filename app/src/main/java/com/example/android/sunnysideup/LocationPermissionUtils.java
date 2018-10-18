package com.example.android.sunnysideup;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class LocationPermissionUtils extends AppCompatActivity {

    private Activity thisActivity;
    View thisView;

    public LocationPermissionUtils(Activity activity, View view){
        thisActivity = activity;
        thisView= view;
    }


    /**
     * ID to identify a location permissions request.
     */
    public static final int REQUEST_LOCATION = 13;

    /**
     * Permissions required to access locations.
     */
    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    /**
     * Requests the Locations permissions.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    public void requestLocationPermissions(){

        if(ActivityCompat.shouldShowRequestPermissionRationale(thisActivity, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(thisActivity, Manifest.permission.ACCESS_FINE_LOCATION)){

            Snackbar sb = Snackbar.make(thisView, "Please grant location access permissions.", Snackbar.LENGTH_INDEFINITE);
            sb.setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(thisActivity, PERMISSIONS_LOCATION, REQUEST_LOCATION);
                }
            });
            sb.show();

        }

        else {
            ActivityCompat.requestPermissions(thisActivity, PERMISSIONS_LOCATION, REQUEST_LOCATION);
        }
    }


    public static boolean verifyPermissions(int[] grantResults){

        if(grantResults.length<1){
            return false;
        }

        for(int result: grantResults){
            if(result!= PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }

        return true;
    }

}
