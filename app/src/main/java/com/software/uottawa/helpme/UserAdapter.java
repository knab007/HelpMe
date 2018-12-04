package com.software.uottawa.helpme;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

public class UserAdapter extends ArrayAdapter<User> {

    private List<User> mUserList;
    private String days;
    private DatabaseReference mDatabaseServices;
    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAuth;

    private String mUserId;
    private int mUserPoints;

    public UserAdapter(Context context, List<User> userList) {
        super(context, R.layout.user_list_item_view, userList);
        this.mUserList = userList;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rowView = LayoutInflater.from(getContext()).inflate(R.layout.user_list_item_view, parent, false);

        final User user = mUserList.get(position);

        mDatabaseServices = FirebaseDatabase.getInstance().getReference("services");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();
        mUserId = mAuth.getCurrentUser().getUid();

        ImageView providerIcon = rowView.findViewById(R.id.provider_icon);
        TextView providerFirstName = rowView.findViewById(R.id.first_name);
        TextView providerLastName = rowView.findViewById(R.id.last_name);
        TextView providerEmail = rowView.findViewById(R.id.email);
        if(user.getDisponibility()!=null) {
            days = "[ ";
            for (String day : user.getDisponibility()) {
                days = days +day+" ";
            }
            days = days +"]";
        }
        TextView providerDisponibility = rowView.findViewById(R.id.disponibility);

        providerIcon.setImageResource(R.drawable.account);
        System.out.println(providerFirstName);
        providerFirstName.setText(user.getFirstName());
        providerLastName.setText(user.getLastName());
        providerEmail.setText(user.getEmail());
        providerDisponibility.setText(days);

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

        mDatabaseServices.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Service service = snapshot.getValue(Service.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rowView;
    }


}
