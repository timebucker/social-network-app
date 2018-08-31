package com.example.kokolo.socialnetwork.contracts;

import com.theartofdev.edmodo.cropper.CropImage;

public interface SetupContract {
    interface View {
        void showLoadingBar(String title, String message);
        void dismissLoadingBar();
        void onSetupSuccess();
        void onSetupFailed(String errorMessage);
        boolean isSetImageProfile();
        CropImage.ActivityResult getImageCropResult();
    }

    interface Presenter {
        void setAccountSetupInformation( String name, String fullName, String country);
    }
}
