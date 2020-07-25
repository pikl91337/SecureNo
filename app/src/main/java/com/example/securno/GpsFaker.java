package com.example.securno;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.CountDownTimer;
import android.os.SystemClock;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;

public class GpsFaker {
    LocationManager _LocationManager;
    boolean _GpsStatus;
    FusedLocationProviderClient fusedLocationProviderClient;
    private Context _Context;
    Location _NewLocation;
    private String Tag;

    /**
    конструктор
     */
    public GpsFaker(){
        _Context = GlobalApplication.getAppContext();
        _LocationManager = (LocationManager) _Context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * Меняем координаты пользователя на заданные
     * @param latitude широта
     * @param longitude долгота
     */
    public void FakeGps(int latitude, int longitude,Context context){
        SetCoordinatesValues(latitude, longitude);
        _LocationManager = (LocationManager) _Context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);


        GetPermissions(context);

        _LocationManager.addTestProvider(LocationManager.GPS_PROVIDER,
                "requiresNetwork" == "",
                "requiresSatellite" == "",
                "requiresCell" == "",
                "hasMonetaryCost" == "",
                "supportsAltitude" == "",
                "supportsSpeed" == "",
                "supportsBearing" == "",
                android.location.Criteria.POWER_LOW,
                android.location.Criteria.ACCURACY_FINE);
    }

    /**
     * заполняем переменную типа Location
     * @param latitude широта
     * @param longitude долгота
     */
    private void SetCoordinatesValues(int latitude, int longitude ){
        _NewLocation = new Location(LocationManager.GPS_PROVIDER);

        _NewLocation.setLatitude(latitude);
        _NewLocation.setLongitude(longitude);
        _NewLocation.setAccuracy(10);
        _NewLocation.setTime(System.currentTimeMillis());
        _NewLocation.setAltitude(50);
        _NewLocation.setSpeed(1);
        _NewLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
    }

    public void GetPermissions(final Context context){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        Permissions.check(context/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                RequestUpdates(context);
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                GetPermissions(context);
            }
        });
    }


    public void RequestUpdates(final Context context){

        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                SetNewCoordinates();
            }

            public void onFinish() {
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                context.startActivity(intent);
            }
        }.start();
    }

    private void SetNewCoordinates(){
        _LocationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, false);
        _LocationManager.clearTestProviderEnabled(LocationManager.GPS_PROVIDER);
        _LocationManager.clearTestProviderLocation(LocationManager.GPS_PROVIDER);
        _LocationManager.removeTestProvider(LocationManager.GPS_PROVIDER);

        _LocationManager = (LocationManager) _Context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        _LocationManager.addTestProvider(LocationManager.GPS_PROVIDER,
                "requiresNetwork" == "",
                "requiresSatellite" == "",
                "requiresCell" == "",
                "hasMonetaryCost" == "",
                "supportsAltitude" == "",
                "supportsSpeed" == "",
                "supportsBearing" == "",
                android.location.Criteria.POWER_LOW,
                android.location.Criteria.ACCURACY_FINE);


        _LocationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);

        _LocationManager.setTestProviderStatus(LocationManager.GPS_PROVIDER, LocationProvider.AVAILABLE, null, System.currentTimeMillis());
        _LocationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, _NewLocation);
    }

    public void RemoveTestProvider(){
        try {
            _LocationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, false);
            _LocationManager.clearTestProviderEnabled(LocationManager.GPS_PROVIDER);
            _LocationManager.clearTestProviderLocation(LocationManager.GPS_PROVIDER);
            _LocationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
        }
        catch (Exception e){

        }
    }
}
