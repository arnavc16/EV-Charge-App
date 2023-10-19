//Shubhams Code - T00619152

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

import com.electric.bunk.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.omninos.util_data.CommonUtils;

import static android.content.ContentValues.TAG;


public class ChangeAutherizationFragment extends Fragment implements View.OnClickListener {

    private View view;
    private TextView heading;

    private String emailStr, passwordStr, newPassStr;

    private FirebaseAuth mAuth;
    private String uid;
    private EditText emailEt, passEt, et_Newpassword;
    private FirebaseUser user;

    public ChangeAutherizationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_change_autherization, container, false);

        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();

        findids();

        emailEt.setText(user.getEmail());

        return view;
    }

    private void findids() {
        emailEt = view.findViewById(R.id.et_email);
        passEt = view.findViewById(R.id.et_password);
        et_Newpassword = view.findViewById(R.id.et_Newpassword);

        heading = view.findViewById(R.id.tv_head);
        heading.setText(getString(R.string.update_credentials));

        view.findViewById(R.id.tv_user).setOnClickListener(this);
        view.findViewById(R.id.img_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_user:
                getData();
                break;

            case R.id.img_back:
                getActivity().onBackPressed();
                break;
        }
    }


    private void getData() {

        emailStr = emailEt.getText().toString();
        passwordStr = passEt.getText().toString();
        newPassStr = et_Newpassword.getText().toString();


        if (emailStr.isEmpty() || passwordStr.isEmpty() || passwordStr.length() < 8 || newPassStr.isEmpty() || newPassStr.length() < 8 || newPassStr.equals(passwordStr)) {

            if (emailStr.isEmpty()) {
                emailEt.setError(getString(R.string.fieldRequired));
            }
            if (passwordStr.isEmpty() || passwordStr.length() < 8) {
                if (passwordStr.isEmpty()) {
                    passEt.setError(getString(R.string.fieldRequired));
                } else {
                    passEt.setError(getString(R.string.atleas8char));
                }

            }

            if (newPassStr.isEmpty() || newPassStr.length() < 8 || newPassStr.equals(passwordStr)) {
                if (newPassStr.isEmpty()) {
                    et_Newpassword.setError(getString(R.string.fieldRequired));
                } else if (newPassStr.equals(passwordStr)) {
                    et_Newpassword.setError(getString(R.string.oldNewcheck));
                } else {
                    et_Newpassword.setError(getString(R.string.atleas8char));
                }

            }

        } else {
            updatePassword();
        }

    }

    private void updatePassword() {

        CommonUtils.showProgress(getActivity());

        AuthCredential credential = EmailAuthProvider
                .getCredential(emailStr, passwordStr);

// Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPassStr).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    CommonUtils.dismissProgress();
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Password updated");
                                        Toast.makeText(getActivity(), getString(R.string.passSuccessFully), Toast.LENGTH_SHORT).show();
                                        getActivity().onBackPressed();
                                    } else {
                                        Log.d(TAG, "Error password not updated");
                                        Toast.makeText(getActivity(), getString(R.string.somethingWentWrong), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            CommonUtils.dismissProgress();
                            Toast.makeText(getActivity(), getString(R.string.authorizationFailed), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Error auth failed");
                        }
                    }
                });
    }

}