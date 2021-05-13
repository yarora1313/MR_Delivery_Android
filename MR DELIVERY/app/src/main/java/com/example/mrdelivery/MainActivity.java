package com.example.mrdelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mrdelivery.outletactivity.CurrentCustomer;
import com.example.mrdelivery.outletactivity.Outlets;
import com.example.mrdelivery.outletactivity.OutletsFragment;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private boolean isDelivery = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button joinNowButton = findViewById(R.id.main_join_now_btn);
        Button loginButton = findViewById(R.id.main_login_btn);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAuth.getCurrentUser() == null){
                    Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
                else{
                    //loadUserActivity(mAuth.getCurrentUser().getUid());
                    Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginFBButton = findViewById(R.id.fb_login_button);
        loginFBButton.setPermissions("email", "public_profile");

        loginFBButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("FBLog", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Login Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart()
    {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            //loadUserActivity(currentUser.getUid());
        }
        // Redirect to main page and pass user obj
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token)
    {
        Log.d("FBLog", "FB Token" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                    if(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getAdditionalUserInfo()).isNewUser())
                    {
                        showDelivPrompt();
                    }
                    else{
                        loadUserActivity(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                    }
                }
                else
                {
                    Log.w("FBLog", "SignInFAILURE");
                    Toast.makeText(MainActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void signOut()                           // Need to add SignOut button
    {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
    }

    private void loadUserActivity(final String UID){
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Intent in;

                    String name = Objects.requireNonNull(dataSnapshot.child("Name").getValue()).toString();
                    String email = Objects.requireNonNull(dataSnapshot.child("Email").getValue()).toString();
                    String number = Objects.requireNonNull(dataSnapshot.child("Mobile Number").getValue()).toString();

                    if((boolean)dataSnapshot.child("DeliverPerson").getValue())
                    {
                        DelivActivity.currentDelivPerson = new CurrentDelivPerson(UID , name, email , number);
                        in = new Intent(MainActivity.this, DelivActivity.class);
                    }
                    else
                    {
                        com.example.mrdelivery.outletactivity.Outlets.currentCustomer = new CurrentCustomer(UID, name, email, number);
                        in = new Intent(MainActivity.this, Outlets.class);
                    }
                    startActivity(in);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showDelivPrompt()
    {
        AlertDialog.Builder askDelivery = new AlertDialog.Builder(MainActivity.this);
        final String UID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        askDelivery.setTitle("Register");
        askDelivery.setMessage("Would you like to register as a Delivery Person?");

        askDelivery.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.isDelivery = true;
                storeFBData();
                Toast.makeText(MainActivity.this,"Registered as a delivery Person",Toast.LENGTH_SHORT).show();
            }
        });

        askDelivery.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                storeFBData();
            }
        });
        askDelivery.show();
    }

    private void storeFBData()
    {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;

        String phoneNum = user.getPhoneNumber();
        String email = user.getEmail();
        final String UID = user.getUid();

        if(email == null){
            email = "";
        }
        if(phoneNum == null){
            phoneNum = "";
        }

        final HashMap<String,Object> userData =new HashMap<>();
        userData.put("Name", user.getDisplayName());
        userData.put("Email", email);
        userData.put("Mobile Number", phoneNum);
        userData.put("DeliverPerson", isDelivery);

        final DatabaseReference rootRef;
        rootRef= FirebaseDatabase.getInstance().getReference().child("Users");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(!(dataSnapshot.child(UID).exists()))
                {
                    rootRef.child(UID).updateChildren(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            loadUserActivity(UID);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this,"Account creation cancelled",Toast.LENGTH_LONG).show();
            }
        });
    }
}
