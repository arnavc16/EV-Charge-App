//Shubhams Code - T00619152

package com.electric.bunk.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.electric.bunk.MainActivity;
import com.electric.bunk.R;
import com.electric.bunk.SharePrefrence.App;
import com.electric.bunk.SharePrefrence.AppConstants;
import com.electric.bunk.pojoClasses.RegisterPojo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infideap.drawerbehavior.Advance3DDrawerLayout;
import com.omninos.util_data.CommonUtils;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private CardView card_user, card_provider;
    private View view;
    private String loginType;
    private Advance3DDrawerLayout drawer;
    private ImageView img_back;
    private TextView tv_help;
    CardView tv_search, tv_createBunk;
    private TextView tv_name, tv_email;
    LinearLayout tv_myFeedbacks;
    private DatabaseReference databaseReference;

    private String userType = App.getAppPreference().GetString(AppConstants.USER_TYPE);

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    private String uid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference(userType);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            uid = firebaseUser.getUid();
        }


        findids();

        setups();


        return view;
    }

    private void setups() {

        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    RegisterPojo registerPojo = snapshot.getValue(RegisterPojo.class);

                    tv_name.setText(registerPojo.getName());
                    tv_email.setText(firebaseUser.getEmail());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                tv_name.setText(getString(R.string.app_name));
                tv_email.setVisibility(View.GONE);
            }
        });

//        action_homeFragment_to_searchBunkFragment
    }

    private void findids() {


        tv_name = view.findViewById(R.id.tv_name);
        tv_email = view.findViewById(R.id.tv_email);

        tv_help = view.findViewById(R.id.tv_help);
        tv_search = view.findViewById(R.id.tv_search);
        tv_createBunk = view.findViewById(R.id.tv_createBunk);
        img_back = view.findViewById(R.id.img_back);
        card_user = view.findViewById(R.id.card_user);
        card_provider = view.findViewById(R.id.card_provider);
        tv_myFeedbacks = view.findViewById(R.id.tv_myFeedbacks);
        loginType = App.getSinltonPojo().getLoginType();
        drawer = (Advance3DDrawerLayout) view.findViewById(R.id.drawer_layout);

        img_back.setImageResource(R.drawable.ic_baseline_menu_24);
        img_back.setOnClickListener(this);
        tv_createBunk.setOnClickListener(this);
        tv_help.setOnClickListener(this);

        tv_search.setOnClickListener(this);

        drawer.setViewScale(Gravity.START, 0.96f);
        drawer.setRadius(Gravity.START, 20);
        drawer.setViewElevation(Gravity.START, 8);
        drawer.setViewRotation(Gravity.START, 15);


        view.findViewById(R.id.tv_logout).setOnClickListener(this);
        view.findViewById(R.id.tv_profile).setOnClickListener(this);
        view.findViewById(R.id.tv_aboutUs).setOnClickListener(this);
        view.findViewById(R.id.tv_changeAuth).setOnClickListener(this);
        view.findViewById(R.id.tv_bookings2).setOnClickListener(this);
        view.findViewById(R.id.tv_bookings).setOnClickListener(this);
        view.findViewById(R.id.tv_home).setOnClickListener(this);
        view.findViewById(R.id.tv_myFeedbacks).setOnClickListener(this);
        view.findViewById(R.id.tv_myBunk).setOnClickListener(this);

        if (App.getAppPreference().GetString(AppConstants.USER_TYPE).equalsIgnoreCase(AppConstants.PROVIDER_TYPE)) {
            card_provider.setVisibility(View.VISIBLE);
            tv_myFeedbacks.setVisibility(View.VISIBLE);
        } else {
            card_user.setVisibility(View.VISIBLE);
            tv_myFeedbacks.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                drawer.openDrawer(Gravity.START);
                break;

            case R.id.tv_home:
                drawer.closeDrawer(Gravity.START);
                break;

            case R.id.tv_profile:
                closeDrawer();
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_profileFragment);
                break;
            case R.id.tv_changeAuth:
                closeDrawer();
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_changeAutherizationFragment);
                break;

            case R.id.tv_search:
                closeDrawer();
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_searchBunkFragment);
                break;
            case R.id.tv_createBunk:
                closeDrawer();
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_createBunkFragment);
                break;

            case R.id.tv_help:
                closeDrawer();
                App.getSinltonPojo().setWebViewLink(AppConstants.HELP);
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_helpFragment);
                break;
            case R.id.tv_aboutUs:
                closeDrawer();
                App.getSinltonPojo().setWebViewLink(AppConstants.ABOUTUS);
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_helpFragment);
                break;

            case R.id.tv_logout:
                closeDrawer();
                CommonUtils.showProgress(getActivity());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        CommonUtils.dismissProgress();
                        FirebaseAuth.getInstance().signOut();
                        App.getAppPreference().Logout();

                        startActivity(new Intent(getActivity(), MainActivity.class));
                        getActivity().finishAffinity();

                    }
                }, 500);
                break;

            case R.id.tv_bookings2:
            case R.id.tv_bookings:
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_bookingListFragment);
                break;

            case R.id.tv_myFeedbacks:
                closeDrawer();
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_myFeedbacksFragment);

                break;
            case R.id.tv_myBunk:
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_myBunksListFragment);

                break;

        }
    }

    private void closeDrawer() {
        drawer.closeDrawer(Gravity.LEFT);
    }
}