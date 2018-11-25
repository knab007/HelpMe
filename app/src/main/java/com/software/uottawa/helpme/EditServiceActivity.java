package com.software.uottawa.helpme;

import android.content.Context;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EditServiceActivity extends AppCompatActivity {

    private static final String EXTRA_SERVICE_ID = "com.majes.uottawa.helpme.service_id";
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseServices;
    private FirebaseAuth mAuth;

    private List<Service> mServices = new ArrayList<>();
    private List<User> mUsers = new ArrayList<>();
    private List<String> mAssignedUserIds = new ArrayList<>();
    private Service mService = new Service();
    private User mUser;

    private EditText mServiceName;
    private EditText mServiceDescription;
    private EditText mServiceInstruction;
    private EditText mServiceHourlyRate;

    private TextInputLayout mServiceNameLayout;
    private TextInputLayout mServiceDescLayout;
    private TextInputLayout mServiceInstructionLayout;
    private TextInputLayout mServiceHourlyRateLayout;
    private Button mBtnAssignUser;
    private Button mBtnAssignResources;
    private Button mBtnDelService;
    private Button mBtnSaveService;
    private View mDialogAssignResourcesView;
    private ListView mResourcesListView;
    private ArrayAdapter<String> mResourcesListAdapter;

    private String defaultResource = "Default";
    private String checkedResource;
    private SparseBooleanArray mCheckedResources;

    private ListView mUserListView;
    private View mDialogAssignView;
    private SparseBooleanArray mCheckedUsers;

    private String extraServiceId;

    public static Intent newIntent(Context packageContext, String serviceId) {
        Intent intent = new Intent(packageContext, EditServiceActivity.class);
        intent.putExtra(EXTRA_SERVICE_ID, serviceId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_service);

        extraServiceId = getIntent().getStringExtra(EXTRA_SERVICE_ID);

        mDatabaseServices = FirebaseDatabase.getInstance().getReference("services");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        mServiceName = findViewById(R.id.edit_service_name);
        mServiceDescription = findViewById(R.id.edit_service_description);
        mServiceInstruction = findViewById(R.id.edit_service_instruction);
        mServiceHourlyRate = findViewById(R.id.edit_due_hourly_rate);
        mServiceNameLayout = findViewById(R.id.edit_service_name_layout);
        mServiceDescLayout = findViewById(R.id.edit_service_description_layout);
        mServiceInstructionLayout = findViewById(R.id.edit_service_instruction_layout);
        mServiceHourlyRateLayout = findViewById(R.id.edit_due_hourly_rate_layout);

        mBtnAssignUser = findViewById(R.id.edit_btn_assign_user);
        mBtnAssignUser.setVisibility(View.GONE);

        mBtnDelService = findViewById(R.id.btn_del_service);
        mBtnDelService.setVisibility(View.GONE);

        mBtnSaveService = findViewById(R.id.btn_save_service);

        mBtnAssignResources = findViewById(R.id.edit_btn_assign_resources);
        mBtnAssignResources.setVisibility(View.GONE);
        mBtnAssignResources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] resources = {"Cleaning", "Plumber", "Gardening", "Painting", "Extra"};//TODO same list as the EditServiceActivity
                mDialogAssignResourcesView = LayoutInflater.from(EditServiceActivity.this).inflate(R.layout.dialog_assign_resources, null);
                mResourcesListView = mDialogAssignResourcesView.findViewById(R.id.resourcesListView);
                mResourcesListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                mResourcesListAdapter =new ArrayAdapter<String>(EditServiceActivity.this, android.R.layout.simple_list_item_single_choice, resources);
                mResourcesListView.setAdapter(mResourcesListAdapter);
                if(mCheckedResources != null){
                    for(int i =0; i < mCheckedResources.size() + 1; i++){
                        mResourcesListView.setItemChecked(i, mCheckedResources.get(i));


                    }

                }
                AlertDialog.Builder builder = new AlertDialog.Builder(EditServiceActivity.this);
                builder.setTitle("Assign Resource")
                        .setView(mDialogAssignResourcesView)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                mCheckedResources = mResourcesListView.getCheckedItemPositions();
                                for(int i =0; i<= mResourcesListView.getAdapter().getCount() ; i++){
                                    if(mCheckedResources.get(i)){
                                        //mAssignedResources.add(resources[i]);
                                        checkedResource = resources[i];
                                        updateResource(checkedResource);
                                    }


                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(mCheckedResources != null){
                                    mCheckedResources.clear();
                                }
                            }
                        })
                        .show();


            }
        });


        mBtnAssignUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  mAssignedUserIds.add(mUser.getId());
                  Toast.makeText(EditServiceActivity.this, "Service assigned to you!", Toast.LENGTH_SHORT).show();

            }
        });



        mBtnDelService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if (mUser.getTypeOfUser() == "ADMIN") {

                final Intent goBack = new Intent(EditServiceActivity.this, ServiceListActivity.class);
                mServiceName.setText(mService.getTitle());
                mServiceDescription.setText(mService.getDescription());
                mServiceInstruction.setText(mService.getInstruction());
                mServiceHourlyRate.setText(mService.getRate());
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(EditServiceActivity.this);
                builder.setTitle("Delete service?").setMessage("Are you sure you want to delete service?").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteService(mService);
                        Toast.makeText(EditServiceActivity.this, "Service Deleted!", Toast.LENGTH_SHORT).show();
                        startActivity(goBack);
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                }).show();
                //}
            }
        });

        mBtnSaveService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Intent goBack = new Intent(EditServiceActivity.this, ServiceListActivity.class);
                if (isValidService()) {
                    updateService();
                    //TODO TOAST? Toast.makeText(this, "Service Saved!", Toast.LENGTH_SHORT).show();
                    startActivity(goBack);

                }
            }

        });

        if (!mAuth.getCurrentUser().getUid().equals(mService.getCreatorId())) {
            mServiceName.setFocusable(true);
            mServiceDescription.setFocusable(true);
            mServiceInstruction.setFocusable(true);
            mServiceHourlyRate.setFocusable(true);
        }

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        mServiceName.addTextChangedListener(textWatcher);
        mServiceDescription.addTextChangedListener(textWatcher);
        mServiceInstruction.addTextChangedListener(textWatcher);
        mServiceHourlyRate.addTextChangedListener(textWatcher);
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
                    if (service.getId().equals(extraServiceId)) {
                        mService = service;
                    }
                }
                serviceStatus();

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
                    if (user.getId().equals(mAuth.getCurrentUser().getUid())) {
                        mUser = user;
                        if (mUser.getTypeOfUser().equals("ADMIN")) {
                            mBtnAssignResources.setVisibility(View.VISIBLE);
                            mBtnDelService.setVisibility(View.VISIBLE);
                        }
                        if (mUser.getTypeOfUser().equals("SP")) {
                            mBtnAssignUser.setVisibility(View.VISIBLE);
                        }

                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void serviceStatus() {
        mServiceName.setText(mService.getTitle());
        mServiceDescription.setText(mService.getDescription());
        mServiceInstruction.setText(mService.getInstruction());
        mServiceHourlyRate.setText(mService.getRate());


    }




    private void updateService() {
        String userId = mUser.getId();
        int userPoints = mUser.getPoints();

        String serviceId = mService.getId();
        List<String> existingAssignees = mService.getAssignedUsers();

        String name = mServiceName.getText().toString();
        String description = mServiceDescription.getText().toString();
        String instruction = mServiceInstruction.getText().toString();
        String hourly_rate = mServiceHourlyRate.getText().toString();
        String status = "";
        String res = "";


        String creator = mService.getCreatorId();

        Service service = new Service();
        service.setId(serviceId);
        service.setTitle(name);
        service.setDescription(description);
        service.setInstruction(instruction);
        service.setRate(hourly_rate);
        service.setResource(defaultResource);
        if (mAssignedUserIds.isEmpty()) {
            service.setAssignedUsers(mService.getAssignedUsers());
        } else {
            service.setAssignedUsers(mAssignedUserIds);
        }
        service.setCreatorId(creator);

        mDatabaseServices.child(serviceId).setValue(service);
        updateAssignedUsers(existingAssignees, serviceId);
        Toast.makeText(this, "Service Updated", Toast.LENGTH_SHORT).show();
    }

    private void updateAssignedUsers(List<String> existingAssignees, String serviceId) {

        if (mAssignedUserIds != null) {
            for (String assignId : mAssignedUserIds) {
                if (!existingAssignees.contains(assignId)) {
                    for (User user : mUsers) {
                        if (user.getId().equals(assignId) && user.getTypeOfUser() == "SP") {
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
            }
        }

        if (existingAssignees != null) {
            for (String deleteId : existingAssignees) {
                if (!mAssignedUserIds.contains(deleteId)) {
                    for (User user : mUsers) {
                        if (user.getId().equals(deleteId) && user.getAssignedServices() != null) {
                            List<String> assignedServices = user.getAssignedServices();
                            assignedServices.remove(serviceId);
                            user.setAssignedServices(assignedServices);
                            mDatabaseUsers.child(user.getId()).setValue(user);
                        }
                    }
                }
            }
        }
    }

    private void deleteService(Service service) {

        if ((service.getAssignedUsers() != null)) {
            for (String deleteId : service.getAssignedUsers()) {
                for (User user : mUsers) {
                    if (user.getId().equals(deleteId) && user.getAssignedServices() != null) {
                        List<String> assignedServices = user.getAssignedServices();
                        assignedServices.remove(service.getId());
                        user.setAssignedServices(assignedServices);
                        mDatabaseUsers.child(user.getId()).setValue(user);
                    }
                }
            }
        }
        mDatabaseServices.child(service.getId()).removeValue();
    }

    private void updateResource (String defaultResource) {
        this.defaultResource = defaultResource;
    }

    private boolean isValidService() {

        boolean isValid = true;

        String name = mServiceName.getText().toString().trim();
        String description = mServiceDescription.getText().toString().trim();
        String instruction = mServiceInstruction.getText().toString().trim();
        String dueHourlyRate = mServiceHourlyRate.getText().toString();

        if (TextUtils.isEmpty(name)) {
            mServiceNameLayout.setError("Required Field");
            isValid = false;
        }
        if (TextUtils.isEmpty(description)) {
            mServiceDescLayout.setError("Required Field");
            isValid = false;
        }
        if (TextUtils.isEmpty(instruction)) {
            mServiceInstructionLayout.setError("Required Field");
            isValid = false;
        }
        if (TextUtils.isEmpty(dueHourlyRate)) {
            mServiceHourlyRateLayout.setError("Required Field");
            isValid = false;
        }

        return isValid;
    }



}

