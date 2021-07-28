package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SoftCopyModel;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Objects;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class SoftBookHomeActivity extends MainActivity {

    private final String TAG = "SoftBookHomeActivity";
    private ImageView bookImageView;
    private SoftCopyModel softCopyModel;
    private TextView textView;
    private InterstitialAd mInterstitialAd;
    private ProgressBar progressOuter;

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
        progressOuter = findViewById(R.id.progressOuter);
        //
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setTitle("");
        toolbar.setSubtitle("");

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
        toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.off_notification_color));
        toggle.setDrawerIndicatorEnabled(false);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        //
        findViewById(R.id.getPDFButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToBookView();
                showAds();
            }
        });
        softCopyModel = (SoftCopyModel)routing.getParam("softCopyModel");
//        Intent intent=getIntent();
//        softCopyModel =(SoftCopyModel) intent.getParcelableExtra("softCopyModel");

        if(softCopyModel != null){
            setupDetails();
        }


        if( SplashActivity.USER_DATA == null || (!SplashActivity.USER_DATA.isPrimeMember() && !MainActivity.DOWNLOAD_IN_PROGRESS)) {

            if(softCopyModel.isFree()){
                initAd();
            }

        }

    }

    private void showPB(boolean loadingActive){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if(loadingActive){
            progressOuter.setVisibility(View.VISIBLE);
        }


    }
    private void hidePB(boolean loadingActive){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        if(loadingActive) {
            progressOuter.setVisibility(View.GONE);
        }

    }

    private void initAd(){

        if(SplashActivity.APP_INFO == null){
            return;
        }
        showPB(true);
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,SplashActivity.APP_INFO.getDownloadingGapAdId().trim(), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        setupAdListeners();
                        hidePB(true);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                        hidePB(true);
                    }
                });

    }
    private void setupAdListeners(){

        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when fullscreen content is dismissed.
                Log.d("TAG", "The ad was dismissed.");
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when fullscreen content failed to show.
                Log.d("TAG", "The ad failed to show.");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when fullscreen content is shown.
                // Make sure to set your reference to null so you don't
                // show it a second time.
                mInterstitialAd = null;
                Log.d("TAG", "The ad was shown.");
            }
        });
    }
    private void showAds(){
        if (mInterstitialAd != null) {
            mInterstitialAd.show(SoftBookHomeActivity.this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setupDetails(){

        String picUrl;
        if(softCopyModel.getCoverUri() != null){
            picUrl = softCopyModel.getCoverUri().toString();
        }else {
            picUrl = softCopyModel.getPicUrl();
        }
        Glide.with(SoftBookHomeActivity.this).load(picUrl).error(R.drawable.book_placeholder).fallback(R.drawable.book_placeholder)
                .apply(bitmapTransform(new BlurTransformation(60))).into(bookImageView);

        textView.setText(softCopyModel.getDescription());
    }
    private void navigateToBookView(){

        routing.clearParams();
        routing.appendParams("softCopyModel", softCopyModel);
        routing.navigate(SoftBookViewActivity.class, false);

    }
}
