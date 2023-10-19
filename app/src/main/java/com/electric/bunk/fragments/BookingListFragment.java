//Arnav's Code - T00630177

package com.electric.bunk.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.electric.bunk.Adapter.AdapterBookings;
import com.electric.bunk.R;
import com.electric.bunk.SharePrefrence.App;
import com.electric.bunk.SharePrefrence.AppConstants;
import com.electric.bunk.pojoClasses.GetBookingsListPojo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.omninos.util_data.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class BookingListFragment extends Fragment implements View.OnClickListener {

    private View view;
    private TextView heading;
    private RecyclerView bookingRecyclerView;
    private AdapterBookings adapterBookings;
    private List<GetBookingsListPojo> list = new ArrayList<>();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(AppConstants.BOOKINGS);
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private String pathUid = "vendorUid";

    public BookingListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_booking_list, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        findIds(view);

        list.clear();

        adapterBookings = new AdapterBookings(getActivity(), list, new AdapterBookings.Select() {
            @Override
            public void click(int poisition) {
                App.getSinltonPojo().setGetBookingsListPojo(list.get(poisition));
                Navigation.findNavController(view).navigate(R.id.action_bookingListFragment_to_bookingDetailsFragment);
            }
        });
        bookingRecyclerView.setAdapter(adapterBookings);

        CommonUtils.showProgress(getActivity());

        if (App.getAppPreference().GetString(AppConstants.USER_TYPE).equalsIgnoreCase(AppConstants.USER_TYPE)) {
            pathUid = "userUid";
        }

        Query query = databaseReference.orderByChild(pathUid).equalTo(firebaseUser.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CommonUtils.dismissProgress();

                if(!snapshot.exists()){

                        Toast.makeText(getActivity(), "No List found", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                CommonUtils.dismissProgress();

            }
        });

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if (snapshot.exists()) {
                    GetBookingsListPojo getBookingsListPojo = snapshot.getValue(GetBookingsListPojo.class);
                    list.add(getBookingsListPojo);

                    adapterBookings.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private void findIds(View view) {

        heading = view.findViewById(R.id.tv_head);
        heading.setText(getString(R.string.myBookings));

        bookingRecyclerView = view.findViewById(R.id.rv_bookings);

        view.findViewById(R.id.img_back).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                getActivity().onBackPressed();
                break;
        }
    }
}