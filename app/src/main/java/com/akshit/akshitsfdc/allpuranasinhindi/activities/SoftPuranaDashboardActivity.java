package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.akshit.akshitsfdc.allpuranasinhindi.models.BookDisplaySliderModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SoftCopy;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SoftCopyModel;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.FileUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class SoftPuranaDashboardActivity extends MainActivity {

    private static final String TAG = "SoftPuranaDashboardActi";

    private ProgressBar progress;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FileUtils fileUtils;
    private String type;

    private SoftCopyRecyclerViewAdapter adapter;

    private ArrayList<SoftCopyModel> offlineBookModels;

    private SoftCopyModel recentlyDeletedItem;
    private int recentlyDeletedItemPosition;

    private boolean snackbarActive;
    private RelativeLayout emptyView;
    private boolean fromHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_soft_purana_dashboard);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_soft_purana_dashboard, null, false);
        drawer.addView(contentView, 0);
        //drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        if(MainActivity.USER_DATA.getPurchasedBooks() == null){
            MainActivity.USER_DATA.setPurchasedBooks(new ArrayList<SoftCopyModel>());
        }

        progress = findViewById(R.id.progress);
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = findViewById(R.id.recycler_view);
        emptyView = findViewById(R.id.emptyView);

        fileUtils = new FileUtils(SoftPuranaDashboardActivity.this);


        recyclerView.setLayoutManager(new GridLayoutManager(SoftPuranaDashboardActivity.this, 2));

        recyclerView.addItemDecoration(new DividerItemDecoration(SoftPuranaDashboardActivity.this,
                DividerItemDecoration.VERTICAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(SoftPuranaDashboardActivity.this,
                DividerItemDecoration.HORIZONTAL));

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        try {
           type = getIntent().getExtras().getString("type");
            fromHome = getIntent().getExtras().getBoolean("fromHome");
            if(TextUtils.equals(type, "parts")){
                SoftCopyModel softCopyModel = (SoftCopyModel) getIntent().getSerializableExtra("softCopyModel");
                if(softCopyModel.getBookParts() == null || softCopyModel.getBookParts().size() <= 0){
                    fileUtils.showShortToast("No parts available");
                    emptyView.setVisibility(View.VISIBLE);
                }else {
                    populateList(softCopyModel.getBookParts());
                }

            }else {
                loadBooks(type);
            }
        }catch (Exception e){
            e.printStackTrace();
            loadBooks("purans");
        }

        changePrime();

        if(TextUtils.equals(type, "offline")) {
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }

    }
    private void changePrime(){
        if(!MainActivity.USER_DATA.isPrimeMember()){
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_prime).setVisible(true);
            super.headerView.findViewById(R.id.primeIndicator).setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchItem.setVisible(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(fileUtils.isNetworkConnected()){
                    adapter.getFilter().filter(newText);
                    return false;
                }else {
                    fileUtils.showShortToast("You are disconnected!");
                    return true;
                }
            }
        });

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6C7071")));
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F7F7F7")));
                }
                return true;
            }
        });


        return true;
    }
    @Override
    public void onBackPressed() {
        if(snackbarActive){
            deleteBookFiles(recentlyDeletedItem);
            deleteFromRecent(recentlyDeletedItem);
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

    private void loadBooks(String document){

        if(TextUtils.equals(type, "offline")){
            offlineBookModels = loadOfflineBooks();
            if(offlineBookModels.size() <= 0){
                fileUtils.showLongToast("You have not downloaded any book yet.");
                emptyView.setVisibility(View.VISIBLE);
            }else{
                populateList(offlineBookModels);
            }
            return;
        }

        if(TextUtils.equals(type, "favorite_books")){
            ArrayList<SoftCopyModel> softCopyModels = loadFavBooks();
            if(softCopyModels.size() <= 0){
                fileUtils.showLongToast("You have not added any favorite yet.");
                emptyView.setVisibility(View.VISIBLE);
            }else{
                populateList(softCopyModels);
            }
            return;
        }

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

        db.collection("softCopy").document(document).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if(documentSnapshot.exists()){

                        SoftCopy softCopyDB = (SoftCopy)documentSnapshot.toObject(SoftCopy.class);

                        ArrayList<SoftCopyModel> softCopyModels = softCopyDB.getSoftCopy();

                        populateList(softCopyModels);

                        hidePB(true);

                    }else {
                        fileUtils.showLongToast("Could not load the books try again later.");
                        emptyView.setVisibility(View.VISIBLE);
                        hidePB(true);
                    }
                }else{
                    fileUtils.showLongToast("Could not load the books try again later.");
                    emptyView.setVisibility(View.VISIBLE);
                    hidePB(true);
                }
            }
        });
    }
    private void populateList(ArrayList<SoftCopyModel> softCopyModels){

        if(softCopyModels.size() <= 0){
            emptyView.setVisibility(View.VISIBLE);
        }
        if(fromHome){
            String scrollToId = getIntent().getExtras().getString("scrollToId");
            int scrollToPosition = getItemPosition(softCopyModels, scrollToId);
            adapter = new SoftCopyRecyclerViewAdapter(SoftPuranaDashboardActivity.this, softCopyModels,true, scrollToPosition, recyclerView);
        }else {
            adapter = new SoftCopyRecyclerViewAdapter(SoftPuranaDashboardActivity.this, softCopyModels,false, 0, recyclerView);
        }

        recyclerView.setAdapter(adapter);
    }
    private int getItemPosition(ArrayList<SoftCopyModel> softCopyModels, String key){
        int position = 0;
        for(int i = 0 ; i < softCopyModels.size(); ++i){
            if(TextUtils.equals(softCopyModels.get(i).getBookId(), key)){
                position = i;
                break;
            }
        }

        return  position;
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
    private ArrayList<SoftCopyModel> loadOfflineBooks() {

        ArrayList<SoftCopyModel> offlineBookList;

        SharedPreferences sharedPreferences = getSharedPreferences("offline_book_list", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("offlineBookList", null);
        Type type = new TypeToken<ArrayList<SoftCopyModel>>() {}.getType();
        offlineBookList = gson.fromJson(json, type);

        if (offlineBookList == null) {
            offlineBookList = new ArrayList<>();
        }

        return offlineBookList;
    }

    private ArrayList<SoftCopyModel> loadFavBooks() {

        ArrayList<SoftCopyModel> favList;

        SharedPreferences sharedPreferences = getSharedPreferences("offline_book_list", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("favoriteBookList", null);
        Type type = new TypeToken<ArrayList<SoftCopyModel>>() {}.getType();
        favList = gson.fromJson(json, type);

        if (favList == null) {
            favList = new ArrayList<>();
        }

        return favList;
    }

    private SoftCopyModel getOfflineBookReference(ArrayList<SoftCopyModel> softCopyModels, SoftCopyModel bookModel){
        SoftCopyModel book = null;

        for(SoftCopyModel softCopyModel : softCopyModels){
            if(TextUtils.equals(softCopyModel.getBookId(), bookModel.getBookId())){
                book = softCopyModel;
                break;
            }
        }

        return book;
    }
    private void deleteOffline(SoftCopyModel offlineBook) {

        ArrayList<SoftCopyModel> offlineBookList = loadOfflineBooks();

        SoftCopyModel bookModel = getOfflineBookReference(offlineBookList, offlineBook);

        if(bookModel != null){
            if(offlineBookList.contains(bookModel)){
                offlineBookList.remove(bookModel);
            }
        }
        SharedPreferences sharedPreferences = getSharedPreferences("offline_book_list", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(offlineBookList);
        editor.putString("offlineBookList", json);
        editor.apply();
    }

    private void deleteBookFiles(SoftCopyModel softCopyModel){

        String bookName = softCopyModel.getFileName();

        String fileName = bookName+".pdf";

        File folder = fileUtils.getFolder("puran_collection");

        final File file = new File(folder, fileName);

        boolean deleteResult = false;
        if(file.exists()){

            try{
                deleteResult = file.getAbsoluteFile().delete();
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        if(deleteResult){
            Log.d(TAG, "deleteBookFiles: File deleted!");
        }
    }

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

            try {
                int position = viewHolder.getAdapterPosition();
                SoftCopyModel softCopyModel = offlineBookModels.get(position);
                recentlyDeletedItem = softCopyModel;
                recentlyDeletedItemPosition = position;

                deleteOffline(softCopyModel);
                showUndoSnackbar(softCopyModel);

                offlineBookModels.remove(position);
                adapter.notifyDataSetChanged();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };

    private void showUndoSnackbar(SoftCopyModel softCopyModel) {

        try {
            snackbarActive = true;
            View view = findViewById(R.id.parent);
            Snackbar snackbar = Snackbar.make(view, "Undo delete ? ",
                    Snackbar.LENGTH_LONG);
            snackbar.setAction("Undo", v -> undoDelete());
            snackbar.addCallback(new Snackbar.Callback(){
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    snackbarActive = false;
                    if (event != Snackbar.Callback.DISMISS_EVENT_ACTION){
                        deleteBookFiles(softCopyModel);
                        deleteFromRecent(softCopyModel);
                    }
                }

                @Override
                public void onShown(Snackbar snackbar) {

                }
            });
            snackbar.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void undoDelete() {

        try {
            snackbarActive = false;
            offlineBookModels.add(recentlyDeletedItemPosition,
                    recentlyDeletedItem);
            saveDataAt(recentlyDeletedItem, recentlyDeletedItemPosition);

            adapter.notifyDataSetChanged();
        }catch (Exception e){

        }

    }

    private void saveDataAt(SoftCopyModel offlineBook, int position) {

        try {
            ArrayList<SoftCopyModel> offlineBookList = loadOfflineBooks();

            offlineBookList.add(position, offlineBook);

            SharedPreferences sharedPreferences = getSharedPreferences("offline_book_list", MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();

            Gson gson = new Gson();
            String json = gson.toJson(offlineBookList);
            editor.putString("offlineBookList", json);
            editor.apply();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void deleteFromRecent(SoftCopyModel softCopyModel){

        try{

            ArrayList<BookDisplaySliderModel> bookDisplaySliderModels = loadContinueReadingList();

            int bookIndex = bookDisplaySliderModelIndex(bookDisplaySliderModels, softCopyModel.getBookId());

            if( bookIndex != -1){
                bookDisplaySliderModels.remove(bookIndex);
            }
            SharedPreferences sharedPreferences = getSharedPreferences("offline_book_list", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(bookDisplaySliderModels);
            editor.putString("bookDisplaySliderModels", json);
            editor.apply();

            SplashActivity.DISPLAY_UPDATE_NOTIFIER = true;
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private ArrayList<BookDisplaySliderModel> loadContinueReadingList(){

        try {
            ArrayList<BookDisplaySliderModel> bookDisplaySliderModels;

            SharedPreferences sharedPreferences = getSharedPreferences("offline_book_list", MODE_PRIVATE);
            Gson gson = new Gson();
            String json = sharedPreferences.getString("bookDisplaySliderModels", null);
            Type type = new TypeToken<ArrayList<BookDisplaySliderModel>>() {}.getType();
            bookDisplaySliderModels = gson.fromJson(json, type);

            if (bookDisplaySliderModels == null) {
                bookDisplaySliderModels = new ArrayList<>();
            }
            return bookDisplaySliderModels;
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private int bookDisplaySliderModelIndex(ArrayList<BookDisplaySliderModel> bookDisplaySliderModels, String bookId){

        try {
            for (int i = 0; i < bookDisplaySliderModels.size(); ++i){
                if(TextUtils.equals(bookId, bookDisplaySliderModels.get(i).getBookId())){
                    return i;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return -1;
    }
}
