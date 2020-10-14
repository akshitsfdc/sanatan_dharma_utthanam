package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.adapters.HardCopyRecyclerViewAdapter;
import com.akshit.akshitsfdc.allpuranasinhindi.fragments.SearchFragment;
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
import java.util.List;
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

    //pagination

    private boolean loading = true;
    private final int listLimit = 5;
    private DocumentSnapshot lastVisible;
    private boolean listended = false;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private GridLayoutManager mLayoutManager;
    private CardView toolbarCard;

    private RelativeLayout lazyProgress;

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
        lazyProgress = findViewById(R.id.lazyProgress);

        fileUtils = new FileUtils(HardCopyBookDashboardActivity.this);

        mLayoutManager = new GridLayoutManager(HardCopyBookDashboardActivity.this, 2);

        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(HardCopyBookDashboardActivity.this,
                DividerItemDecoration.VERTICAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(HardCopyBookDashboardActivity.this,
                DividerItemDecoration.HORIZONTAL));

        toolbarCard = findViewById(R.id.toolbarCard);

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

        fromHome = getIntent().getExtras().getBoolean("fromHome");

        if(fromHome){
            String id = getIntent().getExtras().getString("scrollToId");
            loadBookById(id);
        }else{
            loadAllBooksList();
            paginationScroll();
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


        MenuItem searchItem = menu.findItem(R.id.action_search);

        searchItem.setVisible(true);

        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                showSearchFragment();
                return false;
            }
        });
        return true;
    }

    private void loadBookById(String id){

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

        Query query = db.collection("physical_books")
                .whereEqualTo("bookId", id)
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
    private void loadAllBooksList(){

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

        Query query = db.collection("physical_books")
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
    private void loadMore(){

        Query query;

        if(lastVisible == null){
            return;
        }

        query = db.collection("physical_books")
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
    @Override
    public void onBackPressed() {
        showToolBar();
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


        adapter = new HardCopyRecyclerViewAdapter(HardCopyBookDashboardActivity.this, hardCopyModels);

        recyclerView.setAdapter(adapter);
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
    private void showLazyProgress(){
        lazyProgress.setVisibility(View.VISIBLE);
    }
    private void hideLazyProgress(){
        lazyProgress.setVisibility(View.GONE);
    }


    private void showSearchFragment(){

        try{
            SearchFragment searchFragment = new SearchFragment();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.parent, searchFragment,"search_fragment");
            transaction.addToBackStack(null);
            transaction.commit();
            hideToolbar();

        }catch (Exception e){
            e.printStackTrace();
        }


    }
    private void hideToolbar(){
        toolbarCard.setVisibility(View.GONE);
    }
    public void showToolBar(){
        toolbarCard.setVisibility(View.VISIBLE);
    }
}
