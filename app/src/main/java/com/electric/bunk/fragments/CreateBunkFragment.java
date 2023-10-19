//Shubhams Code - T00619152

package com.electric.bunk.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.electric.bunk.R;
import com.electric.bunk.SharePrefrence.AppConstants;
import com.electric.bunk.pojoClasses.BunkDetailModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.omninos.util_data.CommonUtils;

import java.util.HashMap;

public class CreateBunkFragment extends Fragment implements View.OnClickListener {
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
        tv_head.setText("Create Bunk");

        view.findViewById(R.id.tv_submit).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.img_back:

                getActivity().onBackPressed();
                break;

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
                    hashMap.put("lati", latStr);
                    hashMap.put("longi", longStr);
                    hashMap.put("uid", uid);
                    hashMap.put("regId", FirebaseInstanceId.getInstance().getToken());


                    databaseReference.push().setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "Submitted", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


                break;
        }
    }
}