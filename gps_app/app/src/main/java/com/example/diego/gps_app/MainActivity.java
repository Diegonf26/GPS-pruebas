package com.example.diego.gps_app;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LocationListener {
    //protected LocationManager locationManager;

    double lat_inicial, lon_inicial, lat_actual, lon_actual, lat_ant, lon_ant;
    float dist = 0;
    float[] result;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // in Meters
    private static final long MIN_TIME_BW_UPDATES = 300;

    Button btComenzar;
    TextView tvUbicacion;
    EditText etLongitud;
    EditText etLatitud;
    EditText etDistancia;
    EditText etLocation;
    EditText etPrecsGPS;
    EditText etHasBearing;

    TextView tvAcond;

    private LocationManager locationManager;
    private Location location;

    private boolean iniciar_carrera=false;

    //Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instanciar objetos de layout
        tvUbicacion = (TextView) findViewById(R.id.tvUbicacion);
        etLatitud = (EditText) findViewById(R.id.etLatitud);
        etLongitud = (EditText) findViewById(R.id.etLongitud);
        etDistancia = (EditText) findViewById(R.id.etDistancia);
        etLocation = (EditText) findViewById(R.id.etLocation);
        etPrecsGPS= (EditText) findViewById(R.id.etPrescGPS);
        etHasBearing =(EditText) findViewById(R.id.etHasBearing);

        btComenzar = (Button) findViewById(R.id.btComenzar);

        tvUbicacion.setText("Ubicacion de GPS");
        tvAcond = (TextView) findViewById(R.id.tvAcond);

        //Creacion de LocationManager
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Log.i("state", "1");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("state", "2");
            ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);//new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Log.i("state", "3");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            Log.i("state", "4");
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Log.i("state", "5");
            }
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.i("state", "6");
            if (location != null) {
                Log.i("state", "7");
                etLatitud.setText(" " + lat_inicial);
                etLongitud.setText(" " + lon_inicial);
                //latitudePosition.setText(String.valueOf(location.getLatitude()));
                //longitudePosition.setText(String.valueOf(location.getLongitude()));
                //getAddressFromLocation(location, getApplicationContext(), new GeoCoderHandler());
            }
        } else {
            showGPSDisabledAlertToUser();
        }

        btComenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iniciar_carrera==false) {
                    iniciar_carrera = true;
                    btComenzar.setText("Finalizar");
                }
                else {
                    iniciar_carrera = false;
                    btComenzar.setText("Comenzar");
                }
            }
        });

        //Log.d("GPS Enabled", "GPS Enabled");
        Criteria criteria= new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //String provider = locationManager.getBestProvider(criteria, true);
        //Location location=locationManager.getLastKnownLocation(provider);
        // Define a listener that responds to location updates
        //lat_inicial=location.getAltitude();
        //long_inicial=location.getLongitude();*/
    }


    @Override
    public void onLocationChanged(Location location) {
        //etLocation.setText(""+location.hasAccuracy());
        etPrecsGPS.setText(" "+location.getAccuracy());
        if (location.hasBearing()){
            etHasBearing.setText("Movimeinto  ,"+location.hasSpeed());
        }
        else
            etHasBearing.setText("Estatico");

        etLatitud.setText(" "+location.getLatitude());
        etLongitud.setText(" "+location.getLongitude());

        if(iniciar_carrera==true) {
            lat_actual = location.getLatitude();
            lon_actual = location.getLongitude();

            float[] results = new float[1];
            Location.distanceBetween(lat_ant, lon_ant, lat_actual, lon_actual, results);
            etLocation.setText(" "+results[0]);

            if (location.getAccuracy()<=5 && results[0]<=10){
                Location.distanceBetween(lat_ant, lon_ant, lat_actual, lon_actual, results);
                etLocation.setText(" "+results[0]);
                dist = dist + results[0];
                etDistancia.setText(String.format("%1$s [m]", dist));
            }

            lat_ant = lat_actual;
            lon_ant = lon_actual;
        }
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
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPSSettingIntent);
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}