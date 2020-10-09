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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.akshit.akshitsfdc.allpuranasinhindi.BuildConfig;
import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.models.AppInfo;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.FileUtils;
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

import org.json.JSONObject;

import java.text.DecimalFormat;

public class PrimeActivity extends MainActivity implements PaymentResultListener {


    private Button buyButton;
    private FileUtils fileUtils;
    private ProgressBar progressOuter;
    private boolean fromHome = false;
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

        fromHome = getIntent().getExtras().getBoolean("fromHome");

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
    }

    @Override
    public void onBackPressed() {
        if(fromHome){
            navigateToHome(fromHome);
        }else {
            super.onBackPressed();
        }

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
                        setView();
                        hidePB(true);

                    }else {
                        fileUtils.showLongToast("Could not connect to the server please try again later.");
                        setView();
                        hidePB(true);
                    }
                }else{
                    fileUtils.showLongToast("Could not connect to the server please try again later.");
                    setView();
                    hidePB(true);
                }
            }
        });

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
            int total = SplashActivity.APP_INFO.getPrimePrice();

            co.setKeyID(SplashActivity.APP_INFO.getPaymentApiProduction());

            JSONObject options = new JSONObject();
            options.put("key", SplashActivity.APP_INFO.getPaymentApiSandbox());
            options.put("name", getString(R.string.app_name));
            String descriptionValue = "Prime Membership";
            options.put("description", descriptionValue);
            //You can omit the image option to fetch the image from dashboard

            //options.put("image", softCopyModel.getPicUrl());

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
        Intent intent = new Intent(PrimeActivity.this, OrderCompletedActivity.class);
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
