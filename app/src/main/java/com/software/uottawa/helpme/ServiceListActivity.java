package com.software.uottawa.helpme;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

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
    private DatabaseReference mDatabaseReservations;
    private FirebaseAuth mAuth;
    private List<Service> mServices;
    private List<User> mUsers;
    private List<Reservation> mReservations;
    //private List<Service> mUserServices;
    private List<Reservation> mUserReservations;
    private User loggedInUser;

    private FloatingActionButton mFAB;
    private EditText filterText;

    private FloatingActionButton mFAB2;
    private View mDialogAssignDisponibilityView;
    private ListView mDisponibilityListView;
    private ArrayAdapter<String> mDisponibilityListAdapter;

    private List<String> mAssignedDisponibility;
    private SparseBooleanArray mCheckedDisponibility;

    private LinearLayout mAddServiceLayout;
    private Switch mReservationSwitch;

    private ListView mServicesListView;
    private ListView mReservationsListView;
    private ServiceAdapter mServiceAdapter;
    private ReservationAdapter mReservationAdapter;
    //private boolean userServicesOnly = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);

        mDatabaseServices = FirebaseDatabase.getInstance().getReference("services");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");
        mDatabaseReservations = FirebaseDatabase.getInstance().getReference("reservations");

        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        filterText = (EditText) findViewById(R.id.filterText);
        mServicesListView = (ListView) findViewById(R.id.service_list_view);
        mReservationsListView = (ListView) findViewById(R.id.reservation_list_view);

        //mServiceAdapter = new ServiceAdapter(ServiceListActivity.this, mServices);
        //mServiceAdapter.notifyDataSetChanged();

        filterText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    ArrayList<Service> tmp = new ArrayList<>();
                    for (Service sv : mServices) {
                    //We have done both in one search bar. So we can find service by provider name or name of the service(title)
                        if(sv.getAssignedUsers()!=null) {
                            for (String id : sv.getAssignedUsers()) {
                                for (User u : mUsers) {
                                    if(u.getFirstName().toLowerCase().contains(s.toString()) && id.equals(u.getId()) && !tmp.contains(sv)){
                                        tmp.add(sv);

                                        //System.out.println("size userservice "+mUserServices.size());
                                    }
                                }
                            }
                        }


                        if (sv.getTitle().toLowerCase().contains(s.toString())) {
                            tmp.add(sv);
                        }
                    }
                    //System.out.println(tmp.size()+ "Size userServices "+mUserServices.size()+ "Size user "+mUsers.size());

                    mServiceAdapter = new ServiceAdapter(ServiceListActivity.this, tmp);
                    mServicesListView.setAdapter(mServiceAdapter);
                } else {
                    mServiceAdapter = new ServiceAdapter(ServiceListActivity.this, mServices);
                    mServicesListView.setAdapter(mServiceAdapter);
                }
                mServiceAdapter.notifyDataSetChanged();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //mAddServiceLayout = findViewById(R.id.layout_add_service);
        mReservationSwitch = findViewById(R.id.reservation_switch);
        mReservationSwitch.setVisibility(View.GONE);


        mServices = new ArrayList<>();
        mUsers = new ArrayList<>();
        //mReservations = new ArrayList<>();
        mUserReservations = new ArrayList<>();
        //mUserServices = new ArrayList<>();
        //Assigning Disponibility
        mAssignedDisponibility = new ArrayList<>();


        mReservationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    filterText.setVisibility(View.GONE);
                    mServicesListView.setVisibility(View.GONE);
                    mReservationAdapter = new ReservationAdapter(ServiceListActivity.this, mUserReservations);
                    mReservationsListView.setAdapter(mReservationAdapter);
                    mReservationAdapter.notifyDataSetChanged();
                } else {
                    filterText.setVisibility(View.VISIBLE);
                    mReservationsListView.setVisibility(View.GONE);
                    mServicesListView.setVisibility(View.VISIBLE);
                    mServiceAdapter = new ServiceAdapter(ServiceListActivity.this, mServices);
                    mServicesListView.setAdapter(mServiceAdapter);
                    mServiceAdapter.notifyDataSetChanged();
                }
            }
        });


        mFAB = findViewById(R.id.fab_add_service);
        mFAB.setVisibility(View.GONE);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ServiceListActivity.this, NewServiceActivity.class);
                startActivity(intent);
            }
        });

        mFAB2 = findViewById(R.id.fab_add_disponibility);
        mFAB2.setVisibility(View.GONE);

        getCurrentUser();
        if(loggedInUser != null) {
            System.out.println(loggedInUser.getTypeOfUser());
        }

        mFAB2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
                mDialogAssignDisponibilityView = LayoutInflater.from(ServiceListActivity.this).inflate(R.layout.dialog_assign_resources, null);
                mDisponibilityListView = mDialogAssignDisponibilityView.findViewById(R.id.resourcesListView);
                mDisponibilityListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                mDisponibilityListAdapter = new ArrayAdapter<String>(ServiceListActivity.this, android.R.layout.simple_list_item_multiple_choice, days);
                mDisponibilityListView.setAdapter(mDisponibilityListAdapter);
                if (mCheckedDisponibility != null) {
                    for (int i = 0; i < mCheckedDisponibility.size() + 1; i++) {
                        mDisponibilityListView.setItemChecked(i, mCheckedDisponibility.get(i));

                    }

                }
                AlertDialog.Builder builder = new AlertDialog.Builder(ServiceListActivity.this);
                builder.setTitle("Assign/update your days").setView(mDialogAssignDisponibilityView).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        mCheckedDisponibility = mDisponibilityListView.getCheckedItemPositions();
                        for (int i = 0; i < mDisponibilityListView.getAdapter().getCount(); i++) {
                            if (mCheckedDisponibility.get(i)) {
                                if (mCheckedDisponibility.get(i)) mAssignedDisponibility.add(days[i]);
                            }
                        }
                        updateUser();


                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mAssignedDisponibility.clear();
                        if (mCheckedDisponibility != null) {
                            mCheckedDisponibility.clear();
                        }
                    }
                }).show();


            }
        });


        mServicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Service selected = mServices.get(position);
        /*        if (userServicesOnly) {
                    selected = mUserServices.get(position);
                }
        */
                String serviceId = selected.getId();
                Intent intent = ViewServiceActivity.newIntent(ServiceListActivity.this, serviceId);
                startActivity(intent);
            }
        });

        //HomeOwner can't do the long Click because he doesn't have right to edit anything

        mServicesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {

                final int position = pos;

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(ServiceListActivity.this);
                builder.setTitle("Edit Service").setMessage("Are you sure you want to edit this service?").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //deletes entry from database
                        Service selected = mServices.get(position);
                        String serviceId = selected.getId();
                        Intent intent = EditServiceActivity.newIntent(ServiceListActivity.this, serviceId);
                        startActivity(intent);


                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                }).show();

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
                //mUserServices.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Service service = postSnapshot.getValue(Service.class);
                    mServices.add(service);
                }
                    mServiceAdapter = new ServiceAdapter(ServiceListActivity.this, mServices);
                    mServicesListView.setAdapter(mServiceAdapter);
                    mServiceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    mUsers.add(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseReservations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //mReservations.clear();
                mUserReservations.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Reservation reservation = postSnapshot.getValue(Reservation.class);
                    //mReservations.add(reservation);
                    if(reservation.getUserId().equals(loggedInUser.getId())){
                        mUserReservations.add(reservation);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void updateUser() {
        loggedInUser.setDisponibility(mAssignedDisponibility);
        mDatabaseUsers.child(loggedInUser.getId()).setValue(loggedInUser);
        Toast.makeText(this, "User Updated", Toast.LENGTH_SHORT).show();
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
                getMenuInflater().inflate(R.menu.menu_service_list, menu);

                return true;

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


    private void getCurrentUser() {


        mDatabaseUsers.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loggedInUser = dataSnapshot.getValue(User.class);
                if (loggedInUser.getTypeOfUser().equals("ADMIN")) {
                    mFAB.setVisibility(View.VISIBLE);
                }
                if (loggedInUser.getTypeOfUser().equals("SP")) {
                    mFAB.setVisibility(View.VISIBLE);
                    mFAB2.setVisibility(View.VISIBLE);
                }
                if (loggedInUser.getTypeOfUser().equals("HOMEOWNER")) {
                    mReservationSwitch.setVisibility(View.VISIBLE);
                    mServicesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            return false;
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



}