package com.software.uottawa.helpme;

import android.content.Context;
import android.media.Rating;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ReservationAdapter extends ArrayAdapter<Reservation>{

    private List<Reservation> mUserReservationList;
    private User mPsUser;
    private Service mService;

    private DatabaseReference mDatabaseReservations;
    private DatabaseReference mDatabaseServices;
    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAuth;

    private String mUserId;
    private RatingBar rating;

    public ReservationAdapter(Context context, List<Reservation> reservationList) {
        super(context, R.layout.reservation_list_item_view, reservationList);
        this.mUserReservationList = reservationList;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rowView = LayoutInflater.from(getContext()).inflate(R.layout.reservation_list_item_view, parent, false);

        final Reservation reservation = mUserReservationList.get(position);
        mDatabaseServices = FirebaseDatabase.getInstance().getReference("services");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");
        mDatabaseReservations = FirebaseDatabase.getInstance().getReference("reservations");
        mAuth = FirebaseAuth.getInstance();
        mUserId = mAuth.getCurrentUser().getUid();

        TextView date = rowView.findViewById(R.id.date);
        TextView service_title = rowView.findViewById(R.id.service_title);
        TextView service_description = rowView.findViewById(R.id.service_description);
        TextView assignedPS = rowView.findViewById(R.id.assignedPS);
        TextView service_resource = rowView.findViewById(R.id.resource);
        TextView assignedEmail = rowView.findViewById(R.id.email);
        rating = rowView.findViewById(R.id.ratingBar);
        rating.setRating(reservation.getPsAssignedRating());

        date.setText(reservation.getDate());
        service_title.setText(reservation.getServiceName());
        service_description.setText(reservation.getServiceDescription());
        assignedEmail.setText(reservation.getPsAssignedEmail());
        assignedPS.setText(reservation.getPsAssignedName());
        service_resource.setText(reservation.getResource());

        mDatabaseReservations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Reservation res = snapshot.getValue(Reservation.class);

                }
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
                    if (user.getId().equals(reservation.getPSAssignedId())) {
                        mPsUser = user;
                        //mUserPoints = user.getPoints();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseServices.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Service service = snapshot.getValue(Service.class);
                    if(reservation.getServiceId().equals(service.getId())){
                        mService = service;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rowView;
    }


}
