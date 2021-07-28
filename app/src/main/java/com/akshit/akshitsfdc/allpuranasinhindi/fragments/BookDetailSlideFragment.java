package com.akshit.akshitsfdc.allpuranasinhindi.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.akshit.akshitsfdc.allpuranasinhindi.R;

import java.io.InputStream;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookDetailSlideFragment extends Fragment {

    private String imageUrl;
    private ImageView displayImage;
    private ProgressBar progress;
    private Activity mContex;
    public BookDetailSlideFragment(String imageUrl, Activity mContex) {
        // Required empty public constructor
        this.imageUrl = imageUrl;
        this.mContex = mContex;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_detail_slide, container, false);
        displayImage = view.findViewById(R.id.displayImage);
        progress = view.findViewById(R.id.progress);

        loadDisplayImage();
        return view;
    }

    private void loadDisplayImage(){

        if(TextUtils.isEmpty(imageUrl) || TextUtils.equals(imageUrl, null)  || imageUrl == null){
            displayImage.setImageResource(R.drawable.book_placeholder);
        }else {

            try {
                new DownloadImageTask(displayImage)
                        .execute(imageUrl);
            }catch (Exception e){
                e.printStackTrace();
                Log.d(TAG, "loadDisplayImage: >> error loading images!");
            }

        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
