package com.akshit.akshitsfdc.allpuranasinhindi.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.fragments.CommentFragment;
import com.akshit.akshitsfdc.allpuranasinhindi.fragments.PostFragment;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SoftCopyModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.UpdeshModel;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.FileUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UpdeshRecyclerViewAdapter extends RecyclerView.Adapter<UpdeshRecyclerViewAdapter.ViewHolder>{


    private  ArrayList<UpdeshModel> updeshModels;
    private Context mContext;
    private FirebaseUser currentUser;
    private FileUtils fileUtils;
    private UpdeshModel currentUpadeshModel;

    public UpdeshRecyclerViewAdapter(Context context, ArrayList<UpdeshModel> updeshModels) {
        this.updeshModels = updeshModels;
        mContext = context;
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        fileUtils = new FileUtils(mContext);
    }
    public void addData(ArrayList<UpdeshModel> updeshModels){
        this.updeshModels.addAll(updeshModels);
    }
    @NonNull
    @Override
    public UpdeshRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.updesh_layout, parent, false);
        return new UpdeshRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
       return  updeshModels.size();
    }

    private int getWidthPixels(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    @Override
    public void onBindViewHolder(@NonNull UpdeshRecyclerViewAdapter.ViewHolder holder, int position) {

        UpdeshModel updeshModel = updeshModels.get(position);

        setPublisherView(holder, updeshModel);

        holder.updeshText.setText(updeshModel.getUpdesh());

        holder.timeText.setText(updeshModel.getUpdeshTime());

        if(isAlreadyLiked(updeshModel)){
            holder.likeText.setTextColor(mContext.getResources().getColor(R.color.gblue));
            holder.likeImage.setImageResource(R.drawable.ic_hert_filled);
        }
        String text;

        text = updeshModel.getLikeList().size()+" likes";
        holder.likeCounter.setText(text);

        text = updeshModel.getCommentsList().size()+" comments | "+updeshModel.getShareCount()+" Shares";
        holder.commentsShareCounter.setText(text);

        setListeners(holder, updeshModel);

    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView updeshakImageView;
        TextView updeshakNameText;
        TextView timeText;
        TextView updeshText;
        TextView likeCounter;
        TextView commentsShareCounter;
        LinearLayout likeActionLayout;
        LinearLayout commentActionLayout;
        LinearLayout shareActionLayout;
        ImageView likeImage;
        TextView likeText;
        ImageView commentImage;
        TextView commentText;
        ImageView shareImage;
        TextView shareText;
        ProgressBar progress;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            updeshakImageView = itemView.findViewById(R.id.updeshakImageView);
            updeshakNameText = itemView.findViewById(R.id.updeshakNameText);
            timeText = itemView.findViewById(R.id.timeText);
            updeshText = itemView.findViewById(R.id.updeshText);
            likeCounter = itemView.findViewById(R.id.likeCounter);
            commentsShareCounter = itemView.findViewById(R.id.commentsShareCounter);
            likeActionLayout = itemView.findViewById(R.id.likeActionLayout);
            commentActionLayout = itemView.findViewById(R.id.commentActionLayout);
            shareActionLayout = itemView.findViewById(R.id.shareActionLayout);
            likeImage = itemView.findViewById(R.id.likeImage);
            likeText = itemView.findViewById(R.id.likeText);
            commentImage = itemView.findViewById(R.id.commentImage);
            commentText = itemView.findViewById(R.id.commentText);
            shareImage = itemView.findViewById(R.id.shareImage);
            shareText = itemView.findViewById(R.id.shareText);
            progress = itemView.findViewById(R.id.progress);
        }
    }

    private void setPublisherView(UpdeshRecyclerViewAdapter.ViewHolder holder, UpdeshModel updeshModel){

        holder.updeshakNameText.setText(updeshModel.getUpdeshakName());
        Glide.with(mContext).load(updeshModel.getUpdeshakPicId()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }
            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).error(R.drawable.profile_placeholder).fallback(R.drawable.profile_placeholder).into( holder.updeshakImageView);
    }

    private void setListeners(UpdeshRecyclerViewAdapter.ViewHolder holder, UpdeshModel updeshModel){
        holder.likeActionLayout.setOnClickListener(v->{
            likeAction(holder, updeshModel);
        });
        holder.likeImage.setOnClickListener(v->{
            likeAction(holder, updeshModel);
        });
        holder.likeText.setOnClickListener(v->{
            likeAction(holder, updeshModel);
        });
        holder.commentActionLayout.setOnClickListener(v->{
            commentAction(updeshModel);
        });
        holder.commentImage.setOnClickListener(v->{
            commentAction(updeshModel);
        });
        holder.commentText.setOnClickListener(v->{
            commentAction(updeshModel);
        });
        holder.shareActionLayout.setOnClickListener(v->{
            shareAction(holder, updeshModel);
        });
        holder.shareImage.setOnClickListener(v->{
            shareAction(holder, updeshModel);
        });
        holder.shareText.setOnClickListener(v->{
            shareAction(holder, updeshModel);
        });
    }

    private void likeAction(UpdeshRecyclerViewAdapter.ViewHolder holder, UpdeshModel updeshModel){

        if(!fileUtils.isNetworkConnected()){
            fileUtils.showShortToast("You are not connected to the internet.");
            return;
        }

        if(isAlreadyLiked(updeshModel)){
            unlikeUpdesh(holder, updeshModel);
        }else {
            likeUpdesh(holder, updeshModel);
        }

    }
    private void commentAction(UpdeshModel updeshModel){
        startCommentFragment(updeshModel);
    }

    private void shareAction(UpdeshRecyclerViewAdapter.ViewHolder holder, UpdeshModel updeshModel){

        int SHARE_INTENT_CODE = 101;
        this.currentUpadeshModel = updeshModel;

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, updeshModel.getUpdesh()+" - "+mContext.getString(R.string.app_name));
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        ((Activity)mContext).startActivityForResult(shareIntent, SHARE_INTENT_CODE);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 101 && resultCode != 0){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            this.currentUpadeshModel.setShareCount(this.currentUpadeshModel.getShareCount()+1);
            final DocumentReference docRef = db.collection("updesh_collection").document(""+this.currentUpadeshModel.getCreatedMilliseconds());
            docRef.update("shareCount",  this.currentUpadeshModel.getShareCount()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    notifyDataSetChanged();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
    }
    private boolean isAlreadyLiked(UpdeshModel updeshModel){

        if(currentUser != null){
            return updeshModel.getLikeList().contains(currentUser.getUid());
        }else {
            return false;
        }

    }

    private void showPB(ProgressBar progress){
            progress.setVisibility(View.VISIBLE);
    }

    private void hidePB(ProgressBar progress){
            progress.setVisibility(View.GONE);

    }

    private void likeUpdesh(UpdeshRecyclerViewAdapter.ViewHolder holder, UpdeshModel updeshModel){
        updeshModel.getLikeList().add(currentUser.getUid());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("updesh_collection").document(""+updeshModel.getCreatedMilliseconds());
        showPB(holder.progress);
        holder.likeText.setTextColor(mContext.getResources().getColor(R.color.gblue));
        holder.likeImage.setImageResource(R.drawable.ic_hert_filled);
        docRef.update("likeList",  updeshModel.getLikeList()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                notifyDataSetChanged();
                hidePB(holder.progress);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hidePB(holder.progress);
            }
        });
    }
    private void unlikeUpdesh(UpdeshRecyclerViewAdapter.ViewHolder holder, UpdeshModel updeshModel){

        updeshModel.getLikeList().remove(currentUser.getUid());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("updesh_collection").document(""+updeshModel.getCreatedMilliseconds());
        showPB(holder.progress);
        holder.likeText.setTextColor(mContext.getResources().getColor(R.color.off_notification_color));
        holder.likeImage.setImageResource(R.drawable.ic_heart_empty);
        docRef.update("likeList",  updeshModel.getLikeList()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                notifyDataSetChanged();
                hidePB(holder.progress);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hidePB(holder.progress);
            }
        });
    }

    private void startCommentFragment(UpdeshModel updeshModel){

        CommentFragment commentFragment = new CommentFragment(updeshModel);
        FragmentManager manager = ((FragmentActivity)mContext).getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.parentLayout, commentFragment,"comment_fragment");
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
