package com.akshit.akshitsfdc.allpuranasinhindi.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.akshit.akshitsfdc.allpuranasinhindi.R;

import com.akshit.akshitsfdc.allpuranasinhindi.activities.HomeActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.SearchActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.SoftPuranaDashboardActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.SplashActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.adapters.SearchHistoryRecyclerViewAdapter;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SearchAnalytics;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private ImageView backButton;
    private ImageView crossImage;
    private EditText searchEditText;

    private SearchHistoryRecyclerViewAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private Activity activity;
    private String searchHistoryCollectionName;
    private String analyticsCollectionName;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        backButton = view.findViewById(R.id.backButton);
        crossImage = view.findViewById(R.id.crossImage);
        searchEditText = view.findViewById(R.id.searchEditText);

        crossImage.setOnClickListener(view1 -> {
            searchEditText.setText("");
        });

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String key = searchEditText.getText().toString();

                if(key.length() > 0){
                    hideKeyboard(activity);
                    saveSearchHistory(key);
                    if(SplashActivity.APP_INFO != null && SplashActivity.APP_INFO.isSaveSearchAnalytics()){
                        saveSearchAnalytics(key);
                    }

                    navigateToSearchActivity(key);

                }

                return true;
            }
            return false;
        });




        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        backButton.setOnClickListener(view2 -> {
            hideKeyboard(activity);
            closeFragment();
        });

        return view;
    }
    private void saveSearchAnalytics(String key){

        try{
            SearchAnalytics searchAnalytics = new SearchAnalytics();

            searchAnalytics.setName(SplashActivity.USER_DATA.getName());
            searchAnalytics.setUserId(SplashActivity.USER_DATA.getuId());
            searchAnalytics.setEmail(SplashActivity.USER_DATA.getEmail());
            searchAnalytics.setPrime(SplashActivity.USER_DATA.isPrimeMember());
            searchAnalytics.setTimestamp(System.currentTimeMillis());
            searchAnalytics.setSearchedKeyword(key);


            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

            String docId = firebaseFirestore.collection(analyticsCollectionName).document().getId();
            firebaseFirestore.collection(analyticsCollectionName)
                    .document(docId).set(searchAnalytics);

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public void showSoftKeyboard(Activity activity, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)activity. getSystemService(Activity.INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = requireActivity();
        showSoftKeyboard(activity, searchEditText);

        searchHistoryCollectionName = "soft_book_search_history_collection";
        analyticsCollectionName = "search_analytics_digital";
        try {
            linearLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            adapter = new SearchHistoryRecyclerViewAdapter(loadSearchHistory(), activity, searchEditText);
            recyclerView.setAdapter(adapter);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void navigateToSearchActivity(String key){
        Intent intent = new Intent(activity, SearchActivity.class);
        intent.putExtra("key", key);
        activity.startActivity(intent);
        //finish();
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void saveSearchHistory(String search) {

        try{
            ArrayList<String> history = loadSearchHistory();

            Log.d("TAG", " saveSearchHistory: >> "+history);

            if(hasSearchKey(history, search)){
              return;
            }
            history.add(search);

            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(searchHistoryCollectionName, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(history);
            editor.putString("history", json);
            editor.apply();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private boolean hasSearchKey(ArrayList<String> history, String key){
       boolean result = false;

        for (String str : history
             ) {
            if(TextUtils.equals(str.toLowerCase(), key.toLowerCase())){
                result = true;
                break;
            }
        }

       return result;
    }

    private ArrayList<String> loadSearchHistory() {

        ArrayList<String> history = new ArrayList<>();

        try{

            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(searchHistoryCollectionName, MODE_PRIVATE);
            Gson gson = new Gson();

            String json = sharedPreferences.getString("history", null);

            Type type = new TypeToken<ArrayList<String>>() {}.getType();

            history = gson.fromJson(json, type);



        }catch (Exception e){
            e.printStackTrace();
        }
        if(history == null){
            history = new ArrayList<>();
        }
        return history;
    }

    public void closeFragment(){

        try {
            Fragment fr = requireActivity().getSupportFragmentManager().findFragmentByTag("search_fragment");
            if(fr != null){
                FragmentManager manager = requireActivity().getSupportFragmentManager();
                FragmentTransaction trans = manager.beginTransaction();
                trans.remove(fr);
                trans.commit();
                manager.popBackStack();
                if(activity instanceof HomeActivity){
                    ((HomeActivity)activity).showToolBar();
                }else {
                    ((SoftPuranaDashboardActivity)activity).showToolBar();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}