package com.example.demoApp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.demoApp.userid.SEND_TEXT;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class cusMap extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap,showmap;
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;
    double mlating, mlong;

    public String  userid ;
    public String vehiid ;

    private LocationRequest mlocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static int UPDATE_INTERVAL = 5000;

    private static int DISPLACEMENT = 10;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;


    DatabaseReference ref;
    GeoFire geoFire;
    Marker mCurrent;

    MaterialAnimatedSwitch Location_Switch;
    SupportMapFragment mapFragment;
    private Button action,logout,location;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private double wayLatitude = 0.0, wayLongitude = 0.0;

    private long FASTEST_INTERVAL = 2000;
    private String vehiid3="null";
    private ToggleButton t;
    public boolean checkSwich;
    public LatLng driverlocation1;
    public LatLng pick;
    public boolean checkLoMetho;
    public TextView dis;
    public Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cus_map);


        action=(Button)findViewById(R.id.btnopen);
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cusMap.this,customerActions.class);

                startActivity(intent);

                return;




            }
        });

        logout = (Button) findViewById(R.id.btnlogout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                finish();
                startActivity(new Intent(cusMap.this,CustomerLogin.class));
            }
        });

        dis =(TextView)findViewById(R.id.distance);


        location=(Button)findViewById(R.id.mylocation) ;
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lUp();
            }
        });

        t= (ToggleButton) findViewById(R.id.pickUp);

        t.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if(isChecked) {


                    checkLoMetho=true;

                }
                else
                {

                    checkLoMetho=false;

                }
            }
        });





        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = getFusedLocationProviderClient(this);




        Location_Switch = (MaterialAnimatedSwitch) findViewById(R.id.pin);


        Location_Switch.setOnCheckedChangeListener(new MaterialAnimatedSwitch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean IsOnline) {
                if (IsOnline) {
                    check=true;
                    checkSwich=true;

                    if(check){
                        startLocationUpdates();

                        Toast.makeText(getApplicationContext(), "Online..", Toast.LENGTH_SHORT).show();
                        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference getb1 = FirebaseDatabase.getInstance().getReference();
                        getb1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                showdata(dataSnapshot);


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });







                    }

                } else {

                    check=false;

                    mMap.clear();


                    Toast.makeText(getApplicationContext(), "Offline..", Toast.LENGTH_SHORT).show();
                }
            }
        });





    }

    private void lUp() {

        // Create the location request to start receiving updates
        mlocationRequest = new LocationRequest();
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mlocationRequest.setInterval(UPDATE_INTERVAL);
        mlocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mlocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mlocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged2(locationResult.getLastLocation());

                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged2(Location location){

        mLastLocation=location;
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());

        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mlating = location.getLatitude();
        mlong = location.getLongitude();


        if (mMap != null) {
            mMap.clear();
            LatLng gps = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(gps).title("Your Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.you)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 16));
        }





    }







    private void showdata(DataSnapshot dataSnapshot) {


              getvehicleID vi = new getvehicleID();
             vi.setVehicleID(dataSnapshot.child("users").child("Customers").child(userid).getValue(getvehicleID.class).getVehicleID());
            vehiid3 = vi.getVehicleID();



    }

    private void locationremove() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


    }

    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mlocationRequest = new LocationRequest();
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mlocationRequest.setInterval(UPDATE_INTERVAL);
        mlocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mlocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mlocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());

                    }
                },
                Looper.myLooper());
    }

    private void stopLocationUpdate() {
        Toast.makeText(getApplicationContext(), "Stop..", Toast.LENGTH_SHORT).show();
    }

    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // reuqest for permission

        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();

                    }

                    String inuNilaiString = Double.toString(wayLatitude);
                    String inuNilaiString1 = Double.toString(wayLongitude);

                    Toast.makeText(getApplicationContext(), inuNilaiString, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), inuNilaiString1, Toast.LENGTH_SHORT).show();



                }
            });
        }


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        showmap=googleMap;

        if (checkPermissions()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

        }

        // Add a marker in Sydney and move the camera


    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {

            return false;
        }
    }


    public boolean check=false;

    @Override
    public void onLocationChanged(Location location) {
        if(check) {

            mLastLocation=location;
            String msg = "Updated Location: " +
                    Double.toString(location.getLatitude()) + "," +
                    Double.toString(location.getLongitude());

            // You can now create a LatLng Object for use with maps
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mlating = location.getLatitude();
            mlong = location.getLongitude();


            //if (mMap != null) {
               // mMap.clear();
                //LatLng gps = new LatLng(location.getLatitude(), location.getLongitude());
                // mMap.addMarker(new MarkerOptions().position(gps).title("Your Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.you)));
               // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 18));
            //}


                getbusid();

            if(checkLoMetho){
                Location cus = new Location("");
                cus.setLatitude(latLng.latitude);
                cus.setLongitude(latLng.longitude);

                Location dri = new Location("");
                dri.setLatitude(driverlocation1.latitude);
                dri.setLongitude(driverlocation1.longitude);

                float distancecal = cus.distanceTo(dri);

                dis.setText("Vehicle Found:" +String.valueOf(distancecal));
                vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                if(distancecal == 0.0f)
                {
                       vibrator.vibrate(500);
                }



            }
            else
            {


            }



        }

    }

    private void distance() {



    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    boolean getdrivers = false;
    List<Marker> markerList = new ArrayList<Marker>();
    private void getbusid(){



        getdrivers=true;



        DatabaseReference getb = FirebaseDatabase.getInstance().getReference("users").child(vehiid3);
        GeoFire geoFire = new GeoFire(getb);
        GeoQuery gq = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 10000000);
        gq.addGeoQueryEventListener(new GeoQueryEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                for (Marker markerIt : markerList) {

                    if(Objects.equals(markerIt.getTag(), key))
                    {
                        return;
                    }



                }

                driverlocation1 = new LatLng(location.latitude, location.longitude);

                Marker busmarker = mMap.addMarker(new MarkerOptions().position(driverlocation1).title("School Bus").icon(BitmapDescriptorFactory.fromResource(R.drawable.bus)));
                busmarker.setTag(key);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(driverlocation1, 16));
                markerList.add(busmarker);

            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onKeyExited(String key) {
                for (Marker markerIt : markerList) {

                    if(Objects.equals(markerIt.getTag(), key))
                    {
                        markerIt.remove();
                        markerList.remove(markerIt);
                        return;
                    }


                }
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for (Marker markerIt : markerList) {
                    if(Objects.equals(markerIt.getTag(), key))
                    {
                        markerIt.setPosition(new LatLng(location.latitude, location.longitude));
                    }




                }
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }












}
