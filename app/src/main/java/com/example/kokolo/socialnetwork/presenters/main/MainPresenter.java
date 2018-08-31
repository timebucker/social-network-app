package com.example.kokolo.socialnetwork.presenters.main;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.example.kokolo.socialnetwork.R;
import com.example.kokolo.socialnetwork.contracts.FindFriendContract;
import com.example.kokolo.socialnetwork.contracts.MainContract;
import com.example.kokolo.socialnetwork.models.main.Posts;
import com.example.kokolo.socialnetwork.views.clickpost.ClickPostActivity;
import com.example.kokolo.socialnetwork.views.comment.CommentsActivity;
import com.example.kokolo.socialnetwork.views.main.MainActivity;
import com.example.kokolo.socialnetwork.views.main.PostsViewHolder;
import com.example.kokolo.socialnetwork.views.profile.ProfileActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainPresenter implements MainContract.Presenter{
    final MainContract.View mView;
    final FirebaseAuth mFirebaseAuth;
    final Context mContext;
    DatabaseReference usersRef, postsRef, likesRef, commentsRef;
    String currentUserId;
    Boolean likeChecker = false;


    public MainPresenter(Context mContext) {
        this.mView = (MainContract.View) mContext;
        this.mFirebaseAuth = FirebaseAuth.getInstance();
        this.mContext = mContext;

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        commentsRef = FirebaseDatabase.getInstance().getReference().child("Posts").child("Comments");
    }


    public void displayAllUsersPosts() {

        Query ordersPostByDateQuery = postsRef.orderByChild("timeabs");
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

                        //Send to Owner of post
                        viewHolder.postProfileImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                postsRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            String userId = dataSnapshot.child(PostKey).child("uid").getValue().toString();
                                            mView.sendUserToProfileActivity(userId);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });

                        viewHolder.txtFullName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                postsRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            String userId = dataSnapshot.child(PostKey).child("uid").getValue().toString();
                                            mView.sendUserToProfileActivity(userId);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                    }


                };

        mView.setPostListAdapter(firebaseRecyclerAdapter);
    }

    public void loadUserInfoToNavView(FirebaseUser currentUser) {
        currentUserId = currentUser.getUid();
        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String fullName = dataSnapshot.child("fullname").getValue().toString();
                    mView.setNavProfileUserName(fullName);
                    String asd = dataSnapshot.child("country").getValue().toString();
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    if (!myProfileImage.equals("none")) {
                        mView.setNavProfileImage(myProfileImage);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void validateUser() {
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if(currentUser == null){
            mView.sendUserToLoginActivity();
        }
        else{
            checkUserExistence();
            loadUserInfoToNavView(currentUser);
        }
    }

    @Override
    public void signOut() {
        mFirebaseAuth.signOut();
    }

    @Override
    public void sendUserToOwnProfile() {
        Intent profileIntent = new Intent(mContext, ProfileActivity.class);
        profileIntent.putExtra("userId", currentUserId);
        mContext.startActivity(profileIntent);
    }

    private void checkUserExistence() {
        final String current_user_id = mFirebaseAuth.getCurrentUser().getUid();
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(current_user_id)){
                    mView.sendUserToSetupActivity();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
