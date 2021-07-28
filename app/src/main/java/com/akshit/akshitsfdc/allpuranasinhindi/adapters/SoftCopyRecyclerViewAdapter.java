package com.akshit.akshitsfdc.allpuranasinhindi.adapters;


import android.graphics.drawable.Drawable;

import android.text.TextUtils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.BaseActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.SoftBookHomeActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.SoftBookPurchaseActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.SoftPuranaDashboardActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.SplashActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SoftCopyModel;
import com.akshit.akshitsfdc.allpuranasinhindi.service.SQLService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import java.util.ArrayList;
import java.util.List;

public class SoftCopyRecyclerViewAdapter  extends RecyclerView.Adapter<SoftCopyRecyclerViewAdapter.ViewHolder> implements Filterable {

    private List<SoftCopyModel> softCopyModels;
    private List<SoftCopyModel> softCopyModelsFullCopy;
    private AppCompatActivity mContext;
    private SQLService sqlService;
    private List<String> favoriteBookIds;
    private BaseActivity baseActivity;

    public SoftCopyRecyclerViewAdapter(AppCompatActivity context, List<SoftCopyModel> softCopyModels ) {
        this.softCopyModels = softCopyModels;
        this.softCopyModelsFullCopy = new ArrayList<>(softCopyModels);
        mContext = context;
        baseActivity = (BaseActivity)mContext;
        sqlService = new SQLService(context);
        loadListOfFavorite();

    }


    public void addData(List<SoftCopyModel> softCopyModels){
        this.softCopyModels.addAll(softCopyModels);
        ((SoftPuranaDashboardActivity)mContext).checkEmptyList(this.softCopyModels);
        notifyDataSetChanged();
    }
    public void removeDataByPosition(int position){
        SoftCopyModel softCopyModel = getBookByPosition(position);
        if(softCopyModel != null){
            softCopyModels.remove(softCopyModel);
            ((SoftPuranaDashboardActivity)mContext).checkEmptyList(this.softCopyModels);
            notifyDataSetChanged();
        }
    }
    public void removeData(SoftCopyModel softCopyModel){
        if(softCopyModel != null){
            try{
                softCopyModels.remove(softCopyModel);
                ((SoftPuranaDashboardActivity)mContext).checkEmptyList(this.softCopyModels);
                notifyDataSetChanged();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public SoftCopyModel getBookByPosition(int position){
        if(softCopyModels != null && softCopyModels.size() > position){
            return softCopyModels.get(position);
        }
        return null;
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

        holder.progress.setVisibility(View.VISIBLE);

        boolean purchased = false;

        holder.title.setText(softCopyModel.getName());
        holder.language.setText(softCopyModel.getLanguage());

        if(!softCopyModel.isFree()){
            holder.ribbonImage.setVisibility(View.VISIBLE);
        }else {
            holder.ribbonImage.setVisibility(View.GONE);
        }


        if(softCopyModel.getCoverUri() == null){
            showPicture(softCopyModel.getPicUrl(), holder.bookImage, holder.progress);
        }else {
            showPicture(softCopyModel.getCoverUri().toString(), holder.bookImage, holder.progress);
        }
        if(!softCopyModel.isBooksInPart()) {
            holder.favoriteButton.setVisibility(View.VISIBLE);
            if (SplashActivity.USER_DATA == null ||  !SplashActivity.USER_DATA.isPrimeMember()) {
                if (softCopyModel.isFree()) {
                   // holder.price.setTextColor(mContext.getResources().getColor(R.color.color_leaderboard_green));
                    holder.price.setText("Free");
                } else {
//                    holder.price.setTextColor(mContext.getResources().getColor(R.color.gblue));
//                    DecimalFormat df = new DecimalFormat("#");
                    String text;
                    if (isPurchased(softCopyModel.getBookId())) {
                        //holder.price.setTextColor(mContext.getResources().getColor(R.color.color_leaderboard_green));
                        text = "Purchased";
                        purchased = true;
                    } else {
                        text = "PRIME";//"Price: " + mContext.getString(R.string.rs) + "" + df.format(softCopyModel.getPrice());
                    }
                    holder.price.setText(text);
                }
            } else {
                if (softCopyModel.isFree()) {
                    //holder.price.setTextColor(mContext.getResources().getColor(R.color.color_leaderboard_green));
                    holder.price.setText("Free");
                } else {
                   // holder.price.setTextColor(mContext.getResources().getColor(R.color.color_leaderboard_green));

                    holder.price.setText("Prime");
                }
            }
            if(isFavorite(softCopyModel)){
                setBookFavorite(holder.favoriteButton);
            }else {
                setBookNotFavorite(holder.favoriteButton);

            }
        }else {
            holder.favoriteButton.setVisibility(View.GONE);
            //holder.price.setTextColor(mContext.getResources().getColor(R.color.color_leaderboard_yellow));
            String s = softCopyModel.getBookParts().size()+" Parts";
            holder.price.setText(s);
//            holder.favoriteButton.setText("Expandable");

        }



        holder.favoriteCard.setOnClickListener(v -> {
            if(isFavorite(softCopyModel)){
                removeFavorite(holder.favoriteButton, softCopyModel);
            }else{
                addToFavorite(holder.favoriteButton, softCopyModel);
            }
        });

        boolean finalPurchased = purchased;
        holder.bookImage.setOnClickListener(v -> {

            if(softCopyModel.isBooksInPart()){

                navigateToSoftPuranaDashBoard("parts", softCopyModel);
                return;
            }

            if(softCopyModel.isFree() || finalPurchased ||
                    (SplashActivity.USER_DATA != null && SplashActivity.USER_DATA.isPrimeMember())){
                navigateToBookHome(softCopyModel);
            }else {
                navigateToBookPurchase(softCopyModel);
            }
        });
    }


    private void showPicture(String uri, ImageView imageView, ProgressBar progressBar){

        Glide.with(mContext).load(uri)
                .listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }
        }).error(R.drawable.book_placeholder).fallback(R.drawable.book_placeholder).into(imageView);
    }
    private void navigateToSoftPuranaDashBoard(String type, SoftCopyModel softCopyModel){

        baseActivity.routing.clearParams();
        baseActivity.routing.appendParams("singleBook", false);
        baseActivity.routing.appendParams("type", type);
        baseActivity.routing.appendParams("fromHome", false);
        baseActivity.routing.appendParams("softCopyModel", softCopyModel);
        baseActivity.routing.navigate(SoftPuranaDashboardActivity.class, false);

        //finish();
    }

