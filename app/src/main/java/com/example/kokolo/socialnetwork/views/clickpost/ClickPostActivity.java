package com.example.kokolo.socialnetwork.views.clickpost;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kokolo.socialnetwork.R;
import com.example.kokolo.socialnetwork.contracts.ClickPostContract;
import com.example.kokolo.socialnetwork.models.comment.Comments;
import com.example.kokolo.socialnetwork.views.main.MainActivity;
import com.example.kokolo.socialnetwork.views.profile.ProfileActivity;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class ClickPostActivity extends AppCompatActivity {

    // User
    CircleImageView userProfileImage;
    TextView UserName;
    TextView Date;
    TextView Time;

    // Post
    ImageView PostImage;
    TextView PostDescription;
    ImageButton clickLikeButton, clickCommentButton;
    int countLikes;
    TextView displayNoOfLikes;
    ImageButton DeletePostButton, EditPostButton;

    // Comment
    RecyclerView commentsList;
    TextView inputTextView;
    ImageButton postCommentButton;

    DatabaseReference ClickPostRef, CommentsRef, UsersRef, LikeRef;
    FirebaseAuth mAuth;

    String PostKey, currentUserID, description;
    Boolean likeChecker = false;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        PostKey = getIntent().getStringExtra("PostKey");
        ClickPostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        LikeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        CommentsRef = ClickPostRef.child("Comments");

        initViews();

        loadData();
    }

    private void initViews() {
        PostImage = findViewById(R.id.click_post_image);
        PostDescription = findViewById(R.id.click_post_description);
        clickLikeButton = findViewById(R.id.click_post_like_button);
        clickCommentButton = findViewById(R.id.click_post_comment_button);
        displayNoOfLikes = findViewById(R.id.click_post_display_no_of_likes);
        DeletePostButton = findViewById(R.id.delete_post_button);
        EditPostButton = findViewById(R.id.edit_post_button);

        userProfileImage = findViewById(R.id.click_post_profile_image);
        UserName = findViewById(R.id.click_post_user_name);
        Date = findViewById(R.id.click_post_date);
        Time = findViewById(R.id.click_post_time);

        // Comment
        commentsList = findViewById(R.id.click_post_comment_list);
        commentsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        commentsList.setLayoutManager(linearLayoutManager);
        commentsList.setFocusable(false);
        inputTextView = findViewById(R.id.click_post_comment_input);
        postCommentButton = findViewById(R.id.click_post_send_comment_button);

        DeletePostButton.setVisibility(View.INVISIBLE);
        EditPostButton.setVisibility(View.INVISIBLE);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    private void EditCurrentPost(String description) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("Edit Post: ");

        final EditText inputField = new EditText(ClickPostActivity.this);
        inputField.setText(description);
        builder.setView(inputField);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ClickPostRef.child("description").setValue(inputField.getText().toString());
                Toast.makeText(ClickPostActivity.this, "Post updated successfully", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void DeleteCurrentPost() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("Do you want to delete post?");

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ClickPostRef.removeValue();
                SendUserToMainActivity();
                Toast.makeText(ClickPostActivity.this, "Post has been deleted", Toast.LENGTH_SHORT).show();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void loadData() {
        ClickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    description = dataSnapshot.child("description").getValue().toString();
                    String image = dataSnapshot.child("postimage").getValue().toString();
                    String databaseUserID = dataSnapshot.child("uid").getValue().toString();
                    String profileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String userName = dataSnapshot.child("fullname").getValue().toString();
                    String date = dataSnapshot.child("date").getValue().toString();
                    String time = dataSnapshot.child("time").getValue().toString();

                    PostDescription.setText(description);
                    Picasso.get().load(image).into(PostImage);
                    Picasso.get().load(profileImage).placeholder(R.drawable.profile).into(userProfileImage);
                    UserName.setText(userName);
                    Date.setText(date);
                    Time.setText(time);

                    LikeRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot1) {
                            countLikes = (int) dataSnapshot1.child(PostKey).getChildrenCount();
                            String s = "";
                            if (countLikes > 1){
                                s = "s";
                            }
                            if (dataSnapshot1.child(PostKey).hasChild(currentUserID)){
                                clickLikeButton.setImageResource(R.drawable.like);
                                displayNoOfLikes.setText(Integer.toString(countLikes) + " Like" + s);
                            }
                            else {
                                clickLikeButton.setImageResource(R.drawable.dislike);
                                displayNoOfLikes.setText(Integer.toString(countLikes) + " Like" + s);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    clickLikeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            likeChecker = true;

                            LikeRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (likeChecker.equals(true)){
                                        if (dataSnapshot.child(PostKey).hasChild(currentUserID)){
                                            LikeRef.child(PostKey).child(currentUserID).removeValue();
                                            likeChecker = false;
                                        }
                                        else{
                                            LikeRef.child(PostKey).child(currentUserID).setValue(true);
                                            likeChecker = false;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });

                    clickCommentButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(ClickPostActivity.this,"Write comments below!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    if (currentUserID.equals(databaseUserID)){
                        DeletePostButton.setVisibility(View.VISIBLE);
                        EditPostButton.setVisibility(View.VISIBLE);
                    }

                    EditPostButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditCurrentPost(description);
                        }
                    });

                    userProfileImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClickPostRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        String userId = dataSnapshot.child("uid").getValue().toString();
                                        SendUserToProfileActivity(userId);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });

                    UserName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClickPostRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        String userId = dataSnapshot.child("uid").getValue().toString();
                                        SendUserToProfileActivity(userId);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String userName = dataSnapshot.child("username").getValue().toString();

                            validateComment(userName);

                            inputTextView.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        DeletePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteCurrentPost();
            }
        });
    }


    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(ClickPostActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        showCommentsList();
    }

    private void showCommentsList() {
        Query ordersCommentByTime = CommentsRef.orderByChild("timeabs");
        FirebaseRecyclerAdapter<Comments, ClickPostCommentsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Comments, ClickPostCommentsViewHolder>(
                Comments.class,
                R.layout.all_comments_layout,
                ClickPostCommentsViewHolder.class,
                ordersCommentByTime
        ) {
            @Override
            protected void populateViewHolder(ClickPostCommentsViewHolder viewHolder, Comments model, int position) {
                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setComment(model.getComment());
            }
        };

        commentsList.setAdapter(firebaseRecyclerAdapter);
    }

    // Quang code
    private void SendUserToProfileActivity(String userId) {
        Intent profileIntent = new Intent(ClickPostActivity.this, ProfileActivity.class);
        profileIntent.putExtra("userId", userId);
        startActivity(profileIntent);
    }

    public static class ClickPostCommentsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public ClickPostCommentsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUsername(String username) {
            TextView myUserName = mView.findViewById(R.id.comment_username);
            myUserName.setText("@" + username + "  ");
        }

        public void setTime(String time) {
            TextView myTime = mView.findViewById(R.id.comment_time);
            myTime.setText(time);
        }

        public void setDate(String date) {
            TextView myDate = mView.findViewById(R.id.comment_date);
            myDate.setText(" " + date + " ");
        }

        public void setComment(String comment) {
            TextView myComment = mView.findViewById(R.id.comment_content);
            myComment.setText(comment);
        }
    }


    private void validateComment(String userName) {
        String commentText = inputTextView.getText().toString();
        if (TextUtils.isEmpty(commentText)){
            Toast.makeText(this, "Please insert comment", Toast.LENGTH_SHORT).show();
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

            final String randomKey = currentUserID + saveCurrentDate + saveCurrentTime;

            HashMap commentsMap = new HashMap();
            commentsMap.put("uid", currentUserID);
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
                                Toast.makeText(ClickPostActivity.this, "Error occured, Try again...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            showCommentsList();
        }
    }
}