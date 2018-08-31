package com.example.kokolo.socialnetwork.presenters.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import com.example.kokolo.socialnetwork.R;
import com.example.kokolo.socialnetwork.contracts.ProfileContract;
import com.example.kokolo.socialnetwork.models.main.Posts;
import com.example.kokolo.socialnetwork.views.clickpost.ClickPostActivity;
import com.example.kokolo.socialnetwork.views.comment.CommentsActivity;
import com.example.kokolo.socialnetwork.views.main.PostsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ProfilePresenter implements ProfileContract.Presenter {
    final ProfileContract.View mView;
    final Context mContext;

    DatabaseReference profileUserRef, postsRef, likesRef, friendRequestRef, friendsRef;
    FirebaseAuth mAuth;

    static String currentUserId, userId, CURRENT_STATE;
    Boolean likeChecker = false;

    public ProfilePresenter(Context mContext, String userId) {
        this.mView = (ProfileContract.View) mContext;
        this.mContext = mContext;
        this.userId = userId;

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        if (currentUserId.equals(userId)) {
            mView.setSendFriendRequestButtonVisibility(View.INVISIBLE);
            mView.setDeclineFriendRequestButtonVisibility(View.INVISIBLE);
        }

        CURRENT_STATE = "not_friends";

        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        friendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        friendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
    }

    @Override
    public void loadDataToView() {
        profileUserRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myUserName = dataSnapshot.child("username").getValue().toString();
                    String myProfileName = dataSnapshot.child("fullname").getValue().toString();
                    String myProfileStatus = dataSnapshot.child("status").getValue().toString();
                    String myDOB = dataSnapshot.child("dob").getValue().toString();
                    String myCountry = dataSnapshot.child("country").getValue().toString();
                    String myGender = dataSnapshot.child("gender").getValue().toString();
                    String myRelationshipStatus = dataSnapshot.child("relationshipstatus").getValue().toString();

                    mView.setViewData(myProfileImage,
                            myUserName,
                            myProfileName,
                            myProfileStatus,
                            myDOB,
                            myCountry,
                            myGender,
                            myRelationshipStatus);

                    maintananceOfButtons();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void maintananceOfButtons() {
        friendRequestRef.child(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(userId)){
                            String request_type = dataSnapshot.child(userId).child("request_type")
                                    .getValue().toString();
                            if (request_type.equals("sent")) {
                                CURRENT_STATE = "request_sent";
                                mView.setSendFriendRequestButtonName("Cancel Friend Request");

                                mView.setDeclineFriendRequestButtonVisibility(View.INVISIBLE);
                                mView.setDeclineFriendRequestButtonStatus(false);
                            }else if (request_type.equals("received")){
                                CURRENT_STATE = "request_received";
                                mView.setSendFriendRequestButtonName("Accept Friend Request");

                                mView.setDeclineFriendRequestButtonVisibility(View.VISIBLE);
                                mView.setDeclineFriendRequestButtonStatus(true);
                            }
                        } else {
                            friendsRef.child(currentUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(userId)){
                                                CURRENT_STATE = "friends";
                                                mView.setSendFriendRequestButtonName("Unfriend");


                                                mView.setDeclineFriendRequestButtonVisibility(View.INVISIBLE);
                                                mView.setDeclineFriendRequestButtonStatus(false);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void displayAllCurrentUsersPosts() {
        Query ordersPostByDateQuery = postsRef.orderByChild("uid").startAt(userId).endAt(userId);
        FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Posts, PostsViewHolder>
                        (
                                Posts.class,
                                R.layout.all_post_layout,
                                PostsViewHolder.class,
                                ordersPostByDateQuery
                        )
                {
                    @Override
                    protected void populateViewHolder(PostsViewHolder viewHolder, Posts model, int position)
                    {
                        final String PostKey = getRef(position).getKey();

                        viewHolder.setFullName(model.getFullname());
                        viewHolder.setTime(model.getTime());
                        viewHolder.setDate(model.getDate());
                        viewHolder.setDescription(model.getDescription());
                        viewHolder.setPostimage(model.getPostimage());
                        viewHolder.setLikeButtonStatus(PostKey);
                        viewHolder.displaysCommentsNumber(PostKey);

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent clickPostIntent =  new Intent(mContext, ClickPostActivity.class);
                                clickPostIntent.putExtra("PostKey", PostKey);
                                mContext.startActivity(clickPostIntent);
                            }
                        });

                        if (!model.profileimage.equals("none"))
                        {
                            viewHolder.setProfileimage(model.getProfileimage());
                        }
                        else{
                            viewHolder.setDefaultProfileimage();
                        }

                        viewHolder.likePostButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                likeChecker = true;

                                likesRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (likeChecker.equals(true)){
                                            if (dataSnapshot.child(PostKey).hasChild(currentUserId)){
                                                likesRef.child(PostKey).child(currentUserId).removeValue();
                                                likeChecker = false;
                                            }
                                            else{
                                                likesRef.child(PostKey).child(currentUserId).setValue(true);
                                                likeChecker = false;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                        viewHolder.likeClick.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                likeChecker = true;

                                likesRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (likeChecker.equals(true)){
                                            if (dataSnapshot.child(PostKey).hasChild(currentUserId)){
                                                likesRef.child(PostKey).child(currentUserId).removeValue();
                                                likeChecker = false;
                                            }
                                            else{
                                                likesRef.child(PostKey).child(currentUserId).setValue(true);
                                                likeChecker = false;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });

                        viewHolder.commentPostButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent commentPostIntent =  new Intent(mContext, CommentsActivity.class);
                                commentPostIntent.putExtra("PostKey", PostKey);
                                mContext.startActivity(commentPostIntent);
                            }
                        });
                        viewHolder.commentClick.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent commentPostIntent =  new Intent(mContext, CommentsActivity.class);
                                commentPostIntent.putExtra("PostKey", PostKey);
                                mContext.startActivity(commentPostIntent);
                            }
                        });
                    }


                };

        mView.setPostListAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public void UnfriendExistingFriend() {
        friendsRef.child(currentUserId).child(userId).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            friendsRef.child(userId).child(currentUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                mView.setSendFriendRequestButtonStatus(true);
                                                CURRENT_STATE = "not_friends";
                                                mView.setSendFriendRequestButtonName("Send Friend Request");

                                                mView.setDeclineFriendRequestButtonVisibility(View.INVISIBLE);
                                                mView.setDeclineFriendRequestButtonStatus(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    @Override
    public void AcceptFriendRequest() {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy", Locale.US);
        final String saveCurrentDateType = currentDate.format(calFordDate.getTime());

        friendsRef.child(currentUserId).child(userId).child("date").setValue(saveCurrentDateType)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            friendsRef.child(userId).child(currentUserId).child("date").setValue(saveCurrentDateType)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                friendRequestRef.child(currentUserId).child(userId).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    friendRequestRef.child(userId).child(currentUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        mView.setSendFriendRequestButtonStatus(true);
                                                                                        CURRENT_STATE = "friends";
                                                                                        mView.setSendFriendRequestButtonName("Unfriend");

                                                                                        mView.setDeclineFriendRequestButtonVisibility(View.INVISIBLE);
                                                                                        mView.setDeclineFriendRequestButtonStatus(false);
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    @Override
    public void CancelFriendRequest() {
        friendRequestRef.child(currentUserId).child(userId).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            friendRequestRef.child(userId).child(currentUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                mView.setSendFriendRequestButtonStatus(true);
                                                CURRENT_STATE = "not_friends";
                                                mView.setSendFriendRequestButtonName("Send Friend Request");

                                                mView.setDeclineFriendRequestButtonVisibility(View.INVISIBLE);
                                                mView.setDeclineFriendRequestButtonStatus(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    @Override
    public void SendFriendRequest() {
        friendRequestRef.child(currentUserId).child(userId).child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            friendRequestRef.child(userId).child(currentUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                mView.setSendFriendRequestButtonStatus(true);
                                                CURRENT_STATE = "request_sent";
                                                mView.setSendFriendRequestButtonName("Cancel Friend Request");

                                                mView.setDeclineFriendRequestButtonVisibility(View.INVISIBLE);
                                                mView.setDeclineFriendRequestButtonStatus(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    @Override
    public void buttonNameResolver() {
        if (!currentUserId.equals(userId)) {
            mView.setSendFriendRequestButtonStatus(false);

            if (CURRENT_STATE.equals("not_friends")) {
                mView.setSendFriendRequestButtonName("Send Friend Request");
                SendFriendRequest();
            } else if (CURRENT_STATE.equals("request_sent")) {
                mView.setSendFriendRequestButtonName("Cancel Friend Request");
                CancelFriendRequest();
            } else if (CURRENT_STATE.equals("request_received")) {
                AcceptFriendRequest();
            } else if (CURRENT_STATE.equals("friends")) {
                UnfriendExistingFriend();
            }
        }
    }
}
