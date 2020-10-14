package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.adapters.HardCopyRecyclerViewAdapter;
import com.akshit.akshitsfdc.allpuranasinhindi.adapters.OrdersRecyclerViewAdapter;
import com.akshit.akshitsfdc.allpuranasinhindi.models.HardCopyDB;
import com.akshit.akshitsfdc.allpuranasinhindi.models.HardCopyModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.UserOrderDB;
import com.akshit.akshitsfdc.allpuranasinhindi.models.UserOrderList;
import com.akshit.akshitsfdc.allpuranasinhindi.models.UserOrderModel;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.FileUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class UserOrdersActivity extends MainActivity {

    private static final String TAG = "UserOrdersActivity";

    private ProgressBar progress;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FileUtils fileUtils;
    private RelativeLayout emptyView;
    private RelativeLayout internetLostView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_user_orders);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_user_orders, null, false);
        drawer.addView(contentView, 0);

        progress = findViewById(R.id.progress);
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = findViewById(R.id.recycler_view);
        emptyView = findViewById(R.id.emptyView);
        internetLostView = findViewById(R.id.internetLostView);
        fileUtils = new FileUtils(UserOrdersActivity.this);


        recyclerView.setLayoutManager(new LinearLayoutManager(UserOrdersActivity.this));

        recyclerView.addItemDecoration(new DividerItemDecoration(UserOrdersActivity.this,
                DividerItemDecoration.VERTICAL));

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

        loadOrderList();
    }

    private void loadOrderList(){

        if(!fileUtils.isNetworkConnected()){
            fileUtils.showLongToast("You are not connected to the internet.");
            internetLostView.setVisibility(View.VISIBLE);
            return;
        }
        if(currentUser == null){
            fileUtils.showLongToast("You are not logged in, please log in again..");
            return;
        }
        showPB(true);

        db.collection("book_orders").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        UserOrderList userOrderList = documentSnapshot.toObject(UserOrderList.class);

                        if(userOrderList.getOrderList() == null){
                            fileUtils.showLongToast("No order details fetched.");
                            emptyView.setVisibility(View.VISIBLE);
                        }else {
                            if(userOrderList.getOrderList().size() <=0){
                                fileUtils.showLongToast("No order yet.");
                                emptyView.setVisibility(View.VISIBLE);
                            }else {
                                Collections.reverse(userOrderList.getOrderList());
                                populateList(userOrderList.getOrderList());
                            }
                        }

                        hidePB(true);
                    }else {
                        fileUtils.showLongToast("No order details fetched.");
                        emptyView.setVisibility(View.VISIBLE);
                        hidePB(true);
                    }
                }else {
                    fileUtils.showLongToast("Something went wrong, please try later.");
                    emptyView.setVisibility(View.VISIBLE);
                    hidePB(true);
                }
            }
        });

    }


    @Override
    public void onBackPressed() {
        if (super.drawer.isDrawerOpen(GravityCompat.START)) {
            super.drawer.closeDrawer(GravityCompat.START);
        } else {
            navigateToHome();
        }
    }

    private void navigateToHome(){
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();

    }

    private void populateList(ArrayList<UserOrderModel> hardCopyModels){

        OrdersRecyclerViewAdapter adapter = new OrdersRecyclerViewAdapter(UserOrdersActivity.this, hardCopyModels);
        recyclerView.setAdapter(adapter);
    }
    private void showPB(boolean loadingActive){
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        //WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if(loadingActive){
            progress.setVisibility(View.VISIBLE);
        }


    }
    private void hidePB(boolean loadingActive){
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        if(loadingActive) {
            progress.setVisibility(View.GONE);
        }

    }
}
