package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.models.AddressModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.HardCopyModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.UserOrderModel;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.FileUtils;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AddressDetailsActivity extends MainActivity {

    private static final String TAG = "AddressDetailsActivity";

    private RequestQueue mQueue;

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText address1EditText;
    private EditText address2EditText;
    private EditText countryEditText;
    private EditText pinEditText;
    private EditText stateEditText;
    private EditText cityEditText;
    private Button buyButton;

    private ProgressBar progress;
    private FileUtils fileUtils;
    private HardCopyModel hardCopyModel;

    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_address_details);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_address_details, null, false);
        drawer.addView(contentView, 0);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

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

        Intent intent=getIntent();
        hardCopyModel =(HardCopyModel) intent.getSerializableExtra("hardCopyModel");
        getReferences();

        applyEventListeners();

        if(currentUser != null){
            emailEditText.setText(currentUser.getEmail());
            nameEditText.setText(currentUser.getDisplayName());
        }else{
            fileUtils.showLongToast("You are not logged in.");
        }


    }

    private void getReferences(){

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        address1EditText = findViewById(R.id.address1EditText);
        address2EditText = findViewById(R.id.address2EditText);
        countryEditText = findViewById(R.id.countryEditText);
        pinEditText = findViewById(R.id.pinEditText);
        stateEditText = findViewById(R.id.stateEditText);
        cityEditText = findViewById(R.id.cityEditText);
        buyButton = findViewById(R.id.buyButton);

        //For pincode API
        mQueue = Volley.newRequestQueue(this);
        progress = findViewById(R.id.progress);
        fileUtils = new FileUtils(AddressDetailsActivity.this);

    }

    private void applyEventListeners(){

        pinEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                cityEditText.setText("");
                stateEditText.setText("");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        pinEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    String text = pinEditText.getText().toString();
                    if(!text.isEmpty() && text != null){
                        checkPinStatus();
                    }else{
                        pinEditText.setText("");
                    }

                }
            }
        });

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeOrder();
            }
        });
    }
    private void composeOrder(){
        if(!validateForm()){
            return;
        }
        AddressModel addressModel = new AddressModel(getEditTextValue(nameEditText),getEditTextValue(countryEditText),
                getEditTextValue(stateEditText), getEditTextValue(pinEditText), getEditTextValue(cityEditText),getEditTextValue(address1EditText),
                getEditTextValue(address2EditText));
        UserOrderModel userOrderModel = new UserOrderModel();

        userOrderModel.setBook(hardCopyModel);
        userOrderModel.setAddress(addressModel);
        userOrderModel.setEmail(getEditTextValue(emailEditText));
        userOrderModel.setName(getEditTextValue(nameEditText));
        userOrderModel.setPaid(false);
        userOrderModel.setPhone(getEditTextValue(phoneEditText));
        userOrderModel.setStatus("Order Placed");
        //to be set order date
        //to be set order time

        navigateToOrderConfirmation(userOrderModel);
    }

    private void navigateToOrderConfirmation(UserOrderModel userOrderModel){
        Intent intent = new Intent(AddressDetailsActivity.this, OrderConfirmationActivity.class);
        intent.putExtra("userOrderModel",userOrderModel);
        startActivity(intent);
    }
    private String getEditTextValue(EditText editText){
        return editText.getText().toString().trim();
    }


    public void checkPinStatus(){

        Log.d(TAG, "checkPinStatus");
        if(!fileUtils.isNetworkConnected()){
            fileUtils.showLongToast("You are not connected to the internet!");
            return;
        }

        String text = pinEditText.getText().toString().trim();
        String url = "http://postalpincode.in/api/pincode/"+text;

        getCityState(url, cityEditText, stateEditText);

    }

    private void getCityState(String url, final EditText t1, final EditText t2) {

        Log.d(TAG, "getCityState");
        //String url = "https://api.myjson.com/bins/kp9wz";

        showPB(true);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, "getCityState >> onResponse");
                        //List<String> li = new ArrayList<>();
                        //li.add(SPINNER_TEXT);
                        try {
                            JSONArray jsonArray = response.getJSONArray("PostOffice");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject postOffice = jsonArray.getJSONObject(i);

                                String district = postOffice.getString("District");
                                String state = postOffice.getString("State");

                                //mTextViewResult.append(firstName + ", " + String.valueOf(age) + ", " + mail + "\n\n");

                                if(i==0){
                                    t1.setText(district);
                                    t2.setText(state);
                                }

                                //li.add(postOffice.getString("Name").trim());
                                hidePB(true);

                            }
                            //setDropSpinner(li);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            hidePB(true);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "getCityState >> onErrorResponse");
                error.printStackTrace();
                hidePB(true);
            }
        });

        mQueue.add(request);
    }


    private boolean validateForm() {
        boolean valid = true;

        String name = nameEditText.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Required.");
            valid = false;
        } else {
            nameEditText.setError(null);
        }
        String phone = phoneEditText.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            phoneEditText.setError("Required.");
            valid = false;
        } else {
            phoneEditText.setError(null);
        }
        if (!TextUtils.isEmpty(phone) && phone.length() < 10) {
            phoneEditText.setError("Must Be 10 Digit Number.");
            valid = false;
        } else {
            //phoneEditText.setError(null);
        }

        String address = address1EditText.getText().toString().trim();
        if (TextUtils.isEmpty(address)) {
            address1EditText.setError("Required.");
            valid = false;
        } else {
            address1EditText.setError(null);
        }

        String city = cityEditText.getText().toString().trim();
        if (TextUtils.isEmpty(city)) {
            cityEditText.setError("Required.");
            valid = false;
        } else {
            cityEditText.setError(null);
        }
        String state = stateEditText.getText().toString().trim();
        if (TextUtils.isEmpty(state)) {
            stateEditText.setError("Required.");
            valid = false;
        } else {
            stateEditText.setError(null);
        }
        if (!validatePINForm()) {
            pinEditText.setError("Required.");
            valid = false;
        } else {
            pinEditText.setError(null);
        }

        return valid;
    }
    private boolean validatePINForm() {
        boolean valid = true;

        String pin = pinEditText.getText().toString().trim();
        if (TextUtils.isEmpty(pin)) {
            pinEditText.setError("Required.");
            valid = false;
        } else {
            pinEditText.setError(null);
        }
        if (!TextUtils.isEmpty(pin) && pin.length()<6) {
            pinEditText.setError("Must be at least 6 digits.");
            valid = false;
        } else {
            pinEditText.setError(null);
        }

        return valid;
    }
    private void showPB(boolean loadingActive){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if(loadingActive){
            progress.setVisibility(View.VISIBLE);
        }


    }
    private void hidePB(boolean loadingActive){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        if(loadingActive) {
            progress.setVisibility(View.GONE);
        }

    }

}
