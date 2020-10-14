package com.akshit.akshitsfdc.allpuranasinhindi.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akshit.akshitsfdc.allpuranasinhindi.R;

import com.akshit.akshitsfdc.allpuranasinhindi.activities.HardCopyBookDashboardActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.SearchActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.SearchHardCopyActivity;

import java.util.ArrayList;
import java.util.List;


public class SearchHistoryRecyclerViewAdapter extends RecyclerView.Adapter<SearchHistoryRecyclerViewAdapter.ViewHolder> implements Filterable {

    private ArrayList<String> historyList;
    private ArrayList<String> historyListCopy;
    private Context mContext;
    private EditText searchEditText;

    public SearchHistoryRecyclerViewAdapter(ArrayList<String> historyList, Context mContext, EditText searchEditText) {
        this.historyList = historyList;
        this.historyListCopy = new ArrayList<>(historyList);
        this.mContext = mContext;
        this.searchEditText = searchEditText;
    }

    @NonNull
    @Override
    public SearchHistoryRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_history_layout, parent, false);
        return new SearchHistoryRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHistoryRecyclerViewAdapter.ViewHolder holder, int position) {

        String search = historyList.get(position);

        holder.historyText.setText(search);

        holder.parent.setOnClickListener(view -> {
            searchEditText.setText(search);
            if(mContext instanceof HardCopyBookDashboardActivity){
                navigateToSearchHardCopyActivity(search);
            }else {
                navigateToSearchActivity(search);
            }

        });

    }
    private void navigateToSearchActivity(String key){
        Intent intent = new Intent(mContext, SearchActivity.class);
        intent.putExtra("key", key);
        mContext.startActivity(intent);
        //finish();
    }
    private void navigateToSearchHardCopyActivity(String key){
        Intent intent = new Intent(mContext, SearchHardCopyActivity.class);
        intent.putExtra("key", key);
        mContext.startActivity(intent);
        //finish();
    }
    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView historyIcon;
        TextView historyText;
        RelativeLayout parent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            historyIcon = itemView.findViewById(R.id.historyIcon);
            historyText = itemView.findViewById(R.id.historyText);
            parent = itemView.findViewById(R.id.parent);

        }
    }

    @Override
    public Filter getFilter() {
        return historyFilter;
    }

    private Filter historyFilter = new Filter(){

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<String> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(historyListCopy);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();


                for (String key : historyListCopy) {
                    if (key.toLowerCase().contains(filterPattern)
                    ) {
                        filteredList.add(key);
                    }
                }
            }


            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            historyList.clear();
            historyList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
