package com.example.kokolo.socialnetwork.contracts;

import com.example.kokolo.socialnetwork.models.findfriend.FindFriends;
import com.example.kokolo.socialnetwork.views.findfriend.FindFriendsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

public interface FindFriendContract {
    interface View {
        void setSearchResultAdapter( FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder> firebaseRecyclerAdapter);
    }

    interface Presenter {
        void searchFriends(String searchBoxInput);
    }
}
