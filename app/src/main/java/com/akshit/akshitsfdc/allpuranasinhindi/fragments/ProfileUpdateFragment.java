package com.akshit.akshitsfdc.allpuranasinhindi.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.FileUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileUpdateFragment extends Fragment {

    private ImageView profileImageView;
    private EditText nameEditText;
    private Button updateButton;
    private TextView skipTxt;
    private ProgressBar progress;

    private boolean profilePictureSelected=false;
    private Uri profilePicUri = null;
    private FirebaseUser currentUser;
    private FileUtils fileUtils;
    private PostFragment postFragment;
    private ProgressBar profileImageProgress;

    public ProfileUpdateFragment(PostFragment postFragment) {
        this.postFragment = postFragment;
    }
    public ProfileUpdateFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_update, container, false);
        fileUtils = new FileUtils(getContext());
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        profileImageView = view.findViewById(R.id.profileImageView);
        nameEditText = view.findViewById(R.id.nameEditText);
        profileImageProgress = view.findViewById(R.id.profileImageProgress);
        updateButton = view.findViewById(R.id.updateButton);
        skipTxt = view.findViewById(R.id.skipTxt);

        progress = view.findViewById(R.id.progress);

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageSelecter("Profile Picture");
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser != null) {
                    updateProfile();
                }
            }
        });
        skipTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });

        if(currentUser != null){
            preloadDetails();
        }

        return view;
    }

    private void preloadDetails(){
        try {
            if(currentUser.getPhotoUrl() != null) {
                if(!TextUtils.isEmpty(currentUser.getPhotoUrl().toString())){
                    Glide.with(requireContext()).load(currentUser.getPhotoUrl()).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            profileImageProgress.setVisibility(View.GONE);
                            return false;
                        }
                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            profileImageProgress.setVisibility(View.GONE);
                            return false;
                        }
                    }).error(R.drawable.profile_placeholder).fallback(R.drawable.profile_placeholder).into( profileImageView);
                }else {
                    profileImageProgress.setVisibility(View.GONE);
                }

            }else {
                profileImageProgress.setVisibility(View.GONE);
            }
            if(!TextUtils.isEmpty(currentUser.getDisplayName()) && currentUser.getDisplayName() != null){
                nameEditText.setText(currentUser.getDisplayName());
            }
        }catch (Exception e){

        }


    }
    private void updateProfile(){

        if(!validateForm()) {
            return;
        }
        //Firebase
        FirebaseStorage storage;
        StorageReference storageReference;
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        String picName = "profile_pic_"+currentUser.getUid();

        StorageReference ref = storageReference.child("profile_pictures/"+picName);

        showPB();
        ref.putBytes(compressImageSmall(profilePicUri)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(nameEditText.getText().toString().trim())
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            fileUtils.showShortToast("Profile updated");
                                           // navigateToMainActivity();
                                            closeFragment();
                                            hidePB();
                                        }else{
                                            fileUtils.showShortToast("Something went wrong!");
                                        }
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        fileUtils.showShortToast("Profile picture could not be uploaded please try again.");
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                fileUtils.showShortToast("Profile picture could not be uploaded please try again.");
            }
        });

    }

    private void openImageSelecter(String title){
        // start picker to get image for cropping and then use the image in cropping activity

        try{
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setActivityTitle(title)
                    .setActivityMenuIconColor(Color.parseColor("#ffffff"))
                    .setGuidelinesColor(Color.parseColor("#2B90FF"))
                    .setMinCropResultSize(1000,1000)
                    .setAspectRatio(4,4)
                    .setFixAspectRatio(true)
                    .start(getActivity());
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private boolean validateForm(){
        boolean valid = true;

        String name = nameEditText.getText().toString().trim();

        if(TextUtils.isEmpty(name)){
            valid = false;
            nameEditText.setError("Required.");
        }

        if (!profilePictureSelected){
            valid = false;
            fileUtils.showLongToast("Please select profile picture.");
        }
        return valid;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    profilePictureSelected = true;
                    profilePicUri = result.getUri();
                    profileImageView.setImageURI(profilePicUri);
                }else{
                    if(profilePicUri == null){
                        profilePictureSelected = false;
                    }
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                //Exception error = result.getError();
                fileUtils.showShortToast("Something went wrong!");
            }
        }catch (Exception e){
            e.printStackTrace();
            fileUtils.showShortToast("Something went wrong. Try again");
        }

    }

    private void closeFragment(){

        try {
            Fragment fr = requireActivity().getSupportFragmentManager().findFragmentByTag("profile_fragment");
            if(fr != null){
                postFragment.setPublisherView();
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
    private void showPB(){
        if( getActivity() != null){
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        progress.setVisibility(View.VISIBLE);

    }
    private void hidePB(){
        if( getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        progress.setVisibility(View.GONE);

    }

    private byte[] compressImageSmall(Uri imgUri){


        try {
            Bitmap bitmap;
            File file = new File(imgUri.getPath());
            bitmap = new Compressor(getContext())
                    .setMaxHeight(500) //Set height and width
                    .setMaxWidth(500)
                    .setQuality(1)// Set Quality
                    .compressToBitmap(file);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
            final byte[] bytes = baos.toByteArray();

            return bytes;

        } catch (IOException e) {
            e.printStackTrace();
            fileUtils.showLongToast("Something went wrong!");
            return new byte[]{};
        }


    }
}
