package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.adapters.HardCopyRecyclerViewAdapter;
import com.akshit.akshitsfdc.allpuranasinhindi.adapters.SoftCopyRecyclerViewAdapter;
import com.akshit.akshitsfdc.allpuranasinhindi.models.HardCopyDB;
import com.akshit.akshitsfdc.allpuranasinhindi.models.HardCopyModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SoftCopyModel;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.FileUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class HardCopyBookDashboardActivity extends MainActivity {

    private static final String TAG = "HardCopyBookDashboard";

    private ProgressBar progress;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FileUtils fileUtils;

    private boolean fromHome;

    private RelativeLayout internetLostView;
    private RelativeLayout emptyView;

    private HardCopyRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_hard_copy_book_dashboard);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_hard_copy_book_dashboard, null, false);
        drawer.addView(contentView, 0);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        progress = findViewById(R.id.progress);
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = findViewById(R.id.recycler_view);
        internetLostView = findViewById(R.id.internetLostView);
        emptyView = findViewById(R.id.emptyView);

        fileUtils = new FileUtils(HardCopyBookDashboardActivity.this);


        recyclerView.setLayoutManager(new GridLayoutManager(HardCopyBookDashboardActivity.this, 2));

        recyclerView.addItemDecoration(new DividerItemDecoration(HardCopyBookDashboardActivity.this,
                DividerItemDecoration.VERTICAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(HardCopyBookDashboardActivity.this,
                DividerItemDecoration.HORIZONTAL));

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        fromHome = getIntent().getExtras().getBoolean("fromHome");

        loadAllBooksList();
    }

    private void loadAllBooksList(){

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

        db.collection("hardCopy").orderBy("priority", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try{
                    if (task.isSuccessful()) {
                        ArrayList<HardCopyModel> hardCopyModels = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                           HardCopyDB hardCopyDB = document.toObject(HardCopyDB.class);
                           hardCopyModels.addAll(hardCopyDB.getBookCollection());
                        }
                        populateList(hardCopyModels);
                        // Log.d(TAG, " name : "+leaderboardModels.get(0).getPoints()+" : "+leaderboardModels.size());

                        hidePB(true);


                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        hidePB(true);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally {
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
        super.onBackPressed();

    }

    private void populateList(ArrayList<HardCopyModel> hardCopyModels){
        if(hardCopyModels.size() <= 0){
            fileUtils.showLongToast("There is no item to show.");
            emptyView.setVisibility(View.VISIBLE);
        }
        if(fromHome){
            String scrollToId = Objects.requireNonNull(getIntent().getExtras()).getString("scrollToId");
            int scrollToPosition = getItemPosition(hardCopyModels, scrollToId);
            adapter = new HardCopyRecyclerViewAdapter(HardCopyBookDashboardActivity.this, hardCopyModels,true, scrollToPosition, recyclerView);
        }else {
            adapter = new HardCopyRecyclerViewAdapter(HardCopyBookDashboardActivity.this, hardCopyModels,false, 0, recyclerView);
        }
        recyclerView.setAdapter(adapter);
    }
    private int getItemPosition(ArrayList<HardCopyModel> hardCopyModels, String key){
        int position = 0;
        for(int i = 0 ; i < hardCopyModels.size(); ++i){
            if(TextUtils.equals(hardCopyModels.get(i).getBookId(), key)){
                position = i;
                break;
            }
        }

        return  position;
    }
    private void showPB(boolean loadingActive){
        if(loadingActive){
            progress.setVisibility(View.VISIBLE);
        }


    }
    private void hidePB(boolean loadingActive){
        if(loadingActive) {
            progress.setVisibility(View.GONE);
        }

    }
}
