package com.felocru.backgroundgeo;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


public class BackgroundFusedService extends Service  {
    private final LocationServiceBinder binder = new LocationServiceBinder();

    private final String TAG = "BackgroundFusedService";
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (googleApiClient !=  null){
            googleApiClient.connect();
        }
        startLocationUpdates();
        return START_REDELIVER_INTENT; //Recuperar
    }

    @Override
    public void onDestroy() {
        stopLocationUpdates();
        super.onDestroy();
    }

    @Override
    public void onCreate()
    {

        Log.i(TAG, "onCreate");
        if (Build.VERSION.SDK_INT >= 26){
            startForeground(12345678, getNotification());
        }else{
            startForeground(12345678, getNotification2());
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult == null){
                    return;
                }
                for (Location location: locationResult.getLocations()){
                    //Update UI with location data
                    RxBus.publish(location);
                }
            }
        };
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates(){
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates(){
        if(mFusedLocationClient != null)
            mFusedLocationClient.removeLocationUpdates(locationCallback);
    }

    public void startTracking(){

    }
    public void stopTracking(){
        // stop location updates
        stopLocationUpdates();
    }

    @TargetApi(26)
    private Notification getNotification() {

        NotificationChannel channel = new NotificationChannel("channel_01", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        Notification.Builder builder = new Notification.Builder(getApplicationContext(), "channel_01").setAutoCancel(true);
        return builder.build();
    }
    private Notification getNotification2(){
        Notification noti = new Notification.Builder(getApplicationContext())
                .setContentTitle("Background geo")
                .setContentText("Content Text")
                //.setSmallIcon(R.drawable.new_mail)
                //.setLargeIcon(aBitmap)
                .build();
        return noti;

    }
    public class LocationServiceBinder extends Binder {
        public BackgroundFusedService getService() {
            return BackgroundFusedService.this;
        }
    }
}
