package com.felocru.backgroundgeo;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

@NativePlugin(permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
public class BackgroundGeo extends Plugin {

    public BackgroundService gpsService;
    public boolean mTracking = false;
    private Intent intent = null;

    /**
     * TODO: add success and failer
     */
    @PluginMethod()
    public void startBackground(final PluginCall call){
        this.intent = new Intent(getContext().getApplicationContext(),BackgroundService.class);
        getContext().startService(this.intent);
        gpsService.startTracking();
        mTracking = true;
        Disposable disposable = RxBus.subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception{
                if (o instanceof JSObject){
                    Location location = (Location)((JSObject) o).get("location");
                    notifyListeners("backgroundEvent", getJSObjectForLocation(location));
                }
            }
        });
    }

    @PluginMethod()
    public void stopBackground(PluginCall call){
        getContext().stopService(this.intent);
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
}
