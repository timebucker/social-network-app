package com.example.kokolo.socialnetwork.presenters.findfriend;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.example.kokolo.socialnetwork.R;
import com.example.kokolo.socialnetwork.contracts.FindFriendContract;
import com.example.kokolo.socialnetwork.contracts.MainContract;
import com.example.kokolo.socialnetwork.models.findfriend.FindFriends;
import com.example.kokolo.socialnetwork.views.findfriend.FindFriendsViewHolder;
import com.example.kokolo.socialnetwork.views.profile.ProfileActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class FindFriendPresenter implements FindFriendContract.Presenter{
    final FindFriendContract.View mView;
    //final FirebaseAuth mFirebaseAuth;
    final Context mContext;
    DatabaseReference allUsersDatabaseRef;

    public FindFriendPresenter(Context mContext) {
        this.mView = (FindFriendContract.View) mContext;
        //this.mFirebaseAuth = FirebaseAuth.getInstance();
        allUsersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        this.mContext = mContext;
    }

    public void searchFriends(String searchBoxInput) {
        Query searchFriendsQuery = allUsersDatabaseRef.orderByChild("fullname")
                .startAt(searchBoxInput).endAt(searchBoxInput + "\uf8ff");
        FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder>
                        (
                                FindFriends.class,
                                R.layout.all_users_display_display,
                                FindFriendsViewHolder.class,
                                searchFriendsQuery
                        ) {
                    @Override
                    protected void populateViewHolder(FindFriendsViewHolder viewHolder, FindFriends model, final int position) {
                        viewHolder.setFullname(model.getFullname());

                        if (!model.profileimage.equals("none"))
                        {
                            viewHolder.setProfileimage(model.getProfileimage());
                        }
                        else{
                            viewHolder.setDefaultProfileimage();
                        }

                        String modelStatus = model.getStatus();
                        if (modelStatus.equals("none"))
                            modelStatus = "";
                        viewHolder.setStatus(modelStatus);

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String userId = getRef(position).getKey();
                                Intent intent = new Intent(mContext, ProfileActivity.class);
                                intent.putExtra("userId", userId);
                                mContext.startActivity(intent);
                            }
                        });
                    }
                };

        mView.setSearchResultAdapter(firebaseRecyclerAdapter);
    }
}
