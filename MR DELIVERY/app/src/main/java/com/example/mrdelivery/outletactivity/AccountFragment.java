package com.example.mrdelivery.outletactivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mrdelivery.DelivActivity;
import com.example.mrdelivery.MainActivity;
import com.example.mrdelivery.R;
import com.example.mrdelivery.SettingProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ListView settingList;
    private ImageView logoutBtn;
    private String[] titleSettingMenu ={"Name","Email","Mobile Number","Password"};
    private String[] subTitleSettingMenu ={"","","",""};
    String userType;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_setting_profile, container, false);
        settingList = view.findViewById(R.id.settingList);
        logoutBtn = view.findViewById(R.id.logoutBtn);

        assert getArguments() != null;
        userType = getArguments().getString("USER_TYPE");
        assert userType != null;
        initSettingUpdates();

        final MySettingAdapter mySettingAdapter = new MySettingAdapter(this.getContext(),titleSettingMenu,subTitleSettingMenu);
        settingList.setAdapter(mySettingAdapter);

        settingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeFucntionCall(position,mySettingAdapter);
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Signed Out ");
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        return view;
    }

    private void changeMobileNumer(String userID , String newMobileNumber){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Mobile Number");
        reference.setValue(newMobileNumber).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getActivity(),"Mobile Number Changed",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initSettingUpdates(){
        if(userType.equalsIgnoreCase("CUSTOMER")){
            subTitleSettingMenu[0] = Outlets.currentCustomer.getName();
            subTitleSettingMenu[1] = Outlets.currentCustomer.getEmail();
            subTitleSettingMenu[2] = Outlets.currentCustomer.getMobile();
            subTitleSettingMenu[3] = "***";
        }else{
            subTitleSettingMenu[0] = DelivActivity.currentDelivPerson.getName();
            subTitleSettingMenu[1] = DelivActivity.currentDelivPerson.getEmail();
            subTitleSettingMenu[2] = DelivActivity.currentDelivPerson.getMobile();
            subTitleSettingMenu[3] = "***";
        }
    }

    private void changeFucntionCall(final int pos, final MySettingAdapter mySettingAdapter){
        AlertDialog.Builder myBuilder = new AlertDialog.Builder(getContext());

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
            final EditText inputText = new EditText(getContext());
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
        }
        else if (pos==3){
            myBuilder.setTitle("Change Password");

            Context context = getContext();

            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            final EditText oldPass = new EditText(context);
            oldPass.setHint("Old Password");
            layout.addView(oldPass);
            final EditText newPass = new EditText(context);
            newPass.setHint("New Password");
            layout.addView(newPass);
            final EditText cnfNewPass = new EditText(context);
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
                        if(newPassText.length() >= 8){
                            changePasswordInDatabase(userEmail,oldPassText,newPassText);
                        }
                        else{
                            Toast.makeText(getContext(),"Your password should contain at least 8 characters.",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getContext(),"New passwords didn't match",Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getContext(),"Password Changed",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getContext(), "Failed to update password.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(getContext(), "Incorrect old password.",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}

class MySettingAdapter extends ArrayAdapter<String> {

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