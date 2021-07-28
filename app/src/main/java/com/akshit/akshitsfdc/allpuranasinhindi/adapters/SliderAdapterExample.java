package com.akshit.akshitsfdc.allpuranasinhindi.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.BaseActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.HomeActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.MainActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.SoftPuranaDashboardActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SliderItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapterExample extends
        SliderViewAdapter<SliderAdapterExample.SliderAdapterVH> {

    private AppCompatActivity context;
    private List<SliderItem> mSliderItems = new ArrayList<>();
    private BaseActivity baseActivity;

    public SliderAdapterExample(AppCompatActivity context) {
        this.context = context;
        baseActivity = (BaseActivity) context;
    }

    public void renewItems(List<SliderItem> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(SliderItem sliderItem) {
        this.mSliderItems.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_slider_layout, null);

        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        SliderItem sliderItem = mSliderItems.get(position);


        setImage(viewHolder, sliderItem.getImageUrl());


        viewHolder.itemView.setOnClickListener(v -> {
            if(sliderItem.isExternalLink()){
                if(!TextUtils.isEmpty(sliderItem.getExternalUrl() ) ){
                    openUrl(sliderItem.getExternalUrl());
                }
            }else{
                navigateToSoftPuranaDashBoard(sliderItem.getDescription().trim());
            }

        });




    }
    private void setImage(SliderAdapterVH viewHolder, String imageUrl){
        Glide.with(viewHolder.itemView)
                .load(imageUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        //                holder.progress.setVisibility(View.GONE);
                        viewHolder.progress.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        viewHolder.progress.setVisibility(View.GONE);
                        return false;
                    }
                })
                .fitCenter().error(R.drawable.banner_placeholder).fallback(R.drawable.banner_placeholder)
                .into(viewHolder.imageViewBackground);
    }

    private void openUrl(String url){
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
    private void navigateToSoftPuranaDashBoard(String type){

        baseActivity.routing.clearParams();
        baseActivity.routing.appendParams("singleBook", false);
        baseActivity.routing.appendParams("type", type);
        baseActivity.routing.appendParams("fromHome", false);
        baseActivity.routing.navigate(SoftPuranaDashboardActivity.class, false);

        //((Activity)context).finish();
    }
    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        ImageView imageGifContainer;
        TextView textViewDescription;
        ProgressBar progress;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            imageGifContainer = itemView.findViewById(R.id.iv_gif_container);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
            progress = itemView.findViewById(R.id.progress);
            this.itemView = itemView;
        }
    }

}