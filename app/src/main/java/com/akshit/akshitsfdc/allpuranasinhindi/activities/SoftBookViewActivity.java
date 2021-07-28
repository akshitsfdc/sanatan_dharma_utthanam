package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.models.CurrentDownloadingModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SoftCopyModel;
import com.akshit.akshitsfdc.allpuranasinhindi.service.SQLService;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.FileUtils;
import com.bumptech.glide.Glide;
import com.github.barteksc.pdfviewer.PDFView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.Timer;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class SoftBookViewActivity extends BaseActivity {

    private FileUtils fileUtils;
    private static final int  MEGABYTE = 1024 * 1024;
    public static String fileName;
    private File generatedFile;
    private String urlLink;
    private File folder;
    private SoftCopyModel softCopyModel;
    private PDFView pdfView;
    private ProgressBar determinateBar;
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
    private RelativeLayout internetLostView;
    private Uri offlineBookUri, offlineCoverUri;
    private SQLService sqlService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soft_book_view);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);


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

        uiUtils.setSnakebarView(getSnakBarView(findViewById(R.id.snakebarLayout)));

        sqlService = new SQLService(this);

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

        softCopyModel = (SoftCopyModel)routing.getParam("softCopyModel");


        if(MainActivity.DOWNLOAD_IN_PROGRESS){

            bookDownloading();
            pdfView.setOnClickListener(v->{
                if(headerView.getVisibility() == View.VISIBLE){
                    headerView.setVisibility(View.GONE);
                }else {
                    headerView.setVisibility(View.VISIBLE);
                }

            });
        }else {

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
            uiUtils.showLongErrorSnakeBar("Your device is running low memory please delete some books from offline or clean up your device");
        }

    }
    private void goToPageAction(){
        String numberText = currentNumberEditText.getText().toString().trim();

        hideSoftKeyboard(currentNumberEditText);
        if(TextUtils.isEmpty(numberText)){
            uiUtils.showShortErrorSnakeBar("Not a valid page number.");
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


    private void beforePdfLoad(){

        pageCountText.setVisibility(View.VISIBLE);

        determinateBar.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.GONE);

        pdfView.enableRenderDuringScale(true);
        pdfView.recycle();
        pdfView.useBestQuality(true);

    }
    private void changePageNumberText(int page , int pageCount){
        currentPageIndex = page;
        String text = currentPageIndex+1+"/"+pageCount;
        pageCountText.setText(text);
        currentNumberEditText.setText(String.valueOf(page+1));
    }
    private void setView(){


        SoftCopyModel offlineCopy = sqlService.getOfflineBookById(softCopyModel.getBookId());


        if(offlineCopy == null){
            firstTimeBook();
        }else {
            if(offlineCopy.getBookUri() == null){
                firstTimeBook();
            }else {

                currentPageIndex = offlineCopy.getReadingPage();
                beforePdfLoad();
                pdfView.fromUri(offlineCopy.getBookUri())
                        .defaultPage(offlineCopy.getReadingPage())
                        .onPageChange((page, pageCount) -> {
                            changePageNumberText(page, pageCount);
                            sqlService.updateOfflineBookReadingNumber(offlineCopy.getBookId(), page);
                        })
                        .onRender(nbPages -> {
                            totalPages = pdfView.getPageCount();
                            totalNumberEditText.setText(String.valueOf(pdfView.getPageCount()));
                        })
                        .onLongPress(e -> uiUtils.showShortSuccessSnakeBar("You are reading "+offlineCopy.getName()))
                        .enableSwipe(true)
                        .onError(t -> uiUtils.showShortErrorSnakeBar("Load error please try again.."))
                        .enableAnnotationRendering(true)
                        .load();

            }
        }
    }

    private void firstTimeBook(){
        if(fileUtils.isNetworkConnected()){
                    download();
                    startTimer();
                }else{
                    offlineMode();
                }
    }
    @Override
    public void onBackPressed() {
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
    private void offlineMode(){
        determinateBar.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.GONE);
        MainActivity.DOWNLOAD_IN_PROGRESS = false;
        MainActivity.CURRENT_DOWNLOAD_PROGRESS = 0;
        internetLostView.setVisibility(View.VISIBLE);
        uiUtils.showShortSnakeBar("You might got disconnected please try again.");
    }
    private void startTimer(){

        determinateBar.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.VISIBLE);

        timer = new Timer();

        timer.schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(() -> mainThreadCode(MainActivity.CURRENT_DOWNLOAD_PROGRESS));

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

                uiUtils.showLongErrorSnakeBar("You are not connected to the internet!");
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

            offlineBookUri = fileUtils.getUri(directory);



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

        String bookName = softCopyModel.getFileName();

        fileName = bookName+".pdf";

        urlLink = softCopyModel.getDownloadUrl();//getArguments().getString("url");

        folder = fileUtils.getFolder(getString(R.string.puran_collection_name));


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
                handler.post(() -> {
                    //setView();
                   offlineWork();
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
    protected void onDestroy() {
        super.onDestroy();
    }

    private void offlineWork(){
        runOnUiThread(() -> {
            prepareOfflineMode();
        });
    }
    private void prepareOfflineMode(){


        try{

            showLoading();

            final Bitmap[] bitmapForOffline = new Bitmap[1];

            Handler handler = new Handler();

            Thread.UncaughtExceptionHandler h = (th, ex) -> {

                if(generatedFile.exists()){
                    generatedFile.getAbsoluteFile().delete();
                }

                MainActivity.DOWNLOAD_IN_PROGRESS = false;
                MainActivity.CURRENT_DOWNLOAD_PROGRESS = 0;

                setBookOffline();
                hideLoading();
            };
            Thread background = new Thread(() -> {
                try {
                    bitmapForOffline[0] = getBitmapFromURL(softCopyModel.getPicUrl());

                    offlineCoverUri = saveBookImageForOffline(softCopyModel.getFileName(), bitmapForOffline[0]);

                } catch (Throwable t) {
                    setBookOffline();
                }
                handler.post(() -> {
                    setBookOffline();
                    hideLoading();
                });
            });
            background.setUncaughtExceptionHandler(h);
            // Start Thread
            background.start();

        }catch (Exception e){
            setBookOffline();
            e.printStackTrace();
        }finally {
            hideLoading();
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

    private void setBookOffline(){

        if(offlineBookUri != null ){

            long result = sqlService.insertOfflineBook(softCopyModel,
                    (offlineCoverUri == null?null:offlineCoverUri.toString()), offlineBookUri.toString());
        }
        navigateToItself();
        MainActivity.DOWNLOAD_IN_PROGRESS = false;
        MainActivity.CURRENT_DOWNLOAD_PROGRESS = 0;

    }
    private Uri saveBookImageForOffline(String fileName, Bitmap bitmap){

        String mImageName = "image_"+fileName+".jpg";

        File folder = fileUtils.getFolder("offline_book_images");

        final File file = new File(folder, mImageName);

        if (file.exists ()) {
            try{
                fileUtils.getUri(file);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        try{

            OutputStream stream = null;

            stream = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG,100, stream);

            stream.flush();

            stream.close();

        }catch (IOException e)
        {
            e.printStackTrace();
        }
        if(file.exists()) {
            return fileUtils.getUri(file);
        }

        return null;
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




}
