package com.akshit.akshitsfdc.allpuranasinhindi.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.AddressDetailsActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.HardCopyBookDetailsActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.models.HardCopyModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class HardCopyRecyclerViewAdapter extends RecyclerView.Adapter<HardCopyRecyclerViewAdapter.ViewHolder>{

    private ArrayList<HardCopyModel> hardCopyModels;
    private Context mContext;
    private boolean hasScrolled;

    public HardCopyRecyclerViewAdapter(Context context, ArrayList<HardCopyModel> hardCopyModels) {
        this.hardCopyModels = hardCopyModels;
        mContext = context;
    }

    public void addData(ArrayList<HardCopyModel> hardCopyModels){
        this.hardCopyModels.addAll(hardCopyModels);
    }

    @NonNull
    @Override
    public HardCopyRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hard_copy_book_layout, parent, false);
        return new HardCopyRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HardCopyRecyclerViewAdapter.ViewHolder holder, int position) {

        HardCopyModel hardCopyModel = hardCopyModels.get(position);

        holder.title.setText(hardCopyModel.getName());

        holder.language.setText(hardCopyModel.getLanguage());

        Log.d("TAG", "onBindViewHolder: hardCopyModel.isBook() "+hardCopyModel.isIsBook());
        if(!hardCopyModel.isIsBook()){
            holder.language.setVisibility(View.GONE);
        }

        Glide.with(mContext).load(hardCopyModel.getPicUrl()).listener(new RequestListener<Drawable>() {
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
        }).error(R.drawable.book_placeholder).fallback(R.drawable.book_placeholder).into( holder.bookImage);

        String price;
      if(hardCopyModel.getDiscount() > 0){
          DecimalFormat df = new DecimalFormat("#");
          DecimalFormat df2 = new DecimalFormat("#.#");
            price = mContext.getString(R.string.rs)+""+  df.format(hardCopyModel.getPrice())+" (-"+df2.format(hardCopyModel.getDiscount())+"%)"+" | "+mContext.getString(R.string.rs)+""+df.format(hardCopyModel.getDeliveryCharge());
            holder.price.setText(price);
        }else{
          DecimalFormat df = new DecimalFormat("#");
            price = mContext.getString(R.string.rs)+""+ df.format(hardCopyModel.getPrice())+" | "+mContext.getString(R.string.rs)+""+df.format(hardCopyModel.getDeliveryCharge());
            holder.price.setText(price);
        }


      if(!hardCopyModel.isAvailable() || hardCopyModel.getStock() <=0){
          holder.buyButton.setVisibility(View.GONE);
          holder.outOfStockLabel.setVisibility(View.VISIBLE);
      }else {
          holder.buyButton.setVisibility(View.VISIBLE);
          holder.outOfStockLabel.setVisibility(View.GONE);

          holder.buyButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  navigateToAddressDetails(hardCopyModel);
              }
          });
      }

        holder.bookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToBookDetails(hardCopyModel);
            }
        });

    }

    private void navigateToBookDetails(HardCopyModel hardCopyModel){
        Intent intent = new Intent(mContext, HardCopyBookDetailsActivity.class);
        intent.putExtra("hardCopyModel",hardCopyModel);
        mContext.startActivity(intent);

    }
    private void navigateToAddressDetails(HardCopyModel hardCopyModel){
        Intent intent = new Intent(mContext, AddressDetailsActivity.class);
        intent.putExtra("hardCopyModel",hardCopyModel);
        mContext.startActivity(intent);
        // overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        //finish();
    }
    @Override
    public int getItemCount() {
        return hardCopyModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView bookImage;
        TextView title;
        TextView language;
        TextView price;
        ProgressBar progress;
        Button buyButton;
        TextView outOfStockLabel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bookImage = itemView.findViewById(R.id.bookImage);
            title = itemView.findViewById(R.id.title);
            language = itemView.findViewById(R.id.language);
            price = itemView.findViewById(R.id.price);
            progress = itemView.findViewById(R.id.progress);
            buyButton = itemView.findViewById(R.id.buyButton);
            outOfStockLabel = itemView.findViewById(R.id.outOfStockLabel);
        }
    }
}
