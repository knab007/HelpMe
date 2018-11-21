package com.software.uottawa.helpme;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.List;

/**
 * Created by Samir T.
 */

public class ServiceAdapter extends ArrayAdapter<Service> {

    private final List<Service> mServiceList;

    private DatabaseReference mDatabaseServices;
    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAuth;

    private String mUserId;
    private int mUserPoints;

    public ServiceAdapter(Context context, List<Service> serviceList) {
        super(context, R.layout.service_list_item_view, serviceList);
        mServiceList = serviceList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rowView = LayoutInflater.from(getContext()).inflate(R.layout.service_list_item_view, parent, false);

        final Service service = mServiceList.get(position);

        mDatabaseServices = FirebaseDatabase.getInstance().getReference("services");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();
        mUserId = mAuth.getCurrentUser().getUid();

        ImageView serviceIcon = rowView.findViewById(R.id.service_icon);
        TextView serviceTitle = rowView.findViewById(R.id.service_title);
        TextView serviceResource = rowView.findViewById(R.id.service_resource);

        serviceIcon.setImageResource(R.drawable.service_icon);
        serviceTitle.setText(service.getTitle());
        serviceResource.setText(service.getResource());

        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user.getId().equals(mUserId))
                        mUserPoints = user.getPoints();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rowView;
    }
}
