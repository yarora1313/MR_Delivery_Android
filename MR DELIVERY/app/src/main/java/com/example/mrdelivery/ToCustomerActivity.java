package com.example.mrdelivery;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ToCustomerActivity extends AppCompatActivity implements AsyncResponse{

    TextView orderDetails;
    Button delivCompeleteBtn, dispRoute;
    private MapRequest req;
    private String curr_rest;
    private String[] dest_loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_customer);

        orderDetails = findViewById(R.id.orderDetails);
        delivCompeleteBtn = findViewById(R.id.delivCompeleteBtn);
        dispRoute = findViewById(R.id.dispRoute);


        final String nameID = getIntent().getExtras().getString("NAMEID");
        updateScreenFromDatabase(nameID);

        delivCompeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference rFrence = FirebaseDatabase.getInstance().getReference().child("Active Orders").child(nameID);
                rFrence.child("Status").setValue("COMPLETED").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),"Good Job !! ",Toast.LENGTH_SHORT).show();
                        Intent in = new Intent(getApplicationContext(),DelivActivity.class);
                        startActivity(in);
                        /*new FetchURL();
                        Direction*/
                    }
                });

            }
        });

        dispRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DeliveryMapActivity.class);
                intent.putExtra("CURR_REST", curr_rest);
                intent.putExtra("DEST_LOC", dest_loc);
                startActivity(intent);
            }
        });
    }

    private void updateScreenFromDatabase(final String nameID){
        DatabaseReference rFrence = FirebaseDatabase.getInstance().getReference().child("Active Orders");
        final StringBuilder sb = new StringBuilder();

        rFrence.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Geocoder geoCoder;

                String name = dataSnapshot.child(nameID).child("Name").getValue().toString();
                String order = dataSnapshot.child(nameID).child("Orders").getValue().toString();
                double latitude = Double.parseDouble(dataSnapshot.child(nameID).child("Latitude").getValue().toString());
                double longitude = Double.parseDouble(dataSnapshot.child(nameID).child("Longitude").getValue().toString());
                String number = dataSnapshot.child(nameID).child("Number").getValue().toString();
                curr_rest = dataSnapshot.child(nameID).child("Outlet").getValue().toString();
                dest_loc = new String[]{String.valueOf(latitude), String.valueOf(longitude)};

                List<Address> address = null;
                geoCoder = new Geocoder(ToCustomerActivity.this, Locale.getDefault());

                try {
                    address = geoCoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                }

                assert address != null;
                String addresse = address.get(0).getAddressLine(0);

                sb.append("Name : ").append(name);
                sb.append("\nNumber : ").append(number);
                sb.append("\nOrder : ").append(order);
                sb.append("\nLocation : ").append(addresse);
                sb.append("\n");
                orderDetails.setText(sb.toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void processFinish(ArrayList wayPoints) {

    }
}
