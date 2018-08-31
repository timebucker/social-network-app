package com.example.kokolo.socialnetwork.contracts;

import android.graphics.Bitmap;
import android.widget.EditText;

public interface PostContract {
    interface View {
        void onPostFailed(String errorMessage);
        void onPostSuccess();
        void showLoadingBar(String title, String message);
        void dismissLoadingBar();
    }

    interface Presenter {
        void validatePostInfo(EditText PostDescription, Bitmap BitMapPostImage);
        Bitmap resizeBitMap(Bitmap image, int maxWidth, int maxHeight);
    }
}
