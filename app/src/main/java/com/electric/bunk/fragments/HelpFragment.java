//Both

package com.electric.bunk.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.electric.bunk.R;
import com.electric.bunk.SharePrefrence.App;
import com.electric.bunk.SharePrefrence.AppConstants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseRegistrar;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.omninos.util_data.CommonUtils;

public class HelpFragment extends Fragment implements View.OnClickListener {
    private View view;
    private TextView tv_head, tv_webText;
    private ImageView img_back;

    private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(AppConstants.OTHERINFO);
    private String pathStr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_help, container, false);


        tv_head=view.findViewById(R.id.tv_head);
        tv_webText=view.findViewById(R.id.tv_webText);
        img_back=view.findViewById(R.id.img_back);

        pathStr=App.getSinltonPojo().getWebViewLink();

        if(pathStr.equalsIgnoreCase(AppConstants.HELP)){
            tv_head.setText(getString(R.string.help));
        }else {
            tv_head.setText(getString(R.string.About));
        }

        img_back.setOnClickListener(this);

        CommonUtils.showProgress(getActivity());
        databaseReference.child(pathStr).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tv_webText.setText(snapshot.getValue().toString());
                CommonUtils.dismissProgress();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                CommonUtils.dismissProgress();
            }
        });


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                getActivity().onBackPressed();
                break;
        }
    }
}