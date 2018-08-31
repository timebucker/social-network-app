package com.example.kokolo.socialnetwork.contracts;

import android.net.Uri;

public interface SettingContract {
    interface View {
        void loadViewData(String myProfileImage,
                          String myUserName,
                          String myProfileName,
                          String myCountry,
                          String myProfileStatus,
                          String myDOB,
                          String myGender,
                          String myRelationshipStatus);
        void showLoadingBar(String title, String message);
        void dismissLoadingBar();
        void onUpdateSuccess();
        void onUpdateFailed();
        void setViewError(String ViewName);
    }

    interface Presenter {
        void loadDataToViews();
        void validateAccountInfo(String username,
        String profname,
        String status,
        String dob,
        String country,
        String gender,
        String relation);

        void storeImageToStorage(Uri resultUri);
    }
}
