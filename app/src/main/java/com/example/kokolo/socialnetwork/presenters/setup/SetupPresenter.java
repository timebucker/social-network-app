package com.example.kokolo.socialnetwork.presenters.setup;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.kokolo.socialnetwork.contracts.SetupContract;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class SetupPresenter implements SetupContract.Presenter{
    final SetupContract.View mView;
    final FirebaseAuth mFirebaseAuth;
    final Context mContext;

    DatabaseReference userRef;
    StorageReference userProfileImageRef;

    boolean isSetupSuccess = false;

    String currentUserId;

    public SetupPresenter(Context mContext) {
        this.mView = (SetupContract.View) mContext;
        this.mFirebaseAuth = FirebaseAuth.getInstance();
        this.mContext = mContext;

        currentUserId = mFirebaseAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
    }


    @Override
    public void setAccountSetupInformation(String name, String fullName, String country) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(mContext, "Please insert username", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(fullName)) {
            Toast.makeText(mContext, "Please insert full name", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(country)) {
            Toast.makeText(mContext, "Please insert country", Toast.LENGTH_SHORT).show();
        }
        else{
            mView.showLoadingBar("Saving information","Please wait..." );

            HashMap userMap = new HashMap();

            userMap.put("username", name);
            userMap.put("fullname", fullName);
            userMap.put("country", country);
            userMap.put("status", "none");
            userMap.put("gender", "none");
            userMap.put("dob", "none");
            userMap.put("relationshipstatus", "none");
            userMap.put("profileimage", "none");


            userRef.updateChildren(userMap)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful())
                            {
                                mView.onSetupSuccess();
                            }
                            else{
                                mView.onSetupFailed("");
                            }

                        }
                    });

            if (mView.isSetImageProfile())
                addProfileImage(mView.getImageCropResult());

            mView.dismissLoadingBar();
            if (isSetupSuccess){
                mView.onSetupSuccess();
            }
            else {
            }
        }
    }

    public void addProfileImage(CropImage.ActivityResult imageCropResult) {
        final Uri resultUri = imageCropResult.getUri();
        StorageReference filePath = userProfileImageRef.child(currentUserId+".jpg");
        filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful())
                {
                    final String downloadUrl = task.getResult().getDownloadUrl().toString();
                    userRef.child("profileimage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {

                            }
                            else{
                            }
                        }
                    });
                }
                else{
                }
            }
        });
    }
}
