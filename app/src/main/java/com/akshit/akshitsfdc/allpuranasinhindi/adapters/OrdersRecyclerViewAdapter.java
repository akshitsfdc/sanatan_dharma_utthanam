package com.akshit.akshitsfdc.allpuranasinhindi.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.OrderConfirmationActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.models.UserOrderModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

public class OrdersRecyclerViewAdapter extends RecyclerView.Adapter<OrdersRecyclerViewAdapter.ViewHolder> {

    private ArrayList<UserOrderModel> userOrderModels;
    private Context mContext;

    public OrdersRecyclerViewAdapter(Context context, ArrayList<UserOrderModel> UserOrderModels ) {
        this.userOrderModels = UserOrderModels;
        mContext = context;
    }

    @NonNull
    @Override
    public OrdersRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_order_layout, parent, false);
        return new OrdersRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersRecyclerViewAdapter.ViewHolder holder, int position) {

        UserOrderModel userOrderModel = userOrderModels.get(position);

        holder.title.setText(userOrderModel.getBook().getName());

        if(TextUtils.equals(userOrderModel.getStatus(), "Delivered")){
            holder.statusText.setTextColor(mContext.getResources().getColor(R.color.color_leaderboard_green));
        }

        holder.statusText.setText(userOrderModel.getStatus());
        String orderId = "Order Id: "+userOrderModel.getOrderId();

        holder.orderId.setText(orderId);
        Glide.with(mContext).load(userOrderModel.getBook().getPicUrl()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                holder.progress.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.progress.setVisibility(View.GONE);
                return false;
            }
        }).error(R.drawable.book_placeholder).fallback(R.drawable.book_placeholder).into(holder.bookImage);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.bookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void navigateToOrderDetailsActivity(){

    }
    @Override
    public int getItemCount() {
        return userOrderModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView bookImage;
        TextView title;
        TextView statusText;
        TextView orderId;
        ProgressBar progress;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bookImage = itemView.findViewById(R.id.bookImage);
            title = itemView.findViewById(R.id.title);
            statusText = itemView.findViewById(R.id.statusText);
            orderId = itemView.findViewById(R.id.orderId);
            progress = itemView.findViewById(R.id.progress);
            parentLayout = itemView.findViewById(R.id.parentLayout);
        }
    }
}
