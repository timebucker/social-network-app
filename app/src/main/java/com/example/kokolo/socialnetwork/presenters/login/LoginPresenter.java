package com.example.kokolo.socialnetwork.presenters.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.kokolo.socialnetwork.contracts.LoginContract;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPresenter implements LoginContract.Presenter {
    final LoginContract.View mView;
    final FirebaseAuth mFirebaseAuth;
    final Context mContext;
    private ProgressDialog loadingBar;
    private GoogleApiClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "LoginActivity";


    public LoginPresenter(Context context) {
        this.mView = (LoginContract.View) context;
        this.mContext = context;
        this.mFirebaseAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(context);
    }

    @Override
    public void login(String email, String password) {
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(mContext, "Please write your email...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(mContext, "Please write your password...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mView.showLoadingBar("Login", "Please wait, while we are allowing you to login into your Account...");

            mFirebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                mView.loginSuccess();
                            }
                            else
                            {
                                String errorMessage = task.getException().getMessage();
                                mView.loginFailed(errorMessage);
                            }
                            mView.dismissLoadingBar();
                        }
                    });
        }
    }


}
