package com.akshit.akshitsfdc.allpuranasinhindi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.models.BookDisplayCollectionModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SliderItem;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SliderModel;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class HomeParentDisplayRecyclerViewAdapter extends RecyclerView.Adapter<HomeParentDisplayRecyclerViewAdapter.ViewHolder>{

    private ArrayList<BookDisplayCollectionModel> bookDisplayCollection;
    private ArrayList<SliderModel> imageUrls;
    private Context context;

    public HomeParentDisplayRecyclerViewAdapter(ArrayList<BookDisplayCollectionModel> bookDisplayCollection, Context context, ArrayList<SliderModel> imageUrls) {
        this.bookDisplayCollection = bookDisplayCollection;
        this.imageUrls = imageUrls;
        this.context = context;
       // this.bookDisplayCollection.add(0, new BookDisplayCollectionModel());
    }
    @NonNull
    @Override
    public HomeParentDisplayRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_parent_display_layout, parent, false);
        return new HomeParentDisplayRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeParentDisplayRecyclerViewAdapter.ViewHolder holder, int position) {


            BookDisplayCollectionModel bookDisplayCollectionModel = bookDisplayCollection.get(position);

            //setupDisplayBooks(holder.recycler_view, bookDisplayCollectionModel);

            if(position == 0){
                holder.booksImageCard.setVisibility(View.VISIBLE);
               // holder.bookDisplayLayout.setVisibility(View.GONE);
                setBanner(holder.imageSlider, imageUrls);
            }else {
                holder.booksImageCard.setVisibility(View.GONE);
                //holder.bookDisplayLayout.setVisibility(View.VISIBLE);
                //ViewGroup.LayoutParams params = holder.booksImageCard.getLayoutParams();
                //params.height = 0;
                //holder.booksImageCard.setLayoutParams(params);
                //holder.booksImageCard.setVisibility(View.GONE);
               //holder.parent.removeView(holder.booksImageCard);
                //holder.booksImageCard.setVisibility(View.GONE);
                //holder.booksImageCard.setLayoutParams(new RecyclerView.LayoutParams(0, 0));

            }
        holder.titleText.setText(bookDisplayCollectionModel.getHeaderTitle());
        setupDisplayBooks(holder.recycler_view, bookDisplayCollectionModel);


    }

    private void setBanner(SliderView imageSlider, ArrayList<SliderModel> imageUrls){

        SliderAdapterExample sliderAdapterExample = new SliderAdapterExample(context);

        for(SliderModel sliderModel : imageUrls){
            sliderAdapterExample.addItem(new SliderItem(sliderModel.getMoveTo(), sliderModel.getImageUrl(), sliderModel.isExternalLink(), sliderModel.getExternalUrl()));
        }
        imageSlider.setIndicatorAnimation(IndicatorAnimations.DROP);
        imageSlider.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
        imageSlider.startAutoCycle();
        imageSlider.setSliderAdapter(sliderAdapterExample);

    }

    private void setupDisplayBooks(RecyclerView recyclerView, BookDisplayCollectionModel bookDisplayCollectionModel){

        DisplaySliderRecyclerViewAdapter adapter = new DisplaySliderRecyclerViewAdapter(bookDisplayCollectionModel.getBookDisplaySliders(), context);
       recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
       recyclerView.setAdapter(adapter);

    }

    private void setupDisplaySlider(RelativeLayout displaySliderLayout, BookDisplayCollectionModel bookDisplayCollectionModel){

        RecyclerView recyclerView = displaySliderLayout.findViewById(R.id.recycler_view);
        TextView titleText = displaySliderLayout.findViewById(R.id.titleText);
        titleText.setText(bookDisplayCollectionModel.getHeaderTitle());
        DisplaySliderRecyclerViewAdapter adapter = new DisplaySliderRecyclerViewAdapter(bookDisplayCollectionModel.getBookDisplaySliders(), context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }
    @Override
    public int getItemCount() {
        return bookDisplayCollection.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout parent;
        RecyclerView recycler_view;
        CardView booksImageCard;
        SliderView imageSlider;
        TextView titleText;
        RelativeLayout bookDisplayLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            parent = itemView.findViewById(R.id.parent);
            recycler_view = itemView.findViewById(R.id.recycler_view);
            booksImageCard = itemView.findViewById(R.id.booksImageCard);
            imageSlider = itemView.findViewById(R.id.imageSlider);
            titleText = itemView.findViewById(R.id.titleText);
           bookDisplayLayout = itemView.findViewById(R.id.bookDisplayLayout);

        }
    }
}
