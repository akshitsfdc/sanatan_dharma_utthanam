package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.adapters.UpdeshRecyclerViewAdapter;
import com.akshit.akshitsfdc.allpuranasinhindi.fragments.PostFragment;
import com.akshit.akshitsfdc.allpuranasinhindi.models.UpdeshModel;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.FileUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;

public class UpdeshActivity extends MainActivity {

    private ProgressBar progress;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FileUtils fileUtils;
    private String type;
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private UpdeshRecyclerViewAdapter adapter;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<UpdeshModel> updeshModels;

    private DocumentSnapshot lastVisible;
    private boolean listended = false;
    private final int listLimit = 10;
    public static UpdeshActivity updeshActivity;
    private PostFragment postFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updesh);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        updeshActivity = this;

        findViewById(R.id.backImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.addUpdeshImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // addUpdesh();
                showPostFragment();         }
        });
        findViewById(R.id.addUpdeshActivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // addUpdesh();
                showPostFragment();         }
        });
        fileUtils = new FileUtils(UpdeshActivity.this);
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        progress = findViewById(R.id.progress);
        recyclerView = findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        paginationScroll();

        loadUpdesha(true);


    }

    private void showPostFragment(){

        postFragment = new PostFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.parentLayout, postFragment,"post_fragment");
        transaction.addToBackStack(null);
        transaction.commit();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101 && adapter != null){
            adapter.onActivityResult(requestCode, resultCode, data);
        }else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            postFragment.profileUpdateFragment.onActivityResult(requestCode, resultCode, data);
        }

    }
    private void paginationScroll(){
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) { //check for scroll down
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            if(!listended){
                                loadUpdesha(false);
                            }
                        }
                    }
                }
            }
        });
    }
    private void loadUpdesha(boolean isFirstTime){

        Query query;

        if(isFirstTime){
            query = db.collection("updesh_collection")
                    .orderBy("createdMilliseconds",  Query.Direction.DESCENDING)
                    .limit(listLimit);
        }else {
            query = db.collection("updesh_collection")
                    .orderBy("createdMilliseconds", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(listLimit);
        }

    showPB(true);
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {

                        if(documentSnapshots.size() <= 0){
                            fileUtils.showShortToast("End of the list!");
                            listended = true;
                            loading = true;
                            hidePB(true);
                            return;
                        }
                        // Get the last visible document
                       lastVisible = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size()-1 );

                        ArrayList<UpdeshModel> updeshModels = new ArrayList<>();
                        for (DocumentSnapshot document : documentSnapshots.getDocuments()) {
                            updeshModels.add(document.toObject(UpdeshModel.class));
                        }
                        if(isFirstTime){
                            populateList(updeshModels);
                        }else {
                            adapter.addData(updeshModels);
                        }
                        hidePB(true);
                        loading = true;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        fileUtils.showShortToast("Load failed!");
                        loading = true;
                        hidePB(true);
                    }
        });


    }

    public void notifyDataset(){
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }

    public void reloadData(){
        loadUpdesha(true);
    }
    private void populateList(ArrayList<UpdeshModel> updeshModels){

        adapter = new UpdeshRecyclerViewAdapter(UpdeshActivity.this, updeshModels);
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
