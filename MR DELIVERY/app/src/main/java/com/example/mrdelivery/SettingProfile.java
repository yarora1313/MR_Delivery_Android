package com.example.mrdelivery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mrdelivery.outletactivity.Outlets;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

public class SettingProfile extends AppCompatActivity {

    ListView settingList;
    String[] titleSettingMenu ={"Name","Email","Mobile Number","Password"};
    String[] subTitleSettingMenu ={"","","",""};
    ImageView logoutBtn;
    final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_profile);
        settingList=findViewById(R.id.settingList);
        userType = getIntent().getStringExtra("USER_TYPE");
        assert userType != null;
        initSettingUpdates();
        final MySettingAdapter mySettingAdapter = new MySettingAdapter(this,titleSettingMenu,subTitleSettingMenu);
        settingList.setAdapter(mySettingAdapter);
        logoutBtn=findViewById(R.id.logoutBtn);

        settingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   changeFucntionCall(position,mySettingAdapter);
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SettingProfile.this,MainActivity.class));
                finish();
            }
        });
    }

    private void changeMobileNumer(String userID , String newMobileNumber){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Mobile Number");
        reference.setValue(newMobileNumber).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(),"Mobile Number Changed",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void changeFucntionCall(final int pos, final MySettingAdapter mySettingAdapter){
        AlertDialog.Builder myBuilder = new AlertDialog.Builder(SettingProfile.this);

        final String userEmail;
        final String userID;

        if(userType.equalsIgnoreCase("CUSTOMER")){
             userEmail = Outlets.currentCustomer.getEmail();
             userID = Outlets.currentCustomer.getNameID();
        }
        else{
            userEmail = DelivActivity.currentDelivPerson.getEmail();
            userID = DelivActivity.currentDelivPerson.getNameID();
        }

        if(pos==2){
            myBuilder.setTitle("Change/Update Mobile Number");
            final EditText inputText = new EditText(SettingProfile.this);
            inputText.setInputType(InputType.TYPE_CLASS_PHONE);
            myBuilder.setView(inputText);

            myBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String tst = inputText.getText().toString();

                    changeMobileNumer(userID,tst);
                    mySettingAdapter.setPricesaman(pos,tst);
                }
            });

            myBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            myBuilder.show();
        }else if (pos==3){
            myBuilder.setTitle("Change Password");

            Context context = SettingProfile.this;
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            final EditText oldPass = new EditText(context);
            oldPass.setHint("Old Password");
            layout.addView(oldPass);
            final EditText newPass = new EditText(SettingProfile.this);
            newPass.setHint("New Password");
            layout.addView(newPass);
            final EditText cnfNewPass = new EditText(SettingProfile.this);
            cnfNewPass.setHint("Confirm New Password");
            layout.addView(cnfNewPass);

            oldPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            newPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            cnfNewPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

            myBuilder.setView(layout);

            myBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String oldPassText = oldPass.getText().toString();
                    String newPassText = newPass.getText().toString();
                    String cnfNewPassText = cnfNewPass.getText().toString();
                    if(newPassText.equalsIgnoreCase(cnfNewPassText)){
                        changePasswordInDatabase(userEmail,oldPassText,newPassText);
                    }else{
                        Toast.makeText(getApplicationContext(),"New passwords didn't match",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            myBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            myBuilder.show();
        }

    }

    private void changePasswordInDatabase(final String userEmail , final String oldpass, final String newPass){
        final FirebaseUser user;
        user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(userEmail, oldpass);

        assert user != null;
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Password Changed",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Failed to update password.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(), "Incorrect old password.",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initSettingUpdates(){
        if(userType.equalsIgnoreCase("CUSTOMER")){
            subTitleSettingMenu[0]=Outlets.currentCustomer.getName();
            subTitleSettingMenu[1]=Outlets.currentCustomer.getEmail();
            subTitleSettingMenu[2]=Outlets.currentCustomer.getMobile();
            subTitleSettingMenu[3]="***";
        }else{
            subTitleSettingMenu[0]=DelivActivity.currentDelivPerson.getName();
            subTitleSettingMenu[1]=DelivActivity.currentDelivPerson.getEmail();
            subTitleSettingMenu[2]=DelivActivity.currentDelivPerson.getMobile();
            subTitleSettingMenu[3]="***";
        }
    }
}

class MySettingAdapter extends ArrayAdapter<String>{

    private Context context;

    private String[] menusaman;
    private String[] pricesaman;
    private String[] changeText;

    MySettingAdapter(Context c, String[] menu, String[] price){
        super(c,R.layout.settingrow,R.id.titleSettingRow,menu);
        this.context=c;
        this.menusaman=menu;
        this.pricesaman=price;
        changeText=new String[pricesaman.length];
        changeText[changeText.length-1]="Tap to Change";
        changeText[changeText.length-2]="Tap to Change";
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

        menu.setText(menusaman[position]);
        price.setText(pricesaman[position]);
        change.setText(changeText[position]);

        return settingRow;
    }

    public void setPricesaman(int pos ,String pricesaman) {
        this.pricesaman[pos] = pricesaman;
    }
}


