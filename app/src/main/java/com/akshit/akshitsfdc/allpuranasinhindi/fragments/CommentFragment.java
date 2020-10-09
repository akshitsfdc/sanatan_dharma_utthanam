package com.akshit.akshitsfdc.allpuranasinhindi.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.UpdeshActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.adapters.CommentListRecyclerViewAdapter;
import com.akshit.akshitsfdc.allpuranasinhindi.adapters.UpdeshRecyclerViewAdapter;
import com.akshit.akshitsfdc.allpuranasinhindi.models.CommentModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.UpdeshModel;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.FileUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentFragment extends Fragment {


    private FirebaseUser currentUser;
    private EditText commentEditText;
    private RecyclerView recycler_view;
    private TextView noCommentsText;
    private UpdeshModel updeshModel;
    private ProgressBar progress;
    private CommentListRecyclerViewAdapter adapter;
    private FileUtils fileUtils;
    private ImageView likeImage;
    private TextView commentCounter;

    public CommentFragment(UpdeshModel updeshModel) {
        this.updeshModel = updeshModel;
    }
    public CommentFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment, container, false);

        commentEditText = view.findViewById(R.id.commentEditText);
        recycler_view = view.findViewById(R.id.recycler_view);
        noCommentsText = view.findViewById(R.id.noCommentsText);
        progress = view.findViewById(R.id.progress);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        fileUtils = new FileUtils(getContext());
        likeImage = view.findViewById(R.id.likeImage);
        commentCounter = view.findViewById(R.id.commentCounter);

        if(updeshModel.getCommentsList() != null){
            if(updeshModel.getCommentsList().size() <=0){
                noCommentsText.setVisibility(View.VISIBLE);
            }else {
                loadComments();
            }
        }
        setCommentCount();
        setLikeview();
        view.findViewById(R.id.sendLayout).setOnClickListener(v->{
            hideKeyboard();
            postComment();
        });
        view.findViewById(R.id.sendImage).setOnClickListener(v->{
            hideKeyboard();
            postComment();
        });
        view.findViewById(R.id.backButtonImage).setOnClickListener(v->{
            closeFragment();
        });
        view.findViewById(R.id.likeImage).setOnClickListener(v->{
            if(isAlreadyLiked()){
                unlikeUpdesh();
            }else {
                likeUpdesh();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(layoutManager);
    }

    private void setCommentCount(){
        if(updeshModel.getCommentsList() != null){
            if(updeshModel.getCommentsList().size() > 0){
                String text = updeshModel.getCommentsList().size()+" comments";
                commentCounter.setText(text);
            }
        }
    }
    private void setLikeview(){
        if(updeshModel.getLikeList() != null){
           if(isAlreadyLiked()){
               likeImage.setImageResource(R.drawable.ic_hert_filled);
           }else {
               likeImage.setImageResource(R.drawable.ic_heart_empty);
           }
        }
    }
    private boolean isAlreadyLiked(){

        if(currentUser != null){
            return updeshModel.getLikeList().contains(currentUser.getUid());
        }else {
            return false;
        }

    }
    private void likeUpdesh(){
        updeshModel.getLikeList().add(currentUser.getUid());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("updesh_collection").document(""+updeshModel.getCreatedMilliseconds());
        showPB(true);
        likeImage.setImageResource(R.drawable.ic_hert_filled);
        docRef.update("likeList",  updeshModel.getLikeList()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                hidePB(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hidePB(true);
            }
        });
    }
    private void unlikeUpdesh(){

        updeshModel.getLikeList().remove(currentUser.getUid());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("updesh_collection").document(""+updeshModel.getCreatedMilliseconds());
        showPB(true);
        likeImage.setImageResource(R.drawable.ic_heart_empty);
        docRef.update("likeList",  updeshModel.getLikeList()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                hidePB(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hidePB(true);
            }
        });
    }
    private void closeFragment(){

        try {
            Fragment fr = requireActivity().getSupportFragmentManager().findFragmentByTag("comment_fragment");
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
    private boolean validate(){
        boolean valid = false;
        String text = commentEditText.getText().toString().trim();

        if(!TextUtils.isEmpty(text)){
            valid = true;
        }else{
            commentEditText.setError("Required.");
            valid = false;
        }
        return valid;
    }
    private void postComment(){
        if(!validate()){
            return;
        }
        if(updeshModel != null){
            if(updeshModel.getCommentsList() != null){
                Collections.reverse(updeshModel.getCommentsList());
                updeshModel.getCommentsList().add(composeCommentModel());

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                final DocumentReference docRef = db.collection("updesh_collection").document(""+updeshModel.getCreatedMilliseconds());
                showPB(true);
                docRef.update("commentsList",  updeshModel.getCommentsList()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        setCommentCount();
                        commentEditText.setText("");
                        loadComments();
                        noCommentsText.setVisibility(View.GONE);
                        hidePB(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Collections.reverse(updeshModel.getCommentsList());
                        fileUtils.showShortToast("There is an error posting your comment at this moment.");
                        hidePB(true);
                    }
                });
            }
        }
    }
    private CommentModel composeCommentModel(){

        CommentModel commentModel = new CommentModel();
        if(TextUtils.isEmpty(currentUser.getDisplayName())){
            commentModel.setName("Unknown name");
        }else {
            commentModel.setName(currentUser.getDisplayName());
        }


        if(currentUser.getPhotoUrl() != null){
            commentModel.setPicUrl(currentUser.getPhotoUrl().toString());
        }
        commentModel.setUid(currentUser.getUid());
        String comment = fileUtils.initCaps(commentEditText.getText().toString().trim());
        commentModel.setComment(comment);
        commentModel.setDateTime(fileUtils.getCurrentDate()+" "+fileUtils.getCurrentTime());

        return commentModel;
    }
    private void loadComments(){

        if(updeshModel != null){
            if(updeshModel.getCommentsList().size() > 0){
                Collections.reverse(updeshModel.getCommentsList());
                populateList(updeshModel.getCommentsList());
            }
        }

    }

    private void populateList(ArrayList<CommentModel> commentModels){

        adapter = new CommentListRecyclerViewAdapter(getContext(), commentModels);
        recycler_view.setAdapter(adapter);
    }

    private void showPB(boolean loadingActive){
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        //WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if(loadingActive){
            progress.setVisibility(View.VISIBLE);
        }


    }
    private void hidePB(boolean loadingActive){
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        if(loadingActive) {
            progress.setVisibility(View.GONE);
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        UpdeshActivity.updeshActivity.notifyDataset();
    }

    private void hideKeyboard(){
        try {
            InputMethodManager inputManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert inputManager != null;
            inputManager.hideSoftInputFromWindow(Objects.requireNonNull(requireActivity().getCurrentFocus()).getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
