//Shubhams Code - T00619152

package com.electric.bunk.Adapter;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.electric.bunk.R;
import com.electric.bunk.pojoClasses.GetBunkListPojo;

import java.util.List;

public class AdapterBunk extends RecyclerView.Adapter<AdapterBunk.ViewHolder> {
    private Context context;
    private List<GetBunkListPojo> list;
    private Select select;
    private String latStr, longStr;

    public interface Select {
        void click(int poisition);

    }


    public AdapterBunk(Context context, List<GetBunkListPojo> list, Select select, String latStr, String longStr) {
        this.context = context;
        this.list = list;
        this.select = select;
        this.latStr = latStr;
        this.longStr = longStr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_bunk, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.bunkName.setText(list.get(position).getBunkName());
        holder.slotS.setText(context.getString(R.string.charging_slot) + ": " + list.get(position).getChargingSlots());
        holder.addressName.setText(list.get(position).getBunkAddress());

        holder.tv_distance.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView bunkName, addressName, slotS, tv_distance;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            bunkName = itemView.findViewById(R.id.tv_nameBunk);
            addressName = itemView.findViewById(R.id.tv_address);
            slotS = itemView.findViewById(R.id.tv_slots);
            tv_distance = itemView.findViewById(R.id.tv_distance);


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