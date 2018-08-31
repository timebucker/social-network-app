package com.example.kokolo.socialnetwork.contracts;

public interface RegisterContract {
    interface View {
        void showLoadingBar(String title, String message);
        void dismissLoadingBar();
        void onRegisterSuccess();
        void onRegisterFailed(String errorMessage);
    }

    interface Presenter {
        void createNewAccount(String email, String pass, String confirmPass);
    }
}
