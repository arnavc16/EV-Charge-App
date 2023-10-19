//Arnavs Code - T00630177

package com.electric.bunk.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.electric.bunk.R;
import com.electric.bunk.SharePrefrence.App;
import com.electric.bunk.SharePrefrence.AppConstants;
import com.electric.bunk.pojoClasses.RegisterPojo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.omninos.util_data.CommonUtils;

import java.util.HashMap;

public class LoginFragment extends Fragment implements View.OnClickListener {
    private TextView tv_Register;
    private View view;
    private String emailStr, passwordStr;

    private FirebaseAuth mAuth;
    private boolean validCheck = false;
    private String uid;
    private EditText emailEt, passEt;
    private String nameStr, phoneStr;

    private DatabaseReference databaseReference;
    private FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view==null) {
            view = inflater.inflate(R.layout.fragment_login, container, false);

            mAuth = FirebaseAuth.getInstance();

            findids();
          if (App.getSinltonPojo().getLoginType().equalsIgnoreCase(AppConstants.PROVIDER_TYPE)){
              tv_Register.setVisibility(View.GONE);
          }
            setups();

        }
        return view;
    }

    private void setups() {

    }

    private void findids() {
        emailEt = view.findViewById(R.id.et_email);
        passEt = view.findViewById(R.id.et_password);

        tv_Register = view.findViewById(R.id.tv_Register);
        tv_Register.setOnClickListener(this);

        view.findViewById(R.id.tv_user).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_Register:
                Navigation.findNavController(getView()).navigate(R.id.action_loginFragment_to_registerFragment);

                break;

            case R.id.tv_user:
                getData();
                break;
        }
    }


    private void login() {
        CommonUtils.showProgress(getActivity());
        mAuth.signInWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            user = mAuth.getCurrentUser();


//                            App.getAppPreference().SaveString(AppConstants.USER_TYPE, App.getSinltonPojo().getLoginType());
                            uid = user.getUid();

                            databaseReference = FirebaseDatabase.getInstance().getReference(App.getSinltonPojo().getLoginType());

                            retriveData();


                        } else {
                            CommonUtils.dismissProgress();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    private void getData() {

        emailStr = emailEt.getText().toString();
        passwordStr = passEt.getText().toString();


        if (emailStr.isEmpty() || passwordStr.isEmpty() || passwordStr.length() < 7) {

            if (emailStr.isEmpty()) {
                emailEt.setError(getString(R.string.fieldRequired));
            }
            if (passwordStr.isEmpty() || passwordStr.length() < 7) {
                if (passwordStr.isEmpty()) {
                    passEt.setError(getString(R.string.fieldRequired));
                } else {
                    passEt.setError(getString(R.string.atleas8char));
                }

            }

        } else {
            login();
        }

    }

    private void retriveData() {
        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                
                if (snapshot.exists()) {
                    RegisterPojo registerPojo = snapshot.getValue(RegisterPojo.class);
                    nameStr = (registerPojo.getName());
                    phoneStr = (registerPojo.getPhoneNumber());
                    createUserOnFirebase(user);

//
//                    if(registerPojo.getUserType().equalsIgnoreCase(App.getSinltonPojo().getLoginType())){
//
//                    }else {
//                        CommonUtils.dismissProgress();
//                        Toast.makeText(getActivity(), "Account already exist with other info", Toast.LENGTH_SHORT).show();
//                    }


                }else {
                    CommonUtils.dismissProgress();
                    Toast.makeText(getActivity(), "Account already exist with other info", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createUserOnFirebase(FirebaseUser user) {

        databaseReference.child(user.getUid()).child("regId").setValue(FirebaseInstanceId.getInstance().getToken()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                CommonUtils.dismissProgress();

                App.getAppPreference().SaveString(AppConstants.USER_TYPE, App.getSinltonPojo().getLoginType());


                
//                NavDirections navDirections=LoginFragmentDirections.actionLoginFragmentToHomeFragment();
//                Navigation.findNavController(getActivity().getCurrentFocus()).navigate(navDirections);
                try {

                    Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homeFragment);

                }
                catch (Exception e){

                }
//                Navigation.findNavController(getActivity().getCurrentFocus()).navigate(R.id.action_loginFragment_to_homeFragment);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonUtils.dismissProgress();
            }
        });
    }


}