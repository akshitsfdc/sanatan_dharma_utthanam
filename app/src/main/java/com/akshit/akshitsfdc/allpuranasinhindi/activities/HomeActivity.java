package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.widget.Toolbar;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.adapters.SliderAdapterExample;
import com.akshit.akshitsfdc.allpuranasinhindi.fragments.SearchFragment;
import com.akshit.akshitsfdc.allpuranasinhindi.helpers.DisplayRecyclerHelper;
import com.akshit.akshitsfdc.allpuranasinhindi.models.BookDisplayCollectionModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.BookDisplaySliderModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.DisplayModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SliderItem;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SliderModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SoftCopyModel;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.FileUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends MainActivity {

    private static final String TAG = "HomeActivity";

    private FileUtils fileUtils;

    private Toolbar toolbar;
    private CardView toolbarCard;
    private View addContainer;
    private NestedScrollView nestedScrollView;
    private LinearLayout scrollViewInnerLayout;
    private SliderView imageSlider;
    private DisplayRecyclerHelper offlineDisplayHelper;
    private DisplayRecyclerHelper favoriteDisplayHelper;
    private View internetLostView, mainView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_home, null, false);

        fileUtils  = new FileUtils(HomeActivity.this);
        drawer.addView(contentView, 0);
        scrollViewInnerLayout = findViewById(R.id.scrollViewInnerLayout);
        internetLostView = findViewById(R.id.internetLostView);
        mainView = findViewById(R.id.mainView);

        addContainer = findViewById(R.id.addContainer);
        imageSlider = findViewById(R.id.imageSlider);


//        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        toolbarCard = findViewById(R.id.toolbarCard);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        //toolbar.setNavigationIcon(R.drawable.ic_toolbar);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
//        registerReceiver(broadcastReceiver, filter);

        uiUtils.setSnakebarView(getSnakBarView(findViewById(R.id.snakebarLayout)));

        toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

        /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //hideKeyboard();
                // Do whatever you want here
            }

        /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Do whatever you want here
            }
        };

        nestedScrollView = findViewById(R.id.nestedScrollView);

        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.off_notification_color));

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        findViewById(R.id.noInternetButton).setOnClickListener(v -> {
            navigateToSoftPuranaDashBoard(getString(R.string.offline_key));
        });
        setBaseActivity(this);

    }


    public void initBanner(){

        Log.d(TAG, "initBanner: banner child count : "+((LinearLayout)addContainer).getChildCount());
        try {
            if(!SplashActivity.USER_DATA.isPrimeMember() && ((LinearLayout)addContainer).getChildCount() <= 0){

                addContainer.setVisibility(View.VISIBLE);

                AdView adView = new AdView(this);

                adView.setAdSize(AdSize.BANNER);
                adView.setAdUnitId(SplashActivity.APP_INFO.getHomeBannerId().trim());


                ((LinearLayout)addContainer).addView(adView);

                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);

            }else {
                addContainer.setVisibility(View.GONE);
            }
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

        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                showSearchFragment();
                return false;
            }
        });
        return true;
    }

    private void addDisplay(DisplayModel displayModel){

        DisplayRecyclerHelper displayRecyclerHelper = null;
        List<SoftCopyModel> offlineSoftCopyModels = null, favoriteSoftCopyModels = null;

        if(TextUtils.equals(displayModel.getDisplayKey().toLowerCase(), getString(R.string.offline_key)) ){
            offlineSoftCopyModels =  sqlService.showOfflineBooks(String.valueOf(displayModel.getLimit()));

            displayRecyclerHelper = new DisplayRecyclerHelper(this, displayModel, offlineSoftCopyModels);
            offlineDisplayHelper = displayRecyclerHelper;


        }else if(TextUtils.equals(displayModel.getDisplayKey().toLowerCase(), getString(R.string.favorite_key))){
            favoriteSoftCopyModels =  sqlService.showFavoriteBooks(String.valueOf(displayModel.getLimit()));


            displayRecyclerHelper = new DisplayRecyclerHelper(this, displayModel, favoriteSoftCopyModels);

            favoriteDisplayHelper = displayRecyclerHelper;

        }else {

            displayRecyclerHelper = new DisplayRecyclerHelper(this, displayModel);
        }


        if(displayRecyclerHelper != null){

            View displayLayout = displayRecyclerHelper.getDisplayLayout();

            if(displayLayout != null){
                scrollViewInnerLayout.addView(displayLayout);

                if(offlineSoftCopyModels != null){
                    if(offlineSoftCopyModels.size() == 0 ){
                        displayLayout.setVisibility(View.GONE);
                    }
                }
                if(favoriteSoftCopyModels != null){
                    if(favoriteSoftCopyModels.size() == 0 ){
                        displayLayout.setVisibility(View.GONE);
                    }
                }
            }

        }


    }

    @Override
    protected void onResume() {
        super.onResume();


        if(offlineDisplayHelper != null){
            List<SoftCopyModel> softCopyModels =  sqlService.showOfflineBooks(String.valueOf(offlineDisplayHelper.getLimit()));

            if(softCopyModels.size() > 0){
                offlineDisplayHelper.resetRecyclerView(softCopyModels);
                offlineDisplayHelper.getDisplayLayout().setVisibility(View.VISIBLE);
            }else {
                offlineDisplayHelper.getDisplayLayout().setVisibility(View.GONE);
            }

        }

        if(favoriteDisplayHelper != null){
            List<SoftCopyModel> softCopyModels =  sqlService.showFavoriteBooks(String.valueOf(offlineDisplayHelper.getLimit()));

            if(softCopyModels.size() > 0){
                favoriteDisplayHelper.resetRecyclerView(softCopyModels);
                favoriteDisplayHelper.getDisplayLayout().setVisibility(View.VISIBLE);
            }else {
                favoriteDisplayHelper.getDisplayLayout().setVisibility(View.GONE);
            }
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


    @Override
    public void onBackPressed() {

        showToolBar();

        super.onBackPressed();
    }


    public void setBanner(String finalVersion){


        if(SplashActivity.APP_INFO.getDisplayModels() != null){

            for (DisplayModel displayModel : SplashActivity.APP_INFO.getDisplayModels()
            ) {
                addDisplay(displayModel);
            }
        }

        if(SplashActivity.APP_INFO.getBannerUrls() != null){
            setDisplayBanners();
        }
    }

    private void setDisplayBanners(){

        ArrayList<SliderModel> imageUrls =  SplashActivity.APP_INFO.getBannerUrls();

        SliderAdapterExample sliderAdapterExample = new SliderAdapterExample(this);

        for(SliderModel sliderModel : imageUrls){
            sliderAdapterExample.addItem(new SliderItem(sliderModel.getMoveTo(), sliderModel.getImageUrl(), sliderModel.isExternalLink(), sliderModel.getExternalUrl()));
        }
        imageSlider.setIndicatorAnimation(IndicatorAnimations.DROP);
        imageSlider.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
        imageSlider.startAutoCycle();
        imageSlider.setSliderAdapter(sliderAdapterExample);


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
    public void noInternet(){
        nestedScrollView.setVisibility(View.GONE);
        internetLostView.setVisibility(View.VISIBLE);
    }
    public void internetAvailable(){
        nestedScrollView.setVisibility(View.VISIBLE);
        internetLostView.setVisibility(View.GONE);
    }
    private void hideToolbar(){
        toolbarCard.setVisibility(View.GONE);
    }
    public void showToolBar(){
        toolbarCard.setVisibility(View.VISIBLE);
    }
}
