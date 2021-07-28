package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.akshit.akshitsfdc.allpuranasinhindi.BuildConfig;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.models.AppInfo;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SoftCopyModel;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.FileUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SoftBookPurchaseActivity extends MainActivity implements PaymentResultListener {

    private final String TAG = "SoftBookPurchased";
    private FileUtils fileUtils;
    private SoftCopyModel softCopyModel;
    private ImageView bookImage;
    private TextView title;
    private TextView price;
    private TextView pages;
    private Button buyButton;
    private ProgressBar progress;
    private ProgressBar progressOuter;

    private RewardedAd mRewardedAd;
    private Button videoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_soft_book_purchase);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_soft_book_purchase, null, false);
        drawer.addView(contentView, 0);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        fileUtils = new FileUtils(SoftBookPurchaseActivity.this);
        uiUtils.setSnakebarView(getSnakBarView(findViewById(R.id.snakebarLayout)));

        bookImage = findViewById(R.id.bookImage);
        title = findViewById(R.id.title);
        price = findViewById(R.id.price);
        pages = findViewById(R.id.pages);
        buyButton = findViewById(R.id.buyButton);
        progress = findViewById(R.id.progress);
        progressOuter = findViewById(R.id.progressOuter);
        videoButton = findViewById(R.id.videoButton);

        softCopyModel =(SoftCopyModel) routing.getParam("softCopyModel");

        if(softCopyModel != null){
            setView(softCopyModel);
        }else {
            fileUtils.showLongToast("No book details loaded!");
        }
        //Razor pay
        Checkout.preload(getApplicationContext());


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



        if(softCopyModel.isVideoOption()) {
            initRewardedAd();
        }

        findViewById(R.id.primeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToPrimeActivity();
            }
        });


        if(!fireAuthService.isUserLoggedIn()){
            uiUtils.showShortErrorSnakeBar("You must be logged in to access prime book");
        }


    }

    private void setButtons(boolean loadSuccess){

        if(loadSuccess){

            videoButton.setVisibility(View.VISIBLE);
            videoButton.setOnClickListener(v -> watchVideoToGetAccess());
        }

    }
    private void initRewardedAd(){

        showPB(true);
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, SplashActivity.APP_INFO.getBookAccessAdId().trim(),
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d("SoftBookPurchased", loadAdError.getMessage());
                        mRewardedAd = null;
                        hidePB(true);
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        adContentCallbacks();
                        setButtons(true);
                        Log.d("SoftBookPurchased", "Ad was loaded.");
                        hidePB(true);
                    }
                });

    }
    private void adContentCallbacks(){

        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad was shown.");
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                Log.d(TAG, "Ad failed to show.");
                uiUtils.showShortErrorSnakeBar("Unable to show you ad at this time");
                videoButton.setVisibility(View.GONE);
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Ad was dismissed.");
                videoButton.setVisibility(View.GONE);
                mRewardedAd = null;
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void navigateToPrimeActivity(){
        Intent intent = new Intent(SoftBookPurchaseActivity.this, PrimeActivity.class);
        intent.putExtra("fromHome",false);
        startActivity(intent);
        //finish();
    }

    private void watchVideoToGetAccess(){

        if (mRewardedAd != null) {

            Activity activityContext = SoftBookPurchaseActivity.this;

            mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    Log.d(TAG, "The user earned the reward.");
                    navigateToBookView();

                }
            });
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
        }

    }
    private void navigateToBookView(){
        routing.clearParams();
        routing.appendParams("softCopyModel", softCopyModel);
        routing.navigate(SoftBookHomeActivity.class, false);
    }

    private void setView(SoftCopyModel softCopyModel){

        DecimalFormat df = new DecimalFormat("#");

        Glide.with(SoftBookPurchaseActivity.this).load(softCopyModel.getPicUrl()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
               progress.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
               progress.setVisibility(View.GONE);
                return false;
            }
        }).error(R.drawable.book_placeholder).fallback(R.drawable.book_placeholder).into(bookImage);

        title.setText(softCopyModel.getName());
        String priceText = "Price: "+getString(R.string.rs)+""+df.format(softCopyModel.getPrice());
        price.setText(priceText);
        String pagesText = "Pages: "+softCopyModel.getPages();
        pages.setText(pagesText);

        priceText = "Buy This Permanently ("+getString(R.string.rs)+""+df.format(softCopyModel.getPrice())+")";
        buyButton.setText(priceText);


        buyButton.setOnClickListener(v -> {
            if(fireAuthService.getCurrentUser() == null){
                routing.navigate(LoginActivity.class, false);
            }else {
                startPayment(softCopyModel);
            }

        });
    }

    public void startPayment(SoftCopyModel softCopyModel) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(!internetConnected){
            uiUtils.showShortErrorSnakeBar("You are not connected to the internet.");
            return;
        }
        if(!fireAuthService.isUserLoggedIn()){
            uiUtils.showShortErrorSnakeBar("You are not logged in, please log in again..");
            return;
        }
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        showPB(true);
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            int total = Math.round(softCopyModel.getPrice());


            co.setKeyID(SplashActivity.APP_INFO.getPaymentApiProduction());


            JSONObject options = new JSONObject();
            options.put("key", SplashActivity.APP_INFO.getPaymentApiProduction());
            options.put("name", getString(R.string.app_name));
            String descriptionValue = "Book Purchase - "+softCopyModel.getName();
            options.put("description", descriptionValue);
            //You can omit the image option to fetch the image from dashboard
            //options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", String.valueOf(total*100));

            JSONObject preFill = new JSONObject();
            if(currentUser != null){
                preFill.put("email", currentUser.getEmail());
                preFill.put("contact", currentUser.getPhoneNumber());
            }
            //preFill.put("contact", "9876543210");

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }finally {
            hidePB(true);
        }
    }
    @Override
    public void onPaymentSuccess(String s) {
        saveUserData();
    }

    @Override
    public void onPaymentError(int i, String s) {
        navigateToOrderCompleted(false);
    }

    private void navigateToOrderCompleted(boolean isSuccess){
        Intent intent = new Intent(SoftBookPurchaseActivity.this, OrderCompletedActivity.class);
        intent.putExtra("isSuccess", isSuccess);
        intent.putExtra("isHard", false);
        startActivity(intent);
        finish();
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

    private void saveUserData(){


        showPB(true);

        List<SoftCopyModel> purchasedBooks;
        if( SplashActivity.USER_DATA.getPurchasedBooks() == null){
            purchasedBooks = new ArrayList<>();
            purchasedBooks.add(softCopyModel);
        }else {
            purchasedBooks = SplashActivity.USER_DATA.getPurchasedBooks();
            purchasedBooks.add(softCopyModel);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("purchasedBooks",purchasedBooks);

        fireStoreService.updateData("user_data", fireAuthService.getUserId(), map)
                .addOnSuccessListener(aVoid -> {
                    navigateToOrderCompleted(true);
                    hidePB(true);
                })
                .addOnFailureListener(e -> {
                    fileUtils.showShortToast("Error in saving purchased book user data.");
                    hidePB(true);
                });
    }

}
