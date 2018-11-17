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
    private RadioGroup mRadioGroup;
    private RadioGroup mServiceRes;
    private Button mBtnAssignUser;
    private Button mBtnAssignResources;
    private Button mBtnDelService;
    private Button mBtnSaveService;
    private View mDialogAssignResourcesView;
    private ListView mResourcesListView;
    private ArrayAdapter<String> mResourcesListAdapter;
    private List<String> mAssignedResources;
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

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

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
        mRadioGroup = findViewById(R.id.edit_status_radio_group);
        mServiceRes = findViewById(R.id.edit_res_radio_group);
        mBtnAssignUser = findViewById(R.id.edit_btn_assign_user);
        mBtnDelService = findViewById(R.id.btn_del_service);
        mBtnSaveService = findViewById(R.id.btn_save_service);

        //Assigning Resources
        mAssignedResources = new ArrayList<>();
        mBtnAssignResources = findViewById(R.id.edit_btn_assign_resources);
        mBtnAssignResources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] resources = {"Cleaning Kit", "Car", "Axe", "Ball", "Cellphone", "Pencil", "Money", "Food"};//TODO same list as the EditServiceAcitivity
                mDialogAssignResourcesView = LayoutInflater.from(EditServiceActivity.this).inflate(R.layout.dialog_assign_resources, null);
                mResourcesListView = mDialogAssignResourcesView.findViewById(R.id.resourcesListView);
                mResourcesListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                mResourcesListAdapter =new ArrayAdapter<String>(EditServiceActivity.this, android.R.layout.simple_list_item_multiple_choice, resources);
                mResourcesListView.setAdapter(mResourcesListAdapter);
                if(mCheckedResources != null){
                    for(int i =0; i < mCheckedResources.size() + 1; i++){
                        mResourcesListView.setItemChecked(i, mCheckedResources.get(i));

                    }

                }
                AlertDialog.Builder builder = new AlertDialog.Builder(EditServiceActivity.this);
                builder.setTitle("Assign Reources")
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

        mServiceHourlyRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogDatePicker = LayoutInflater.from(EditServiceActivity.this).inflate(R.layout.dialog_date_picker, null);
                final DatePicker datePicker = dialogDatePicker.findViewById(R.id.date_picker);
                datePicker.init(year, month, day, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(EditServiceActivity.this);
                builder.setTitle("Set Due Date")
                        .setView(dialogDatePicker)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                int year = datePicker.getYear();
                                int month = datePicker.getMonth() + 1;
                                int day = datePicker.getDayOfMonth();
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

                mDialogAssignView = LayoutInflater.from(EditServiceActivity.this).inflate(R.layout.dialog_assign_user, null);
                mUserListView = mDialogAssignView.findViewById(R.id.users_list_view);
                mUserListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                ArrayAdapter adapter = new ArrayAdapter<>(EditServiceActivity.this, android.R.layout.simple_list_item_multiple_choice, userNames);
                mUserListView.setAdapter(adapter);

                if (mCheckedUsers != null) {
                    for (int i = 0; i < mCheckedUsers.size() + 1; i++) {
                        mUserListView.setItemChecked(i, mCheckedUsers.get(i));
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(EditServiceActivity.this);
                builder.setTitle("Assign Users")
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

        mBtnDelService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent goBack = new Intent(EditServiceActivity.this, ServiceListActivity.class);
                mServiceName.setText(mService.getTitle());
                mServiceDescription.setText(mService.getDescription());
                mServiceInstruction.setText(mService.getInstruction());

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(EditServiceActivity.this);
                builder.setTitle("Delete service?")
                        .setMessage("Are you sure you want to delete service?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteService(mService);
                                Toast.makeText(EditServiceActivity.this, "Service Deleted!", Toast.LENGTH_SHORT).show();
                                startActivity(goBack);
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
            mServiceHourlyRate.setFocusable(false);
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

        switch (mService.getStatus()) {
            case "incomplete":
                mRadioGroup.check(R.id.edit_radio_incomplete);
                break;
            case "in progress":
                mRadioGroup.check(R.id.edit_radio_in_progress);
                break;
            case "complete":
                mRadioGroup.check(R.id.edit_radio_complete);
                break;
            default:
                break;
        }
        switch (mService.getRes()){
            case "Money":
                mServiceRes.check(R.id.edit_radio_money);
                break;
            case "Cleaning Kit":
                mServiceRes.check(R.id.edit_radio_clean);
                break;
            default:
                break;
        }
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
        String oldStatus = mService.getStatus();
        String res = "";
        String oldRes = mService.getRes();

        switch (mRadioGroup.getCheckedRadioButtonId()) {
            case R.id.edit_radio_incomplete:
                status = "incomplete";
                if (oldStatus.equals("complete")) {
                    mDatabaseUsers.child(userId).child("points").setValue(userPoints - 1);
                }
                break;
            case R.id.edit_radio_in_progress:
                status = "in progress";
                if (oldStatus.equals("complete")) {
                    mDatabaseUsers.child(userId).child("points").setValue(userPoints - 1);
                }
                break;
            case R.id.edit_radio_complete:
                status = "complete";
                if (oldStatus.equals("incomplete") || oldStatus.equals("in progress")) {
                    mDatabaseUsers.child(userId).child("points").setValue(userPoints + 1);
                }
                break;
            default:
                break;
        }
        switch(mServiceRes.getCheckedRadioButtonId()){
            case R.id.edit_radio_money:
                res = "Money";
                break;
            case R.id.edit_radio_clean:
                res = "Cleaning Kit";
                break;
            default:
                break;

        }


        String creator = mService.getCreatorId();

        Service service = new Service();
        service.setId(serviceId);
        service.setTitle(name);
        service.setDescription(description);
        service.setInstruction(instruction);
        service.setRate(hourly_rate);
        service.setStatus(status);
        service.setRes(res);
        service.setRessources(mAssignedResources);
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

        for (String assignId : mAssignedUserIds) {
            if (!existingAssignees.contains(assignId)) {
                for (User user : mUsers) {
                    if (user.getId().equals(assignId)) {
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

    private void deleteService(Service service) {
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
        mDatabaseServices.child(service.getId()).removeValue();
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
        if (mAssignedUserIds.isEmpty()) {
            Toast.makeText(EditServiceActivity.this, "Must assign at least 1 user", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        return isValid;
    }
}
