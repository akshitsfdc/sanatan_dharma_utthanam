package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.StatFs;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.models.BookDisplaySliderModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.CurrentDownloadingModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SoftCopyModel;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.FileUtils;

import com.bumptech.glide.Glide;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnLongPressListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class SoftBookViewActivity extends AppCompatActivity {

    private FileUtils fileUtils;
    private static final int  MEGABYTE = 1024 * 1024;
    public static String fileName;
    private File generatedFile;
    private String urlLink;
    private Uri uri;
    private File folder;
    private SoftCopyModel softCopyModel;
    private PDFView pdfView;
    private ProgressBar determinateBar;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private RelativeLayout loadingLayout;
    private TextView pageCountText;
    private int currentPageIndex;
    private RelativeLayout headerView;
    private EditText currentNumberEditText;
    private EditText totalNumberEditText;
    private Button goToPageButton;
    private RelativeLayout goToPageBackground;
    private RelativeLayout readModeLayout;
    private int totalPages;
    private ImageView progressBookImage;
    private TextView percentText;
    private Timer timer;
    private int recentLimit = 11;
    private RelativeLayout internetLostView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soft_book_view);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitleTextColor(getResources().getColor(R.color.off_notification_color));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        //toolbar.setNavigationIcon(R.drawable.ic_toolbar);
        toolbar.setTitle("");
        toolbar.setSubtitle("");

        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_menu_overflow));

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        fileUtils = new FileUtils(SoftBookViewActivity.this);
        loadingLayout = findViewById(R.id.loadingLayout);
        determinateBar = findViewById(R.id.determinateBar);
        pageCountText = findViewById(R.id.pageCountText);
        headerView = findViewById(R.id.headerView);
        pdfView = findViewById(R.id.pdfView);
        progressBookImage= findViewById(R.id.bookImage);
        percentText = findViewById(R.id.percentText);

        currentNumberEditText = findViewById(R.id.currentNumberEditText);
        totalNumberEditText = findViewById(R.id.totalNumberEditText);
        totalNumberEditText.setKeyListener(null);

        goToPageButton= findViewById(R.id.goToPageButton);
        goToPageBackground = findViewById(R.id.goToPageBackground);
        readModeLayout = findViewById(R.id.readModeLayout);
        internetLostView = findViewById(R.id.internetLostView);

        Intent intent=getIntent();
        softCopyModel =(SoftCopyModel) intent.getSerializableExtra("softCopyModel");




        if(MainActivity.DOWNLOAD_IN_PROGRESS){

            bookDownloading();
        }else {


            sharedPref = getSharedPreferences(
                    "pageIndexes", Context.MODE_PRIVATE);
            currentPageIndex = getPageIndex();
            setView();

            pdfView.setOnClickListener(v->{
                if(headerView.getVisibility() == View.VISIBLE){
                    headerView.setVisibility(View.GONE);
                }else {
                    headerView.setVisibility(View.VISIBLE);
                }

            });
            goToPageButton.setOnClickListener(v->{
                goToPageAction();
            });
            goToPageBackground.setOnClickListener(v->{
                hideSoftKeyboard(currentNumberEditText);
                goToPageBackground.setVisibility(View.GONE);
            });

            currentNumberEditText.addTextChangedListener(new TextWatcher() {

                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                    if(!s.equals("") ) {
                        try{
                            //do your work here
                            int value = Integer.parseInt(s.toString());
                            if(value > totalPages){
                                currentNumberEditText.setText(String.valueOf(totalPages));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {

                }

                public void afterTextChanged(Editable s) {

                }
            });

        }
        long availableMemory = getAvailableMemory();
        if(availableMemory < 100){
            showAlertSnackbar("Your device is running low memory please delete some books from offline or clean up your device", true);
        }

    }
    private void goToPageAction(){
        String numberText = currentNumberEditText.getText().toString().trim();

        hideSoftKeyboard(currentNumberEditText);
        if(TextUtils.isEmpty(numberText)){
            fileUtils.showShortToast("Not a valid page number.");
            return;
        }
        int number = Integer.parseInt(numberText);
        if(number > 0 ){
            number = number - 1;
        }
        pdfView.jumpTo(number, true);

        goToPageBackground.setVisibility(View.GONE);
    }

    protected void hideSoftKeyboard(EditText input) {
        try {

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate your main_menu into the menu
        getMenuInflater().inflate(R.menu.custom_book_view_menues, menu);

        // Find the menuItem to add your SubMenu
        MenuItem goToPageMenu = menu.findItem(R.id.goToPageMenu);
        MenuItem nightMode = menu.findItem(R.id.nightMode);

        goToPageMenu.setOnMenuItemClickListener(v->{
            if(MainActivity.DOWNLOAD_IN_PROGRESS){
                return true;
            }
            if(goToPageBackground.getVisibility() == View.VISIBLE){
                goToPageBackground.setVisibility(View.GONE);
            }else {
                goToPageBackground.setVisibility(View.VISIBLE);
            }
            return true;
        });
        nightMode.setOnMenuItemClickListener(v->{

            if(readModeLayout.getVisibility() == View.VISIBLE){
                readModeLayout.setVisibility(View.GONE);
                nightMode.setTitle(getString(R.string.night_mode_on));
            }else {
                readModeLayout.setVisibility(View.VISIBLE);
                nightMode.setTitle(getString(R.string.night_mode_off));
            }

            return true;
        });
        return true;

    }

    private void bookDownloading(){

        determinateBar.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.VISIBLE);

        startTimer();
    }

    private void mainThreadCode(int progress){
        determinateBar.setProgress(progress);
        String s = progress+"%";
        percentText.setText(s);
    }

    private int getPageIndex(){
        return sharedPref.getInt(softCopyModel.getBookId(), 0);
    }

    private void setView(){

        String bookName = softCopyModel.getFileName();

        fileName = bookName+".pdf";

        urlLink = softCopyModel.getDownloadUrl();//getArguments().getString("url");

        folder = fileUtils.getFolder("puran_collection");

        final File file = new File(folder, fileName);

        //setTitle(title);

        try{

            if(file.exists()){
                pageCountText.setVisibility(View.VISIBLE);

                determinateBar.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);

                pdfView.enableRenderDuringScale(true);
                pdfView.recycle();
                pdfView.useBestQuality(true);

                uri = fileUtils.getUri("com.akshit.akshitsfdc.allpuranasinhindi.provider", file);

                checkOfflineMode();

                pdfView.fromUri(uri).defaultPage(currentPageIndex)
                        .onPageChange(new OnPageChangeListener() {
                            @Override
                            public void onPageChanged(int page, int pageCount) {
                                currentPageIndex = page;
                                String text = currentPageIndex+1+"/"+pageCount;
                                pageCountText.setText(text);
                                currentNumberEditText.setText(String.valueOf(page+1));
                            }
                        }).onRender(new OnRenderListener() {
                            @Override
                            public void onInitiallyRendered(int nbPages) {
                                totalPages = pdfView.getPageCount();
                                totalNumberEditText.setText(String.valueOf(pdfView.getPageCount()));
                                setupContinueReadingList();
                            }
                }).onLongPress(new OnLongPressListener() {
                    @Override
                    public void onLongPress(MotionEvent e) {
                        fileUtils.showLongToast("You are reading "+softCopyModel.getName());
                    }
                }).enableSwipe(true).onError(new OnErrorListener(){
                    @Override public void onError(Throwable t) {

                        file.getAbsoluteFile().delete();
                        showAlertSnackbar("Load error please try again..", false);
                    } }).onLoad(new OnLoadCompleteListener() {
                    @Override public void loadComplete(int nbPages) {  } }).enableAnnotationRendering(true).load();


            }else{
                if(fileUtils.isNetworkConnected()){
                    download();
                    startTimer();
                }else{
                    offlineMode();
                }
            }

        }catch (Exception e){
            fileUtils.showLongToast("Something Went wrong!");
            e.printStackTrace();

        }
    }

    @Override
    public void onBackPressed() {
        savePageIndex();
        if(timer != null){
            try{
                timer.cancel();
                timer.purge();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        super.onBackPressed();
    }

    private void savePageIndex(){

        if(sharedPref != null){
            editor = sharedPref.edit();
            editor.remove(fileName);
            editor.apply();
            editor.putInt(softCopyModel.getBookId(), currentPageIndex);
            editor.apply();
        }
    }
    private void offlineMode(){
        determinateBar.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.GONE);
        MainActivity.DOWNLOAD_IN_PROGRESS = false;
        MainActivity.CURRENT_DOWNLOAD_PROGRESS = 0;
        internetLostView.setVisibility(View.VISIBLE);
        fileUtils.showLongToast("You might got disconnected please try again.");
    }
    private void startTimer(){

        determinateBar.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.VISIBLE);

        timer = new Timer();

        timer.schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {

                        //determinateBar.setProgress(MainActivity.CURRENT_DOWNLOAD_PROGRESS);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mainThreadCode(MainActivity.CURRENT_DOWNLOAD_PROGRESS);
                            }
                        });

                        if (!MainActivity.DOWNLOAD_IN_PROGRESS) {
                            timer.cancel();
                            timer.purge();
                            finish();
                        }

                    }
                },
                100, 100
        );

        Glide.with(SoftBookViewActivity.this).load(MainActivity.CURRENT_DOWNLOADING_MODEL.getPicUrl()).error(R.drawable.book_placeholder).fallback(R.drawable.book_placeholder)
                .apply(bitmapTransform(new BlurTransformation(80))).into(progressBookImage);
        TextView bookNameText = findViewById(R.id.bookNameText);
        String s = MainActivity.CURRENT_DOWNLOADING_MODEL.getBookName()+" is Downloading...";
        bookNameText.setText(s);
    }


    public void downloadFile(String fileUrl, File directory){

        try {

            URL url = new URL(fileUrl);
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.connect();

            if(urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK){

                if(directory.exists()){
                    directory.getAbsoluteFile().delete();
                }
                MainActivity.DOWNLOAD_IN_PROGRESS = false;
                MainActivity.CURRENT_DOWNLOAD_PROGRESS = 0;
                return;
            }

            InputStream inputStream = urlConnection.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(directory);
            int lenghtOfFile = urlConnection.getContentLength();

            byte[] buffer = new byte[MEGABYTE];
            int len1 = 0;
            long total = 0;

            //determinateBar.setVisibility(View.VISIBLE);
            MainActivity.DOWNLOAD_IN_PROGRESS = true;
            MainActivity.CURRENT_DOWNLOAD_PROGRESS = 0;

            if(fileUtils.isNetworkConnected()){

                try{
                    while((len1 = inputStream.read(buffer))>0 ){

                        total += len1; //total = total + len1
                        MainActivity.CURRENT_DOWNLOAD_PROGRESS = (int)((total*100)/lenghtOfFile);
                        fileOutputStream.write(buffer, 0, len1);

                    }
                }catch(Exception e){
                    if(directory.exists()){
                        directory.getAbsoluteFile().delete();
                    }
                    MainActivity.DOWNLOAD_IN_PROGRESS = false;
                    MainActivity.CURRENT_DOWNLOAD_PROGRESS = 0;
                    return;
                }

            }else{
                if(directory.exists()){
                    directory.getAbsoluteFile().delete();
                }
                MainActivity.DOWNLOAD_IN_PROGRESS = false;
                MainActivity.CURRENT_DOWNLOAD_PROGRESS = 0;

                fileUtils.showLongToast("You are not connected to the internet!");
                return;
            }

            fileOutputStream.close();

            try{
                directory.createNewFile();
            }catch (IOException e){
                if(directory.exists()){
                    directory.getAbsoluteFile().delete();
                }
                MainActivity.DOWNLOAD_IN_PROGRESS = false;
                MainActivity.CURRENT_DOWNLOAD_PROGRESS = 0;
                e.printStackTrace();

            }

            generatedFile = directory;

            prepareOfflineMode();

            MainActivity.DOWNLOAD_IN_PROGRESS = false;
            MainActivity.CURRENT_DOWNLOAD_PROGRESS = 0;

        } catch (Exception e){
            if(directory.exists()){
                directory.getAbsoluteFile().delete();
            }
            MainActivity.DOWNLOAD_IN_PROGRESS = false;
            MainActivity.CURRENT_DOWNLOAD_PROGRESS = 0;
        }
    }

    private void download(){

        MainActivity.DOWNLOAD_IN_PROGRESS = true;
        MainActivity.CURRENT_DOWNLOADING_MODEL = new CurrentDownloadingModel(softCopyModel.getPicUrl(), softCopyModel.getName());

        final File pdfFile = new File(folder, fileName);

        Handler handler = new Handler();

        Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread th, Throwable ex) {
                if(generatedFile.exists()){
                    generatedFile.getAbsoluteFile().delete();
                }
                MainActivity.DOWNLOAD_IN_PROGRESS = false;
                MainActivity.CURRENT_DOWNLOAD_PROGRESS = 0;

            }
        };
        Thread background = new Thread(new Runnable() {

            // After call for background.start this run method call
            public void run() {

                try {
                    downloadFile(urlLink, pdfFile);
                } catch (Throwable t) {
                }
                handler.post(new Runnable() {
                    public void run() {
                        //setView();
                        navigateToItself();
                    }
                });
            }



        });
        background.setUncaughtExceptionHandler(h);
        // Start Thread
        background.start();

    }
    private void navigateToItself(){
        Intent intent = new Intent(SoftBookViewActivity.this, SoftBookViewActivity.class);
        intent.putExtra("softCopyModel",softCopyModel);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        savePageIndex();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        savePageIndex();
    }

    @Override
    protected void onDestroy() {
        savePageIndex();
        super.onDestroy();
    }
    private void prepareOfflineMode(){

        if(!softCopyModel.isFree() || softCopyModel.getPrice() == -1){
            if(!isPurchased(softCopyModel.getBookId())){
                return;
            }
        }
        ArrayList<String> softCopyModels;
        if(softCopyModel.getBookParts() == null){
            softCopyModels = new ArrayList<>();
        }else {
            softCopyModels = softCopyModel.getBookParts();
        }

        SoftCopyModel newSoftCopyModel = new SoftCopyModel(softCopyModel.getPicUrl(), softCopyModel.getName(), softCopyModel.getLanguage(), softCopyModel.isFree(),
                softCopyModel.getPrice(), softCopyModel.getDownloadUrl(), softCopyModel.getDescription(), softCopyModel.getFileName(), softCopyModel.getPages()
                ,softCopyModel.getBookId(), softCopyModel.isVideoOption(), softCopyModels, softCopyModel.isBooksInPart());

        try{

            final Bitmap[] bitmapForOffline = new Bitmap[1];

            Handler handler = new Handler();

            Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
                public void uncaughtException(Thread th, Throwable ex) {
                    if(generatedFile.exists()){
                        generatedFile.getAbsoluteFile().delete();
                    }
                    MainActivity.DOWNLOAD_IN_PROGRESS = false;
                    MainActivity.CURRENT_DOWNLOAD_PROGRESS = 0;

                }
            };
            Thread background = new Thread(new Runnable() {


                // After call for background.start this run method call
                public void run() {
                    try {
                       // Bitmap bitmapForOffline = bitmapForOffline;
                        bitmapForOffline[0] = getBitmapFromURL(softCopyModel.getPicUrl());
                    } catch (Throwable t) {
                    }
                    handler.post(new Runnable() {
                        public void run() {
                            newSoftCopyModel.setPicUrl(saveBookImageForOffline(softCopyModel.getBookId(), bitmapForOffline[0]).toString());
                            saveData(newSoftCopyModel);
                        }
                    });
                }
            });
            background.setUncaughtExceptionHandler(h);
            // Start Thread
            background.start();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    private Uri saveBookImageForOffline(String fileName, Bitmap bitmap){

        String mImageName = "image_"+fileName+".jpg";

        File folder = fileUtils.getFolder("offline_book_images");

        final File file = new File(folder, mImageName);

        if (file.exists ()) {
            try{
                file.getAbsoluteFile().delete();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        try{

            OutputStream stream = null;

            stream = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);

            stream.flush();

            stream.close();

        }catch (IOException e)
        {
            e.printStackTrace();
        }
        if(file.exists()) {
            return fileUtils.getUri("com.akshit.akshitsfdc.allpuranasinhindi.provider", file);
        }

        return null;
    }
    private void saveData(SoftCopyModel offlineBook) {

        ArrayList<SoftCopyModel> offlineBookList = loadOfflineBooks();

        offlineBookList.add(offlineBook);

        SharedPreferences sharedPreferences = getSharedPreferences("offline_book_list", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(offlineBookList);
        editor.putString("offlineBookList", json);
        editor.apply();
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

    public final long getAvailableMemory(){
        long free_memory = 0;
        try{
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            free_memory = (stat.getAvailableBlocksLong() * stat.getBlockSizeLong())/(1024 * 1024); //return value is in bytes
        }catch (Exception e){
            e.printStackTrace();
        }

        return free_memory;

    }

    private void showAlertSnackbar(String msg, boolean isLong) {

        View view = findViewById(R.id.parent);
        Snackbar snackbar;

        if(isLong){
            snackbar = Snackbar.make(view, msg,
                    Snackbar.LENGTH_LONG);
            snackbar.setDuration(7000);
        }else {
            snackbar = Snackbar.make(view, msg,
                    Snackbar.LENGTH_SHORT);
        }

        snackbar.show();
    }

    private void checkOfflineMode(){

        ArrayList<SoftCopyModel> offlineBookList = loadOfflineBooks();
        SoftCopyModel searchBook = null;
        for(SoftCopyModel softCopy : offlineBookList){
            if(TextUtils.equals(softCopy.getBookId(), softCopyModel.getBookId())){
                searchBook = softCopy;
                break;
            }
        }

        if(searchBook == null){
            //if(fileUtils.isNetworkConnected()){
                prepareOfflineMode();
            //}
        }
    }

    private void setupContinueReadingList(){

        if(!softCopyModel.isFree()){
            if(!isPurchased(softCopyModel.getBookId())){
                return;
            }
        }

        ArrayList<BookDisplaySliderModel> bookDisplaySliderModels = loadContinueReadingList();

        int bookIndex = bookDisplaySliderModelIndex(bookDisplaySliderModels, softCopyModel.getBookId());
        if( bookIndex != -1){
            if(bookIndex != 0 ){
                BookDisplaySliderModel bookDisplaySliderModel = bookDisplaySliderModels.get(bookIndex);
                bookDisplaySliderModels.remove(bookIndex);
                bookDisplaySliderModels.add(0, bookDisplaySliderModel);
            }
        }else {
            BookDisplaySliderModel bookDisplaySliderModel = new BookDisplaySliderModel();
            bookDisplaySliderModel.setType(getString(R.string.offline_key));
            bookDisplaySliderModel.setBookId(softCopyModel.getBookId());
            bookDisplaySliderModel.setName(softCopyModel.getName());
            bookDisplaySliderModel.setPicUrl(softCopyModel.getPicUrl());
            bookDisplaySliderModel.setDownloadUrl(softCopyModel.getDownloadUrl());
            bookDisplaySliderModel.setFileName(softCopyModel.getFileName());

            if((bookDisplaySliderModels.size()) == recentLimit){
                bookDisplaySliderModels.remove(bookDisplaySliderModels.size() - 1);
            }

            if(bookDisplaySliderModels.size() > 0){
                bookDisplaySliderModels.add(0, bookDisplaySliderModel);
            }else {
                bookDisplaySliderModels.add(bookDisplaySliderModel);
            }

        }

        SharedPreferences sharedPreferences = getSharedPreferences("offline_book_list", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(bookDisplaySliderModels);
        editor.putString("bookDisplaySliderModels", json);
        editor.apply();

        SplashActivity.DISPLAY_UPDATE_NOTIFIER = true;
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

    private int bookDisplaySliderModelIndex(ArrayList<BookDisplaySliderModel> bookDisplaySliderModels, String bookId){

        for (int i = 0; i < bookDisplaySliderModels.size(); ++i){
            if(TextUtils.equals(bookId, bookDisplaySliderModels.get(i).getBookId())){
                return i;
            }
        }

        return -1;
    }

    private boolean isPurchased(String bookId){

        boolean purchased = false;
        ArrayList<SoftCopyModel> softCopyModels = MainActivity.USER_DATA.getPurchasedBooks();

        for (SoftCopyModel softCopyModel : softCopyModels){
            if(TextUtils.equals(softCopyModel.getBookId(), bookId)){
                purchased = true;
                break;
            }
        }

        return purchased;
    }
}
