package com.example.mrdelivery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class Address extends AppCompatActivity {

    /*public double lati, longi;
    public Button proceed;
    public Location mlocation;
    public FusedLocationProviderClient fusedLocationProviderClient;
    final int Request_Code = 101;
    public CheckBox usemylocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);


        proceed = findViewById(R.id.proceedpayment);
        usemylocation = findViewById(R.id.usemylocation);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Address.this, PaymentActivity.class);
                startActivity(in);
            }
        });

        mlocation = MapsActivity.getLoc();

        if(mlocation != null)
        {
            lati = mlocation.getLatitude();
            longi = mlocation.getLongitude();

            Outlets.currentCustomer.getterLatitude(lati);
            Outlets.currentCustomer.getterLongitude(longi);
        }
    }*/
}



