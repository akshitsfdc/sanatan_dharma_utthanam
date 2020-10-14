package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.adapters.SearchHardRecyclerViewAdapter;
import com.akshit.akshitsfdc.allpuranasinhindi.adapters.SearchSoftBooksRecyclerViewAdapter;
import com.akshit.akshitsfdc.allpuranasinhindi.models.HardCopyModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SoftCopyModel;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.FileUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchHardCopyActivity extends AppCompatActivity {


    private String key;

    private static final String TAG = "SoftPuranaDashboardActi";

    private ProgressBar progress;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FileUtils fileUtils;

    private SearchHardRecyclerViewAdapter adapter;

    private RelativeLayout emptyView;

    //pagination

    private boolean loading = true;
    private final int listLimit = 8;
    private DocumentSnapshot lastVisible;
    private boolean listended = false;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    private LinearLayoutManager mLayoutManager;

    private RelativeLayout lazyProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_hard_copy);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        progress = findViewById(R.id.progress);
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = findViewById(R.id.recycler_view);
        emptyView = findViewById(R.id.emptyView);

        ImageView backButton = findViewById(R.id.backButton);
        lazyProgress = findViewById(R.id.lazyProgress);

        backButton.setOnClickListener(view -> {
            onBackPressed();
        });
        fileUtils = new FileUtils(SearchHardCopyActivity.this);

        mLayoutManager = new LinearLayoutManager(SearchHardCopyActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);


        key = getIntent().getExtras().getString("key");
        if(key != null){
            key = key.trim();
        }
        loadBooks(key);


    }


    private void loadBooks(String key){



        paginationScroll();

        if(!fileUtils.isNetworkConnected()){
            fileUtils.showLongToast("You are not connected to the internet.");
            findViewById(R.id.internetLostView).setVisibility(View.VISIBLE);
            return;
        }
        if(currentUser == null){
            fileUtils.showLongToast("You are not logged in, please log in again..");
            return;
        }

        showPB(true);

        Query query;

        query = db.collection("physical_books")
                .whereArrayContains("searchKeywords", key==null?"":key)
                .orderBy("priority").limit(listLimit);



        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                hidePB(true);
                if(queryDocumentSnapshots.size() <= 0){

                    fileUtils.showLongToast("No result found of this type.");
                    emptyView.setVisibility(View.VISIBLE);
                    // hidePB(true);
                    listended = true;
                    loading = true;
                    return;
                }

                // Get the last visible document
                lastVisible = queryDocumentSnapshots.getDocuments()
                        .get(queryDocumentSnapshots.size()-1 );

                ArrayList<HardCopyModel> hardCopyModels = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    hardCopyModels.add(document.toObject(HardCopyModel.class));
                }
                populateList(hardCopyModels);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hidePB(true);
                e.printStackTrace();
            }
        });
    }

    private void loadMore(){

        Query query;


        if(lastVisible == null){
            return;
        }

        query = db.collection("physical_books")
                .whereArrayContains("searchKeywords", key==null?"":key)
                .orderBy("priority")
                .startAfter(lastVisible)
                .limit(listLimit);


        showLazyProgress();

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {

                        hideLazyProgress();

                        if(documentSnapshots.size() <= 0){
                            fileUtils.showShortToast("End of the list!");
                            listended = true;
                            loading = true;
                            return;
                        }
                        // Get the last visible document
                        lastVisible = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size()-1 );

                        ArrayList<HardCopyModel> hardCopyModels = new ArrayList<>();
                        for (DocumentSnapshot document : documentSnapshots.getDocuments()) {
                            hardCopyModels.add(document.toObject(HardCopyModel.class));
                        }
                        Log.d(TAG, "onSuccess: >> "+hardCopyModels);
                        adapter.addData(hardCopyModels);
                        loading = true;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideLazyProgress();
                        fileUtils.showShortToast("Load failed!");
                        loading = true;
                    }
                });

    }
    private void populateList(ArrayList<HardCopyModel> hardCopyModels){

        if(hardCopyModels.size() <= 0){
            emptyView.setVisibility(View.VISIBLE);
        }
        adapter = new SearchHardRecyclerViewAdapter(SearchHardCopyActivity.this, hardCopyModels);

        recyclerView.setAdapter(adapter);
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
                                loadMore();
                            }
                        }
                    }
                }
            }
        });
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
    private void showLazyProgress(){
        lazyProgress.setVisibility(View.VISIBLE);
    }
    private void hideLazyProgress(){
        lazyProgress.setVisibility(View.GONE);
    }
}