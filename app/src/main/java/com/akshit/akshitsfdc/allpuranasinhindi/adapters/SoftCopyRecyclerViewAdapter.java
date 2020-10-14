package com.akshit.akshitsfdc.allpuranasinhindi.adapters;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.MainActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.SoftBookHomeActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.SoftBookPurchaseActivity;

import com.akshit.akshitsfdc.allpuranasinhindi.activities.SoftPuranaDashboardActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SoftCopyModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.UpdeshModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SoftCopyRecyclerViewAdapter  extends RecyclerView.Adapter<SoftCopyRecyclerViewAdapter.ViewHolder> implements Filterable {

    private ArrayList<SoftCopyModel> softCopyModels;
    private ArrayList<SoftCopyModel> softCopyModelsFullCopy;
    private Context mContext;

    public SoftCopyRecyclerViewAdapter(Context context, ArrayList<SoftCopyModel> softCopyModels ) {
        this.softCopyModels = softCopyModels;
        this.softCopyModelsFullCopy = new ArrayList<>(softCopyModels);
        mContext = context;
    }

    public void addData(ArrayList<SoftCopyModel> softCopyModels){
        this.softCopyModels.addAll(softCopyModels);
    }
    @NonNull
    @Override
    public SoftCopyRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.soft_purana_layout, parent, false);
        return new SoftCopyRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SoftCopyRecyclerViewAdapter.ViewHolder holder, int position) {

        SoftCopyModel softCopyModel = softCopyModels.get(position);

        boolean purchased = false;

        holder.title.setText(softCopyModel.getName());
        holder.language.setText(softCopyModel.getLanguage());


        Glide.with(mContext).load(softCopyModel.getPicUrl()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                holder.progress.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.progress.setVisibility(View.GONE);
                return false;
            }
        }).error(R.drawable.book_placeholder).fallback(R.drawable.book_placeholder).into( holder.bookImage);

        if(!softCopyModel.isBooksInPart()) {
            if (!MainActivity.USER_DATA.isPrimeMember()) {
                if (softCopyModel.isFree()) {
                    holder.price.setTextColor(mContext.getResources().getColor(R.color.color_leaderboard_green));
                    holder.price.setText("Free");
                } else {
                    holder.price.setTextColor(mContext.getResources().getColor(R.color.gblue));
                    DecimalFormat df = new DecimalFormat("#");
                    String text;
                    if (isPurchased(softCopyModel.getBookId())) {
                        holder.price.setTextColor(mContext.getResources().getColor(R.color.color_leaderboard_green));
                        text = "Purchased";
                        purchased = true;
                    } else {
                        text = "PRIME";//"Price: " + mContext.getString(R.string.rs) + "" + df.format(softCopyModel.getPrice());
                    }
                    holder.price.setText(text);
                }
            } else {
                if (softCopyModel.isFree()) {
                    holder.price.setTextColor(mContext.getResources().getColor(R.color.color_leaderboard_green));
                    holder.price.setText("Free");
                } else {
                    holder.price.setTextColor(mContext.getResources().getColor(R.color.color_leaderboard_green));
                    holder.price.setText("Prime");
                }
            }
            if(isFavorite(softCopyModel)){
                setBookFavorite(holder.favoriteButton);
            }else {
                setBookNotFavorite(holder.favoriteButton);

            }
        }else {
            holder.price.setTextColor(mContext.getResources().getColor(R.color.color_leaderboard_yellow));
            String s = softCopyModel.getBookParts().size()+" Parts";
            holder.price.setText(s);
            holder.favoriteButton.setEnabled(false);
            holder.favoriteButton.setText("Expandable");

        }



        holder.favoriteButton.setOnClickListener(v -> {
            if(isFavorite(softCopyModel)){
                removeFavorite(holder.favoriteButton, softCopyModel, holder.progress);
            }else{
                addToFavorite(holder.favoriteButton, softCopyModel, holder.progress);
            }
        } );

        boolean finalPurchased = purchased;
        holder.bookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(softCopyModel.isBooksInPart()){

                    navigateToSoftPuranaDashBoard("parts", softCopyModel);
                    return;
                }

                if(softCopyModel.isFree() || finalPurchased || MainActivity.USER_DATA.isPrimeMember()){
                    navigateToBookHome(softCopyModel);
                }else {
                    navigateToBookPurchase(softCopyModel);
                }
            }
        });
    }

    private void navigateToSoftPuranaDashBoard(String type, SoftCopyModel softCopyModel){
        Intent intent = new Intent(mContext, SoftPuranaDashboardActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("softCopyModel",softCopyModel);
        mContext.startActivity(intent);
        //finish();
    }

    private void setBookFavorite(Button button){
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            button.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bordered_button_blue) );
        } else {
            button.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bordered_button_blue));
        }
        button.setText("- Favorite");
        button.setTextColor(Color.parseColor("#4285F4"));
    }
    private void setBookNotFavorite(Button button){
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            button.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bordered_button_green) );
        } else {
            button.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bordered_button_green));
        }
        button.setText("+ Favorite");
        button.setTextColor(Color.parseColor("#0F9D58"));
    }

    private boolean isFavorite(SoftCopyModel softCopyModel){

        SoftCopyModel favBook = getFavoriteBookReference(loadFavBooks(), softCopyModel);

        return favBook != null;
    }

    private void addToFavorite(Button favoriteButton, SoftCopyModel softCopyModel, ProgressBar progress){

        progress.setVisibility(View.VISIBLE);
        favoriteButton.setEnabled(false);

        saveData(softCopyModel);

        setBookFavorite(favoriteButton);
        favoriteButton.setEnabled(true);
        notifyDataSetChanged();
        progress.setVisibility(View.GONE);

    }
    private void removeFavorite(Button favoriteButton, SoftCopyModel softCopyModel, ProgressBar progress){

        progress.setVisibility(View.VISIBLE);
        favoriteButton.setEnabled(false);
        notifyDataSetChanged();

        deleteData(softCopyModel);

        setBookNotFavorite(favoriteButton);
        notifyDataSetChanged();
        favoriteButton.setEnabled(true);
        progress.setVisibility(View.GONE);
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
    private void navigateToBookHome(SoftCopyModel softCopyModel){
        Intent intent = new Intent(mContext, SoftBookHomeActivity.class);
        intent.putExtra("softCopyModel",softCopyModel);
        mContext.startActivity(intent);
    }
    private void navigateToBookPurchase(SoftCopyModel softCopyModel){
        Intent intent = new Intent(mContext, SoftBookPurchaseActivity.class);
        intent.putExtra("softCopyModel",softCopyModel);
        mContext.startActivity(intent);
    }
    @Override
    public int getItemCount() {
        return softCopyModels.size();

    }

    @Override
    public Filter getFilter() {
        return bookFilter;
    }

    private Filter bookFilter = new Filter(){

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<SoftCopyModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(softCopyModelsFullCopy);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (SoftCopyModel item : softCopyModelsFullCopy) {

                    if (item.getName().toLowerCase().contains(filterPattern)
                            || item.getDescription().toLowerCase().contains(filterPattern)
                    ) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            softCopyModels.clear();
            softCopyModels.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImage;
        TextView title;
        TextView language;
        TextView price;
        ProgressBar progress;
        Button favoriteButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bookImage = itemView.findViewById(R.id.bookImage);
            title = itemView.findViewById(R.id.title);
            language = itemView.findViewById(R.id.language);
            price = itemView.findViewById(R.id.price);
            progress = itemView.findViewById(R.id.progress);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
        }
    }

    private void saveData(SoftCopyModel offlineBook) {

        ArrayList<SoftCopyModel> offlineBookList = loadFavBooks();

        offlineBookList.add(offlineBook);

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("offline_book_list", mContext.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(offlineBookList);
        editor.putString("favoriteBookList", json);
        editor.apply();
    }
    private void deleteData(SoftCopyModel favBook) {

        ArrayList<SoftCopyModel> offlineBookList = loadFavBooks();

        SoftCopyModel bookModel = getFavoriteBookReference(offlineBookList, favBook);

        if(bookModel != null){
            if(offlineBookList.contains(bookModel)){
                offlineBookList.remove(bookModel);
            }
        }
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("offline_book_list", mContext.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(offlineBookList);
        editor.putString("favoriteBookList", json);
        editor.apply();
    }
    private ArrayList<SoftCopyModel> loadFavBooks() {

        ArrayList<SoftCopyModel> favList;

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("offline_book_list", mContext.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("favoriteBookList", null);
        Type type = new TypeToken<ArrayList<SoftCopyModel>>() {}.getType();
        favList = gson.fromJson(json, type);

        if (favList == null) {
            favList = new ArrayList<>();
        }

        return favList;
    }

    private SoftCopyModel getFavoriteBookReference(ArrayList<SoftCopyModel> softCopyModels, SoftCopyModel bookModel){
        SoftCopyModel book = null;

        for(SoftCopyModel softCopyModel : softCopyModels){
            if(TextUtils.equals(softCopyModel.getBookId(), bookModel.getBookId())){
                book = softCopyModel;
                break;
            }
        }

        return book;
    }
}
