//Shubhams Code - T00619152

package com.electric.bunk.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.electric.bunk.Adapter.AdapterBunk;
import com.electric.bunk.R;
import com.electric.bunk.SharePrefrence.App;
import com.electric.bunk.SharePrefrence.AppConstants;
import com.electric.bunk.pojoClasses.GetBunkListPojo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.omninos.util_data.CommonUtils;

import java.util.ArrayList;
import java.util.List;


public class MyBunksListFragment extends Fragment implements View.OnClickListener {


    private View view;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(AppConstants.BUNK_STATION);
    private List<GetBunkListPojo> list = new ArrayList<>();
    private AdapterBunk adapterBunk;
    RecyclerView rc_bunk;
    private String latStr = "", longStr = "", uid;
    private TextView tv_head;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    List<String> listKeys=new ArrayList<>();

    public MyBunksListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        view= inflater.inflate(R.layout.fragment_my_bunks_list, container, false);
        view = inflater.inflate(R.layout.fragment_search_bunk, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            uid = firebaseUser.getUid();
        }

        findIds(view);


        adapterBunk = new AdapterBunk(getActivity(), list, new AdapterBunk.Select() {
            @Override
            public void click(int poisition) {
                App.getSinltonPojo().setGetBunkListPojo(list.get(poisition));
                App.getSinltonPojo().setBunkPath(listKeys.get(poisition));
                Navigation.findNavController(view).navigate(R.id.action_myBunksListFragment_to_editNDeleteBunkFragment);
            }
        }, latStr, longStr);
        rc_bunk.setAdapter(adapterBunk);


        CommonUtils.showProgress(getActivity());

        list.clear();
        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CommonUtils.dismissProgress();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                CommonUtils.dismissProgress();
            }
        });

        databaseReference.child(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if (snapshot.exists()) {
                    listKeys.add(snapshot.getKey().toString());
                    Log.i("added", snapshot.toString());
                    GetBunkListPojo getBunkListPojo = snapshot.getValue(GetBunkListPojo.class);
                    list.add(getBunkListPojo);
                    adapterBunk.notifyDataSetChanged();
                } else {
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


        return view;
    }


    private void findIds(View view) {

        tv_head = view.findViewById(R.id.tv_head);
        rc_bunk = view.findViewById(R.id.rc_bunk);
        tv_head.setText(getString(R.string.myStations));

        view.findViewById(R.id.img_back).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                getActivity().onBackPressed();
                break;

        }
    }
}