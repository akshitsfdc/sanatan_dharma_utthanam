package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SoftCopyModel;
import com.bumptech.glide.Glide;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class SoftBookHomeActivity extends MainActivity implements IUnityAdsListener {

    private ImageView bookImageView;
    private SoftCopyModel softCopyModel;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_soft_book_home);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_soft_book_home, null, false);
        drawer.addView(contentView, 0);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        textView = findViewById(R.id.textView);
        bookImageView = findViewById(R.id.blurImageView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        findViewById(R.id.getPDFButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFullScreenAdListeners();

                navigateToBookView();
            }
        });
        Intent intent=getIntent();
        softCopyModel =(SoftCopyModel) intent.getSerializableExtra("softCopyModel");

        if(softCopyModel != null){
            setupDetails();
        }

        UnityAds.addListener(this);
    }

    @Override
    public void onBackPressed() {
        UnityAds.removeListener(this);
        super.onBackPressed();
    }

    private void setupDetails(){

        Glide.with(SoftBookHomeActivity.this).load(softCopyModel.getPicUrl()).error(R.drawable.book_placeholder).fallback(R.drawable.book_placeholder)
                .apply(bitmapTransform(new BlurTransformation(60))).into(bookImageView);

        textView.setText(softCopyModel.getDescription());
    }
    private void navigateToBookView(){


        Intent intent = new Intent(SoftBookHomeActivity.this, SoftBookViewActivity.class);
        intent.putExtra("softCopyModel",softCopyModel);
        startActivity(intent);

    }

    private void setFullScreenAdListeners(){

        if(!MainActivity.USER_DATA.isPrimeMember() && !MainActivity.DOWNLOAD_IN_PROGRESS) {

            if(softCopyModel.isFree()){
                if (UnityAds.isReady(getString(R.string.fullScreenBookView_unity).trim())) {
                    UnityAds.show(SoftBookHomeActivity.this, getString(R.string.fullScreenBookView_unity).trim());
                }
            }

        }


    }

    @Override
    public void onUnityAdsReady(String s) {

    }

    @Override
    public void onUnityAdsStart(String s) {

    }

    @Override
    public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {
        UnityAds.removeListener(this);
    }

    @Override
    public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {
        UnityAds.removeListener(this);
    }
}
