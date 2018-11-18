package com.software.uottawa.helpme;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anthony B.
 */

public class ServiceListActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseServices;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseNotes;
    private FirebaseAuth mAuth;
    private List<Service> mServices;
    private List<User> mUsers;
    private List<Service> mUserServices;
    private User mCurrentUser;

    private FloatingActionButton mFAB;
    private LinearLayout mAddServiceLayout;
    private Switch mServiceSwitch;

    private ListView mServicesListView;
    private ServiceAdapter mServiceAdapter;
    private boolean userServicesOnly = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);

        mDatabaseServices = FirebaseDatabase.getInstance().getReference("services");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");
        mDatabaseNotes = FirebaseDatabase.getInstance().getReference("Notes");

        mAuth = FirebaseAuth.getInstance();



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);







        mServicesListView = findViewById(R.id.service_list_view);
        //mAddServiceLayout = findViewById(R.id.layout_add_service);
        mServiceSwitch = findViewById(R.id.service_switch);

        mServices = new ArrayList<>();
        mUsers = new ArrayList<>();
        mUserServices = new ArrayList<>();


        mServiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    userServicesOnly = true;
                    mServiceAdapter = new ServiceAdapter(ServiceListActivity.this, mUserServices);
                    mServicesListView.setAdapter(mServiceAdapter);
                    mServiceAdapter.notifyDataSetChanged();
                } else {
                    userServicesOnly = false;
                    mServiceAdapter = new ServiceAdapter(ServiceListActivity.this, mServices);
                    mServicesListView.setAdapter(mServiceAdapter);
                    mServiceAdapter.notifyDataSetChanged();
                }
            }
        });

        mFAB = findViewById(R.id.fab_add_service);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ServiceListActivity.this, NewServiceActivity.class);
                startActivity(intent);
            }
        });


/*
        FloatingActionButton addServiceFab = findViewById(R.id.fab_add_service);
        addServiceFab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           Intent intent = new Intent(ServiceListActivity.this, NewServiceActivity.class);
           startActivity(intent);
        }
        });
*/
        mServicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Service selected = mServices.get(position);
                if (userServicesOnly) {
                    selected = mUserServices.get(position);
                }
                String serviceId = selected.getId();
                Intent intent = ViewServiceActivity.newIntent(ServiceListActivity.this, serviceId);
                startActivity(intent);
            }
        });


        mServicesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {

                final int position = pos;

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(ServiceListActivity.this);
                builder.setTitle("Edit Service")
                        .setMessage("Are you sure you want to edit this service?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //deletes entry from database
                                Service selected = mServices.get(position);
                                if (userServicesOnly) {
                                    String serviceId = selected.getId();
                                    Intent intent = EditServiceActivity.newIntent(ServiceListActivity.this, serviceId);
                                    startActivity(intent);
                                }
                                if (!userServicesOnly && mUserServices.contains(selected)) {
                                    selected = mUserServices.get(position);
                                }
                                String serviceId = selected.getId();
                                Intent intent = EditServiceActivity.newIntent(ServiceListActivity.this, serviceId);
                                startActivity(intent);


                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();

                return true;
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
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Service service = postSnapshot.getValue(Service.class);
                    mServices.add(service);
                }
                if (!userServicesOnly) {
                    mServiceAdapter = new ServiceAdapter(ServiceListActivity.this, mServices);
                    mServicesListView.setAdapter(mServiceAdapter);
                }
                mServiceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    mUsers.add(user);
                    if (user.getId().equals(mAuth.getCurrentUser().getUid())) {
                        mCurrentUser = user;
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.user_settings) {
            Intent sendToSettings = new Intent(ServiceListActivity.this, UserActivity.class);
            startActivity(sendToSettings);

        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_list, menu);
        return true;
    }

    private void deleteService(Service service) {
        for (String deleteId : service.getAssignedPS()) {
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







}
