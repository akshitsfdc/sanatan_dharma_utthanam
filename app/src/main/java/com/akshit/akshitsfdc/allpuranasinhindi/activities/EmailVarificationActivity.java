package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVarificationActivity extends AppCompatActivity {

    private static final String TAG = "EmailVarificationActivi";
    
    private Button emailSignInButton;
    private ProgressBar progress;
    private boolean isEmailSent;
    Button resendverificationEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_varification);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        emailSignInButton = findViewById(R.id.emailSignInButton);
        progress = findViewById(R.id.progress);
        resendverificationEmail = findViewById(R.id.resendverificationEmail);

        try{
            isEmailSent = getIntent().getExtras().getBoolean("isEmailSent");

            if(isEmailSent){
                hideResendVerification();
            }else {
                showResendVerification();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        emailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                authorizeUser();

            }
        });

        findViewById(R.id.backImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        resendverificationEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailVerification();
            }
        });

    }

    private void authorizeUser(){

        showPB();
        FirebaseAuth.getInstance().getCurrentUser().reload().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if(currentUser == null){
                    Toast.makeText(EmailVarificationActivity.this,
                            "Something went wrong in signing up the user, please try again.",
                            Toast.LENGTH_SHORT).show();

                    hidePB();
                }else{

                    if(currentUser.isEmailVerified()){
                        navigate();
                    }else{
                        Toast.makeText(EmailVarificationActivity.this,
                                "Email not verified yet, please verify your email address.",
                                Toast.LENGTH_SHORT).show();
                    }
                    hidePB();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EmailVarificationActivity.this,
                        "Email not verified yet, please verify your email address. Or try again later.",
                        Toast.LENGTH_SHORT).show();
                hidePB();
            }
        });


    }

    private void navigate(){

        Intent intent = new Intent(EmailVarificationActivity.this, HomeActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showPB(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progress.setVisibility(View.VISIBLE);

    }
    private void hidePB(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progress.setVisibility(View.GONE);

    }

    private void sendEmailVerification() {

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        showPB();
        try{
            assert user != null;
            user.sendEmailVerification()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(EmailVarificationActivity.this,
                                        "Verification email sent to " + user.getEmail()+" please verify",
                                        Toast.LENGTH_SHORT).show();
                                hideResendVerification();
                            } else {
                                Log.e(TAG, "sendEmailVerification", task.getException());
                                Toast.makeText(EmailVarificationActivity.this,
                                        "Failed to send verification email to "+ user.getEmail()+" this might not be a valid email",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(user);
                            }
                            // [END_EXCLUDE]
                            hidePB();
                        }
                    });
            // [END send_email_verification]
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void hideResendVerification(){
        resendverificationEmail.setVisibility(View.GONE);
    }
    private void showResendVerification(){
        resendverificationEmail.setVisibility(View.VISIBLE);
    }
}
