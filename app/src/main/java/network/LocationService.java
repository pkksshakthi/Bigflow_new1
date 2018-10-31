package network;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Locale;

import DataBase.DataBaseHandler;
import constant.Constant;
import models.Common;
import models.UserDetails;
import presenter.UserSessionManager;
import view.activity.LoginActivity;

public class LocationService extends Service implements LocationListener {
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 10 * 1;
    private Timer mTimer;
    private long notify_interval = 5000;
    private Handler mHandler;
    private LocationManager locationManager;
    private Location location;
    private SharedPreferences sharedPreferences;
    private Geocoder geocoder;
    double latitude, longitude;
    private List<Address> addresses;

//    public LocationService() {
//    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.e("test", "onCreate");
        super.onCreate();
        sharedPreferences = getSharedPreferences(UserSessionManager.PREFER_NAME, UserSessionManager.PRIVATE_MODE);
        String userDetails = sharedPreferences.getString(UserSessionManager.user_details, null);
        if (userDetails != null) {
            try {
                loadData(new JSONObject(userDetails));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            return;
        }
        mHandler = new Handler();
        mTimer = new Timer();
        mTimer.schedule(new TimerTaskToGetLocation(), 5, notify_interval);
    }

    private void loadData(JSONObject jsonObject) throws JSONException {

        UserDetails.setUser_code(jsonObject.getString("employee_code"));
        UserDetails.setToday_date(jsonObject.getString("date"));
        UserDetails.setUser_id(jsonObject.getString("employee_gid"));
        UserDetails.setUser_name(jsonObject.getString("employee_name"));
        UserDetails.setEntity_gid(jsonObject.getString("entity_gid"));
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @SuppressLint("MissingPermission")
    private void fn_getlocation() {
        Boolean isGPSEnable, isNetworkEnable;
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnable && !isNetworkEnable) {
            Toast.makeText(this, "Enable the GPS access", Toast.LENGTH_LONG).show();
            return;

        } /*else if (isNetworkEnable) {
            Log.v("location", "isNetworkEnable");
            location = null;
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
*/
          else if (isGPSEnable) {
            Log.v("location", "isGPSEnable");
            location = null;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }


        }

//            Insert LatLong in SQLite and Decode
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

//            try {
//                geocoder = new Geocoder(this, Locale.getDefault());
//                addresses = geocoder.getFromLocation(latitude, longitude, 1);
//
//                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//                String city = addresses.get(0).getLocality();
//                String state = addresses.get(0).getAdminArea();
//                String country = addresses.get(0).getCountryName();
//                String postalCode = addresses.get(0).getPostalCode();
//                String knownName = addresses.get(0).getFeatureName();
//                String area = addresses.get(0).getSubLocality();
//
//
//            } catch (Exception e) {
//
//            }
            setLatLong(location);
        }


    }

    private void setLatLong(Location location) {
        Log.e("ponraj", "running");
        if (location != null) {
            DataBaseHandler dataBaseHandler = new DataBaseHandler(LocationService.this);

            ContentValues contentValues = new ContentValues();
            contentValues.put(Constant.latitude, location.getLatitude());
            contentValues.put(Constant.longitude, location.getLongitude());
            contentValues.put(Constant.latlong_date, Common.convertDateString(new Date(), "yyyy-MM-dd HH:mm:ss"));
            contentValues.put(Constant.latlong_emp_gid, UserDetails.getUser_id());
            contentValues.put(Constant.entity_gid, UserDetails.getEntity_gid());

            String Out_Message = dataBaseHandler.Insert("fet_trn_tlatlong", contentValues);

            if (!"SUCCESS".equals(Out_Message)) {
                Log.e("Error", "Error On Lat Long Save.");
            }

        }

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.e("test", "onTaskRemoved");
        Intent broadcastIntent = new Intent("restarting.services");
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("test", "onDestroy");
        Intent broadcastIntent = new Intent("restarting.services");
        sendBroadcast(broadcastIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //  Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private class TimerTaskToGetLocation extends TimerTask {


        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    UserSessionManager session;
                    session = new UserSessionManager(getApplicationContext());

                    if (session.isUserLoggedIn()) {
                        fn_getlocation();
                    }

                }
            });

        }
    }
}
