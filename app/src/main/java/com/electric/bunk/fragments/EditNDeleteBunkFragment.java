//Arnavs Code - T00630177

package com.electric.bunk.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.electric.bunk.R;
import com.electric.bunk.SharePrefrence.App;
import com.electric.bunk.SharePrefrence.AppConstants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.omninos.util_data.CommonUtils;

import java.util.HashMap;

public class EditNDeleteBunkFragment extends Fragment implements View.OnClickListener {
    private TextView tv_head;
    private ImageView img_back;
    private View view;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(AppConstants.BUNK_STATION);
    private EditText bunkNameEt, addressEt, slotNoEt;
    private String bunkStr, addressStr, slotsStr, latStr = "", longStr = "";
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private String uid;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_LOCATION = 1;
    private AlertDialog dailogbox;

    private LocationManager locationManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_bunk, container, false);


        firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            uid = firebaseUser.getUid();
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);


        databaseReference = FirebaseDatabase.getInstance().getReference(AppConstants.BUNK_STATION).child(uid);


        findids();
        setups();

        return view;
    }


    private void setups() {
        addressEt.setText(App.getSinltonPojo().getGetBunkListPojo().getBunkAddress());
        slotNoEt.setText(App.getSinltonPojo().getGetBunkListPojo().getChargingSlots());
        bunkNameEt.setText(App.getSinltonPojo().getGetBunkListPojo().getBunkName());

    }

    private void OnGPS() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {

            fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        latStr = String.valueOf(location.getLongitude());
                        longStr = String.valueOf(location.getLatitude());

                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {
            getLocation();
        }
    }

    private void findids() {
        bunkNameEt = view.findViewById(R.id.et_bunkName);
        addressEt = view.findViewById(R.id.et_address);
        slotNoEt = view.findViewById(R.id.et_totalSlot);


        tv_head = view.findViewById(R.id.tv_head);
        img_back = view.findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        tv_head.setText("Edit Bunk");


        view.findViewById(R.id.ll_new).setVisibility(View.VISIBLE);
        view.findViewById(R.id.tv_submit).setVisibility(View.GONE);
        view.findViewById(R.id.tv_delet).setOnClickListener(this);
        view.findViewById(R.id.tv_update).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.img_back:

                getActivity().onBackPressed();
                break;

            case R.id.tv_delet:
dailog();
                break;

            case R.id.tv_update:
            case R.id.tv_submit:

                bunkStr = bunkNameEt.getText().toString();
                slotsStr = slotNoEt.getText().toString();
                addressStr = addressEt.getText().toString();

                if (bunkStr.isEmpty() || slotsStr.isEmpty() || addressStr.isEmpty()) {

                    if (bunkStr.isEmpty()) {
                        bunkNameEt.setError(getString(R.string.fieldRequired));
                    }
                    if (slotsStr.isEmpty()) {
                        slotNoEt.setError(getString(R.string.fieldRequired));
                    }
                    if (addressStr.isEmpty()) {
                        addressEt.setError(getString(R.string.fieldRequired));
                    }

                } else {

                    HashMap hashMap = new HashMap();
                    hashMap.put("bunkName", bunkStr);
                    hashMap.put("bunkAddress", addressStr);
                    hashMap.put("chargingSlots", slotsStr);
                    hashMap.put("lati", App.getSinltonPojo().getGetBunkListPojo().getLati());
                    hashMap.put("longi", App.getSinltonPojo().getGetBunkListPojo().getLongi());
                    hashMap.put("uid", App.getSinltonPojo().getGetBunkListPojo().getUid());
                    hashMap.put("regId", App.getSinltonPojo().getGetBunkListPojo().getRegId());

CommonUtils.showProgress(getActivity());
                    databaseReference.child(App.getSinltonPojo().getBunkPath()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            CommonUtils.dismissProgress();
                            Toast.makeText(getActivity(), "Bunk updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            CommonUtils.dismissProgress();

                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


                break;
        }
    }

    private void dailog() {
        final View confirmdailog = LayoutInflater.from(getActivity()).inflate(R.layout.dailog_confirm_booking, null);
        dailogbox = new AlertDialog.Builder(getActivity()).create();
        dailogbox.setCancelable(false);
        dailogbox.setCanceledOnTouchOutside(false);
        dailogbox.setView(confirmdailog);

        TextView tv_heading,btn_yes;
        tv_heading=confirmdailog.findViewById(R.id.tv_heading);
        btn_yes=confirmdailog.findViewById(R.id.btn_yes);
        tv_heading.setText("Are you sure you want to delete this Bunk?");
        btn_yes.setText("Delete");
        confirmdailog.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                CommonUtils.showProgress(getActivity());
                databaseReference.child(App.getSinltonPojo().getBunkPath()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.dismissProgress();
                        dailogbox.dismiss();

                        Toast.makeText(getActivity(), "Bunk Delete successfully", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        CommonUtils.dismissProgress();
                        dailogbox.dismiss();

                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

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

}