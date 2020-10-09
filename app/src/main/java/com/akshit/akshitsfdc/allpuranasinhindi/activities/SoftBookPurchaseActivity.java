package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import org.json.JSONObject;

import java.text.DecimalFormat;

public class SoftBookPurchaseActivity extends MainActivity implements PaymentResultListener, IUnityAdsListener {

    private FileUtils fileUtils;
    private SoftCopyModel softCopyModel;
    private ImageView bookImage;
    private TextView title;
    private TextView price;
    private TextView pages;
    private Button buyButton;
    private ProgressBar progress;
    private ProgressBar progressOuter;

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


        bookImage = findViewById(R.id.bookImage);
        title = findViewById(R.id.title);
        price = findViewById(R.id.price);
        pages = findViewById(R.id.pages);
        buyButton = findViewById(R.id.buyButton);
        progress = findViewById(R.id.progress);
        progressOuter = findViewById(R.id.progressOuter);

        Intent intent=getIntent();
        softCopyModel =(SoftCopyModel) intent.getSerializableExtra("softCopyModel");

        if(softCopyModel != null){
            setView(softCopyModel);
        }else {
            fileUtils.showLongToast("No book details loaded!");
        }
        //Razor pay
        Checkout.preload(getApplicationContext());
        getAppInfo();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        Button videoButton = findViewById(R.id.videoButton);
        if(softCopyModel.isVideoOption()){
            videoButton.setVisibility(View.VISIBLE);
            videoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    watchVideoToGetAccess();
                }
            });
        }else{
            videoButton.setVisibility(View.GONE);
        }
        findViewById(R.id.primeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToPrimeActivity();
            }
        });


        UnityAds.addListener(this);
    }

    @Override
    public void onBackPressed() {
        UnityAds.removeListener(this);
        super.onBackPressed();
    }

    private void navigateToPrimeActivity(){
        Intent intent = new Intent(SoftBookPurchaseActivity.this, PrimeActivity.class);
        intent.putExtra("fromHome",false);
        startActivity(intent);
        //finish();
    }
    private void watchVideoToGetAccess(){

        if (UnityAds.isReady (getString(R.string.bookAccessRewardedVideo_unity).trim())) {
            UnityAds.show ( SoftBookPurchaseActivity.this,getString(R.string.bookAccessRewardedVideo_unity));
        }else{
            fileUtils.showShortToast("Video is being prepared, please wait few seconds");
        }

    }
    private void navigateToBookView(){
        Intent intent = new Intent(SoftBookPurchaseActivity.this, SoftBookHomeActivity.class);
        intent.putExtra("softCopyModel",softCopyModel);
        startActivity(intent);
    }
    private void getAppInfo(){

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(!fileUtils.isNetworkConnected()){
            fileUtils.showLongToast("You are not connected to the internet.");
            return;
        }
        if(currentUser == null){
            fileUtils.showLongToast("You are not logged in, please log in again..");
            return;
        }
        showPB(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("app_info").document("meta_data").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if(documentSnapshot.exists()){

                        SplashActivity.APP_INFO = (AppInfo) documentSnapshot.toObject(AppInfo.class);

                        hidePB(true);

                    }else {
                        fileUtils.showLongToast("Could not connect to the server please try again later.");
                        hidePB(true);
                    }
                }else{
                    fileUtils.showLongToast("Could not connect to the server please try again later.");
                    hidePB(true);
                }
            }
        });

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

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPayment(softCopyModel);
            }
        });
    }

    public void startPayment(SoftCopyModel softCopyModel) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(!fileUtils.isNetworkConnected()){
            fileUtils.showLongToast("You are not connected to the internet.");
            return;
        }
        if(currentUser == null){
            fileUtils.showLongToast("You are not logged in, please log in again..");
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

            if (BuildConfig.DEBUG) {
                co.setKeyID(SplashActivity.APP_INFO.getPaymentApiSandbox());
            }else {
                co.setKeyID(SplashActivity.APP_INFO.getPaymentApiProduction());
            }

            JSONObject options = new JSONObject();
            options.put("key", SplashActivity.APP_INFO.getPaymentApiSandbox());
            options.put("name", getString(R.string.app_name));
            String descriptionValue = "Book Purchase - "+softCopyModel.getName();
            options.put("description", descriptionValue);
            //You can omit the image option to fetch the image from dashboard
            //options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", String.valueOf(total*100));

            JSONObject preFill = new JSONObject();
            preFill.put("email", currentUser.getEmail());
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

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(!fileUtils.isNetworkConnected()){
            fileUtils.showLongToast("You are not connected to the internet.");
            return;
        }
        if(currentUser == null){
            fileUtils.showLongToast("You are not logged in, please log in again..");
            return;
        }
        showPB(true);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("user_data").document(currentUser.getUid());

         MainActivity.USER_DATA.getPurchasedBooks().add(softCopyModel);

        docRef.set(MainActivity.USER_DATA).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                fileUtils.showShortToast("Book purchase success.");
                navigateToOrderCompleted(true);
                hidePB(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                fileUtils.showShortToast("Error in saving purchased book user data.");
                hidePB(true);
            }
        });
    }

    @Override
    public void onUnityAdsReady(String s) {

    }

    @Override
    public void onUnityAdsStart(String s) {

    }

    @Override
    public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {
        if (finishState == UnityAds.FinishState.COMPLETED) {
            UnityAds.removeListener(this);
            navigateToBookView();
        } else if (finishState == UnityAds.FinishState.SKIPPED) {
            //fileUtils.showShortToast("You must not skip the video to avail the read access");
        } else if (finishState == UnityAds.FinishState.ERROR) {
            fileUtils.showShortToast("Currently no video ad available, please try again later!");
        }
    }

    @Override
    public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {
        UnityAds.removeListener(this);
        if (this instanceof SoftBookPurchaseActivity) {
            fileUtils.showShortToast("Currently no video ad available, please try again later!");
        }

    }
}
