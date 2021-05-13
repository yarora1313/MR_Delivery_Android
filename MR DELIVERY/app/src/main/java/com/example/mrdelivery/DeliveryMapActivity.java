package com.example.mrdelivery;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class DeliveryMapActivity extends FragmentActivity implements OnMapReadyCallback, AsyncResponse {
    private Location mlocation;
    public GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    public static final int Request_Code=101;
    private String currentRest;
    private String[] dest;
    private LatLng currLoc, destLoc;
    private MapRequest req;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_map);

        currentRest = getIntent().getStringExtra("CURR_REST");
        dest = getIntent().getStringArrayExtra("DEST_LOC");

        try {
            currLoc = OutletCoordinates.getOutletCoords(currentRest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        destLoc = new LatLng(Double.parseDouble(dest[0]), Double.parseDouble(dest[1]));

        req = new MapRequest();

        req.resp = this;
        req.execute(currLoc, destLoc);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        GetLastLocation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(currLoc));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLoc,14.0f));
        googleMap.addMarker(new MarkerOptions().position(currLoc).title("Your current location").draggable(false));
        googleMap.addMarker(new MarkerOptions().position(destLoc).title("Your destination").draggable(false));
    }

    public void GetLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
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
                    SupportMapFragment supportMapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.deliv_map);
                    supportMapFragment.getMapAsync(DeliveryMapActivity.this);
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == Request_Code) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GetLastLocation();
            }
        }
    }

    @Override
    public void processFinish(ArrayList wayPoints) {
        PolylineOptions route = new PolylineOptions().clickable(true).color(Color.BLUE);

        route.add(currLoc);
        for(int i=0;i<wayPoints.size();i++){
            route.add((LatLng) wayPoints.get(i));
        }
        route.add(destLoc);

        Polyline line1 = mMap.addPolyline(route);
    }
}
