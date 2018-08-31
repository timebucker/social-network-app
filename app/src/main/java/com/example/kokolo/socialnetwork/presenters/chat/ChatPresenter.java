package com.example.kokolo.socialnetwork.presenters.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.kokolo.socialnetwork.R;
import com.example.kokolo.socialnetwork.contracts.ChatContract;
import com.example.kokolo.socialnetwork.models.chat.Messages;
import com.example.kokolo.socialnetwork.views.chat.ChatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatPresenter implements ChatContract.Presenter {
    ChatContract.View mView;
    Context mContext;

    DatabaseReference rootRef;
    FirebaseAuth mAuth;
    String receiverId, receiverName, senderId, saveCurrentDate, saveCurrentTime;

    public ChatPresenter(Context Context, String receiverId, String receiverName){
        this.mContext = Context;
        this.mView = (ChatContract.View) Context;

        mAuth = FirebaseAuth.getInstance();
        senderId = mAuth.getCurrentUser().getUid();

        rootRef = FirebaseDatabase.getInstance().getReference();

        this.receiverId = receiverId;
        this.receiverName = receiverName;
    }

    @Override
    public void DisplayReceiverInfo() {

        mView.setTextForReceiverNameTextView(receiverName);

        rootRef.child("Users").child(receiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    final String profileImage = dataSnapshot.child("profileimage").getValue().toString();

                    mView.loadFriendProfileImage(profileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void SendMessages() {
        String messageText = mView.getTextFormInputMessage();

        if (TextUtils.isEmpty(messageText)){
            Toast.makeText(mContext, "Please write message", Toast.LENGTH_SHORT).show();
        } else{
            String message_sender_ref = "Messages/" + senderId + "/" + receiverId;
            String message_receiver_ref = "Messages/" + receiverId + "/" + senderId;

            DatabaseReference user_message_key = rootRef.child("Messages").child(senderId)
                    .child(receiverId).push();

            String message_push_id = user_message_key.getKey();

            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
            saveCurrentDate = currentDate.format(calFordDate.getTime());

            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
            saveCurrentTime = currentTime.format(calFordTime.getTime());

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", senderId);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(message_sender_ref + "/" + message_push_id, messageTextBody);
            messageBodyDetails.put(message_receiver_ref + "/" + message_push_id, messageTextBody);

            rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        //Toast.makeText(mContext, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                        mView.setTextForInputMessage("");
                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(mContext, "Error: " + message, Toast.LENGTH_SHORT).show();
                        mView.setTextForInputMessage("");
                    }

                }
            });
        }
    }

    @Override
    public void FetchMessages() {
        rootRef.child("Messages").child(senderId).child(receiverId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.exists()){
                            Messages messages = dataSnapshot.getValue(Messages.class);
                            mView.addMessage(messages);
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
