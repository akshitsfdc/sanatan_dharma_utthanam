package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.FileUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import java.util.Objects;

public class LoginEmail extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginEmail";

    private static final int RC_SIGN_IN = 9001;

    private ProgressBar progressBar;

    private EditText mEmailField;
    private EditText mPasswordField;

    private TextView forgotPwd;
    String brand;
    String model;
    String pin;
    String type;
    FileUtils fileUtils;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        progressBar = findViewById(R.id.progress);
        fileUtils = new FileUtils(LoginEmail.this);


        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);

        forgotPwd = findViewById(R.id.forgotPwd);

        // Buttons
        findViewById(R.id.emailSignInButton).setOnClickListener(this);

        findViewById(R.id.googleSignInButton).setOnClickListener(this);


        findViewById(R.id.signUpNow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goToSignUp();
            }
        });


        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        forgotPwd.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void goToSignUp(){

        Intent intent = new Intent(LoginEmail.this, SignUpEmailActivity.class);
        startActivity(intent);
        //finish();
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();

    }
    // [END on_start_check_user]


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        // [START_EXCLUDE silent]
        showPB();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hidePB();
                        // [END_EXCLUDE]
                    }
                });
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);


        if (!validateForm()) {
            return;
        }

        if(!fileUtils.isNetworkConnected()){
            fileUtils.showLongToast("You are not connected to the internet!");
            return;
        }

        showPB();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            if(!user.isEmailVerified()){
                                sendEmailVerification();
                            }else{
                                updateUI(user);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginEmail.this, "Email or password is invalid!",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
        // [END sign_in_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }
        if(!TextUtils.isEmpty(email) && !isValidEmail(email)){
            valid = false;
            mEmailField.setError("Enter a valid email.");
        }else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        hidePB();
        if (user != null) {
            navigate();
        } else {
            Toast.makeText(LoginEmail.this, "Something went wrong!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // [START signin]
    private void googleSignIn() {
        if(!fileUtils.isNetworkConnected()){
            fileUtils.showLongToast("You are not connected to the internet!");
            return;
        }

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void navigate(){
        Intent intent = new Intent(LoginEmail.this, HomeActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.emailSignInButton) {
            hideKeyboard();
            signIn(mEmailField.getText().toString().trim(), mPasswordField.getText().toString().trim());

        } else if (i == R.id.googleSignInButton) {
            hideKeyboard();
            googleSignIn();
        }else if(i == R.id.forgotPwd){
            navigateToPasswordReset();
        }

    }

    private void navigateToPasswordReset(){
        Intent intent = new Intent(LoginEmail.this, PasswordResetActivity.class);
        startActivity(intent);
        //SignInActivity.this.finish();
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
        try{
            final InputMethodManager imm = (InputMethodManager) LoginEmail.this.getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(Objects.requireNonNull(LoginEmail.this.getCurrentFocus()).getWindowToken(), 0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void sendEmailVerification() {

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();

        showPB();
        try{
            assert user != null;
            user.sendEmailVerification()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(LoginEmail.this,
                                        "Verification email sent to " + user.getEmail()+" please verify",
                                        Toast.LENGTH_SHORT).show();
                                navigateToEmailVerification(true);
                            } else {
                                Log.e(TAG, "sendEmailVerification", task.getException());
                                Toast.makeText(LoginEmail.this,
                                        "Failed to send verification email to "+ user.getEmail()+" this might not be a valid email",
                                        Toast.LENGTH_SHORT).show();

                                navigateToEmailVerification(false);
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

    private void navigateToEmailVerification(boolean isEmailSent){
        Intent intent = new Intent(LoginEmail.this, EmailVarificationActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("isEmailSent", isEmailSent);
        startActivity(intent);
        finish();
    }
}
