package com.akshit.akshitsfdc.allpuranasinhindi.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FileUtils {

    private  Context context;;
    private static final int  MEGABYTE = 1024 * 1024;

    public FileUtils(Context context)
    {
        this.context=context;

    }

    public File getFolder(String folderName){

        File folder = new File(context.getExternalFilesDir(null).toString(), folderName);
        folder.mkdir();

        return folder;
    }

    public File getFile(File folder, String fileName){

        File file = new File(folder, fileName);

        try{
            file.createNewFile();
        }catch (IOException e){
            e.printStackTrace();

        }

        return file;
    }

    public Uri getUri(String authority, File file){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            return FileProvider.getUriForFile(context, authority, file);
        } else {
            return Uri.fromFile(file);
        }
    }
    public Uri getUri(File file){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            return FileProvider.getUriForFile(context, "com.akshit.akshitsfdc.allpuranasinhindi.provider", file);
        } else {
            return Uri.fromFile(file);
        }
    }

    public void showShortToast(String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    public void showLongToast(String msg){
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }


    public  boolean isNetworkConnected() {
        boolean result = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cm != null) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        result = true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        result = true;
                    }
                }
            }
        } else {
            if (cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    // connected to the internet
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        result = true;
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }
    public String capitalize(String str)
    {
        if(str == null) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    public String capitalizeWord(String str)
    {
        if(str == null) return str;
        String result="";
        String[] arrOfStr = str.split(" ");

        for(int i = 0; i < arrOfStr.length; ++i){
            if(i==0){
                result = capitalize(arrOfStr[i]);
            }else{
                result = result + " "+capitalize(arrOfStr[i]);
            }
        }


        return result;
    }

    public String getCurrentDate(){

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(c);
    }
    public String getCurrentTime(){

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        String formattedDate = simpleDateFormat.format(c);
        return formattedDate;
    }

    public boolean deleteFileFromUri(Uri uri){

        if(uri == null){
            Log.d("FileUtils", "deleteFileFromUri: null uri");
            return false;
        }


        try{
            int result = context.getContentResolver().delete(uri, null, null);
           return result > 0;
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.d("FileUtils", "deleteFileFromUri: file does not exists : "+uri.getPath());

        return false;
    }

    public boolean isFileExists(String fileName){

        File folder = getFolder("puran_collection");

        final File file = new File(folder, fileName);

        Log.d("1245", "isFileExists: "+getUri(file));

        return file.exists();
    }
    public String initCaps(String text){
        String result;
        if(text == null) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

}
