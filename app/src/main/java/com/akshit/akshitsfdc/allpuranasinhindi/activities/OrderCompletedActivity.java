package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.FileUtils;

import java.util.Objects;

public class OrderCompletedActivity extends MainActivity {

    private FileUtils fileUtils;
    private boolean isSuccess = false;
    private TextView infoText;
    private Button doneButton;
    private ImageView alertImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_order_completed);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_order_completed, null, false);
        drawer.addView(contentView, 0);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        fileUtils = new FileUtils(OrderCompletedActivity.this);

        isSuccess = getIntent().getExtras().getBoolean("isSuccess");

        infoText = findViewById(R.id.infoText);
        doneButton = findViewById(R.id.doneButton);
        alertImage = findViewById(R.id.alertImage);

        if(isSuccess){
            completedWithSuccess();
        }else{
            completedWithError();
        }

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
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToHome();
            }
        });
    }

    @Override
    public void onBackPressed() {

        if(isSuccess){
            navigateToHome();
        }else{
            super.onBackPressed();
        }

    }
    private void navigateToHome(){
        Intent intent = new Intent(OrderCompletedActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void completedWithSuccess(){

        boolean isHard = getIntent().getExtras().getBoolean("isHard");

        if(isHard){
            infoText.setText("Your payment was successful and your order has been confirmed. Your order will reach you within 5 -10 business days. Please contact support if you need any assistance.");
        }else{
            infoText.setText("Your payment was successful and your order has been confirmed. You can now read this book on your device. Please contact support if you need any assistance.");
        }

        alertImage.setImageResource(R.drawable.ic_check_circle_green_300dp);

    }

    private void completedWithError(){
        infoText.setText("Your payment was not successful some error occurred. It might be that your payment information was incorrect, if all was correct and you still see this error please contact support for assistance.");
        alertImage.setImageResource(R.drawable.ic_error_outline_red_300dp);
    }
}
