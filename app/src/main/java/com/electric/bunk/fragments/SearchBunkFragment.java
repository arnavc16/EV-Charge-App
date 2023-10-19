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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.electric.bunk.Adapter.AdapterBunk;
import com.electric.bunk.R;
import com.electric.bunk.SharePrefrence.App;
import com.electric.bunk.SharePrefrence.AppConstants;
import com.electric.bunk.pojoClasses.GetBunkListPojo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.omninos.util_data.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchBunkFragment extends Fragment implements View.OnClickListener{
    private AdapterBunk adapterBunk;
    private RecyclerView rc_bunk;
    private View view;
    private TextView tv_head;
    private ImageView img_back;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(AppConstants.BUNK_STATION).child("11WWKEAq9KXLGBWN6kTKEWsWUDP2");
    private List<GetBunkListPojo> list = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_LOCATION = 1;

    private LocationManager locationManager;
    private String  latStr = "", longStr = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_search_bunk, container, false);

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

            findids();


            adapterBunk = new AdapterBunk(getActivity(), list, new AdapterBunk.Select() {
                @Override
                public void click(int poisition) {
                    App.getSinltonPojo().setGetBunkListPojo(list.get(poisition));
                    Navigation.findNavController(view).navigate(R.id.action_searchBunkFragment_to_bunkDetailFragment);
                }
            },latStr,longStr);
            rc_bunk.setAdapter(adapterBunk);




            CommonUtils.showProgress(getActivity());

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    CommonUtils.dismissProgress();


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    CommonUtils.dismissProgress();
                }
            });

            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    if (snapshot.exists()) {
                        Log.i("added", snapshot.toString());
                        GetBunkListPojo getBunkListPojo = snapshot.getValue(GetBunkListPojo.class);
                        list.add(getBunkListPojo);
                        adapterBunk.notifyDataSetChanged();
                    }else {
                        Toast.makeText(getActivity(), "No list found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.i("change", snapshot.toString());

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    Log.i("removed", snapshot.toString());
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.i("move", snapshot.toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.i("error", error.getMessage());

                }
            });



        }
        return view;
    }


    private void findids() {
        img_back = view.findViewById(R.id.img_back);
        tv_head = view.findViewById(R.id.tv_head);
        rc_bunk = view.findViewById(R.id.rc_bunk);
        tv_head.setText("Bunks");

        img_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                getActivity().onBackPressed();
                break;
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
                        Log.i("lat", "success");

                    }else {
                        Log.i("lat", "error");
                    }

                   adapterBunk.notifyDataSetChanged();
//                    Toast.makeText(getActivity(), latitudeStr+" "+longitudeStr, Toast.LENGTH_SHORT).show();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }


}