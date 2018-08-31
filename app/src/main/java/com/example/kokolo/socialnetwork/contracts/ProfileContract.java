package com.example.kokolo.socialnetwork.contracts;

import android.widget.Button;

import com.example.kokolo.socialnetwork.models.main.Posts;
import com.example.kokolo.socialnetwork.views.main.PostsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

public interface ProfileContract {
    interface View {
        void setViewData(String myProfileImage,
                         String userName,
                         String userProfName,
                         String userStatus,
                         String userDOB,
                         String userCountry,
                         String userGender,
                         String userRelation);
        void setSendFriendRequestButtonName(String btnName);
        void setSendFriendRequestButtonStatus(boolean status);
        void setSendFriendRequestButtonVisibility(int type);

        void setDeclineFriendRequestButtonName(String btnName);
        void setDeclineFriendRequestButtonStatus(boolean status);
        void setDeclineFriendRequestButtonVisibility(int type);

        void setPostListAdapter(FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter);

    }

    interface Presenter {
        void loadDataToView();
        void maintananceOfButtons();
        void displayAllCurrentUsersPosts();
        void UnfriendExistingFriend();
        void AcceptFriendRequest();
        void CancelFriendRequest();
        void SendFriendRequest();
        void buttonNameResolver();
    }
}
