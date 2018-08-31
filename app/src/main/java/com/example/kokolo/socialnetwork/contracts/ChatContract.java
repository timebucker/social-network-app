package com.example.kokolo.socialnetwork.contracts;

import com.example.kokolo.socialnetwork.models.chat.Messages;

public interface ChatContract {
    interface View {
        void setTextForReceiverNameTextView(String text);
        void setTextForInputMessage(String text);
        String getTextFormInputMessage();
        void loadFriendProfileImage(String profileImage);
        void addMessage(Messages messages);
    }

    interface Presenter {
        void DisplayReceiverInfo();
        void SendMessages();
        void FetchMessages();
    }
}
