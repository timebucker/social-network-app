package com.example.kokolo.socialnetwork.views.main;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kokolo.socialnetwork.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsViewHolder extends RecyclerView.ViewHolder{

    public View mView;
    public LinearLayout likeClick;
    public LinearLayout commentClick;
    public ImageButton likePostButton;
    public ImageButton commentPostButton;
    public CircleImageView postProfileImage;
    TextView displayNoOfLikes;
    TextView displayNoOfComments;
    public TextView txtFullName;
    int countLikes;


    int countComments;
    String currentUserID;
    DatabaseReference LikesRef;
    DatabaseReference PostsRef;

    public PostsViewHolder(View itemView) {
        super(itemView);

        mView = itemView;
        likePostButton = mView.findViewById(R.id.like_button);
        commentPostButton = mView.findViewById(R.id.comment_button);
        displayNoOfLikes = mView.findViewById(R.id.display_no_of_likes);
        displayNoOfComments = mView.findViewById(R.id.display_no_of_comments);
        postProfileImage = mView.findViewById(R.id.post_profile_image);
        likeClick = mView.findViewById(R.id.click_like_layout);
        commentClick = mView.findViewById(R.id.click_comment_layout);

        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void setLikeButtonStatus(final String PostKey){
        LikesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(PostKey).hasChild(currentUserID)){
                    countLikes = (int) dataSnapshot.child(PostKey).getChildrenCount();
                    likePostButton.setImageResource(R.drawable.like);
                    if (countLikes > 0) {
                        if (countLikes > 1)
                        {
                            displayNoOfLikes.setText(Integer.toString(countLikes) + " Likes");
                        }
                        else {
                            displayNoOfLikes.setText(Integer.toString(countLikes) + " Like");
                        }
                    }
                    else{
                        displayNoOfLikes.setText("Like");
                    }
                }
                else {
                    countLikes = (int) dataSnapshot.child(PostKey).getChildrenCount();
                    likePostButton.setImageResource(R.drawable.dislike);
                    if (countLikes > 0) {
                        if (countLikes > 1)
                        {
                            displayNoOfLikes.setText(Integer.toString(countLikes) + " Likes");
                        }
                        else {
                            displayNoOfLikes.setText(Integer.toString(countLikes) + " Like");
                        }
                    }
                    else{
                        displayNoOfLikes.setText("Like");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void displaysCommentsNumber(final String PostKey) {
        PostsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(PostKey).hasChild("Comments")){
                    countComments = (int) dataSnapshot.child(PostKey).child("Comments").getChildrenCount();
                    if (countComments == 0) {
                        displayNoOfComments.setText(Integer.toString(countComments) + " Comment");
                    }
                    else if (countComments > 1)
                    {
                        displayNoOfComments.setText(Integer.toString(countComments) + " Comments");
                    }
                    else{
                        displayNoOfComments.setText(Integer.toString(countComments) + " Comment");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void setProfileimage(String profileimage){
        CircleImageView image = mView.findViewById(R.id.post_profile_image);
        Picasso.get().load(profileimage).into(image);
    }

    public void setDefaultProfileimage(){
        CircleImageView image = mView.findViewById(R.id.post_profile_image);
        image.setImageResource(R.drawable.profile);
    }

    public void setDescription(String description) {
        TextView postDescription = mView.findViewById(R.id.post_description);
        postDescription.setText(description);
    }

    public void setPostimage(String postimage){
        ImageView postImage = mView.findViewById(R.id.post_image);
        Picasso.get().load(postimage).into(postImage);
    }

    public void setDate(String date){
        TextView postDate = mView.findViewById(R.id.post_date);
        postDate.setText("   " + date);
    }

    public void setTime(String time){
        TextView postTime = mView.findViewById(R.id.post_time);
        String timeWithoutSecond = time.substring(0, 5);
        postTime.setText("   " + timeWithoutSecond);
    }

    public void setFullName(String fullName) {
        txtFullName = mView.findViewById(R.id.post_user_name);
        txtFullName.setText(fullName);
    }
}
