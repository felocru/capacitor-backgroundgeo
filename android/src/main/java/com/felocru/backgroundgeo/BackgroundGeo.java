package com.felocru.backgroundgeo;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import android.util.Log;

@NativePlugin(permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
public class BackgroundGeo extends Plugin {

    public BackgroundFusedService gpsService;
    private final String TAG = "BackgroundGeo";

    public boolean mTracking = false;
    private Intent intent = null;

    /**
     * TODO: add success and failer
     */
    @PluginMethod()
    public void startBackground(final PluginCall call){
        Log.i(TAG, "Entra a startBackground");
        intent = new Intent(getContext().getApplicationContext(),BackgroundFusedService.class);
        getContext().startService(intent);
        Log.i(TAG, "startService");
        getContext().getApplicationContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        Disposable disposable = RxBus.subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception{
                if (o instanceof Location){
                    Location location = ((Location) o);
                    Log.i(TAG, "BackgroundEvent noti: "+location.toString());
                    notifyListeners("backgroundEvent", getJSObjectForLocation(location));
                }
            }
        });
    }

    @PluginMethod()
    public void stopBackground(PluginCall call){
        Log.i(TAG, "Stop background geo service");
        gpsService.stopTracking();
        boolean force = call.getBoolean("force", false);
        if (force) {
            gpsService.stopSelf();
        }
        getContext().stopService(intent);
    }

    private JSObject getJSObjectForLocation(Location location) {
        if (location == null) return null;

        JSObject ret = new JSObject();
        JSObject coords = new JSObject();
        ret.put("coords", coords);
        coords.put("latitude", location.getLatitude());
        coords.put("longitude", location.getLongitude());
        coords.put("accuracy", location.getAccuracy());
        coords.put("altitude", location.getAltitude());
        coords.put("speed", location.getSpeed());
        coords.put("heading", location.getBearing());
        return ret;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
          Log.i(TAG, "onServiceConnected");
            String nameClass = name.getClassName();
            if(nameClass.endsWith("BackgroundFusedService")){
                gpsService = ((BackgroundFusedService.LocationServiceBinder) service).getService();
                gpsService.startTracking();
                mTracking = true;
                Log.i(TAG, "GPS Ready");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (name.getClassName().equals("BackgroundFusedService")){
                gpsService = null;
                Log.i(TAG, "GPS disconnected");
            }
        }
    };
}
