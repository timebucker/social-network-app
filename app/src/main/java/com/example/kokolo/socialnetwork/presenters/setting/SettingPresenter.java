package com.example.kokolo.socialnetwork.presenters.setting;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.kokolo.socialnetwork.R;
import com.example.kokolo.socialnetwork.contracts.ProfileContract;
import com.example.kokolo.socialnetwork.contracts.SettingContract;
import com.example.kokolo.socialnetwork.presenters.setup.SetupPresenter;
import com.example.kokolo.socialnetwork.views.setting.SettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SettingPresenter implements SettingContract.Presenter{
    final SettingContract.View mView;
    final Context mContext;

    DatabaseReference settingsUserRef;
    FirebaseAuth mAuth;
    StorageReference userProfileImageRef;

    String currentUserId;

    public SettingPresenter(Context mContext){
        this.mView = (SettingContract.View) mContext;
        this.mContext = mContext;

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        settingsUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
    }

    @Override
    public void loadDataToViews() {
        settingsUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myUserName = dataSnapshot.child("username").getValue().toString();
                    String myProfileName = dataSnapshot.child("fullname").getValue().toString();
                    String myProfileStatus = dataSnapshot.child("status").getValue().toString();
                    String myDOB = dataSnapshot.child("dob").getValue().toString();
                    String myCountry = dataSnapshot.child("country").getValue().toString();
                    String myGender = dataSnapshot.child("gender").getValue().toString();
                    String myRelationshipStatus = dataSnapshot.child("relationshipstatus").getValue().toString();


                    mView.loadViewData(myProfileImage,
                            myUserName,
                            myProfileName,
                            myCountry,
                            myProfileStatus,
                            myDOB,
                            myGender,
                            myRelationshipStatus);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void validateAccountInfo(String username,
                                    String profname,
                                    String status,
                                    String dob,
                                    String country,
                                    String gender,
                                    String relation) {


        if (TextUtils.isEmpty(username)){
            mView.setViewError("username");
        }
        else if (TextUtils.isEmpty(profname)){
            mView.setViewError("profname");
        }
        else if (TextUtils.isEmpty(country)){
            mView.setViewError("country");
        }else {
            mView.showLoadingBar("Profile Image", "Please wait, while we updating your profile image...");

            UpdateAccountInfo(username, profname, status, dob, country, gender, relation);
        }
    }

    @Override
    public void storeImageToStorage(Uri resultUri) {
        StorageReference filePath = userProfileImageRef.child(currentUserId + ".jpg");

        filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(mContext, "Profile Image stored successfully to Firebase storage...", Toast.LENGTH_SHORT).show();

                    final String downloadUrl = task.getResult().getDownloadUrl().toString();

                    settingsUserRef.child("profileimage").setValue(downloadUrl)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Intent selfIntent = new Intent(mContext, SettingsActivity.class);
                                        mContext.startActivity(selfIntent);

                                        Toast.makeText(mContext, "Profile Image stored to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        String message = task.getException().getMessage();
                                        Toast.makeText(mContext, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                    }
                                    mView.dismissLoadingBar();
                                }
                            });
                }
            }
        });
    }

    private void UpdateAccountInfo(String username, String profname, String status, String dob, String country, String gender, String relation) {
        HashMap userMap = new HashMap();
        userMap.put("username", username);
        userMap.put("profname", profname);
        userMap.put("country", country);
        if (!TextUtils.isEmpty(status)){
            userMap.put("status", status);
        }
        if (!TextUtils.isEmpty(dob)){
            userMap.put("dob", dob);
        }
        if (!TextUtils.isEmpty(gender)){
            userMap.put("gender", gender);
        }
        if (!TextUtils.isEmpty(relation)){
            userMap.put("relationshipstatus", relation);
        }

        settingsUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    mView.onUpdateSuccess();
                }else{
                    mView.onUpdateFailed();
               }
                mView.dismissLoadingBar();
            }
        });
    }
}
