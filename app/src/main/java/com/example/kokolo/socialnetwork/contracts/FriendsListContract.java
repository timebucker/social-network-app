package com.example.kokolo.socialnetwork.contracts;

import com.example.kokolo.socialnetwork.models.friendslist.Friends;
import com.example.kokolo.socialnetwork.views.friendslist.FriendViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

public interface FriendsListContract {
    interface View {
        void setFriendListAdapter(FirebaseRecyclerAdapter<Friends, FriendViewHolder> firebaseRecyclerAdapter);
        void SendUserToProfileActivity(String userId);
        void SendUserToChatActivity(String userId, String userName);
    }

    interface Presenter {

        void displayAllFriends();
    }
}
