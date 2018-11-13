package network;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

public class ConnectivityReceiver
        extends BroadcastReceiver {

    private ConnectivityReceiverListener listener;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.e("Receiver", "started");


        } else if (intent.getAction().equals("restarting.services")) {

            boolean serviceRunningStatus = isServiceRunning(LocationService.class, context);
            if (!serviceRunningStatus) {
                Log.e("Receiver", "started");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(new Intent(context, LocationService.class));
                } else {
                    context.startService(new Intent(context, LocationService.class));
                }

            } else {
                Log.e("Receiver", "already service is running");
            }

        } else if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if (listener != null) {
                listener.onNetworkConnectionChanged(isConnected);
            }
        }

    }

    private boolean isServiceRunning(Class<LocationService> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }

    public void setConnectivityReceiver(ConnectivityReceiverListener listener1) {
        listener = listener1;
    }

}
