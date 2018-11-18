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
import java.util.Calendar;
import java.util.List;

public class NewServiceActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseServices;
    private FirebaseAuth mAuth;

    private List<Service> mServices;

    private EditText mServiceTitle;
    private EditText mServiceDescription;
    private EditText mServiceInstruction;
    private EditText mServiceHourlyRate;
    private Button mBtnAssignResources;
    private Button mBtnSavedService;

    private TextInputLayout mTitleLayout;
    private TextInputLayout mDescLayout;
    private TextInputLayout mHourlyRateLayout;
    private TextInputLayout mInstructionLayout;

    private View mDialogAssignResourcesView;
    private ListView mResourcesListView;
    private ArrayAdapter<String> mResourcesListAdapter;

    private List<String> mAssignedResources;
    private SparseBooleanArray mCheckedResources;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_service);


        mDatabaseServices = FirebaseDatabase.getInstance().getReference("services");
        mAuth = FirebaseAuth.getInstance();

        mServices = new ArrayList<>();
        mAssignedResources = new ArrayList<>();

        mServiceTitle = findViewById(R.id.service_name);
        mServiceDescription = findViewById(R.id.service_description);
        mServiceInstruction = findViewById(R.id.service_instruction);
        mServiceHourlyRate = findViewById(R.id.service_hourly_rate);
        mBtnAssignResources = findViewById(R.id.btn_add_resources);
        mBtnSavedService = findViewById(R.id.btn_saved);


        mTitleLayout = findViewById(R.id.service_name_layout);
        mDescLayout = findViewById(R.id.service_description_layout);
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
                final String[] resources = {"Cleaning", "Plumber", "Gardening", "Painting", "Extra"};//TODO same list as the EditServiceAcitivity
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

    }


    private void addService() {

        String name = mServiceTitle.getText().toString().trim();
        String description = mServiceDescription.getText().toString().trim();
        String instruction = mServiceInstruction.getText().toString().trim();
        String hourlyRate = mServiceHourlyRate.getText().toString();
        String serviceId = mDatabaseServices.push().getKey();

        Service service = new Service();
        service.setId(serviceId);
        service.setTitle(name);
        service.setDescription(description);
        service.setInstruction(instruction);
        service.setRate(hourlyRate);
        service.setRessources(mAssignedResources);
        service.setCreatorId(mAuth.getCurrentUser().getUid());

        mDatabaseServices.child(serviceId).setValue(service);


        Toast.makeText(this, "This Service is now effective to all HomeOwner", Toast.LENGTH_LONG).show();
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

        return isValid;
    }
}
