//Both

package com.electric.bunk.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.electric.bunk.Adapter.AdapterReviews;
import com.electric.bunk.R;
import com.electric.bunk.SharePrefrence.App;
import com.electric.bunk.SharePrefrence.AppConstants;
import com.electric.bunk.pojoClasses.GetBunkListPojo;
import com.electric.bunk.pojoClasses.GetReviewsPojo;
import com.electric.bunk.pojoClasses.RegisterPojo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.omninos.util_data.CommonUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BunkDetailFragment extends Fragment implements View.OnClickListener {

    private TextView tv_head;
    private ImageView img_back;
    private View view;
    private GetBunkListPojo getBunkListPojo = App.getSinltonPojo().getGetBunkListPojo();
    private TextView bunkName, addressName, slotS, tv_distance, tv_viewMap;

    private DatabaseReference databaseReference;
    private DatabaseReference reviewDatabaseReference;

    private FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_LOCATION = 1;
    private String latitudeStr = "", longitudeStr = "";

    private LocationManager locationManager;


    private String userType = App.getAppPreference().GetString(AppConstants.USER_TYPE);

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    private String uid, dateStr = "", timeStr = "";
    SimpleDateFormat format1;
    private AlertDialog dailogbox, dialogDateTime;
    private RecyclerView rv_reviews;
    RegisterPojo registerPojo;
    private String pathUid = "vendorUid";
    private List<GetReviewsPojo> list = new ArrayList<>();
    private AdapterReviews adapterReviews;
    private TextView tv_ratingCount;
    Double aDouble;
    private int dateTimeStatus = 1;

    private String date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bunk_detail, container, false);
        findids();
        setups();

        Date today = new Date();

        format1 = new SimpleDateFormat("yyyy-MM-dd");
        dateStr = format1.format(today);


        format1 = new SimpleDateFormat("HH:mm");
        timeStr = format1.format(today);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);


        firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            uid = firebaseUser.getUid();
        }

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {
            getLocation();
        }


        databaseReference = FirebaseDatabase.getInstance().getReference(userType);


        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    registerPojo = snapshot.getValue(RegisterPojo.class);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                CommonUtils.dismissProgress();
            }
        });


        adapterReviews = new AdapterReviews(getActivity(), list, new AdapterReviews.Select() {
            @Override
            public void click(int poisition) {

            }
        });

        rv_reviews.setAdapter(adapterReviews);

        databaseReference = FirebaseDatabase.getInstance().getReference(AppConstants.RATING_N_REVIEWS);


        if (!userType.equalsIgnoreCase(AppConstants.USER_TYPE)) {
            pathUid = "userUid";
        }

        Query query = databaseReference.orderByChild(pathUid).equalTo(getBunkListPojo.getUid());
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if (snapshot.exists()) {
                    Log.i("abc", snapshot.getValue().toString());
                    GetReviewsPojo getReviewsPojo = snapshot.getValue(GetReviewsPojo.class);
                    list.add(getReviewsPojo);
                    if (list.size() > 0 || list != null) {
                        calculaterating();
                    }

                    adapterReviews.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
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


    @Override
    public void onResume() {
        super.onResume();

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
                        longitudeStr = String.valueOf(location.getLongitude());
                        latitudeStr = String.valueOf(location.getLatitude());

                    } else {
                        longitudeStr = "";
                        latitudeStr = "";
                    }

                    Log.i("myLocation", latitudeStr + "," + longitudeStr);


                    if (!getBunkListPojo.getLati().equalsIgnoreCase("") && !getBunkListPojo.getLongi().equalsIgnoreCase("") && !longitudeStr.isEmpty() && !latitudeStr.isEmpty()) {
                        Location locationStart = new Location("myLocation");
                        locationStart.setLatitude(Double.parseDouble(latitudeStr));
                        locationStart.setLongitude(Double.parseDouble(longitudeStr));

                        Location locationEnd = new Location("bunkLocation");
                        locationEnd.setLatitude(Double.parseDouble(getBunkListPojo.getLati()));
                        locationEnd.setLongitude(Double.parseDouble(getBunkListPojo.getLongi()));

                        aDouble = Double.valueOf(locationStart.distanceTo(locationEnd));

                        LatLng latLngS = new LatLng(locationStart.getLatitude(), locationStart.getLongitude());
                        LatLng latLngE = new LatLng(locationEnd.getLatitude(), locationEnd.getLongitude());


                        aDouble = calculationByDistance(latLngS, latLngE);

                        Log.i("dis", aDouble + "");

                        tv_distance.setText(getString(R.string.distance) + ": " + String.valueOf(aDouble).substring(0, 5) + " Km");
                    } else {
                        tv_distance.setText(getString(R.string.distance) + ": ? Km");

                        if (getBunkListPojo.getLati().equalsIgnoreCase("") && getBunkListPojo.getLongi().equalsIgnoreCase("")) {
                            tv_viewMap.setVisibility(View.GONE);
                        }


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

    private void setups() {
        bunkName.setText(getBunkListPojo.getBunkName());
        slotS.setText(getString(R.string.charging_slot) + ": " + getBunkListPojo.getChargingSlots());
        addressName.setText(getBunkListPojo.getBunkAddress());
    }

    private void findids() {
        tv_head = view.findViewById(R.id.tv_head);
        img_back = view.findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        tv_head.setText("Bunk details");


        bunkName = view.findViewById(R.id.tv_nameBunk);
        addressName = view.findViewById(R.id.tv_address);
        slotS = view.findViewById(R.id.tv_slots);
        rv_reviews = view.findViewById(R.id.rv_reviews);
        tv_distance = view.findViewById(R.id.tv_distance);
        tv_ratingCount = view.findViewById(R.id.tv_ratingCount);

        view.findViewById(R.id.btn_bookNow).setOnClickListener(this);
        tv_viewMap = view.findViewById(R.id.tv_viewMap);
        tv_viewMap.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                getActivity().onBackPressed();
                break;

            case R.id.btn_bookNow:
                dailogdatetime();
                break;

            case R.id.tv_viewMap:
                mapIntent();
                break;
        }
    }

    private void setBookings() {


        databaseReference = FirebaseDatabase.getInstance().getReference(AppConstants.BOOKINGS);

        String timeStamp = String.valueOf(new Date().getTime());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("vendorUid", getBunkListPojo.getUid());
        hashMap.put("userUid", uid);
        hashMap.put("userRegId", FirebaseInstanceId.getInstance().getToken());
        hashMap.put("vendorRegId", getBunkListPojo.getRegId());
        hashMap.put("date", dateStr);
        hashMap.put("time", timeStr);
        hashMap.put("bunkName", getBunkListPojo.getBunkName());
        hashMap.put("addressBunk", getBunkListPojo.getBunkAddress());
        hashMap.put("status", 0);
        hashMap.put("feedbackStatus", 0);
        hashMap.put("timeStamp", timeStamp);
        hashMap.put("userName", registerPojo.getName());
        hashMap.put("userPhone", registerPojo.getPhoneNumber());

        CommonUtils.showProgress(getActivity());
        databaseReference.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                CommonUtils.dismissProgress();
                Toast.makeText(getActivity(), getString(R.string.bookingSuccess), Toast.LENGTH_SHORT).show();
                dailogbox.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonUtils.dismissProgress();
                Toast.makeText(getActivity(), getString(R.string.somethingWentWrong), Toast.LENGTH_SHORT).show();

            }
        });

    }


    private void dailog() {
        final View confirmdailog = LayoutInflater.from(getActivity()).inflate(R.layout.dailog_confirm_booking, null);
        dailogbox = new AlertDialog.Builder(getActivity()).create();
        dailogbox.setCancelable(false);
        dailogbox.setCanceledOnTouchOutside(false);
        dailogbox.setView(confirmdailog);

        confirmdailog.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {


                setBookings();

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

    private void dailogdatetime() {
        final View confirmdailog = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_booking__time_date, null);
        dialogDateTime = new AlertDialog.Builder(getActivity()).create();
//        dailogbox.setCancelable(false);
//        dailogbox.setCanceledOnTouchOutside(false);
        dialogDateTime.setView(confirmdailog);
        TextView tv_dialog_text;
        CalendarView cal_date;
        TimePicker timePicker_dialog;
        TextView btn_next;


        timePicker_dialog = confirmdailog.findViewById(R.id.timePicker_dialog);
        tv_dialog_text = confirmdailog.findViewById(R.id.tv_dialog_text);
        cal_date = confirmdailog.findViewById(R.id.cal_date);
        btn_next = confirmdailog.findViewById(R.id.btn_next);

        dateTimeStatus = 1;

        cal_date.setMinDate(new Date().getTime());


        timePicker_dialog.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                String hour = String.valueOf(hourOfDay);

                if (hourOfDay < 10) {
                    hour = "0" + hourOfDay;
                }

                String min = String.valueOf(minute);
                if (minute < 10) {
                    min = "0" + minute;
                }

                timeStr = hour + ":" + min;
            }
        });

        cal_date.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {


                String date = String.valueOf(dayOfMonth);
                if (dayOfMonth < 10) {
                    date = "0" + dayOfMonth;
                }

                month += 1;

                String mnth = String.valueOf(month);

                if (month < 10) {
                    mnth = "0" + month;
                }

                dateStr = year + "-" + mnth + "-" + date;


            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                switch (dateTimeStatus) {
                    case 1:

                        if (!dateStr.isEmpty()) {
                            tv_dialog_text.setText("Select Time");
                            cal_date.setVisibility(View.GONE);
                            timePicker_dialog.setVisibility(View.VISIBLE);

                            dateTimeStatus = 0;
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.selectDate), Toast.LENGTH_SHORT).show();
                        }


                        break;
                    case 2:

                        tv_dialog_text.setText("Select Date");
                        cal_date.setVisibility(View.VISIBLE);
                        timePicker_dialog.setVisibility(View.GONE);


                        dateTimeStatus = 0;

                        break;

                    case 0:
                        dateTimeStatus = 1;

                        dailog();
                        dialogDateTime.dismiss();

                        break;
                }


            }
        });
        dialogDateTime.show();


    }


    private void calculaterating() {

        Double aDouble = 0.0, totalRating = 0.0;


        for (int i = 0; i < list.size(); i++) {

            aDouble += Double.parseDouble(list.get(i).getRating());


        }

        totalRating = aDouble / list.size();
        tv_ratingCount.setText(totalRating.toString().substring(0, 3));

    }

    public double calculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6378;//radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec + " Meter   " + meterInDec);

        return Radius * c;
    }


    private void mapIntent() {

        Log.i("latLng", getBunkListPojo.getLati() + "," + getBunkListPojo.getLongi());

        String geoUri = "http://maps.google.com/maps?q=loc:" + getBunkListPojo.getLati() + "," + getBunkListPojo.getLongi() + " (" + getBunkListPojo.getBunkName() + ")";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
        startActivity(intent);

    }

}