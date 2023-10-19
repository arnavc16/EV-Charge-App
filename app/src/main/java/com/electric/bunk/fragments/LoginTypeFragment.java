//Arnavs Code - T00630177

package com.electric.bunk.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.electric.bunk.R;
import com.electric.bunk.SharePrefrence.App;
import com.electric.bunk.SharePrefrence.AppConstants;

public class LoginTypeFragment extends Fragment implements View.OnClickListener {

    private View view;
    private TextView tv_provider, tv_user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login_type, container, false);
        findids();
        setups();
        return view;
    }

    private void setups() {


    }

    private void findids() {

        tv_provider = view.findViewById(R.id.tv_provider);
        tv_user = view.findViewById(R.id.tv_user);
        tv_provider.setOnClickListener(this);
        tv_user.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_provider:
                App.getSinltonPojo().setLoginType(AppConstants.PROVIDER_TYPE);
                break;

            case R.id.tv_user:
                App.getSinltonPojo().setLoginType(AppConstants.USER_TYPE);
                break;

        }
        Navigation.findNavController(getView()).navigate(R.id.action_loginTypeFragment_to_loginFragment);
    }
}