    private void setBookFavorite(ImageView button){
        ((BaseActivity)mContext).showImage(R.drawable.ic_favorite_active, button);
    }
    private void setBookNotFavorite(ImageView button){
        ((BaseActivity)mContext).showImage(R.drawable.ic_favorite_inactive, button);
    }

    private boolean isFavorite(SoftCopyModel softCopyModel){

        for (String id:favoriteBookIds
             ) {
            if(TextUtils.equals(id, softCopyModel.getBookId())){
                return true;
            }

        }
        return false;
    }

    private void addToFavorite(ImageView favoriteButton, SoftCopyModel softCopyModel){

        softCopyModel.setFavorite(true);
        setBookFavorite(favoriteButton);
        long result = sqlService.updateUpdateFavorite(softCopyModel);
        if(result > 0 ){
            String msg = softCopyModel.getName()+" Added to your favorite list";
            baseActivity.uiUtils.showShortSuccessSnakeBar(msg);
        }else {
            String msg = softCopyModel.getName()+" Removed from favorite list";
            baseActivity.uiUtils.showShortErrorSnakeBar(softCopyModel.getName()+" Could not be removed from your favorite list, try again later");
        }
        loadListOfFavorite();

    }
    private void removeFavorite(ImageView favoriteButton, SoftCopyModel softCopyModel){

        softCopyModel.setFavorite(false);
        setBookNotFavorite(favoriteButton);

        long result = sqlService.updateUpdateFavorite(softCopyModel);
        if(result > 0 ){
            String msg = softCopyModel.getName()+" Removed from your favorite list";
            baseActivity.uiUtils.showShortSnakeBar(msg);
        }else {
            String msg = softCopyModel.getName()+" Removed from favorite list";
            baseActivity.uiUtils.showShortErrorSnakeBar(softCopyModel.getName()+" Could not be removed from your favorite list, try again later");
        }
        loadListOfFavorite();
    }

    private boolean isPurchased(String bookId){

        if(SplashActivity.USER_DATA == null){
            return false;
        }
        boolean purchased = false;
        List<SoftCopyModel> softCopyModels = SplashActivity.USER_DATA.getPurchasedBooks();

        for (SoftCopyModel softCopyModel : softCopyModels){
            if(TextUtils.equals(softCopyModel.getBookId(), bookId)){
                purchased = true;
                break;
            }
        }

        return purchased;
    }
    private void navigateToBookHome(SoftCopyModel softCopyModel){
        baseActivity.routing.clearParams();
        baseActivity.routing.appendParams("softCopyModel", softCopyModel);
        baseActivity.routing.navigate(SoftBookHomeActivity.class, false);
    }
    private void navigateToBookPurchase(SoftCopyModel softCopyModel){
        baseActivity.routing.clearParams();
        baseActivity.routing.appendParams("softCopyModel", softCopyModel);
        baseActivity.routing.navigate(SoftBookPurchaseActivity.class, false);
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
        ImageView favoriteButton;
        View favoriteCard;
        ImageView ribbonImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bookImage = itemView.findViewById(R.id.bookImage);
            title = itemView.findViewById(R.id.title);
            language = itemView.findViewById(R.id.language);
            price = itemView.findViewById(R.id.price);
            progress = itemView.findViewById(R.id.progress);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
            favoriteCard = itemView.findViewById(R.id.favoriteCard);
            ribbonImage = itemView.findViewById(R.id.ribbonImage);

        }
    }




    private void loadListOfFavorite(){
        favoriteBookIds = sqlService.getFavoriteBookIds();
    }


}
