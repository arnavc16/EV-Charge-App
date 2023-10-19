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

import com.electric.bunk.R;
import com.electric.bunk.SharePrefrence.App;
import com.electric.bunk.SharePrefrence.AppConstants;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.omninos.util_data.CommonUtils;

import java.util.HashMap;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    private View view;
    private TextView heading;

    private EditText nameEt, emailEt, phoneNumberEt;
    private FirebaseAuth mAuth;
    private String emailStr = "", nameStr, phoneStr;
    private DatabaseReference databaseReference;

    private String userType = App.getAppPreference().GetString(AppConstants.USER_TYPE);

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    private String uid;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference(userType);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            uid = firebaseUser.getUid();
        }


        findIds(view);
        CommonUtils.showProgress(getActivity());

        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CommonUtils.dismissProgress();
                if (snapshot.exists()) {
                    RegisterPojo registerPojo = snapshot.getValue(RegisterPojo.class);
                    nameEt.setText(registerPojo.getName());
                    phoneNumberEt.setText(registerPojo.getPhoneNumber());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                CommonUtils.dismissProgress();
            }
        });

        return view;
    }

    private void findIds(View view) {

        heading = view.findViewById(R.id.tv_head);
        heading.setText(R.string.myProfile);

        view.findViewById(R.id.img_back).setOnClickListener(this);

        phoneNumberEt = view.findViewById(R.id.et_phoneNum);
        nameEt = view.findViewById(R.id.et_name);
        view.findViewById(R.id.tv_continue).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                getActivity().onBackPressed();
                break;

            case R.id.tv_continue:

                nameStr = nameEt.getText().toString();
                phoneStr = phoneNumberEt.getText().toString();


                if (nameStr.isEmpty() || phoneStr.isEmpty()) {

                    if (nameStr.isEmpty()) {
                        nameEt.setError(getString(R.string.fieldRequired));
                    }
                    if (phoneStr.isEmpty()) {
                        phoneNumberEt.setError(getString(R.string.fieldRequired));
                    }


                } else {
                    updateProfile(firebaseUser);
                }
                break;
        }
    }

    private void updateProfile(FirebaseUser user) {

CommonUtils.showProgress(getActivity());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", nameStr);
        hashMap.put("phoneNumber", phoneStr);
        hashMap.put("email", user.getEmail());
        hashMap.put("uid", user.getUid());
        hashMap.put("userType", userType);
        hashMap.put("regId", FirebaseInstanceId.getInstance().getToken());

        databaseReference.child(user.getUid()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                CommonUtils.dismissProgress();
//                startIntent();
                getActivity().onBackPressed();
                Toast.makeText(getActivity(), getString(R.string.profileUpdateSuccess), Toast.LENGTH_SHORT).show();
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