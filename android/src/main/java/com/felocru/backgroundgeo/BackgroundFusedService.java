package com.felocru.backgroundgeo;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.felocru.backgroundgeo.capacitorbackgroundgeo.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


public class BackgroundFusedService extends Service  {
    private final LocationServiceBinder binder = new LocationServiceBinder();
    private NotificationManager mNM;
    private int NOTIFICATION = R.string.local_service_started;

    private final String TAG = "BackgroundFusedService";
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    @Override
    public IBinder onBind(Intent intent) {
      return binder;
    }

  @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
      Log.i("LocalService", "Received start id " + startId + ": " + intent);

      return START_NOT_STICKY; //Recuperar
    }

    @Override
    public void onDestroy() {
        stopLocationUpdates();
        if(Build.VERSION.SDK_INT >= 23){
            mNM.cancel(NOTIFICATION);
        }
        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
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
      Toast.makeText(this, R.string.local_service_started, Toast.LENGTH_SHORT).show();

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
      startLocationUpdates();
    }
    public void stopTracking(){
        // stop location updates
        stopLocationUpdates();
    }

    @TargetApi(26)
    private Notification getNotification() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        CharSequence text = getText(R.string.local_service_started);
        NotificationChannel channel = new NotificationChannel("channel_01", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);

        mNM.createNotificationChannel(channel);
        Intent notificationIntent = new Intent(this,  getApplication().getClass());
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new Notification.Builder(this, "channel_01")
          //.setSmallIcon(R.drawable.small_icon)
          .setTicker(text)
          .setWhen(System.currentTimeMillis())
          .setContentTitle(getText(R.string.app_name))
          .setContentText(text)
          .setContentIntent(contentIntent)
          .build();
        return notification;
    }

    private Notification getNotification2(){
      CharSequence text = getText(R.string.local_service_started);
      Intent notificationIntent = new Intent(this,  getApplication().getClass());
      PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

      Notification noti = new Notification.Builder(this)
        .setContentText(text)
        .setWhen(System.currentTimeMillis())
        .setContentTitle(getText(R.string.app_name))
        .setContentIntent(contentIntent)
        .build();
      return noti;

    }
    public class LocationServiceBinder extends Binder {
        public BackgroundFusedService getService() {
            return BackgroundFusedService.this;
        }
    }
}
