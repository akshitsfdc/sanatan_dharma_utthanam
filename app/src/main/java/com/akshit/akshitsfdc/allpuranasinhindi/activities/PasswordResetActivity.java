package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.FileUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView msgTextView;
    private TextView emailTextView;
    private Button sendVerificationEmainButton;

    private ProgressBar progressBar;
    FileUtils fileUtils;

    private String TAG = "PasswordResetActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initializeObjects();

        sendVerificationEmainButton.setOnClickListener(this);

        findViewById(R.id.backImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initializeObjects(){
        emailTextView = findViewById(R.id.fieldEmail);
        msgTextView = findViewById(R.id.msgTxt);

        fileUtils = new FileUtils(PasswordResetActivity.this);

        sendVerificationEmainButton = findViewById(R.id.sendVerificationEmail);
        progressBar = findViewById(R.id.progress);
    }

    private boolean validateEmailForm() {
        boolean valid = true;
        String email = emailTextView.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            emailTextView.setError("Required.");
            valid = false;
        } else {
            emailTextView.setError(null);
        }
        return valid;
    }

    private void afterEmailSent(){
        msgTextView.setText("We have sent verification link to your email address please check your email and reset your password.");
        sendVerificationEmainButton.setText("Resend verification email");

    }
    public void sendPasswordReset() {

        if(!validateEmailForm()){
            return;
        }
        hideKeyboard();
        showPB();
        // [START send_password_reset]
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final String emailAddress = emailTextView.getText().toString().trim();

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            fileUtils.showLongToast("Verification email sent to "+emailAddress);
                            afterEmailSent();
                            hidePB();
                        }else{
                            fileUtils.showLongToast("Unsuccessful! Email entered, might be wrong.");
                            hidePB();
                        }
                    }
                });
        // [END send_password_reset]
    }
    @Override
    public void onClick(View view) {
        int i = view.getId();

        if(i == R.id.sendVerificationEmail){
            sendPasswordReset();
        }
    }

    private void showPB(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.VISIBLE);

    }
    private void hidePB(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.GONE);

    }

    private void hideKeyboard(){
        final InputMethodManager imm = (InputMethodManager) PasswordResetActivity.this.getSystemService(PasswordResetActivity.this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(PasswordResetActivity.this.getCurrentFocus().getWindowToken(), 0);
    }
}
