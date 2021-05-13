package com.example.mrdelivery;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mrdelivery.outletactivity.CartItem;
import com.example.mrdelivery.outletactivity.Outlets;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private Location mlocation;
    private LatLng currLoc;
    MarkerOptions markerOptions;
    FusedLocationProviderClient fusedLocationProviderClient;
    public static final int Request_Code=101;
    private Button confLocation;
    public GoogleMap mMap;
    LatLng bitsHyd = new LatLng(17.544180, 78.573512);
    LatLng restLoc = OutletCoordinates.getOutletCoords(Outlets.currentCustomer.getCurrentRest());

    GoogleMap.OnMarkerDragListener setLoc = new GoogleMap.OnMarkerDragListener() {
        @Override
        public void onMarkerDragStart(Marker marker) { }

        @Override
        public void onMarkerDrag(Marker marker) { }

        @Override
        public void onMarkerDragEnd(Marker marker) {
            currLoc = marker.getPosition();
        }
    };

    public MapsActivity() throws Exception {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        confLocation = findViewById(R.id.confirmLoc);

        confLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getDistance(restLoc, currLoc) <= 1000d){
                    Outlets.currentCustomer.getterLatitude(currLoc.latitude);
                    Outlets.currentCustomer.getterLongitude(currLoc.longitude);

                    String order = Outlets.currentCustomer.getOrderReques();
                    double dist = getDistance(currLoc, restLoc);
                    int foodCharges = 0;

                    for(Map.Entry<String, CartItem> entry : CartActivity.cartItemList.entrySet()){
                        foodCharges += entry.getValue().getNumber()*entry.getValue().getPrice();
                    }

                    int weightCharge = Outlets.getWeightCost(getIntent().getIntExtra("TOT_ITEMS", 1));

                    order += "\n\n   Bill : " + "\n\n";
                    order += "   Food Charges :    " + foodCharges + "\n";
                    order += "   Quantity Charges :    " + weightCharge + "\n";
                    order += "   Distance Charges :    " + (int)(dist/10) + "\n";
                    order += "   Total Charges :    " + ((int)(dist/10) + foodCharges + weightCharge) + "\n";

                    Outlets.currentCustomer.setOrderReques(order);

                    Intent in = new Intent(MapsActivity.this, PaymentActivity.class);
                    finish();
                    startActivity(in);
                }
                else{
                    Toast toast = Toast.makeText(MapsActivity.this, "Delivery location can only be up to a maximum of 1000 metres from outlet.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 0);
                    toast.show();
                }
            }
        });

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        GetLastLocation();
    }

    public void GetLastLocation() {
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_Code);
            return;
        }
        Task<Location> task=fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location !=null)
                    {
                        mlocation=location;
                        SupportMapFragment supportMapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                        supportMapFragment.getMapAsync(MapsActivity.this);
                    }

                }
            });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        currLoc = new LatLng(mlocation.getLatitude(), mlocation.getLongitude());
        currLoc = bitsHyd;
        mMap = googleMap;
        markerOptions=new MarkerOptions().position(currLoc).title("Your Current Location");
        markerOptions.draggable(true);

        try {
            googleMap.addCircle(new CircleOptions().center(restLoc).radius(1000).strokeWidth(3f).fillColor(0x350000FF));
        } catch (Exception e) {
            e.printStackTrace();
        }
        googleMap.setOnMarkerDragListener(setLoc);
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(bitsHyd));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bitsHyd,14.0f));
        googleMap.addMarker(markerOptions);
        googleMap.addMarker(new MarkerOptions().position(restLoc).title("Outlet").draggable(false));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch(requestCode)
        {
            case Request_Code:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    GetLastLocation();
                }
                break;
        }
    }

    private double getDistance(LatLng st, LatLng stop){
        Location startLoc = new Location("Start");
        startLoc.setLatitude(st.latitude);
        startLoc.setLongitude(st.longitude);
        Location stopLoc = new Location("Stop");
        stopLoc.setLatitude(stop.latitude);
        stopLoc.setLongitude(stop.longitude);

        return startLoc.distanceTo(stopLoc);
    }
}

