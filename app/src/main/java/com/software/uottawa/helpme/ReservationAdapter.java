package com.software.uottawa.helpme;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
    private User mUser;
    private Service mService;
    private DatabaseReference mDatabaseReservations;
    private DatabaseReference mDatabaseServices;
    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAuth;

    private String mUserId;

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
        TextView service_resource = rowView.findViewById(R.id.resource);
        TextView rating = rowView.findViewById(R.id.rating);


        date.setText(reservation.getDate());
        System.out.println(date);
        service_title.setText(reservation.getServiceName());
        service_resource.setText(reservation.getResource());
        rating.setText("0");


        mDatabaseReservations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Reservation reservation = snapshot.getValue(Reservation.class);
                    reservation.getServiceId();
                   /* if(reservation.get){

                    }*/
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
                    //if (user.getId().equals(mUserId))
                        //mUserPoints = user.getPoints();
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
                    //if()
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rowView;
    }


}
