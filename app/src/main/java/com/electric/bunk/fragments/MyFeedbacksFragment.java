//Shubhams Code - T00619152

package com.electric.bunk.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.electric.bunk.Adapter.AdapterReviews;
import com.electric.bunk.R;
import com.electric.bunk.SharePrefrence.AppConstants;
import com.electric.bunk.pojoClasses.GetReviewsPojo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.omninos.util_data.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class MyFeedbacksFragment extends Fragment implements View.OnClickListener {

    private View view;private DatabaseReference databaseReference;
    private String pathUid = "vendorUid", uid; private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private List<GetReviewsPojo> list = new ArrayList<>();
    private AdapterReviews adapterReviews;
    private RecyclerView rv_reviews;
    private TextView heading;


    public MyFeedbacksFragment() {
        // Required empty public constructor
    }

       @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_my_feedbacks, container, false);
           databaseReference = FirebaseDatabase.getInstance().getReference(AppConstants.RATING_N_REVIEWS);


           firebaseAuth = FirebaseAuth.getInstance();

           firebaseUser = firebaseAuth.getCurrentUser();
           if (firebaseUser != null) {
               uid = firebaseUser.getUid();
           }

           findIds();


           getFeebacks();

        return view;
    }

    private void findIds() {
        rv_reviews=view.findViewById(R.id.rv_reviews);
        heading=view.findViewById(R.id.tv_head);
        heading.setText(getString(R.string.my_feedback));

        view.findViewById(R.id.img_back).setOnClickListener(this);
    }

    private void getFeebacks() {

        adapterReviews = new AdapterReviews(getActivity(), list, new AdapterReviews.Select() {
            @Override
            public void click(int poisition) {

            }
        });

        rv_reviews.setAdapter(adapterReviews);

        CommonUtils.showProgress(getActivity());

        Query query = databaseReference.orderByChild(pathUid).equalTo(uid);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
CommonUtils.dismissProgress();
                if (snapshot.exists()) {
                    Log.i("abc", snapshot.getValue().toString());
                    GetReviewsPojo getReviewsPojo = snapshot.getValue(GetReviewsPojo.class);
                    list.add(getReviewsPojo);
//                    if (list.size() > 0 || list != null) {
//                        calculaterating();
//                    }

                    adapterReviews.notifyDataSetChanged();
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


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                getActivity().onBackPressed();
                break;
        }
    }
}