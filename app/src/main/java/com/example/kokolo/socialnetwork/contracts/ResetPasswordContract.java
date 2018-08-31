package com.example.kokolo.socialnetwork.contracts;

public interface ResetPasswordContract {
    interface View {
        void sendUserToLoginActivity();
    }

    interface Presenter {

        void resetPassword(String userEmail);
    }
}
