//Arnavs Code - T00630177

package com.electric.bunk.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.electric.bunk.R;
import com.electric.bunk.SharePrefrence.App;
import com.electric.bunk.SharePrefrence.AppConstants;

public class SplashFragment extends Fragment {

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_splash, container, false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                // This method will be executed once the timer is over

                if (App.getAppPreference().GetString(AppConstants.USER_TYPE).equalsIgnoreCase(AppConstants.USER_TYPE) || App.getAppPreference().GetString(AppConstants.USER_TYPE).equalsIgnoreCase(AppConstants.PROVIDER_TYPE)) {
                    Navigation.findNavController(view).navigate(R.id.action_splashFragment_to_homeFragment);

                }else {
                    Navigation.findNavController(view).navigate(R.id.action_splashFragment_to_loginTypeFragment);
                }

            }
        }, 3000);
    }
}