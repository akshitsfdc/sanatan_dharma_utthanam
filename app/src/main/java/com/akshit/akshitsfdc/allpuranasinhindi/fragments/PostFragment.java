package com.akshit.akshitsfdc.allpuranasinhindi.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.UpdeshActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.models.CommentModel;
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
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment {


    private View view;
    private ImageView updeshakImageView;
    private TextView updeshakNameText;
    private TextView thoughtText;
    private Button publishButton;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private FileUtils fileUtils;
    private ProgressBar progress;
    private RelativeLayout fragmentParent;
    public ProfileUpdateFragment profileUpdateFragment;

    public PostFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_post, container, false);
        fileUtils = new FileUtils(getContext());
        updeshakImageView = view.findViewById(R.id.updeshakImageView);
        updeshakNameText = view.findViewById(R.id.updeshakNameText);
        thoughtText = view.findViewById(R.id.thoughtText);
        publishButton = view.findViewById(R.id.publishButton);
        progress = view.findViewById(R.id.progress);
        fragmentParent = view.findViewById(R.id.fragmentParent);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        publishButton.setOnClickListener(v->{
            publishPost();
        });
        view.findViewById(R.id.backImageView).setOnClickListener(v -> {
            closeFragment();
        });
        view.findViewById(R.id.editProfile).setOnClickListener(v -> {
            showProfileUpdate();
        });
        view.findViewById(R.id.editProfileOuterLayout).setOnClickListener(v -> {
            showProfileUpdate();
        });
        setPublisherView();
        return view;
    }

    private void showProfileUpdate(){
        try{
            profileUpdateFragment = new ProfileUpdateFragment(this);
            FragmentManager manager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.fragmentParent, profileUpdateFragment,"profile_fragment");
            transaction.addToBackStack(null);
            transaction.commit();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public void setPublisherView(){

        if(!fileUtils.isNetworkConnected()){
            fileUtils.showShortToast("You are not connected to the internet!");
            return;
        }
        if(currentUser == null){
            fileUtils.showShortToast("You are not logged in, please try later!");
            return;
        }
        if(TextUtils.isEmpty(currentUser.getDisplayName())){
            updeshakNameText.setText("Your name not given yet");
        }else {
            updeshakNameText.setText(currentUser.getDisplayName());
        }

        Glide.with(this).load(currentUser.getPhotoUrl()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).error(R.drawable.profile_placeholder).fallback(R.drawable.profile_placeholder).into( updeshakImageView);
    }

    private boolean validate(){
        boolean valid = false;

        String text = thoughtText.getText().toString().trim();

        if(!TextUtils.isEmpty(text)){
            valid = true;
        }else {
            thoughtText.setError("Required.");
            valid = false;
        }
        if(!TextUtils.isEmpty(text) && text.length() > 20){
            valid = true;
        }else {
            thoughtText.setError("There must be at least 20 characters.");
            valid = false;
        }
        return valid;
    }
    private void publishPost(){

        if(!fileUtils.isNetworkConnected()){
            fileUtils.showShortToast("You are not connected to the internet!");
            return;
        }
        if(currentUser == null){
            fileUtils.showShortToast("You are not logged in, please try later!");
            return;
        }
        if(!validate()){
            return;
        }

        showPB(true);
        long currentMilliseconds = System.currentTimeMillis();

        final DocumentReference docRef = db.collection("updesh_collection").document(""+currentMilliseconds);


        docRef.set( getUpdeshModel(currentMilliseconds)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                fileUtils.showShortToast("Your thought has been published!");
                closeFragment();
                hidePB(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                fileUtils.showShortToast("Could not publish your thought at this time please try later.");
                hidePB(true);
            }
        });
    }

    private UpdeshModel getUpdeshModel(long updeshTime){

        UpdeshModel updeshModel = new UpdeshModel();
        if(TextUtils.isEmpty(currentUser.getDisplayName())){
            updeshModel.setUpdeshakName("Unknown name");
        }else{
            updeshModel.setUpdeshakName(currentUser.getDisplayName());
        }

        if(currentUser.getPhotoUrl() != null){
            updeshModel.setUpdeshakPicId(currentUser.getPhotoUrl().toString());
        }else{
            updeshModel.setUpdeshakPicId("na");
        }
        updeshModel.setUpdeshTime(fileUtils.getCurrentDate()+" "+fileUtils.getCurrentTime());
        updeshModel.setUpdesh(fileUtils.initCaps(thoughtText.getText().toString().trim()));
        updeshModel.setCreatedMilliseconds(updeshTime);
        updeshModel.setShareCount(0);
        updeshModel.setLikeList(new ArrayList<String>());
        updeshModel.setCommentsList(new ArrayList<CommentModel>());

        return updeshModel;
    }
    private void closeFragment(){

        try {
            Fragment fr = requireActivity().getSupportFragmentManager().findFragmentByTag("post_fragment");
            if(fr != null){
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction trans = manager.beginTransaction();
                trans.remove(fr);
                trans.commit();
                manager.popBackStack();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void showPB(boolean loadingActive){

        try{
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(loadingActive){
                progress.setVisibility(View.VISIBLE);
            }
        }



    }
    private void hidePB(boolean loadingActive){

        try{
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(loadingActive) {
                progress.setVisibility(View.GONE);
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        UpdeshActivity.updeshActivity.reloadData();
    }
}
