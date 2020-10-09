package com.akshit.akshitsfdc.allpuranasinhindi.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.models.BookDisplaySliderModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

public class DisplaySliderPageAdapter extends PagerAdapter {

    private ArrayList<BookDisplaySliderModel> bookDisplaySliders;
    private LayoutInflater layoutInflater;
    private Context context;

    public DisplaySliderPageAdapter(ArrayList<BookDisplaySliderModel> bookDisplaySliders, Context context) {
        this.bookDisplaySliders = bookDisplaySliders;
        this.context = context;
    }

    @Override
    public int getCount() {
        return bookDisplaySliders.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {


        BookDisplaySliderModel  bookDisplaySliderModel = bookDisplaySliders.get(position);

        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.display_book_layout, container, false);


        ImageView imageView;
        TextView title;
        ProgressBar progress;

        imageView = view.findViewById(R.id.bookImage);
        title = view.findViewById(R.id.title);
        progress = view.findViewById(R.id.progress);

        progress.setVisibility(View.VISIBLE);

        Glide.with(context).load(bookDisplaySliderModel.getPicUrl()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                progress.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                progress.setVisibility(View.GONE);
                return false;
            }
        }).error(R.drawable.book_placeholder).fallback(R.drawable.book_placeholder).into(imageView);
        title.setText(bookDisplaySliderModel.getName());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(context, DetailActivity.class);
                //intent.putExtra("param", models.get(position).getTitle());
                //context.startActivity(intent);
                // finish();

            }
        });

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
