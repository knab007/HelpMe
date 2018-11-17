package com.software.uottawa.helpme;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewServiceActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseServices;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseRes;
    private FirebaseAuth mAuth;

    private List<Service> mServices;
    private List<User> mUsers;

    private EditText mServiceTitle;
    private EditText mServiceDescription;
    private EditText mServiceInstruction;
    private EditText mServiceHourlyRate;
    private RadioGroup mServiceStatus;
    private RadioGroup mServiceRes;
    private Button mBtnAssignUser;
    private Button mBtnAssignResources;
    private Button mBtnSavedService;

    private TextInputLayout mTitleLayout;
    private TextInputLayout mDescLayout;
    private TextInputLayout mHourlyRateLayout;
    private TextInputLayout mInstructionLayout;

    private DatePicker mDatePicker;
    private View mDialogAssignView;
    private ListView mUserListView;
    private ArrayAdapter<String> mUserListAdapter;

    private View mDialogAssignResourcesView;
    private ListView mResourcesListView;
    private ArrayAdapter<String> mResourcesListAdapter;

    private List<String> mAssignedUserIds;
    private List<String> mAssignedResources;
    private SparseBooleanArray mCheckedUsers;
    private SparseBooleanArray mCheckedResources;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_service);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        mDatabaseServices = FirebaseDatabase.getInstance().getReference("services");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        mUsers = new ArrayList<>();
        mServices = new ArrayList<>();
        mAssignedUserIds = new ArrayList<>();
        mAssignedResources = new ArrayList<>();

        mServiceTitle = findViewById(R.id.service_name);
        mServiceDescription = findViewById(R.id.service_description);
        mServiceInstruction = findViewById(R.id.service_instruction);
        mServiceHourlyRate = findViewById(R.id.service_hourly_rate);
        mServiceStatus = findViewById(R.id.status_radio_group);
        mServiceRes = findViewById(R.id.res_radio_group);
        mBtnAssignUser = findViewById(R.id.btn_assign_user);
        mBtnAssignResources = findViewById(R.id.btn_add_resources);
        mBtnSavedService = findViewById(R.id.btn_saved);


        mTitleLayout = findViewById(R.id.service_name_layout);
        mDescLayout = findViewById(R.id.service_instruction_layout);
        mHourlyRateLayout = findViewById(R.id.service_hourly_rate_layout);
        mInstructionLayout = findViewById(R.id.service_instruction_layout);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTitleLayout.setError(null);
                mDescLayout.setError(null);
                mHourlyRateLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mServiceTitle.addTextChangedListener(textWatcher);
        mServiceHourlyRate.addTextChangedListener(textWatcher);
        mServiceDescription.addTextChangedListener(textWatcher);


        //REFERENCE: https://www.youtube.com/watch?v=kD0SqUB0IDE
        mServiceHourlyRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogDatePicker = LayoutInflater.from(NewServiceActivity.this).inflate(R.layout.dialog_date_picker, null);
                mDatePicker = dialogDatePicker.findViewById(R.id.date_picker);
                mDatePicker.init(year, month, day, null);


                //REFERENCE: https://www.youtube.com/watch?v=kD0SqUB0IDE
                AlertDialog.Builder builder = new AlertDialog.Builder(NewServiceActivity.this);
                builder.setTitle("Date")
                        .setView(dialogDatePicker)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                int year = mDatePicker.getYear();
                                int month = mDatePicker.getMonth() + 1;
                                int day = mDatePicker.getDayOfMonth();
                                String date = String.format("%s/%s/%s", day, month, year);
                                mServiceHourlyRate.setText(date);

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });

        mBtnAssignUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> userNames = new ArrayList<>();
                for (User user : mUsers) {
                    String name = user.getFirstName() + " " + user.getLastName();
                    userNames.add(name);
                }

                mDialogAssignView = LayoutInflater.from(NewServiceActivity.this).inflate(R.layout.dialog_assign_user, null);
                mUserListView = mDialogAssignView.findViewById(R.id.users_list_view);
                mUserListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                mUserListAdapter = new ArrayAdapter<>(NewServiceActivity.this, android.R.layout.simple_list_item_multiple_choice, userNames);
                mUserListView.setAdapter(mUserListAdapter);

                if (mCheckedUsers != null) {
                    for (int i = 0; i < mCheckedUsers.size() + 1; i++) {
                        mUserListView.setItemChecked(i, mCheckedUsers.get(i));
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(NewServiceActivity.this);
                builder.setTitle("Users")
                        .setView(mDialogAssignView)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAssignedUserIds.clear();
                                mCheckedUsers = mUserListView.getCheckedItemPositions();
                                for (int i = 0; i < mCheckedUsers.size() + 1; i++) {
                                    if (mCheckedUsers.get(i)) {
                                        mAssignedUserIds.add(mUsers.get(i).getId());
                                    }
                                }
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAssignedUserIds.clear();
                                if (mCheckedUsers != null) {
                                    mCheckedUsers.clear();
                                }
                            }
                        })
                        .show();
            }
        });

        mBtnSavedService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent goBack = new Intent(NewServiceActivity.this, ServiceListActivity.class);
                if (isValidService()) {
                    addService();
                    startActivity(goBack);
                }
            }

        });


        mBtnAssignResources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] resources = {"Cleaning Kit", "Car", "Axe", "Ball", "Cellphone", "Pencil", "Money", "Food"};//TODO same list as the EditServiceAcitivity
                mDialogAssignResourcesView = LayoutInflater.from(NewServiceActivity.this).inflate(R.layout.dialog_assign_resources, null);
                mResourcesListView = mDialogAssignResourcesView.findViewById(R.id.resourcesListView);
                mResourcesListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                mResourcesListAdapter =new ArrayAdapter<String>(NewServiceActivity.this, android.R.layout.simple_list_item_multiple_choice, resources);
                mResourcesListView.setAdapter(mResourcesListAdapter);
                if(mCheckedResources != null){
                    for(int i =0; i < mCheckedResources.size() + 1; i++){
                        mResourcesListView.setItemChecked(i, mCheckedResources.get(i));

                    }

                }
                AlertDialog.Builder builder = new AlertDialog.Builder(NewServiceActivity.this);
                builder.setTitle("Ressources")
                        .setView(mDialogAssignResourcesView)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                mCheckedResources = mResourcesListView.getCheckedItemPositions();
                                for(int i =0; i< mCheckedResources.size()+1 ; i++){
                                    if(mCheckedResources.get(i)){
                                        mAssignedResources.add(resources[i]);
                                    }


                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mAssignedResources.clear();
                                if(mCheckedResources != null){
                                    mCheckedResources.clear();
                                }
                            }
                        })
                        .show();


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabaseServices.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mServices.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Service service = snapshot.getValue(Service.class);
                    mServices.add(service);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    mUsers.add(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




    private void addService() {

        String name = mServiceTitle.getText().toString().trim();
        String description = mServiceDescription.getText().toString().trim();
        String instruction = mServiceInstruction.getText().toString().trim();
        String hourlyRate = mServiceHourlyRate.getText().toString();
        String status = "incomplet";
        int statusId = mServiceStatus.getCheckedRadioButtonId();

        if (statusId == R.id.radio_in_progress) {
            status = "en cours";
        }

        String selectRes = "money";
        int selectResId = mServiceRes.getCheckedRadioButtonId();
        if (selectResId == R.id.radio_clean){
            selectRes = "Cleaning Kit";
        }

        String serviceId = mDatabaseServices.push().getKey();

        Service service = new Service();
        service.setId(serviceId);
        service.setTitle(name);
        service.setDescription(description);
        service.setInstruction(instruction);
        service.setRate(hourlyRate);
        service.setStatus(status);
        service.setRes(selectRes);
        service.setAssignedUsers(mAssignedUserIds);
        service.setRessources(mAssignedResources);
        service.setCreatorId(mAuth.getCurrentUser().getUid());

        mDatabaseServices.child(serviceId).setValue(service);

        for (String userId : mAssignedUserIds) {
            for (User user : mUsers) {
                if (user.getId().equals(userId)) {
                    List<String> assignedServices = new ArrayList<>();
                    if (user.getAssignedServices() != null) {
                        assignedServices = user.getAssignedServices();
                    }
                    assignedServices.add(serviceId);
                    user.setAssignedServices(assignedServices);
                    mDatabaseUsers.child(user.getId()).setValue(user);
                }
            }
        }

        Toast.makeText(this, "Service is now effective to the assign user", Toast.LENGTH_LONG).show();
    }

    private boolean isValidService() {

        boolean isValid = true;

        String name = mServiceTitle.getText().toString().trim();
        String description = mServiceDescription.getText().toString().trim();
        String instruction = mServiceInstruction.getText().toString().trim();
        String hourlyRate = mServiceHourlyRate.getText().toString();

        if (TextUtils.isEmpty(name)) {
            mTitleLayout.setError("Required!");
            isValid = false;
        }
        if (TextUtils.isEmpty(description)) {
            mDescLayout.setError("Required!");
            isValid = false;
        }
        if (TextUtils.isEmpty(instruction)) {
            mInstructionLayout.setError("Required!");
            isValid = false;
        }
        if (TextUtils.isEmpty(hourlyRate)) {
            mHourlyRateLayout.setError("Required!");
            isValid = false;
        }
        if (mAssignedUserIds.isEmpty()) {
            Toast.makeText(NewServiceActivity.this, "A service need 1 user or more!", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        return isValid;
    }
}
