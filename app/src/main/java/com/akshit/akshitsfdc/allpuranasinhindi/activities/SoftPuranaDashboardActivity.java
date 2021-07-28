package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.adapters.SoftCopyRecyclerViewAdapter;
import com.akshit.akshitsfdc.allpuranasinhindi.fragments.SearchFragment;
import com.akshit.akshitsfdc.allpuranasinhindi.models.DisplayModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SoftCopyModel;
import com.akshit.akshitsfdc.allpuranasinhindi.service.SQLService;
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

public class SoftPuranaDashboardActivity extends MainActivity {

    private static final String TAG = "SoftPuranaDashboardActi";

    private ProgressBar progress;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FileUtils fileUtils;
    private String type;

    private SoftCopyRecyclerViewAdapter adapter;

    private RelativeLayout emptyView;
    private boolean fromHome;

    private boolean loadAllowed = true;
    private final int listLimit = 8;
    private DocumentSnapshot lastVisible;
    private boolean listEnded = false;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    private LinearLayoutManager mLayoutManager;

    private Toolbar toolbar;

    private CardView toolbarCard;

    private RelativeLayout lazyProgress;
    private SQLService sqlService;


    private DisplayModel displayModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_soft_purana_dashboard);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_soft_purana_dashboard, null, false);
        drawer.addView(contentView, 0);
        //drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        uiUtils.setSnakebarView(getSnakBarView(findViewById(R.id.snakebarLayout)));

        if(SplashActivity.USER_DATA != null && SplashActivity.USER_DATA.getPurchasedBooks() == null){
            SplashActivity.USER_DATA.setPurchasedBooks(new ArrayList<SoftCopyModel>());
        }

        sqlService = new SQLService(this);
        progress = findViewById(R.id.progress);
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = findViewById(R.id.recycler_view);
        emptyView = findViewById(R.id.emptyView);

        lazyProgress = findViewById(R.id.lazyProgress);

        fileUtils = new FileUtils(SoftPuranaDashboardActivity.this);


        toolbarCard = findViewById(R.id.toolbarCard);

        toolbar = findViewById(R.id.toolbar);

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

        initRecyclerView();

        boolean singleBook = (boolean)routing.getParam("singleBook");

        if(singleBook){
            singleBookFlow();
        }else {
            multiBookFlow();
        }

        if(TextUtils.equals(type, getString(R.string.offline_key))) {
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }



    }

    private void multiBookFlow(){



        type = (String) routing.getParam("type");

        fromHome = (boolean) routing.getParam("fromHome");

        try {
            displayModel = (DisplayModel)routing.getParam("displayModel");
        }catch (Exception e){
            e.printStackTrace();
        }

        if(!TextUtils.equals(type, getString(R.string.offline_key)) && !TextUtils.equals(type, getString(R.string.favorite_key))){
            showLoading();
        }

        loadBooks();
    }

    private void singleBookFlow(){
        SoftCopyModel softCopyModel = (SoftCopyModel) routing.getParam("softCopyModel");
        List<SoftCopyModel> softCopyModels = new ArrayList<>();
        softCopyModels.add(softCopyModel);
        adapter.addData(softCopyModels);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
               onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchItem.setVisible(true);

        searchItem.setOnMenuItemClickListener(menuItem -> {
            showSearchFragment();
            return false;
        });
        return true;
    }
    @Override
    public void onBackPressed() {

        showToolBar();
        boolean frgRem = removeFragmentFirst();
        if(frgRem){
            return;
        }
        if (super.drawer.isDrawerOpen(GravityCompat.START)) {
            super.drawer.closeDrawer(GravityCompat.START);
        }else {
            navigateToHome();
        }

    }

    private void navigateToHome(){
        super.onBackPressed();

    }

    private void loadBooks(){

        if(ifOfflineMode()){
            return;
        }

        paginationScroll();

        Query query;

        if(fromHome){
            query = getQueryForHome();
        }else if(TextUtils.equals(type, getString(R.string.parts_key))){
            query = getQueryForParts();
        }else {
            query = getQueryForDashBoard();
        }

        showLazyProgress();
        query.get()
                .addOnSuccessListener(documentSnapshots -> {


                    if(documentSnapshots.size() <= 0){
                        endList();
                        return;
                    }
                    if(shouldEndList(documentSnapshots.getDocuments().size())){
                        endList();
                    }else {
                        lastVisible = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size() - 1 );
                    }
                    List<SoftCopyModel> softCopyModels = new ArrayList<>();
                    for (DocumentSnapshot document : documentSnapshots.getDocuments()) {
                        if(document != null){
                            softCopyModels.add(document.toObject(SoftCopyModel.class));
                        }
                    }

                    if(softCopyModels.size() > 0){
                        adapter.addData(softCopyModels);
                    }

                    loadAllowed = true;
                    hideLazyProgress();
                    hideLoading();
                }).addOnFailureListener(e -> {
                    e.printStackTrace();
                    hideLazyProgress();
                    hideLoading();
                });
    }

    private boolean ifOfflineMode(){


        if(TextUtils.equals(type, getString(R.string.offline_key))){
            List<SoftCopyModel> offlineBookModels = sqlService.showOfflineBooks(null);

            if(offlineBookModels != null){
                if(offlineBookModels.size() > 0){
                    uiUtils.showShortSuccessSnakeBar("Swipe left to delete books");
                }
                adapter.addData(offlineBookModels);
            }

            return true;
        }

        if(TextUtils.equals(type, getString(R.string.favorite_key))){
            List<SoftCopyModel> softCopyModels = sqlService.showFavoriteBooks(null);
            if(softCopyModels != null){
                adapter.addData(softCopyModels);
            }
            return true;
        }
        return false;
    }
    private boolean shouldEndList(int totalItemCount){
        if(fromHome){
            if(totalItemCount <  displayModel.getLimit()){
                return true;
            }else {
                return false;
            }
        }else {
            if(totalItemCount <  listLimit){
                return true;
            }else {
                return false;
            }
        }
    }

    private void endList(){
        lastVisible = null;
        listEnded = true;
        loadAllowed = false;
        hideLazyProgress();
        hideLoading();
    }

    private Query getQueryForDashBoard(){

        String bookCollection = "digital_books";

        Query query;

        if(lastVisible != null){
            if(TextUtils.equals(type, "all")){
                query = db.collection(bookCollection)
                        .whereEqualTo("isOneOfThePart", false)
                        .orderBy("priority")
                        .startAfter(lastVisible)
                        .limit(listLimit);
            }else {
                query = db.collection("digital_books")
                        .whereEqualTo("type", type)
                        .whereEqualTo("isOneOfThePart", false)
                        .orderBy("priority")
                        .startAfter(lastVisible)
                        .limit(listLimit);
            }
        }else {

            if(TextUtils.equals(type, "all")){
                query = db.collection("digital_books")
                        .whereEqualTo("isOneOfThePart", false)
                        .orderBy("priority").limit(listLimit);
            }else {
                query = db.collection("digital_books")
                        .whereEqualTo("type", type)
                        .whereEqualTo("isOneOfThePart", false)
                        .orderBy("priority").limit(listLimit);
            }
        }


        return query;
    }

    private Query getQueryForHome(){

        String bookCollection = "digital_books";

        String displayKey = displayModel.getDisplayKey().toLowerCase();

        Query query;

        if(TextUtils.equals(displayKey, "new")){

            if(lastVisible != null){
                query = this.fireStoreService.getDB().collection(bookCollection)
                        .whereEqualTo("isOneOfThePart", false)
                        .orderBy("addedTime", Query.Direction.DESCENDING)
                        .startAfter(lastVisible)
                        .limit(displayModel.getLimit());
            }else {
                query = this.fireStoreService.getDB().collection(bookCollection)
                        .whereEqualTo("isOneOfThePart", false)
                        .orderBy("addedTime", Query.Direction.DESCENDING)
                        .limit(displayModel.getLimit());
            }

        }else {
            if(lastVisible != null){
                query = this.fireStoreService.getDB().collection(bookCollection)
                        .whereArrayContains("displayKeys", displayKey)
                        .orderBy("addedTime", Query.Direction.DESCENDING)
                        .startAfter(lastVisible)
                        .limit(displayModel.getLimit());
            }else {
                query = this.fireStoreService.getDB().collection(bookCollection)
                        .whereArrayContains("displayKeys", displayKey)
                        .orderBy("addedTime", Query.Direction.DESCENDING)
                        .limit(displayModel.getLimit());
            }


        }

        return query;
    }

    private Query getQueryForParts(){

        String bookCollection = "digital_books";



        SoftCopyModel softCopyModel = (SoftCopyModel) routing.getParam("softCopyModel");

        if(softCopyModel == null){
           return null;
        }
        Query query;


        if(lastVisible != null){

            query = db.collection(bookCollection)
                    .whereIn("bookId", softCopyModel.getBookParts())
                    .orderBy("priority")
                    .startAfter(lastVisible)
                    .limit(listLimit);
        }else {

            query = db.collection(bookCollection)
                    .whereIn("bookId", softCopyModel.getBookParts())
                    .orderBy("priority")
                    .limit(listLimit);
        }




        return query;
    }

    public void checkEmptyList(List<SoftCopyModel> softCopyModels){
        if(softCopyModels != null && softCopyModels.size() == 0){
            emptyView.setVisibility(View.VISIBLE);
        }else {
            emptyView.setVisibility(View.GONE);
        }
    }

    private void initRecyclerView(){

        mLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(mLayoutManager);

        adapter = new SoftCopyRecyclerViewAdapter(this, new ArrayList<>());

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

                    if (loadAllowed) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loadAllowed = false;
                            if(!listEnded){
                               loadBooks();
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

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

            try {
                int position = viewHolder.getAdapterPosition();
                if(adapter != null){
                    SoftCopyModel softCopyModel = adapter.getBookByPosition(position);
                    exitWarning(softCopyModel);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };

    private void deleteFromList(SoftCopyModel softCopyModel){

        if(adapter != null){
            adapter.removeData(softCopyModel);
        }
    }
    private void exitWarning(SoftCopyModel softCopyModel){
        new AlertDialog.Builder(this)
                .setMessage("Are you sure, you want to delete "+softCopyModel.getName()+" from your phone?")
                .setCancelable(true)
                .setPositiveButton("Yes, Delete It", (dialog, id) -> {
                    deleteFromList(softCopyModel);
                    deleteFromMemory(softCopyModel);
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    adapter.notifyDataSetChanged();
                })
                .show();
    }


    private void deleteFromMemory(SoftCopyModel softCopyModel){


        fileUtils.deleteFileFromUri(softCopyModel.getBookUri());
        fileUtils.deleteFileFromUri(softCopyModel.getCoverUri());

        int result = sqlService.deleteOfflineBook(softCopyModel.getBookId());

        adapter.notifyDataSetChanged();

    }



    private void showLazyProgress(){
        lazyProgress.setVisibility(View.VISIBLE);
    }
    private void hideLazyProgress(){
        lazyProgress.setVisibility(View.GONE);
    }

    private void hideToolbar(){
        toolbarCard.setVisibility(View.GONE);
    }
    public void showToolBar(){

        toolbarCard.setVisibility(View.VISIBLE);

        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }
}
