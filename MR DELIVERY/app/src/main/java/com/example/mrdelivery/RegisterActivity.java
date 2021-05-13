package com.example.mrdelivery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mrdelivery.inputhandler.RegexChecks;
import com.example.mrdelivery.inputhandler.inputvalidators.ConfirmPasswordValidator;
import com.example.mrdelivery.inputhandler.inputvalidators.EmailValidator;
import com.example.mrdelivery.inputhandler.inputvalidators.NameValidator;
import com.example.mrdelivery.inputhandler.inputvalidators.PasswordValidator;
import com.example.mrdelivery.inputhandler.inputvalidators.PhoneNumValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "DEBUGBOI";

    private TextInputLayout inputName, inputEmail, inputPassword, inputMobileNumber, inputConfirmPassword;
    private CheckBox deliveryPerson;
    private ProgressDialog loadingBar;
    private List<TextInputLayout> inputList;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        Button createAccountButt = findViewById(R.id.registerButton);
        inputName = findViewById(R.id.registerNameLayout);
        inputEmail = findViewById(R.id.registerEmailLayout);
        inputMobileNumber = findViewById(R.id.registerPhoneLayout);
        inputConfirmPassword = findViewById(R.id.registerConfirmPasswordLayout);
        inputPassword = findViewById(R.id.registerPasswordLayout);
        loadingBar = new ProgressDialog(this);
        deliveryPerson = findViewById(R.id.deliveryRadio);

        inputList = new ArrayList<>();
        Collections.addAll(inputList, inputName, inputEmail, inputMobileNumber, inputConfirmPassword, inputPassword);

        Objects.requireNonNull(inputName.getEditText()).addTextChangedListener(new NameValidator(inputName));
        Objects.requireNonNull(inputEmail.getEditText()).addTextChangedListener(new EmailValidator(inputEmail));
        Objects.requireNonNull(inputMobileNumber.getEditText()).addTextChangedListener(new PhoneNumValidator(inputMobileNumber));
        Objects.requireNonNull(inputPassword.getEditText()).addTextChangedListener(new PasswordValidator(inputPassword));

        ConfirmPasswordValidator confPass = new ConfirmPasswordValidator(inputConfirmPassword, inputPassword);
        Objects.requireNonNull(inputConfirmPassword.getEditText()).addTextChangedListener(confPass);
        inputPassword.getEditText().addTextChangedListener(confPass.inputPassHelper);

        createAccountButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                createAccount();
            }
        });
    }
    private void createAccount()
    {
        String name = Objects.requireNonNull(Objects.requireNonNull(inputName.getEditText()).getText()).toString();
        String email = Objects.requireNonNull(Objects.requireNonNull(inputEmail.getEditText()).getText()).toString();
        String password = Objects.requireNonNull(Objects.requireNonNull(inputPassword.getEditText()).getText()).toString();
        String confirmPassword = Objects.requireNonNull(Objects.requireNonNull(inputConfirmPassword.getEditText()).getText()).toString();
        String mobileNumber = Objects.requireNonNull(Objects.requireNonNull(inputMobileNumber.getEditText()).getText()).toString();
        boolean deliveryCheck = deliveryPerson.isChecked();

        boolean fieldsNotFilled = (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(mobileNumber) ||
                TextUtils.isEmpty(confirmPassword));

        if(fieldsNotFilled)
        {
            for(TextInputLayout inputViews: inputList)
            {
                if(TextUtils.isEmpty(Objects.requireNonNull(Objects.requireNonNull(inputViews.getEditText()).getText()).toString()))
                {
                    inputViews.setError("Please fill this field.");
                }
            }
        }
        else
        {
            boolean isValidInput = RegexChecks.validateUserReg(name, email, mobileNumber, password, confirmPassword);

            if(isValidInput)
            {
                loadingBar.setTitle("Create Account");
                loadingBar.setMessage("Please Wait while we create your account...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                HashMap<String,Object> userDataMap =new HashMap<>();
                HashMap<Object,String> orderRequest =new HashMap<>();
                userDataMap.put("Name", name);
                userDataMap.put("Email", email);
                userDataMap.put("Mobile Number", mobileNumber);
                userDataMap.put("DeliverPerson", deliveryCheck);
                userDataMap.put("OrderOrRequest", orderRequest);


                createFirebaseAccount(email, password, userDataMap);
            }
        }
    }

    private void createFirebaseAccount(String email, String password, final HashMap<String, Object> userData)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Log.d(TAG, "userCreation:Success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            updateDatabase(Objects.requireNonNull(user).getUid(), userData);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                loadingBar.dismiss();

                if(e instanceof FirebaseAuthInvalidUserException)
                {
                    String errorCode = ((FirebaseAuthInvalidUserException) e).getErrorCode();

                    if(errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE"))
                    {
                        Toast.makeText(RegisterActivity.this,"An account with this Email-ID already exists!", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(RegisterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(RegisterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateDatabase(final String UID, final HashMap<String, Object> userData)
    {
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
                            if(task.isSuccessful())
                            {
                                loadingBar.dismiss();
                                Toast.makeText(RegisterActivity.this,"Congratulations!, your Account has been created",Toast.LENGTH_SHORT).show();

                                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);

                                finish();
                                startActivity(intent);
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingBar.dismiss();
                            Toast.makeText(RegisterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }
                else
                {
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this,"An account with this Email-ID already exists!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                Toast.makeText(RegisterActivity.this,"Account creation cancelled",Toast.LENGTH_LONG).show();
            }
        });
    }
}
