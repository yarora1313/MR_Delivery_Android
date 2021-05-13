package com.example.mrdelivery;

import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RequestActivity extends AppCompatActivity {

    TextView detailText;
    Button delivConfirmBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        detailText = findViewById(R.id.requestDetails);
        delivConfirmBtn = findViewById(R.id.delivConfirmBtn);

        final String nameID = getIntent().getExtras().getString("NAMEID");
        updateScreenFromDatabase(nameID);

        delivConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference rFrence = FirebaseDatabase.getInstance().getReference().child("Active Orders").child(nameID);
                rFrence.child("Status").setValue("INPROGRESS");
                rFrence.child("Assigned").setValue(DelivActivity.currentDelivPerson.getNameID());
                readingEmailFromDataBase(nameID);
                System.out.println(DelivActivity.currentDelivPerson.getNameID());
                Intent in = new Intent(getApplicationContext(),ToCustomerActivity.class);
                in.putExtra("NAMEID",nameID);
                startActivity(in);
            }
        });

    }
    private void readingEmailFromDataBase(String nameID){

        DatabaseReference emailRefence = FirebaseDatabase.getInstance().getReference().child("Users").child(nameID);
        emailRefence.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // UNCOMMENT THIS
                String customerEmail = Objects.requireNonNull(dataSnapshot.child("Email").getValue()).toString();
                String delivEmail = DelivActivity.currentDelivPerson.getEmail();
                //String customerEmail = "divyansh.2927@gmail.com";
                //String delivEmail = "f20160350@hyderabad.bits-pilani.ac.in";
                String subject = "Confirmation of Order from Mr. Delivery";
                String orderDisplay = detailText.getText().toString();
                int otpNumber = (int)(1000*Math.random());
                String messageToCus = "Your order has been confirmed by " + DelivActivity.currentDelivPerson.getName() +
                        "\n" +
                        "Your Order: "+ "\n" + orderDisplay +
                        "\n" + "Your OTP is : "+otpNumber+"\n Please verify the number from delivery boy before taking the order \n Thank you for chosing us !!";
                String messageToDeliv = "You have accepted the order " +
                        "\n" +
                        "Your Order: "+ "\n" + orderDisplay +
                        "\n" + "Your OTP for the order is :"+otpNumber+"\n Please verify it with customer before handing over the parcel ";

                JavaMailAPI javaMailAPICus = new JavaMailAPI(getApplicationContext() , customerEmail,subject, messageToCus);
                javaMailAPICus.execute();
                JavaMailAPI javaMailAPIDev = new JavaMailAPI(getApplicationContext() , delivEmail,subject, messageToDeliv);
                javaMailAPIDev.execute();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateScreenFromDatabase(final String nameID){
        DatabaseReference rFrence = FirebaseDatabase.getInstance().getReference().child("Active Orders");

        rFrence.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Geocoder geoCoder;
                String name = dataSnapshot.child(nameID).child("Name").getValue().toString();
                String order = dataSnapshot.child(nameID).child("Orders").getValue().toString();
                double latitude = Double.parseDouble(dataSnapshot.child(nameID).child("Latitude").getValue().toString());
                double longitude = Double.parseDouble(dataSnapshot.child(nameID).child("Longitude").getValue().toString());

                List<android.location.Address> addresse = null;
                geoCoder = new Geocoder(RequestActivity.this, Locale.getDefault());

                try {
                    addresse = geoCoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                }

                assert addresse != null;
                String address = addresse.get(0).getAddressLine(0);

                StringBuilder sb = new StringBuilder();
                sb.append("Name : "+ name);
                sb.append("\nOrder : "+ order);
                sb.append("\nLocation : "+ address);
                sb.append("\n");
                detailText.setText(sb.toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
