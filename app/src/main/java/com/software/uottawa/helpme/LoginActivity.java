package com.software.uottawa.helpme;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//Login FORM based on https://sourcey.com/beautiful-android-login-and-signup-screens-with-material-design/
public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference mDatabaseUsers;
    FirebaseUser fireUser;

    private EditText mEmailField;
    private EditText mPasswordField;
    private TextInputLayout mEmailLayout;
    private TextInputLayout mPasswordLayout;
    private Button mLoginButton;
    private Button mSignUpButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        System.out.println("mauthhhhhh"+mAuth);
        database = FirebaseDatabase.getInstance();
        mDatabaseUsers = database.getReference("users");
        //fireUser = mAuth.getCurrentUser();

        /* INIT */

        mEmailField = findViewById(R.id.email_edit_text);
        mPasswordField = findViewById(R.id.password_edit_text);
        mEmailLayout = findViewById(R.id.email_input_layout);
        mPasswordLayout = findViewById(R.id.password_input_layout);
        mLoginButton = findViewById(R.id.btn_login);
        mSignUpButton = findViewById(R.id.btn_sign_up);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpScreen = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signUpScreen);
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEmailLayout.setError(null);
                mPasswordLayout.setError(null);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //NEEDED ABSTRACT METHOD
            }
            @Override
            public void afterTextChanged(Editable s) {
                //NEEDED ABSTRACT METHOD
            }
        };
        mEmailField.addTextChangedListener(textWatcher);
        mPasswordField.addTextChangedListener(textWatcher);
    }

    @Override
    public void onStart() {
        super.onStart();
        fireUser = mAuth.getCurrentUser();
        System.out.println("ONSTARRrrrrT"+ fireUser);
      //  if (currentUser != null) {
        //    updateUI();
        //}
    }

    //private void updateUI() {
      //  Intent intent = new Intent(LoginActivity.this, ServiceListActivity.class);
        //startActivity(intent);
        //finish();
    //}

    private void login() {
        if (validateForm()) {
            String password = mPasswordField.getText().toString();
            String email = mEmailField.getText().toString();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete (@NonNull Task < AuthResult > task) {
                //   updateUI();
                fireUser = mAuth.getCurrentUser();
                System.out.println(task.isSuccessful());
                System.out.println(fireUser);
                if (task.isSuccessful())

                    mDatabaseUsers.child(fireUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);

                            if (user.getTypeOfUser() == "ADMIN") {
                                Intent intentAdmin = new Intent(LoginActivity.this, ServiceListActivity.class);
                                startActivity(intentAdmin);
                                finish();

                            } else if (user.getTypeOfUser() == "SP") {
                                Intent intentSP = new Intent(LoginActivity.this, ServiceListActivity.class);
                                startActivity(intentSP);
                                finish();
                            } else {
                                Intent intentHomeOwner = new Intent(LoginActivity.this, ServiceListActivity.class);
                                startActivity(intentHomeOwner);
                                finish();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                else {
                    mEmailLayout.setError("Incorrect Email or Password");
                    mPasswordLayout.setError("Incorrect Email or Password");
                }

            }
            });



        }
    }

    public boolean validateForm() {
        boolean isValid = true;
        String password = mPasswordField.getText().toString();
        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailLayout.setError("Required Field");
            isValid = false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() && !TextUtils.isEmpty(email)) {
            mEmailLayout.setError("Invalid Email");
            isValid = false;
        }
        if (TextUtils.isEmpty(password)) {
            mPasswordLayout.setError("Required Field");
            isValid = false;
        }
        return isValid;
    }




}