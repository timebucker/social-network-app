package com.example.kokolo.socialnetwork.presenters.comment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.kokolo.socialnetwork.R;
import com.example.kokolo.socialnetwork.contracts.CommentContract;
import com.example.kokolo.socialnetwork.models.comment.Comments;
import com.example.kokolo.socialnetwork.views.comment.CommentsActivity;
import com.example.kokolo.socialnetwork.views.comment.CommentsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class CommentPresenter implements CommentContract.Presenter {
    final CommentContract.View mView;
    final Context mContext;
    DatabaseReference UsersRef;
    DatabaseReference CommentsRef;
    FirebaseAuth mAuth;
    String current_user_id;
    String Post_Key;

    public CommentPresenter(Context mContext, String Post_Key) {
        this.mView = (CommentContract.View) mContext;
        this.mContext = mContext;

        mAuth = FirebaseAuth.getInstance();
        this.Post_Key = Post_Key;
        current_user_id = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        CommentsRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(Post_Key).child("Comments");
    }

    @Override
    public void showCommentsList() {
        Query ordersCommentByTime = CommentsRef.orderByChild("timeabs");
        FirebaseRecyclerAdapter<Comments, CommentsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(
                Comments.class,
                R.layout.all_comments_layout,
                CommentsViewHolder.class,
                ordersCommentByTime
        ) {
            @Override
            protected void populateViewHolder(CommentsViewHolder viewHolder, Comments model, int position) {
                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setComment(model.getComment());
            }
        };

        mView.setCommentsListAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public void postComment() {
        UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String userName = dataSnapshot.child("username").getValue().toString();

                    validateComment(userName);

                    mView.setCommentInputText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void validateComment(String userName) {
        String commentText = mView.getCommentInputText();
        if (TextUtils.isEmpty(commentText)){
            Toast.makeText(mContext, "Please insert comment", Toast.LENGTH_SHORT).show();
        }
        else{
            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy", Locale.US);
            currentDate.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
            final String saveCurrentDate = currentDate.format(calFordDate.getTime());

            SimpleDateFormat currentDate2 = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            currentDate2.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
            final String saveCurrentDate2 = currentDate2.format(calFordDate.getTime());


            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
            currentTime.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
            final String saveCurrentTime = currentTime.format(calFordTime.getTime());

            final String randomKey = current_user_id + saveCurrentDate + saveCurrentTime;

            HashMap commentsMap = new HashMap();
            commentsMap.put("uid", current_user_id);
            commentsMap.put("comment", commentText);
            commentsMap.put("date", saveCurrentDate2);
            commentsMap.put("time", saveCurrentTime);
            commentsMap.put("username", userName);
            commentsMap.put("timeabs", ServerValue.TIMESTAMP);

            CommentsRef.child(randomKey).updateChildren(commentsMap)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){

                            }
                            else{
                                mView.onPostCommentFailed(task.getException().getMessage());
                          }
                        }
                    });
        }
    }
}
