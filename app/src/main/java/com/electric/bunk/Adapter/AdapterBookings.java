//Arnavs Code - T00630177

package com.electric.bunk.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.electric.bunk.R;
import com.electric.bunk.SharePrefrence.App;
import com.electric.bunk.SharePrefrence.AppConstants;
import com.electric.bunk.pojoClasses.GetBookingsListPojo;
import com.electric.bunk.pojoClasses.GetBunkListPojo;

import java.util.List;

public class AdapterBookings extends RecyclerView.Adapter<AdapterBookings.ViewHolder> {
    private Context context;
    private List<GetBookingsListPojo> list;
    private Select select;

    public interface Select {
        void click(int poisition);

    }


    public AdapterBookings(Context context, List<GetBookingsListPojo> list, Select select) {
        this.context = context;
        this.list = list;
        this.select = select;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_bookings, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if(App.getAppPreference().GetString(AppConstants.USER_TYPE).equalsIgnoreCase(AppConstants.USER_TYPE)){
            holder.bunkName.setText(list.get(position).getBunkName());
        }else {
            holder.bunkName.setText(list.get(position).getUserName());
        }


        holder.bookingDateTime.setText(context.getString(R.string.dateTime)+": "+list.get(position).getDate()+" "+context.getString(R.string.at)+" "+list.get(position).getTime());
        holder.addressName.setText(list.get(position).getAddressBunk());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView bunkName, addressName, bookingDateTime;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            bunkName=itemView.findViewById(R.id.tv_nameBunk);
            addressName=itemView.findViewById(R.id.tv_address);
            bookingDateTime=itemView.findViewById(R.id.tv_bookingDateTime);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select.click(getLayoutPosition());
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position);
    }

    @Override
    public long getItemId(int position) {
        return (position);
    }
}