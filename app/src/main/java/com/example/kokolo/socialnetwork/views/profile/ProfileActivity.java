package com.example.kokolo.socialnetwork.views.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.kokolo.socialnetwork.R;
import com.example.kokolo.socialnetwork.contracts.ProfileContract;
import com.example.kokolo.socialnetwork.models.main.Posts;
import com.example.kokolo.socialnetwork.presenters.profile.ProfilePresenter;
import com.example.kokolo.socialnetwork.views.main.PostsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements ProfileContract.View, View.OnClickListener {

    TextView userName, userProfName, userStatus, userCountry, userGender, userRelation, userDOB;
    CircleImageView userProfileImage;
    RecyclerView postList;
    public Button sendFriendRequestButton, declineFriendRequestButton;
    ProfilePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");

        initViews();

        presenter = new ProfilePresenter(this, userId);
        presenter.loadDataToView();
        sendFriendRequestButton.setOnClickListener(this);
        presenter.displayAllCurrentUsersPosts();
    }

    private void initViews() {
        userName = findViewById(R.id.my_username);
        userProfName = findViewById(R.id.my_profile_full_name);
        userStatus = findViewById(R.id.my_profile_status);
        userCountry = findViewById(R.id.my_country);
        userGender = findViewById(R.id.my_gender);
        userRelation = findViewById(R.id.my_relationship_status);
        userDOB = findViewById(R.id.my_dob);
        userProfileImage = findViewById(R.id.my_profile_pic);

        sendFriendRequestButton = findViewById(R.id.send_friend_request_button);
        declineFriendRequestButton = findViewById(R.id.decline_friend_request_button);

        postList = findViewById(R.id.all_current_users_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);
        postList.setFocusable(false);

        declineFriendRequestButton.setVisibility(View.INVISIBLE);
        declineFriendRequestButton.setEnabled(false);
    }

    @Override
    public void setViewData(String myProfileImage,String myUserName, String myProfileName, String myProfileStatus, String myDOB, String myCountry, String myGender, String myRelationshipStatus) {
        Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);
        userName.setText("@" + myUserName);
        userProfName.setText(myProfileName);

        if (myProfileStatus.equals("none"))
            myProfileStatus = "";
        userStatus.setText(myProfileStatus);

        userDOB.setText("Birth day: " + myDOB);
        userCountry.setText("Country: "+ myCountry);
        userGender.setText("Gender: " + myGender);
        userRelation.setText("Relationship: " + myRelationshipStatus);
    }

    @Override
    public void setSendFriendRequestButtonName(String btnName) {
        if (btnName.equals("Cancel Friend Request")){
            sendFriendRequestButton.setBackgroundResource(R.drawable.cancel_request_button);
        }
        else {
            sendFriendRequestButton.setBackgroundResource(R.drawable.button);
        }
        sendFriendRequestButton.setText(btnName);
    }

    @Override
    public void setSendFriendRequestButtonStatus(boolean status) {
        sendFriendRequestButton.setEnabled(status);
    }

    @Override
    public void setSendFriendRequestButtonVisibility(int type) {
        sendFriendRequestButton.setVisibility(type);
    }

    @Override
    public void setDeclineFriendRequestButtonName(String btnName) {
        declineFriendRequestButton.setText(btnName);
    }

    @Override
    public void setDeclineFriendRequestButtonStatus(boolean status) {
        declineFriendRequestButton.setEnabled(status);
    }

    @Override
    public void setDeclineFriendRequestButtonVisibility(int type) {
        declineFriendRequestButton.setVisibility(type);
    }

    @Override
    public void setPostListAdapter(FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter) {
        postList.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send_friend_request_button:
                presenter.buttonNameResolver();
                break;
            case R.id.decline_friend_request_button:
                presenter.CancelFriendRequest();
                break;
        }
    }
}
