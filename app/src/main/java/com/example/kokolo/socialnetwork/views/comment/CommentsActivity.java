package com.example.kokolo.socialnetwork.views.comment;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kokolo.socialnetwork.R;
import com.example.kokolo.socialnetwork.contracts.CommentContract;
import com.example.kokolo.socialnetwork.models.comment.Comments;
import com.example.kokolo.socialnetwork.presenters.comment.CommentPresenter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class CommentsActivity extends AppCompatActivity implements CommentContract.View, View.OnClickListener {

    RecyclerView commentsList;
    ImageButton postCommentButton;
    EditText commentInputText;

    String Post_Key;

    CommentPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Post_Key = getIntent().getExtras().get("PostKey").toString();
        presenter = new CommentPresenter(this, Post_Key);

        initViews();

        postCommentButton.setOnClickListener(this);
    }

    private void initViews() {
        commentsList = findViewById(R.id.comment_list);
        commentsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setReverseLayout(true);
        //linearLayoutManager.setStackFromEnd(true);
        commentsList.setLayoutManager(linearLayoutManager);

        commentInputText = findViewById(R.id.comment_input);
        postCommentButton = findViewById(R.id.post_comment_btn);
    }

    @Override
    protected void onStart() {
        super.onStart();

        presenter.showCommentsList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.post_comment_btn:
                presenter.postComment();
                break;
        }
    }


    @Override
    public void setCommentsListAdapter(FirebaseRecyclerAdapter<Comments, CommentsViewHolder> firebaseRecyclerAdapter) {
        this.commentsList.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public void setCommentInputText(String commentText) {
        this.commentInputText.setText(commentText);
    }

    @Override
    public String getCommentInputText() {
        return this.commentInputText.getText().toString();
    }

    @Override
    public void onPostCommentFailed(String errorMessage) {
        Toast.makeText(CommentsActivity.this, "Error " + errorMessage +", Try again...", Toast.LENGTH_SHORT).show();
    }
}
