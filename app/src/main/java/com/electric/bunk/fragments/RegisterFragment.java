//Arnavs Code - T00630177

package com.electric.bunk.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.electric.bunk.R;
import com.electric.bunk.SharePrefrence.App;
import com.electric.bunk.SharePrefrence.AppConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.omninos.util_data.CommonUtils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

public class RegisterFragment extends Fragment implements View.OnClickListener {
    private TextView tv_continue;
    private View view;

    private EditText nameEt, emailEt, phoneNumberEt, passwordEt;
    private FirebaseAuth mAuth;
    private String emailStr = "", passStr = "", nameStr, phoneStr;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(App.getSinltonPojo().getLoginType());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_register, container, false);
        findids();

        mAuth = FirebaseAuth.getInstance();

        setups();
        return view;
    }

    private void setups() {

    }

    private void findids() {
        emailEt = view.findViewById(R.id.et_email);
        phoneNumberEt = view.findViewById(R.id.et_phoneNum);
        nameEt = view.findViewById(R.id.et_name);
        passwordEt = view.findViewById(R.id.et_password);


        tv_continue = view.findViewById(R.id.tv_continue);
        tv_continue.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_continue:

                emailStr = emailEt.getText().toString();
                passStr = passwordEt.getText().toString();
                nameStr = nameEt.getText().toString();
                phoneStr = phoneNumberEt.getText().toString();


                if (!emailValidator(emailStr) || emailStr.isEmpty() || passStr.isEmpty() || passStr.length() < 8 || nameStr.isEmpty() || phoneStr.isEmpty()) {

                    if (!emailValidator(emailStr) || emailStr.isEmpty()) {
                        if (emailStr.isEmpty()) {
                            emailEt.setError(getString(R.string.fieldRequired));
                        } else {
                            emailEt.setError(getString(R.string.proparFormat));
                        }


                    }

                    if (passStr.isEmpty()) {
                        passwordEt.setError(getString(R.string.fieldRequired));
                    } else {
                        passwordEt.setError(getString(R.string.atleast8char));
                    }

                    if (nameStr.isEmpty()) {
                        nameEt.setError(getString(R.string.fieldRequired));
                    }
                    if (phoneStr.isEmpty()) {
                        phoneNumberEt.setError(getString(R.string.fieldRequired));
                    }

                } else {
                    singUp();
                }
                break;
        }

    }


    private void singUp() {
        CommonUtils.showProgress(getActivity());
        mAuth.createUserWithEmailAndPassword(emailStr, passStr)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            createUserOnFirebase(user);


                        } else {
                            CommonUtils.dismissProgress();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

    public boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }


    private void createUserOnFirebase(FirebaseUser user) {


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", nameStr);
        hashMap.put("phoneNumber", phoneStr);
        hashMap.put("email", user.getEmail());
        hashMap.put("uid", user.getUid());
        hashMap.put("userType", App.getSinltonPojo().getLoginType());
        hashMap.put("regId", FirebaseInstanceId.getInstance().getToken());

        databaseReference.child(user.getUid()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                CommonUtils.dismissProgress();
                App.getAppPreference().SaveString(AppConstants.USER_TYPE, App.getSinltonPojo().getLoginType());

                Navigation.findNavController(getView()).navigate(R.id.action_registerFragment_to_homeFragment);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonUtils.dismissProgress();
            }
        });
    }


}