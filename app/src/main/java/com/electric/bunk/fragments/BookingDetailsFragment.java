//Arnav's Code - T00630177

package com.electric.bunk.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.electric.bunk.R;
import com.electric.bunk.SharePrefrence.App;
import com.electric.bunk.SharePrefrence.AppConstants;
import com.electric.bunk.pojoClasses.GetBookingsListPojo;
import com.electric.bunk.pojoClasses.RegisterPojo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.omninos.util_data.CommonUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class BookingDetailsFragment extends Fragment implements View.OnClickListener {


    private View view;
    private TextView heading;
    private TextView bunkName, addressName, bookingDateTime, tv_giveFeedback;

    private AlertDialog dailogbox;
    private GetBookingsListPojo getBookingsListPojo = App.getSinltonPojo().getGetBookingsListPojo();
    private String reviewStr = "";
    private Float ratePoints;
    private DatabaseReference databaseReference;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private RegisterPojo registerPojo;
    private String dateStr, timeStr;
    SimpleDateFormat format1;

    public BookingDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_booking_details, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        findIDs(view);

        setData();

        databaseReference = FirebaseDatabase.getInstance().getReference(App.getAppPreference().GetString(AppConstants.USER_TYPE));


        databaseReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    registerPojo = snapshot.getValue(RegisterPojo.class);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }

    private void setData() {
        bunkName.setText(getBookingsListPojo.getBunkName());
        bookingDateTime.setText(getString(R.string.dateTime) + ": " + getBookingsListPojo.getDate() + " " + getString(R.string.at) + " " + getBookingsListPojo.getTime());
        addressName.setText(getBookingsListPojo.getAddressBunk());


        if (getBookingsListPojo.getFeedbackStatus() == 1|| App.getAppPreference().GetString(AppConstants.USER_TYPE).equalsIgnoreCase(AppConstants.PROVIDER_TYPE)) {
            tv_giveFeedback.setVisibility(View.GONE);
        }
    }

    private void findIDs(View view) {


        bunkName = view.findViewById(R.id.tv_nameBunk);
        addressName = view.findViewById(R.id.tv_address);
        bookingDateTime = view.findViewById(R.id.tv_bookingDateTime);
        heading = view.findViewById(R.id.tv_head);
        heading.setText(getString(R.string.bookingDetails));

        view.findViewById(R.id.img_back).setOnClickListener(this);
        tv_giveFeedback = view.findViewById(R.id.tv_giveFeedback);
        tv_giveFeedback.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                getActivity().onBackPressed();
                break;

            case R.id.tv_giveFeedback:
                dailog();
                break;
        }
    }


    private void dailog() {

        databaseReference = FirebaseDatabase.getInstance().getReference(AppConstants.RATING_N_REVIEWS);

        final View confirmdailog = LayoutInflater.from(getActivity()).inflate(R.layout.dailog_feedback, null);
        dailogbox = new AlertDialog.Builder(getActivity()).create();
        dailogbox.setCancelable(false);
        dailogbox.setCanceledOnTouchOutside(false);
        dailogbox.setView(confirmdailog);

        EditText review = confirmdailog.findViewById(R.id.et_rating);

        RatingBar ratingBar = confirmdailog.findViewById(R.id.ratingBar);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratePoints = rating;
            }
        });

        ratePoints=ratingBar.getRating();

        confirmdailog.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                Date today = new Date();

                format1 = new SimpleDateFormat("yyyy-MM-dd");
                dateStr = format1.format(today);


                format1 = new SimpleDateFormat("HH:mm");
                timeStr = format1.format(today);

                reviewStr = review.getText().toString();
                if (reviewStr.isEmpty()) {
                    review.setError(getString(R.string.writeSomething));
                } else {
                    CommonUtils.showProgress(getActivity());
                    HashMap hashMap = new HashMap();
                    hashMap.put("userName", registerPojo.getName());
                    hashMap.put("userUid", firebaseUser.getUid());
                    hashMap.put("vendorUid", getBookingsListPojo.getVendorUid());
                    hashMap.put("rating", String.valueOf(ratePoints));
                    hashMap.put("comment", reviewStr);
                    hashMap.put("timeStamp", String.valueOf(new Date().getTime()));
                    hashMap.put("date", dateStr);
                    hashMap.put("time", timeStr);
                    hashMap.put("bookingId", getBookingsListPojo.getTimeStamp());

                    databaseReference.push().setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {


                            changeReviewStatus();
                            Toast.makeText(getActivity(), getString(R.string.reviewSuccess), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            CommonUtils.dismissProgress();
                            Toast.makeText(getActivity(), getString(R.string.somethingWentWrong), Toast.LENGTH_SHORT).show();
                        }
                    });


                }


            }
        });
        confirmdailog.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {


                dailogbox.dismiss();

            }
        });

        dailogbox.show();
    }

    private void changeReviewStatus() {

        databaseReference = FirebaseDatabase.getInstance().getReference(AppConstants.BOOKINGS);

        databaseReference.child(getBookingsListPojo.getTimeStamp()).child("feedbackStatus").setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dailogbox.dismiss();
                tv_giveFeedback.setVisibility(View.GONE);
                CommonUtils.dismissProgress();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonUtils.dismissProgress();
            }
        });


    }

}