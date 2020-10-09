package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.adapters.HomeParentDisplayRecyclerViewAdapter;
import com.akshit.akshitsfdc.allpuranasinhindi.models.AppInfo;
import com.akshit.akshitsfdc.allpuranasinhindi.models.BookDisplayCollectionModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.BookDisplaySliderModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SliderModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SoftCopyModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.UserDataModel;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.FileUtils;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smarteist.autoimageslider.SliderView;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.IUnityBannerListener;
import com.unity3d.services.banners.UnityBanners;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Timer;

public class HomeActivity extends MainActivity implements IUnityBannerListener {

    private static final String TAG = "HomeActivity";
    boolean CONECTIVITY_INIT_COMPLETED = false;

    private FileUtils fileUtils = new FileUtils(HomeActivity.this);

    private Dialog dialog;
    private RelativeLayout bannerContainer;
    private RecyclerView homeDisplayRecyclerView;
    private ImageView loadingImage;
    private HomeParentDisplayRecyclerViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_home, null, false);

        drawer.addView(contentView, 0);

        homeDisplayRecyclerView = findViewById(R.id.recycler_view);
        bannerContainer = findViewById(R.id.bannerContainer);
        loadingImage = findViewById(R.id.loadingImage);

        Glide.with(HomeActivity.this).load(R.drawable.loading_home_gif).into(loadingImage);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, filter);



    }

    private void changePrime(){
        if(!MainActivity.USER_DATA.isPrimeMember()){
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_prime).setVisible(true);
            super.headerView.findViewById(R.id.primeIndicator).setVisibility(View.GONE);

            final Timer timer = new Timer();
            timer.schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            if(UnityAds.isInitialized()){
                                UnityBanners.setBannerListener(HomeActivity.this);
                                ToggleBannerAd();
                                timer.cancel();
                                timer.purge();

                                return;
                            }
                        }
                    },
                    100, 100
            );
        }else{
            bannerContainer.setVisibility(View.GONE);
        }

    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                boolean noConnectivity = intent.getBooleanExtra(
                        ConnectivityManager.EXTRA_NO_CONNECTIVITY, false
                );
                if (noConnectivity) {

                    if(!CONECTIVITY_INIT_COMPLETED){
                        showOfflineView();
                        findViewById(R.id.internetLostView).setVisibility(View.VISIBLE);
                    }

                    loadingImage.setVisibility(View.GONE);
                    fileUtils.showLongToast("You are disconnected!");
                } else {
                    findViewById(R.id.internetLostView).setVisibility(View.GONE);
                    if(!CONECTIVITY_INIT_COMPLETED){
                        loadingImage.setVisibility(View.VISIBLE);
                        connectivityRequireCalls();
                    }
                }
            }
        }
    };

    private void showOfflineView(){

            BookDisplayCollectionModel bookDisplayCollectionModel = getBookDisplayCollectionModelForOffline();
            SliderModel sliderModel = new SliderModel(getString(R.string.offline_key), "", false, "");
            ArrayList<SliderModel> sliderModels = new ArrayList<SliderModel>();
            sliderModels.add(sliderModel);
            if(bookDisplayCollectionModel.getBookDisplaySliders().size() > 0){
                ArrayList<BookDisplayCollectionModel> bookDisplayCollectionModels = new ArrayList<>();
                bookDisplayCollectionModels.add(bookDisplayCollectionModel);
                adapter = new HomeParentDisplayRecyclerViewAdapter(bookDisplayCollectionModels, HomeActivity.this, sliderModels);
                homeDisplayRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                homeDisplayRecyclerView.setAdapter(adapter);
            }

    }
    private void connectivityRequireCalls(){

        if(!fileUtils.isNetworkConnected()){
            return;
        }

        try {
            bannerContainer.setVisibility(View.VISIBLE);
            loadUserData();
            checkUpdate();
            CONECTIVITY_INIT_COMPLETED = true;
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            ToggleBannerAd();
            if(SplashActivity.DISPLAY_UPDATE_NOTIFIER){
                updateDataSet();
                adapter.notifyDataSetChanged();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void updateDataSet(){
        try{
            BookDisplayCollectionModel bookDisplayCollectionModel = SplashActivity.APP_INFO.getBookDisplayCollection().get(0);

            String firstItemType = bookDisplayCollectionModel.getBookDisplaySliders().get(0).getType();

            if(TextUtils.equals(firstItemType, getString(R.string.offline_key))){

                BookDisplayCollectionModel bookDisplayCollectionModel1 = getBookDisplayCollectionModelForOffline();

                SplashActivity.APP_INFO.getBookDisplayCollection().remove(0);

                if(bookDisplayCollectionModel1.getBookDisplaySliders().size() > 0){
                    SplashActivity.APP_INFO.getBookDisplayCollection().add(0, bookDisplayCollectionModel1);
                }

            }else {
                BookDisplayCollectionModel bookDisplayCollectionModel1 = getBookDisplayCollectionModelForOffline();
                if(bookDisplayCollectionModel1.getBookDisplaySliders().size() > 0){
                    SplashActivity.APP_INFO.getBookDisplayCollection().add(0, bookDisplayCollectionModel1);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            SplashActivity.DISPLAY_UPDATE_NOTIFIER = false;
        }

    }
    private void loadUserData(){


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(!fileUtils.isNetworkConnected()){
            fileUtils.showLongToast("You are not connected to the internet.");
            return;
        }
        if(currentUser == null){
            fileUtils.showLongToast("You are not logged in, please log in again..");
            return;
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("user_data").document(currentUser.getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        MainActivity.USER_DATA = document.toObject(UserDataModel.class);
                        changePrime();
                    } else {
                        UserDataModel userDataModel = new UserDataModel(currentUser.getDisplayName(), currentUser.getEmail(), currentUser.getUid(), new ArrayList<SoftCopyModel>(), false);

                        docRef.set(userDataModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                MainActivity.USER_DATA = userDataModel;
                                changePrime();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                fileUtils.showShortToast("Error in loading user data.");
                                changePrime();
                            }
                        });
                    }
                } else {
                    fileUtils.showShortToast("Error in loading user data, contact support or restart application.");
                    changePrime();
                }
            }
        });

    }
    private void checkUpdate(){
        String version = "bad";
        try {
            PackageInfo pInfo = HomeActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.d(TAG, "checkUpdate: version >> "+version);
        final DocumentReference docRef = db.collection("app_info").document("meta_data");

        final String finalVersion = version;
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        SplashActivity.APP_INFO = document.toObject(AppInfo.class);
                        setBanner(finalVersion);
                        if(SplashActivity.APP_INFO.isCheckVersionPopup()){
                            if(!TextUtils.equals(finalVersion, SplashActivity.APP_INFO.getLatestVersion()) || SplashActivity.APP_INFO.isAbortApp()) {
                                showUpdateDialog(SplashActivity.APP_INFO);
                            }
                        }

                        loadingImage.setVisibility(View.GONE);
                    } else {
                        Log.d(TAG, "App info fetch error 1"+task.getException());
                        fileUtils.showLongToast("Error fetching user data, please try again later or restart the app.");
                        loadingImage.setVisibility(View.GONE);
                    }
                } else {
                    Log.d(TAG, "App info fetch error 2"+task.getException());
                    fileUtils.showLongToast("Error fetching user data, please try again later or restart the app.");
                    loadingImage.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            exitWarning();
        }
    }
    private void exitWarning(){
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        killTheApp();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
    private void killTheApp(){
        super.onBackPressed();
    }
    private void showUpdateDialog(AppInfo appInfoDB){

        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(appInfoDB.getHeader());
            builder.setMessage(appInfoDB.getFeature());


            //fileUtils.showLongToast(""+dialog.isShowing());

            builder.setPositiveButton(appInfoDB.getOkText(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if(appInfoDB.isAbortApp()){
                        killTheApp();
                    }else{
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                                ("market://details?id=com.akshit.akshitsfdc.allpuranasinhindi")));
                        dialog.dismiss();
                    }
                }
            });
            if(!appInfoDB.isAbortApp()){
                builder.setNegativeButton(appInfoDB.getCancelText(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // background.start();

                        //dialog.setCancelable(false);
                        if(appInfoDB.isForceUpdate()){
                            dialog.cancel();
                            killTheApp();
                        }else{
                            dialog.cancel();
                            // dialog.setCancelable(true);
                        }
                    }
                });
            }



            try{
                dialog = builder.create();

                if(appInfoDB.isForceUpdate()){
                    dialog.setCancelable(false);
                }else {
                    dialog.setCancelable(true);
                }
                dialog.show();// = builder.show();
            }catch(Exception e){
                e.printStackTrace();
            }

            if(appInfoDB.isAbortApp()){
                dialog.setCancelable(false);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void setBanner(String finalVersion){

        BookDisplayCollectionModel bookDisplayCollectionModel = getBookDisplayCollectionModelForOffline();
        ArrayList<SliderModel> sliderModels = new ArrayList<>();
        if(bookDisplayCollectionModel.getBookDisplaySliders().size() > 0){
            SplashActivity.APP_INFO.getBookDisplayCollection().add(0, bookDisplayCollectionModel);
        }
        if(SplashActivity.APP_INFO.getBannerUrls() != null && SplashActivity.APP_INFO.getBannerUrls().size() > 0){
            if(TextUtils.equals(finalVersion, SplashActivity.APP_INFO.getLatestVersion())){
                SplashActivity.APP_INFO.getBannerUrls().remove(0);
            }
        }
        if(SplashActivity.APP_INFO.getBannerUrls() == null ){
            SplashActivity.APP_INFO.setBannerUrls(sliderModels);
        }
        adapter = new HomeParentDisplayRecyclerViewAdapter(SplashActivity.APP_INFO.getBookDisplayCollection(), HomeActivity.this, SplashActivity.APP_INFO.getBannerUrls());
        homeDisplayRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        homeDisplayRecyclerView.setAdapter(adapter);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    // Implement a function to display or destroy a banner ad:
    public void ToggleBannerAd () {
        try {

            UnityBanners.loadBanner (HomeActivity.this, getString(R.string.homeBanner_unity));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUnityBannerLoaded(String s, View view) {
        try{
            if(bannerContainer.getChildCount()>0){
                bannerContainer.removeAllViews();
            }
            bannerContainer.addView (view);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUnityBannerUnloaded(String s) {

    }

    @Override
    public void onUnityBannerShow(String s) {

    }

    @Override
    public void onUnityBannerClick(String s) {

    }

    @Override
    public void onUnityBannerHide(String s) {

    }

    @Override
    public void onUnityBannerError(String s) {

    }

    private BookDisplayCollectionModel getBookDisplayCollectionModelForOffline(){

        ArrayList<BookDisplaySliderModel> bookDisplaySliderModels = loadContinueReadingList();
        BookDisplayCollectionModel bookDisplaySliderModel = new BookDisplayCollectionModel();

        String headerTitle = "Continue Reading";

        bookDisplaySliderModel.setHeaderTitle(headerTitle);
        bookDisplaySliderModel.setBookDisplaySliders(bookDisplaySliderModels);

        /*if(bookDisplaySliderModels.size() <= 0){
            return null;
        }*/

        return bookDisplaySliderModel;
    }

    private ArrayList<BookDisplaySliderModel> loadContinueReadingList(){

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
    }

}
