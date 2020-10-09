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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

public class SplashActivity extends AppCompatActivity implements IUnityAdsListener {

    private static final String TAG = "SplashActivity";

    public static AppInfo APP_INFO = new AppInfo();

    public static boolean DISPLAY_UPDATE_NOTIFIER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UnityAds.addListener(this);
        UnityAds.initialize(SplashActivity.this, getString(R.string.unity_ads_game_id), false); //production change

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setupCloudMessagingServices();


        int SPLASH_DISPLAY_LENGTH = 1000;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if(currentUser != null){
                    if(!currentUser.isEmailVerified()){
                        sendEmailVerification(currentUser);
                    }else{
                        navigateToHomeActivity();
                    }

                }else {
                    navigateToLoginActivity();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);

    }
    private void sendEmailVerification(final FirebaseUser user) {

        // Send verification email
        // [START send_email_verification]

        try{
            assert user != null;
            user.sendEmailVerification()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(SplashActivity.this,
                                        "Verification email sent to " + user.getEmail()+" please verify",
                                        Toast.LENGTH_SHORT).show();
                                navigateToEmailverificationActivity(true);
                            } else {
                                Log.e(TAG, "sendEmailVerification", task.getException());
                                Toast.makeText(SplashActivity.this,
                                        "Failed to send verification email to "+ user.getEmail() +" this might not be a valid email",
                                        Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut();

                                navigateToLoginActivity();
                            }
                            // [END_EXCLUDE]
                        }
                    });
            // [END send_email_verification]
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void navigateToHomeActivity(){

        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();

    }

    private void navigateToLoginActivity(){

        //Intent intent = new Intent(SplashActivity.this, LoginActivity.class);

        Intent intent = new Intent(SplashActivity.this, LoginEmail.class);

        startActivity(intent);
        finish();

    }
    private void navigateToEmailverificationActivity(boolean isEmailSent){

        Intent intent = new Intent(SplashActivity.this, EmailVarificationActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("isEmailSent", isEmailSent);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        return;
    }

    private void setupCloudMessagingServices(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                //Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
        // [END handle_data_extras]

        FirebaseMessaging.getInstance().subscribeToTopic("weather")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                           // msg = getString(R.string.msg_subscribe_failed);
                        }
                    }
                });

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                    }
                });

    }

    @Override
    public void onUnityAdsReady(String s) {
        UnityAds.removeListener(this);
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
