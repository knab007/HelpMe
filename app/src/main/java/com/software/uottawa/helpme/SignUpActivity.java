package com.software.uottawa.helpme;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

//Sign up form based on https://sourcey.com/beautiful-android-login-and-signup-screens-with-material-design/
//Used of attributename and is layout in the xml files for not getting error in the view
public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";

    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mPasswordConfirmField;
    private EditText mFirstName;
    private EditText mLastName;

    private TextView mConstraintUser;
    private TextView mEmailLayout;
    private TextView mPasswordLayout;
    private TextView mPasswordConfirmLayout;
    private TextView mFirstNameLayout;
    private TextView mLastNameLayout;


    private List<User> mUsers;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private String typeOfUser;


    private  RadioGroup rg;

    private RadioButton radioHome;

    private Button mSignUp;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");

        mFirstName = findViewById(R.id.first_name_edit_text);
        mLastName = findViewById(R.id.last_name_edit_text);
        mEmailField = findViewById(R.id.email_edit_text);
        mPasswordField = findViewById(R.id.password_edit_text);
        mPasswordConfirmField = findViewById(R.id.password_confirm_edit_text);

        mEmailLayout = findViewById(R.id.email_input_layout);
        mPasswordLayout = findViewById(R.id.password_input_layout);
        mPasswordConfirmLayout = findViewById(R.id.password_confirm_input_layout);
        mFirstNameLayout = findViewById(R.id.service_name);
        mLastNameLayout = findViewById(R.id.last_name_input_layout);

        rg = (RadioGroup) findViewById(R.id.radioGroup);
        radioHome =  (RadioButton) findViewById(R.id.radioHomeO);

        getTypeOfUser();

        mSignUp = findViewById(R.id.btn_sign_up);


        getTypeOfUser();

    }


    public void createAccount(View v) {
        if (isValidForm()) {

            final String email = mEmailField.getText().toString();
            final String password = mPasswordField.getText().toString();
            final String firstName = mFirstName.getText().toString();
            final String lastName = mLastName.getText().toString();




            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign up success, sign-in with new user
                        mAuth.signInWithEmailAndPassword(email,password);

                        //Add user to database
                        String id = mAuth.getCurrentUser().getUid();
                        User user = new User(id, firstName, lastName, email, typeOfUser);
                        mDatabaseUsers.child(id).setValue(user);

                        //Send user to service list
                        Intent sendToCurrentServiceList = new Intent(SignUpActivity.this, ServiceListActivity.class);
                        startActivity(sendToCurrentServiceList);
                        Toast.makeText(SignUpActivity.this,firstName + " is now connected!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignUpActivity.this, "Error in the sign up process, check your connection or try again!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public boolean isValidForm() {

        boolean isValid = true;

        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        String passwordConfirm = mPasswordConfirmField.getText().toString();
        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();


        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Email is required!");
            isValid = false;
        }
        if (TextUtils.isEmpty(firstName)) {
            mFirstName.setError("Name is required!");
            isValid = false;
        }
        if (TextUtils.isEmpty(lastName)) {
            mLastName.setError("Name is required");
            isValid = false;
        }
        if (TextUtils.isEmpty(password)) {
            mPasswordLayout.setError("Password is required!");
            isValid = false;
        }
        if (TextUtils.isEmpty(passwordConfirm)) {
            mPasswordConfirmField.setError("Password Confirmation is required");
            isValid = false;
        }
        //firebase limitation to a minimum of 6 characters for a password
        if (password.length() < 6) {
            mPasswordLayout.setError("Password need atleast 6 characters");
            mPasswordConfirmField.setError("Password need atleast 6 characters");
            isValid = false;
        }
        if (!password.equals(passwordConfirm)) {
            mPasswordLayout.setError("Passwords don't match");
            mPasswordConfirmField.setError("Passwords don't match");
            isValid = false;
        }


        if (rg.getCheckedRadioButtonId() == -1)
        {
            radioHome.setError("Please select a Type of User");
        }


        return isValid;
    }

   /* @Override
    protected void onStart() {
        super.onStart();

        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    mUsers.add(user);
                    if(user.getEmail() == "Admin"){ IsAdmin = false;}
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

*/
    private void getTypeOfUser() {




        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radioAdmin:
                        typeOfUser = "ADMIN";
                        break;
                    case R.id.radioSP:
                            typeOfUser = "SP";
                        break;
                    case R.id.radioHomeO:

                        typeOfUser = "HOMEOWNER";
                        break;
                }
            }
        });

    }
}
