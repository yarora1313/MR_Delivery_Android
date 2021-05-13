package com.example.mrdelivery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class DelivActivity extends AppCompatActivity {

    public static CurrentDelivPerson currentDelivPerson;
    public static double RADIUS = 1000d;
    private List<RequestOrders> requests = new LinkedList<>();
    ListView requestList;
    Switch aSwitch;
    TextView deliverTitle;
    TextView onOff;
    ImageView settingDeliv,refresh;
    SeekBar seekBar;
    final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliv);

        requestList=findViewById(R.id.requestList);
        deliverTitle = findViewById(R.id.deliverTitle);
        onOff=findViewById(R.id.onOff);
        refresh= findViewById(R.id.refreshBtn);
        settingDeliv = findViewById(R.id.settingDeliv);
        deliverTitle.setText("Hi! " + DelivActivity.currentDelivPerson.getName());
        seekBar=findViewById(R.id.seekBar2);
        seekBar.setProgress(4);

        final RequestAdapter[] requestAdapter = new RequestAdapter[1];
        // Switch Function
        aSwitch = findViewById(R.id.switch1);
        aSwitch.setChecked(true);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(aSwitch.isChecked()){
                    onOff.setText("Pending Orders");
                    seekBar.setEnabled(true);
                    requestList.setVisibility(View.VISIBLE);
                }else{
                    onOff.setText("Offline");
                    seekBar.setEnabled(false);
                    requestList.setVisibility(View.INVISIBLE);
                }
            }
        });

        /*** RADIUS will take values of Slider
         * Value 1 = 0
         * Value 2 = 250
         * Value 3 = 500
         * Value 4 = 1000
         * ***/
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Toast.makeText(getApplicationContext(),"Radius : "+ (progress*250),Toast.LENGTH_SHORT).show();
                RADIUS = (progress*250);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // Refresh Button
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootRef.child("Active Orders").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        requests.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String nameID = Objects.requireNonNull(Objects.requireNonNull(snapshot.child("NameID").getValue()).toString());
                            String names = Objects.requireNonNull(Objects.requireNonNull(snapshot.child("Name").getValue()).toString());
                            String numbers = Objects.requireNonNull(Objects.requireNonNull(snapshot.child("Number").getValue()).toString());
                            String orders = Objects.requireNonNull(Objects.requireNonNull(snapshot.child("Orders").getValue()).toString());
                            String latitude = Objects.requireNonNull(Objects.requireNonNull(snapshot.child("Latitude").getValue()).toString());
                            String longitude = Objects.requireNonNull(Objects.requireNonNull(snapshot.child("Longitude").getValue()).toString());
                            String outlet = Objects.requireNonNull(Objects.requireNonNull(snapshot.child("Outlet").getValue()).toString());
                            //String outlet = "";
                            String assigned = Objects.requireNonNull(Objects.requireNonNull(snapshot.child("Assigned").getValue()).toString());
                            String status = Objects.requireNonNull(Objects.requireNonNull(snapshot.child("Status").getValue()).toString());

                            LatLng destLoc = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                            LatLng currLoc = null;
                            try {
                                currLoc = OutletCoordinates.getOutletCoords(outlet);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            assert currLoc != null;
                            if(status.equalsIgnoreCase("WAITING") && (getDistance(currLoc, destLoc) <= RADIUS)){
                                System.out.println(getDistance(currLoc, destLoc));
                                requests.add(new RequestOrders(nameID,names,orders,latitude, longitude, outlet,numbers,assigned));
                            }

                        }
                        List<String> name = new LinkedList<>();
                        List<String> nameIDs = new LinkedList<>();
                        List<String> phoneNum = new LinkedList<>();
                        for(RequestOrders n : requests){
                            name.add(n.getName());
                            nameIDs.add(n.getNameID());
                            phoneNum.add(n.getNumber());
                        }
                        requestAdapter[0] = new RequestAdapter(getApplication(),name,nameIDs, phoneNum);
                        requestList.setAdapter(requestAdapter[0]);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });



        // Settings
        settingDeliv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(),SettingProfile.class);
                in.putExtra("USER_TYPE", "DELIVER");
                startActivity(in);
            }
        });

        //requestList
        requestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent in = new Intent(getApplicationContext() ,RequestActivity.class);
                in.putExtra("PHONENUM",requestAdapter[0].getPhone(position));
                in.putExtra("NAMEID",requestAdapter[0].getNameID(position));
                startActivity(in);

            }
        });

        refresh.performClick();
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


class RequestAdapter extends ArrayAdapter<String>{

    private Context context;

    private List<String> name;
    private List<String> nameID;
    private List<String> phoneNum;

    private String[] changeText;

    RequestAdapter(Context c, List<String> name,List<String> nameID,List<String> phoneNum){
        super(c,R.layout.settingrow,R.id.titleSettingRow,name);
        this.context=c;
        this.name=name;
        this.nameID=nameID;
        this.phoneNum = phoneNum;
        changeText=new String[name.size()];
        Arrays.fill(changeText,"Tap to accept");
    }
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater layoutInflater=(LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        final View settingRow = layoutInflater.inflate(R.layout.settingrow,parent,false);
        TextView menu=settingRow.findViewById(R.id.titleSettingRow);
        TextView price=settingRow.findViewById(R.id.settingrowDetails);
        TextView change = settingRow.findViewById(R.id.changeText);

        menu.setText(name.get(position));
        price.setText(phoneNum.get(position));
        change.setText(changeText[position]);

        return settingRow;
    }


    String getPhone(int pos) {
        return phoneNum.get(pos);
    }
    String getNameID(int pos) {
        return nameID.get(pos);
    }
}

class RequestOrders {
    private String nameID;
    private String name;
    private String number;
    private String order;
    private String latitude;
    private String longitude;
    private String outlet;
    private String delivIDs;

    public RequestOrders(String nameID,String name, String order, String latitude, String longitude, String outlet,String number,String delivIDs)
    {
        this.name = name;
        this.order = order;
        this.latitude = latitude;
        this.longitude = longitude;
        this.outlet = outlet;
        this.nameID=nameID;
        this.number=number;
        this.delivIDs=delivIDs;
    }

    public String getNameID() {
        return nameID;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getOrder() {
        return order;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude(){
        return longitude;
    }

    public String getOutlet(){
        return outlet;
    }

    public String getDelivIDs() {
        return delivIDs;
    }
}
