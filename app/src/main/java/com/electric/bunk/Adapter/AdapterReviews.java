//Shubhams Code - T00619152

package com.electric.bunk.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.electric.bunk.R;
import com.electric.bunk.pojoClasses.GetBookingsListPojo;
import com.electric.bunk.pojoClasses.GetReviewsPojo;

import java.util.List;

public class AdapterReviews extends RecyclerView.Adapter<AdapterReviews.ViewHolder> {
    private Context context;
    private List<GetReviewsPojo> list;
    private Select select;

    public interface Select {
        void click(int poisition);

    }


    public AdapterReviews(Context context, List<GetReviewsPojo> list, Select select) {
        this.context = context;
        this.list = list;
        this.select = select;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_reviews, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.userName.setText(list.get(position).getUserName());
        holder.bookingDateTime.setText(list.get(position).getDate()+" "+context.getString(R.string.at)+" "+list.get(position).getTime());
        holder.comment.setText(list.get(position).getComment());
        holder.ratingBar.setRating(Float.valueOf(list.get(position).getRating()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView userName, comment, bookingDateTime;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            userName=itemView.findViewById(R.id.tv_userName);
            comment=itemView.findViewById(R.id.tv_comment);
            ratingBar=itemView.findViewById(R.id.ratingBar);
            bookingDateTime=itemView.findViewById(R.id.tv_dateNTime);



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