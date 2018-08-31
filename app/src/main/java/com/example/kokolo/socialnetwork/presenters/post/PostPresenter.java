package com.example.kokolo.socialnetwork.presenters.post;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kokolo.socialnetwork.contracts.PostContract;
import com.example.kokolo.socialnetwork.views.post.PostActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class PostPresenter implements PostContract.Presenter {
    final PostContract.View mView;
    final Context mContext;

    StorageReference PostImageRef;
    DatabaseReference UsersRef, PostRef;
    FirebaseAuth mAuth;

    String saveCurrentTime, saveCurrentDate, postRandomName, downloadURL, current_user_id;
    String description;

    String errorMessage;

    public PostPresenter(Context mContext) {
        this.mView = (PostContract.View) mContext;
        this.mContext = mContext;

        mAuth = FirebaseAuth.getInstance();
        PostImageRef = FirebaseStorage.getInstance().getReference();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        current_user_id = mAuth.getCurrentUser().getUid();
    }


    @Override
    public void validatePostInfo(EditText PostDescription, Bitmap BitMapPostImage) {
        description = PostDescription.getText().toString();

        if (BitMapPostImage == null){
            Toast.makeText(mContext, "Please select post image", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(description)){
            Toast.makeText(mContext, "Please say something about your image", Toast.LENGTH_SHORT).show();
        }
        else{

            mView.showLoadingBar("Updating your post", "Please wait...");

            storeImageToFirebaseStorage(BitMapPostImage);
        }
    }

    @Override
    public Bitmap resizeBitMap(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }


    private void storeImageToFirebaseStorage(Bitmap bitmap) {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate1 = new SimpleDateFormat("dd-MMMM-yyyy", Locale.US);
        currentDate1.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        String saveCurrentDateType1 = currentDate1.format(calFordDate.getTime());

        SimpleDateFormat currentDate2 = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        currentDate2.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        saveCurrentDate = currentDate2.format(calFordDate.getTime());


        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        currentTime.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        saveCurrentTime = currentTime.format(calFordTime.getTime());

        postRandomName = current_user_id + saveCurrentDateType1 + saveCurrentTime;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference filePath = PostImageRef.child("Post Images").child(postRandomName + ".jpg");
        filePath.putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful())
                {
                    downloadURL = task.getResult().getDownloadUrl().toString();
                    savePostInformationToDatabase();
                    mView.onPostSuccess();
                }
                else{
                    errorMessage = errorMessage + task.getException().getMessage();
                    mView.onPostFailed(errorMessage);
                }

                mView.dismissLoadingBar();
            }
        });
    }

    private void savePostInformationToDatabase() {

        UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String userFullName = dataSnapshot.child("fullname").getValue().toString();
                    String userProfileImage = dataSnapshot.child("profileimage").getValue().toString();

                    HashMap postsMap = new HashMap();
                    postsMap.put("uid", current_user_id);
                    postsMap.put("date", saveCurrentDate);
                    postsMap.put("time", saveCurrentTime);
                    postsMap.put("description", description);
                    postsMap.put("postimage", downloadURL);
                    postsMap.put("profileimage", userProfileImage);
                    postsMap.put("fullname", userFullName);
                    postsMap.put("timeabs", ServerValue.TIMESTAMP);

                    PostRef.child(current_user_id + postRandomName).updateChildren(postsMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) { }
                                    else{
                                        errorMessage = errorMessage + task.getException().getMessage();
                                     }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
