package com.software.uottawa.helpme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**Classe semblable à EditServiceActivity sauf qu'on nfait juste qu'afficher
 *
 *
 */
public class ViewServiceActivity extends AppCompatActivity {

    private static final String EXTRA_SERVICE_ID = "com.software.uottawa.helpme.service_i";
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseServices;
    private  DatabaseReference mDatabaseReservations;
    private FirebaseAuth mAuth;

    private ListView mUsersListView;

    private List<User> mUsers;
    private Service mService;
    private User loggedInUser;
    private String psAssignedId;
    private String psAssignedName;
    private String psAssignedEmail;
    private int psAssignedRating;


    private TextView mServiceName;
    private TextView mServiceDescription;
    private TextView mServiceInstruction;
    private TextView mServiceHourlyRate;

    private UserAdapter mUserAdapter;
    private String extraServiceId;

    private Button mBook;

    public static Intent newIntent(Context packageContext, String serviceId) {
        Intent intent = new Intent(packageContext, ViewServiceActivity.class);
        intent.putExtra(EXTRA_SERVICE_ID, serviceId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_service);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        extraServiceId = getIntent().getStringExtra(EXTRA_SERVICE_ID);

        mDatabaseServices = FirebaseDatabase.getInstance().getReference("services");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");
        mDatabaseReservations = FirebaseDatabase.getInstance().getReference("reservations");

        mUsers = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();


        mServiceName = findViewById(R.id.edit_service_name);
        mServiceDescription = findViewById(R.id.edit_service_description);
        mServiceInstruction = findViewById(R.id.edit_service_instruction);
        mServiceHourlyRate = findViewById(R.id.edit_due_hourly_rate);
        mUsersListView = findViewById(R.id.user_list_view);
        mUsersListView.setVisibility(View.GONE);
        mBook = findViewById(R.id.book);
        getCurrentUser();


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

    private void setSupportActionBar(Toolbar toolbar) {
    }

    @Override
    protected void onStart() {
        super.onStart();

        mDatabaseServices.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Service service = snapshot.getValue(Service.class);
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
                    if(loggedInUser != null) {
                        if (loggedInUser.getTypeOfUser().equals("ADMIN")) {
                            mUsersListView.setVisibility(View.GONE);
                        }
                        if (loggedInUser.getTypeOfUser().equals("SP")) {
                            mUsersListView.setVisibility(View.GONE);
                        }
                        if (loggedInUser.getTypeOfUser().equals("HOMEOWNER")) {
                            mUsersListView.setVisibility(View.VISIBLE);
                        }
                    }


                List<User> tmp = new ArrayList<>();
                tmp = mUsers;
                mUsers.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);

                    if(mService.getAssignedUsers() != null) {
                        for (String assigned : mService.getAssignedUsers()) {
                                System.out.println(user.getFirstName());
                                if (user.getId().equals(assigned)) {
                                    mUsers.add(user);
                                }
                        }
                        mUserAdapter = new UserAdapter(ViewServiceActivity.this, mUsers);
                        mUsersListView.setAdapter(mUserAdapter);
                        mUserAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getCurrentUser() {

        mDatabaseUsers.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loggedInUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void createReservation(View v){
        int position = mUsersListView.getPositionForView(v);
        User user = mUsers.get(position);
        psAssignedId = user.getId();
        psAssignedName = user.getFirstName()+user.getLastName();
        psAssignedRating = user.getRating();
        psAssignedEmail = user.getEmail();

        System.out.println("Positionnnnnn "+position);
        System.out.println("UserIddddd "+psAssignedId);
        Date dateNow = new Date();
        String date = dateNow.toString();

        //Add reservation to database
        String key = mDatabaseReservations.push().getKey();

        //String Id,  String homeOwnerId, String homeOwnerName, String psAssignedId, String psAssignedName, int psAssignedRating, String serviceId, String serviceName, String serviceDescription, String date, String resource, String psAssignedEmail
        Reservation reservation = new Reservation(key, loggedInUser.getId(), loggedInUser.getFirstName()+loggedInUser.getLastName(), psAssignedId, psAssignedName, psAssignedEmail, psAssignedRating, mService.getId(), mService.getTitle(), mService.getDescription(), date, mService.getResource());
        mDatabaseReservations.child(key).setValue(reservation);

        //Send user to service list
        Intent sendToCurrentServiceList = new Intent(ViewServiceActivity.this, ServiceListActivity.class);
        startActivity(sendToCurrentServiceList);
        Toast.makeText(ViewServiceActivity.this,"The reservation of "+reservation.getHomeOwnerName() + " is created!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void serviceStatus() {
        mServiceName.setText(mService.getTitle());
        mServiceDescription.setText(mService.getDescription());
        mServiceInstruction.setText(mService.getInstruction());
        mServiceHourlyRate.setText(mService.getRate());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_service_list, menu);
        return true;
    }


}