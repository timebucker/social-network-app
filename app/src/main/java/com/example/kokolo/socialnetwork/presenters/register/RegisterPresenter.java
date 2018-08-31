package com.example.kokolo.socialnetwork.presenters.register;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.kokolo.socialnetwork.contracts.RegisterContract;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterPresenter implements RegisterContract.Presenter{
    final RegisterContract.View mView;
    final FirebaseAuth mFirebaseAuth;
    final Context mContext;

    public RegisterPresenter(Context mContext) {
        this.mView = (RegisterContract.View) mContext;
        this.mFirebaseAuth = FirebaseAuth.getInstance();
        this.mContext = mContext;
    }


    @Override
    public void createNewAccount(String email, String pass, String confirmPass) {

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(mContext, "Please insert email", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pass))
        {
            Toast.makeText(mContext, "Please insert password", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(confirmPass))
        {
            Toast.makeText(mContext, "Please insert confirm password", Toast.LENGTH_SHORT).show();
        }
        else if(!pass.equals(confirmPass))
        {
            Toast.makeText(mContext, "password does not match with confirm password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mView.showLoadingBar("Creating your account", "Please wait...");

            mFirebaseAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                mView.onRegisterSuccess();
                            }
                            else{
                                String errorMessage = task.getException().getMessage();
                                mView.onRegisterFailed(errorMessage);
                            }
                            mView.dismissLoadingBar();
                        }
                    });
        }
    }
}
