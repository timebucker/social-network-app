package com.example.kokolo.socialnetwork.contracts;

import com.example.kokolo.socialnetwork.models.comment.Comments;
import com.example.kokolo.socialnetwork.views.comment.CommentsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

public interface CommentContract {
    interface View {
        void setCommentsListAdapter(FirebaseRecyclerAdapter<Comments, CommentsViewHolder> firebaseRecyclerAdapter);
        void setCommentInputText(String commentText);
        String getCommentInputText();
        void onPostCommentFailed(String errorMessage);
    }

    interface Presenter {
        void showCommentsList();
        void postComment();
    }
}
