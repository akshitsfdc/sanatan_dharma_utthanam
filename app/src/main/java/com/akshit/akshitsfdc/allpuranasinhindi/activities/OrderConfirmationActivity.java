package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akshit.akshitsfdc.allpuranasinhindi.BuildConfig;
import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.models.AppInfo;

import com.akshit.akshitsfdc.allpuranasinhindi.models.UserOrderList;
import com.akshit.akshitsfdc.allpuranasinhindi.models.UserOrderModel;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.FileUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class OrderConfirmationActivity extends MainActivity implements PaymentResultListener {

    private static final String TAG = "OrderConfirmationActivi";

    private UserOrderModel userOrderModel;
    private FileUtils fileUtils;
    private ProgressBar progress;

    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_order_confirmation);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_order_confirmation, null, false);
        drawer.addView(contentView, 0);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        fileUtils = new FileUtils(OrderConfirmationActivity.this);
        progress = findViewById(R.id.progressOuter);

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
        Intent intent=getIntent();
        userOrderModel =(UserOrderModel) intent.getSerializableExtra("userOrderModel");

        showConfirmationDetails(userOrderModel);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser == null){
            fileUtils.showLongToast("You are not logged in please login first to continue");
        }
        if(!fileUtils.isNetworkConnected()){
            fileUtils.showLongToast("Please connect to the internet to continue.");
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

    public void startPayment(UserOrderModel userOrderModel) {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        showPB(true);
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            int total = Math.round(userOrderModel.getBook().getPrice()+userOrderModel.getBook().getDeliveryCharge());

            if (BuildConfig.DEBUG) {
                co.setKeyID(SplashActivity.APP_INFO.getPaymentApiSandbox());
            }else {
                co.setKeyID(SplashActivity.APP_INFO.getPaymentApiProduction());
            }

            JSONObject options = new JSONObject();
            options.put("key", SplashActivity.APP_INFO.getPaymentApiSandbox());
            options.put("name", "All Puranas Hindi");
            options.put("description", "Book Purchase");
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", String.valueOf(total*100));

            JSONObject preFill = new JSONObject();
            preFill.put("email", userOrderModel.getEmail());
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
    private void showConfirmationDetails(UserOrderModel userOrderModel){

        ImageView bookImage = findViewById(R.id.bookImage);
        ProgressBar progress = findViewById(R.id.progress);
        TextView title = findViewById(R.id.title);
        TextView price = findViewById(R.id.price);
        TextView deliveryCharge = findViewById(R.id.deliveryCharge);
        TextView total = findViewById(R.id.total);
        TextView deliveryText = findViewById(R.id.deliveryText);
        Button buyButton = findViewById(R.id.buyButton);

        DecimalFormat df = new DecimalFormat("#");
        title.setText(userOrderModel.getBook().getName());
        String priceText;
        priceText = "Price: "+getString(R.string.rs)+""+df.format(userOrderModel.getBook().getPrice());
        price.setText(priceText);
        priceText = "Delivery: "+getString(R.string.rs)+""+df.format(userOrderModel.getBook().getDeliveryCharge());
        deliveryCharge.setText(priceText);

        priceText = "Total: "+getString(R.string.rs)+""+df.format(userOrderModel.getBook().getPrice() + userOrderModel.getBook().getDeliveryCharge());
        total.setText(priceText);

        priceText = "Pay ("+getString(R.string.rs)+""+df.format(userOrderModel.getBook().getPrice() + userOrderModel.getBook().getDeliveryCharge())+")";
        buyButton.setText(priceText);

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!fileUtils.isNetworkConnected()){
                    fileUtils.showLongToast("Please connect to the internet to continue.");
                    return;
                }
                startPayment(userOrderModel);
            }
        });

        String deliveryString = userOrderModel.getAddress().getAddress()+" "+userOrderModel.getAddress().getLandmark()+" PIN: "
                +userOrderModel.getAddress().getPin()+" City: "+userOrderModel.getAddress().getCity()+" State: "+userOrderModel.getAddress().getState();

        deliveryText.setText(deliveryString);

        Glide.with(OrderConfirmationActivity.this).load(userOrderModel.getBook().getPicUrl()).listener(new RequestListener<Drawable>() {
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

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {

        showPB(true);
        saveOrder();

    }

    @Override
    public void onPaymentError(int code, String response) {

        navigateToOrderCompleted(false);
    }

    private void navigateToOrderCompleted(boolean isSuccess){
        Intent intent = new Intent(OrderConfirmationActivity.this, OrderCompletedActivity.class);
        intent.putExtra("isSuccess", isSuccess);
        intent.putExtra("isHard", true);
        startActivity(intent);
        finish();
    }

    public static String getCurrentDate(){

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(c);
    }
    public static String getCurrentTime(){

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        String formattedDate = simpleDateFormat.format(c);
        return formattedDate;
    }
    private void saveOrder(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRefUser = db.collection("user_data").document(currentUser.getUid()).collection("orders").document(currentUser.getUid());
        final DocumentReference docRefServer = db.collection("book_orders").document(currentUser.getUid());

        userOrderModel.setuId(currentUser.getUid());
        userOrderModel.setOrderDate(getCurrentDate());
        userOrderModel.setOrderTime(getCurrentTime());

        //creating Calendar instance
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        //Returns current time in millis
        long timeMilli = calendar.getTimeInMillis();

        String orderId = ""+timeMilli;

        userOrderModel.setOrderId(orderId);
        userOrderModel.setPaid(true);


        docRefServer.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    DocumentSnapshot document = task.getResult();

                    if(document.exists()){
                        //User already has some order
                        UserOrderList userOrderList = document.toObject(UserOrderList.class);

                        userOrderList.getOrderList().add(userOrderModel);

                        saveOrderAtServer(docRefServer, docRefUser, userOrderList);


                    }else{
                        //Users first order
                        UserOrderList userOrderList = new UserOrderList();
                        ArrayList<UserOrderModel> userOrderModels= new ArrayList<>();
                        userOrderModels.add(userOrderModel);
                        userOrderList.setOrderList(userOrderModels);

                        saveOrderAtServer(docRefServer, docRefUser, userOrderList);
                    }
                }
            }
        });
    }

    private void saveOrderAtServer(DocumentReference docRefServer, DocumentReference docRefUser,  UserOrderList userOrderList){

        docRefServer.set(userOrderList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    //order list added to server side
                    docRefUser.set(userOrderList).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //order list added to client side
                            if(task.isSuccessful()){
                                fileUtils.showLongToast("Order placed successfully");
                                navigateToOrderCompleted(true);
                                hidePB(true);
                            }else{
                                //Failed to add list at server side
                                fileUtils.showLongToast("Failed to add order at user end");
                                navigateToOrderCompleted(true);
                                hidePB(true);
                            }
                        }
                    });
                }else{
                    //Failed to add list at client side
                    fileUtils.showLongToast("Failed to add order, please contact support");
                    navigateToOrderCompleted(false);
                    hidePB(true);
                }
            }
        });
    }
}
