package com.example.mrdelivery;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.mrdelivery.outletactivity.CartFragment;
import com.example.mrdelivery.outletactivity.CartItem;
import com.example.mrdelivery.outletactivity.Outlets;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    final int SEND_SMS= 1;
    Button cnfOrderBtn;
    RadioButton cod ;
    ImageView googlePay;
    TextView orderLIsttext;
    public static final String CHANNEL_ID = "10001";
    final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        cnfOrderBtn = findViewById(R.id.cnfOrderBtn);
        cod = findViewById(R.id.radioButton);
        orderLIsttext=findViewById(R.id.orderlistText);

        String order = Outlets.currentCustomer.getOrderReques();
        orderLIsttext.setText(order);

        cnfOrderBtn.setEnabled(cod.isChecked());

        cod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cnfOrderBtn.setEnabled(cod.isChecked());
            }
        });


        final String message = getNotificationMessage();
        cnfOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotification(message);
                String userEmail = Outlets.currentCustomer.getNameID();
                updateDatabase(userEmail,Outlets.currentCustomer.getOrderReques());
                CartFragment.cartItemList.clear();
                CartActivity.cartItemList.clear();
                Intent in = new Intent(getApplicationContext() , CompleteActivity.class);
                startActivity(in);
            }
        });



    }

    /***
     * Database Fuctions
     ***/

    private void updateDatabase(final String userEmail , final String message){
        final HashMap<String,Object> orderRequest = new HashMap<>();
        orderRequest.put(String.valueOf(100) , message);

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rootRef.child("Users").child(userEmail).child("OrderOrRequest").updateChildren(orderRequest);
                makeNewTable(userEmail,orderRequest,message);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void makeNewTable(final String userEmail , final HashMap<String,Object> orderRequest , final String message){
        final String assignedPerson = "NO404NO";
        final String status = "WAITING";
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    HashMap<String,Object> orderMap =new HashMap<>();
                    orderMap.put("NameID", userEmail);
                    orderMap.put("Name", Outlets.currentCustomer.getName());
                orderMap.put("Number", Outlets.currentCustomer.getMobile());
                orderMap.put("Latitude",Outlets.currentCustomer.setLat());
                orderMap.put("Longitude",Outlets.currentCustomer.setLongi());
                orderMap.put("Orders",message);
                orderMap.put("Assigned",assignedPerson);
                orderMap.put("Status",status);
                orderMap.put("Outlet",Outlets.currentCustomer.getCurrentRest());
                    rootRef.child("Active Orders").child(userEmail).updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            System.out.println("Successful !!");
                        }
                    });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("NOT Successful !!");
            }
        });


    }

    /***
     * Notification Fuctions
     ***/

    private void createNotification(String message){

        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(PaymentActivity.this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle("Order Confirmed")
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(PaymentActivity.this, CompleteActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(PaymentActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,builder.build());
        PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent,0);

    }
    private String getNotificationMessage(){
        int total=0;
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String , CartItem> entry : CartActivity.cartItemList.entrySet()){
                total+=entry.getValue().getPrice();
                sb.append(entry.getValue().getName()+" ");
        }
        sb.append(".");
        String message = "Mr. Delivery : Your Order for "+ sb.toString() +" has been confirmed with the total payment of "+total;
        return String.valueOf(message);
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Title";
            String description = "Message this is notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public boolean checkPermission(String permission){
        int check = ContextCompat.checkSelfPermission(this,permission);
        return (check== PackageManager.PERMISSION_GRANTED);
    }
}

