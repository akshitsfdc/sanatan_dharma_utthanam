package com.akshit.akshitsfdc.allpuranasinhindi.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.HardCopyBookDashboardActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.SoftBookViewActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.SoftPuranaDashboardActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.models.BookDisplaySliderModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SoftCopyModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

public class DisplaySliderRecyclerViewAdapter extends RecyclerView.Adapter<DisplaySliderRecyclerViewAdapter.ViewHolder>{

    private ArrayList<BookDisplaySliderModel> bookDisplaySliders;
    private Context context;

    public DisplaySliderRecyclerViewAdapter(ArrayList<BookDisplaySliderModel> bookDisplaySliders, Context context) {
        this.bookDisplaySliders = bookDisplaySliders;
        this.context = context;
    }
    @NonNull
    @Override
    public DisplaySliderRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_book_layout, parent, false);
        return new DisplaySliderRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DisplaySliderRecyclerViewAdapter.ViewHolder holder, int position) {

        BookDisplaySliderModel bookDisplaySliderModel = bookDisplaySliders.get(position);

        holder.title.setText(bookDisplaySliderModel.getName());

        Glide.with(context).load(bookDisplaySliderModel.getPicUrl()).listener(new RequestListener<Drawable>() {
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

        holder.bookImage.setOnClickListener(v->{
            if(TextUtils.equals(context.getString(R.string.offline_key), bookDisplaySliderModel.getType())){
                navigateToView(bookDisplaySliderModel);
            }else if(TextUtils.equals(context.getString(R.string.hard_key), bookDisplaySliderModel.getType())){
                navigateToHardCopyDashBoard(bookDisplaySliderModel.getType(), bookDisplaySliderModel.getBookId());
            } else {
                navigateToBookDashboard(bookDisplaySliderModel.getType(), bookDisplaySliderModel.getBookId());
            }

        });
    }

    private void navigateToHardCopyDashBoard(String type, String scrollToId){

        Intent intent = new Intent(context, HardCopyBookDashboardActivity.class);
        intent.putExtra("fromHome", true);
        intent.putExtra("scrollToId", scrollToId);
        context.startActivity(intent);
        //finish();
    }

    private void navigateToBookDashboard(String type, String scrollToId){
        Intent intent = new Intent(context, SoftPuranaDashboardActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("fromHome", true);
        intent.putExtra("scrollToId", scrollToId);
        context.startActivity(intent);
    }

    private void navigateToView(BookDisplaySliderModel bookDisplaySliderModel){

        Intent intent = new Intent(context, SoftBookViewActivity.class);

        SoftCopyModel softCopyModel = new SoftCopyModel();
        softCopyModel.setFree(true);
        softCopyModel.setBookId(bookDisplaySliderModel.getBookId());
        softCopyModel.setPicUrl(bookDisplaySliderModel.getPicUrl());
        softCopyModel.setPrice(-1);
        softCopyModel.setName(bookDisplaySliderModel.getName());
        softCopyModel.setDownloadUrl(bookDisplaySliderModel.getDownloadUrl());
        softCopyModel.setFileName(bookDisplaySliderModel.getFileName());

        intent.putExtra("softCopyModel",softCopyModel);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return bookDisplaySliders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImage;
        ProgressBar progress;
        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bookImage = itemView.findViewById(R.id.bookImage);
            title = itemView.findViewById(R.id.title);
            progress = itemView.findViewById(R.id.progress);
        }
    }
}
