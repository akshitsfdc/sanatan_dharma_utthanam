package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.akshit.akshitsfdc.allpuranasinhindi.BuildConfig;
import com.akshit.akshitsfdc.allpuranasinhindi.R;

import com.akshit.akshitsfdc.allpuranasinhindi.utils.FileUtils;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PrimeActivity extends MainActivity implements PaymentResultListener {


    private Button buyButton;
    private FileUtils fileUtils;
    private ProgressBar progressOuter;
    private boolean fromHome = false;
    private SwitchMaterial switchEnglish;
    private TextView intro1Text, intro2Text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_prime);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_prime, null, false);
        drawer.addView(contentView, 0);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


        fileUtils = new FileUtils(PrimeActivity.this);
        progressOuter = findViewById(R.id.progressOuter);
        buyButton = findViewById(R.id.buyButton);
        switchEnglish = findViewById(R.id.switchEnglish);
        intro1Text = findViewById(R.id.intro1Text);
        intro2Text = findViewById(R.id.intro2Text);

        fromHome = (boolean)routing.getParam("fromHome");

        //Razor pay
        Checkout.preload(getApplicationContext());
        setView();

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

        switchEnglish.setOnCheckedChangeListener((buttonView, isChecked) -> {
            changeTextLanguage(isChecked);
        });
    }

    private void changeTextLanguage(boolean isEnglish){
        if(isEnglish){
            intro1Text.setText(getString(R.string.prime_intro1_en));
            intro2Text.setText(getString(R.string.prime_intro2_en));
        }else {
            intro1Text.setText(getString(R.string.prime_intro1));
            intro2Text.setText(getString(R.string.prime_intro2));
        }
    }

    @Override
    public void onBackPressed() {
        if(fromHome){
            navigateToHome(fromHome);
        }else {
            super.onBackPressed();
        }

    }


    private void setView(){

        DecimalFormat df = new DecimalFormat("#.##");

        String priceText;
        priceText = "Checkout ("+getString(R.string.rs)+""+df.format(SplashActivity.APP_INFO.getPrimePrice())+")";
        buyButton.setText(priceText);

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SplashActivity.APP_INFO.getPrimePrice() <= 0){
                    fileUtils.showLongToast("Prime membership not available");
                    return;
                }
                startPayment();
            }
        });
    }
    public void startPayment() {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(!internetConnected){
            fileUtils.showLongToast("You are not connected to the internet.");
            return;
        }

        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        showPB(true);
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            int total = SplashActivity.APP_INFO.getPrimePrice();


            co.setKeyID(SplashActivity.APP_INFO.getPaymentApiProduction());


            JSONObject options = new JSONObject();
            options.put("key", SplashActivity.APP_INFO.getPaymentApiProduction());
            options.put("name", getString(R.string.app_name));
            String descriptionValue = "Prime Membership";
            options.put("description", descriptionValue);
            //You can omit the image option to fetch the image from dashboard

            //options.put("image", softCopyModel.getPicUrl());

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
        Intent intent = new Intent(PrimeActivity.this, OrderCompletedActivity.class);
        intent.putExtra("isSuccess", isSuccess);
        intent.putExtra("isHard", false);
        startActivity(intent);
        if(isSuccess){
            finish();
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

    private void saveUserData(){


        Map<String, Object> map = new HashMap<>();
        map.put("primeMember", true);
        fireStoreService.updateData("user_data", fireAuthService.getUserId(), map)
                .addOnSuccessListener(aVoid -> {
                    navigateToHome(fromHome);
                    hidePB(true);
                })
                .addOnFailureListener(e -> {
                    fileUtils.showShortToast("Error processing your request, please contact support .");
                    hidePB(true);
                });
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        showPB(true);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("user_data").document(currentUser.getUid());

        docRef.update("primeMember", true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                fileUtils.showLongToast("Congratulations! You are now prime member");
                navigateToHome(fromHome);
                hidePB(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                fileUtils.showShortToast("Error processing your request, please contact support .");
                hidePB(true);
            }
        });
    }

    private void navigateToHome(boolean fromHome){
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        if(fromHome){
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        }else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        finish();

    }
}
