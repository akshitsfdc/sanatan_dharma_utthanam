package com.akshit.akshitsfdc.allpuranasinhindi.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.models.CommentModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SoftCopyModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

public class CommentListRecyclerViewAdapter extends RecyclerView.Adapter<CommentListRecyclerViewAdapter.ViewHolder>{

    private ArrayList<CommentModel> commentModels;
    private Context mContext;

    public CommentListRecyclerViewAdapter(Context context, ArrayList<CommentModel> commentModels ) {
        this.commentModels = commentModels;
        mContext = context;
    }

    public void addComment(CommentModel commentModel){
        this.commentModels.add(commentModel);
    }

    @NonNull
    @Override
    public CommentListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout, parent, false);
        return new CommentListRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentListRecyclerViewAdapter.ViewHolder holder, int position) {

        CommentModel commentModel = commentModels.get(position);

        Glide.with(mContext).load(commentModel.getPicUrl()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }
            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).error(R.drawable.profile_placeholder).fallback(R.drawable.profile_placeholder).into( holder.profileImageView);

        holder.nameText.setText(commentModel.getName());
        holder.commentText.setText(commentModel.getComment());
        holder.timeText.setText(commentModel.getDateTime());
    }

    @Override
    public int getItemCount() {
        return commentModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImageView;
        TextView nameText;
        TextView commentText;
        TextView timeText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImageView = itemView.findViewById(R.id.profileImageView);
            nameText = itemView.findViewById(R.id.nameText);
            commentText = itemView.findViewById(R.id.commentText);
            timeText = itemView.findViewById(R.id.timeText);

        }
    }
}
