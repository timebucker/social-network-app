package com.example.kokolo.socialnetwork.presenters.friendslist;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.example.kokolo.socialnetwork.R;
import com.example.kokolo.socialnetwork.contracts.FriendsListContract;
import com.example.kokolo.socialnetwork.models.friendslist.Friends;
import com.example.kokolo.socialnetwork.views.friendslist.FriendViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FriendsListPresenter  implements FriendsListContract.Presenter{
    FriendsListContract.View mView;
    Context mContext;
    DatabaseReference friendsRef, usersRef;
    FirebaseAuth mAuth;
    String onlineUserId;

    public FriendsListPresenter(Context mContext){
        this.mView = (FriendsListContract.View) mContext;
        this.mContext = mContext;

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        friendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(onlineUserId);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    @Override
    public void displayAllFriends() {
        FirebaseRecyclerAdapter<Friends, FriendViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Friends, FriendViewHolder>
                (
                        Friends.class,
                        R.layout.all_users_display_display,
                        FriendViewHolder.class,
                        friendsRef

                ){
            @Override
            protected void populateViewHolder(final FriendViewHolder viewHolder, Friends model, int position) {
                viewHolder.setDate(model.getDate());

                final String usersId = getRef(position).getKey();

                usersRef.child(usersId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            final String userName = dataSnapshot.child("fullname").getValue().toString();
                            final String profileImage = dataSnapshot.child("profileimage").getValue().toString();

                            viewHolder.setFullname(userName);
                            viewHolder.setProfileimage(profileImage);

                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CharSequence options[] = new CharSequence[]
                                            {
                                                    userName + "'s Profile",
                                                    "Send Message"
                                            };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                    builder.setTitle("Select Options");

                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == 0){
                                                mView.SendUserToProfileActivity(usersId);
                                            }
                                            if (which == 1){
                                                mView.SendUserToChatActivity(usersId, userName);
                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        mView.setFriendListAdapter(firebaseRecyclerAdapter);
    }
}
