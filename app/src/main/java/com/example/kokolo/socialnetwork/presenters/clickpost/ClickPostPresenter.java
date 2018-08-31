package com.example.kokolo.socialnetwork.presenters.clickpost;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kokolo.socialnetwork.R;
import com.example.kokolo.socialnetwork.contracts.ClickPostContract;
import com.example.kokolo.socialnetwork.views.clickpost.ClickPostActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickPostPresenter implements ClickPostContract.Presenter {
    final ClickPostContract.View mView;
    final Context mContext;

    DatabaseReference ClickPostRef, CommentsRef, UsersRef, LikeRef;
    FirebaseAuth mAuth;

    String PostKey, currentUserID, description;
    Boolean likeChecker = false;

    public ClickPostPresenter(Context mContext, String Post_Key) {
        this.mView = (ClickPostContract.View) mContext;
        this.mContext = mContext;
        this.PostKey = Post_Key;
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        ClickPostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        LikeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        CommentsRef = ClickPostRef.child("Comments");

    }
}
