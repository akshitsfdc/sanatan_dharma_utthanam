package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.models.AppInfo;
import com.akshit.akshitsfdc.allpuranasinhindi.models.UserDataModel;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class SplashActivity extends BaseActivity {

    private static final String TAG = "SplashActivity";

    public static AppInfo APP_INFO;
    public static UserDataModel USER_DATA;
    public static boolean DISPLAY_UPDATE_NOTIFIER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiUtils.setNoLimitForWindow();
        uiUtils.setTooltipColor(R.color.white);

        MobileAds.initialize(this, initializationStatus -> {
            Log.d(TAG, "onCreate: >> ads init completed");
        });

        int SPLASH_DISPLAY_LENGTH = 2000;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                routing.navigate(HomeActivity.class, true);

            }
        }, SPLASH_DISPLAY_LENGTH);

    }
    @Override
    public void onBackPressed() {
        return;
    }
}
