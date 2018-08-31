package com.example.kokolo.socialnetwork.contracts;

import com.example.kokolo.socialnetwork.models.main.Posts;
import com.example.kokolo.socialnetwork.views.main.PostsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

public interface MainContract {
    interface View {
        void setPostListAdapter(FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter);
        void setNavProfileUserName(String fullname);
        void setNavProfileImage(String myProfileImage);
        void sendUserToLoginActivity();
        void sendUserToSetupActivity();
        void sendUserToProfileActivity(String userId);
    }

    interface Presenter {
        void displayAllUsersPosts();
        void validateUser();
        void signOut();

        void sendUserToOwnProfile();
    }

}
