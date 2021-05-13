package com.example.mrdelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mrdelivery.outletactivity.Outlets;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class CompleteActivity extends AppCompatActivity {

    ImageView finalRefresh;
    TextView detailText;
    Button complete;
    private static String assignedDeliv="NOTASSIGNED";
    final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    @Override
    public void onBackPressed(){
        AlertDialog.Builder backDialog = new AlertDialog.Builder(CompleteActivity.this);
        backDialog.setMessage("You can only place one order at a time, returning to main page will cancel the current order.");

        backDialog.setPositiveButton("Return", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                Intent intent = new Intent(CompleteActivity.this, Outlets.class);
                startActivity(intent);
            }
        });

        backDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });
        backDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);

        finalRefresh =findViewById(R.id.finalRefresh);
        detailText =findViewById(R.id.cutomerDetailText);
        complete =findViewById(R.id.customerCompleteBtn);
        complete.setVisibility(View.INVISIBLE);
        complete.setEnabled(false);


        final String userEmail = Outlets.currentCustomer.getNameID();
        finalRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goToDatabase(userEmail);

            }
        });
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference rfrence = FirebaseDatabase.getInstance().getReference().child("Active Orders").child(userEmail);
                rfrence.removeValue();
                Intent in = new Intent(getApplicationContext(), Outlets.class);
                startActivity(in);
            }
        });

    }

    private void goToDatabase(final String userEmail){
        rootRef.child("Active Orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userEmail).exists()){
                    System.out.println("------------- Checking for Status----------------");
                    if(dataSnapshot.child(userEmail).child("Status").getValue().toString().equalsIgnoreCase("WAITING")){
                        detailText.setText("Your order has not yet been assigned, keep refreshing for updates!!");
                    }
                    else if(dataSnapshot.child(userEmail).child("Status").getValue().toString().equalsIgnoreCase("INPROGRESS")){
                        String assignedPerson = dataSnapshot.child(userEmail).child("Assigned").getValue().toString();
                        System.out.println("------------- Person is asigned -----------------"+ assignedPerson);
                        searchDetails(assignedPerson);
                    }else if(dataSnapshot.child(userEmail).child("Status").getValue().toString().equalsIgnoreCase("COMPLETED")){
                        detailText.setText("Your Delivery is done !!");
                        complete.setEnabled(true);
                        complete.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


//    private void requestProcess(String delivMan){
//        System.out.println("Delivery GUY :"+delivMan);
//        if(!delivMan.equalsIgnoreCase("NO404NO") || !delivMan.equalsIgnoreCase("")){
//            System.out.println("Searching Details of Delivery guy");
//            detailText.setText(searchDetails(delivMan));
//            System.out.println("You Order Details");
//            Toast.makeText(getApplicationContext(),"You Order Details",Toast.LENGTH_SHORT).show();
//        }else if(delivMan.equalsIgnoreCase("COMPLETED")){
//            StringBuilder sb =  new StringBuilder();
//            sb.append("YOUR DELIVERY WAS COMPLETED SUCCESSFULLY !!").append("\n").append("THANK YOU FOR CHOSING US :) ");
//            detailText.setText(sb.toString());
//            Toast.makeText(getApplicationContext(),"You Order Completed",Toast.LENGTH_SHORT).show();
//            complete.setEnabled(true);
//            complete.setVisibility(View.VISIBLE);
//        }
//    }

//    private void updateFromDataBase(final String userEmail){
//
//        rootRef.child("Active Orders").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.child(userEmail).exists()){
//                        if(dataSnapshot.child(userEmail).child("Status").getValue().toString().equalsIgnoreCase("INPROGRESS")){
//                            assignedDeliv = Objects.requireNonNull(dataSnapshot.child(userEmail).child("Assigned").getValue()).toString();
//                            System.out.println("-------------------Delivery Guy :"+assignedDeliv+"-------------------");
//                        }
//
//                }else {
//                    assignedDeliv="COMPLETED";
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                System.out.println("-------------------Not Found updateFromDataBase() CompleteActivity-------------------");
//            }
//        });
//
//
//    }

    private void searchDetails(final String delivMain){

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Users").child(delivMain).exists()){
                    System.out.println("-------------- DATA FOUND-------------------");
                    System.out.println("-------------- Displaying Data---------------");
                    String name = Objects.requireNonNull(dataSnapshot.child("Users").child(delivMain).child("Name").getValue()).toString();
                    String email = Objects.requireNonNull(dataSnapshot.child("Users").child(delivMain).child("Email").getValue()).toString();
                    String number = Objects.requireNonNull(dataSnapshot.child("Users").child(delivMain).child("Mobile Number").getValue()).toString();
                    StringBuilder sb = new StringBuilder();
                    sb.append(name);
                    sb.append("\n");
                    sb.append(email);
                    sb.append("\n");
                    sb.append(number);
                    sb.append("\n");
                    sb.append("Will be delivering your Order");
                    detailText.setText(sb.toString());
                }else if(!dataSnapshot.child("Users").child(delivMain).exists()){

                    detailText.setText("Your order has not yet been assigned, keep refreshing for updates!!");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("-------------------NOT FOUND Seraching -------------------");
            }
        });



    }


}
