package com.example.mrdelivery;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;

import java.util.ArrayList;
import java.util.List;


public class RestrictArea extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap gmap;
    Polygon  rectangle=null;
    List<LatLng> Latlnglist =new ArrayList<>();
    List<Marker> markerList=new ArrayList<>();
    LatLng bitsHyd = new LatLng(17.544180, 78.573512);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restrict_area);

        SupportMapFragment supportMapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.area_restriction);
        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap=googleMap;
        googleMap.addCircle(new CircleOptions().center(bitsHyd).radius(750).strokeWidth(3f).fillColor(0x550000FF));
        CameraUpdate center=
                CameraUpdateFactory.newLatLng(new LatLng(17.544338,
                        78.572766));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15.0f);

        googleMap.moveCamera(center);
        googleMap.animateCamera(zoom);
        googleMap.addMarker(new MarkerOptions().position(new LatLng(17.543568, 78.575904)).title("Dosa Palace"));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(17.541162, 78.574988)).title("Fruit Wizard"));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(17.543808, 78.572222)).title("Home Cooked"));

    }
}
