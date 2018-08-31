package com.example.kokolo.socialnetwork.views.resetpassword;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kokolo.socialnetwork.R;
import com.example.kokolo.socialnetwork.contracts.ResetPasswordContract;
import com.example.kokolo.socialnetwork.presenters.resetpassword.ResetPasswordPresenter;
import com.example.kokolo.socialnetwork.views.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity implements ResetPasswordContract.View, View.OnClickListener {

    Toolbar mToolbar;

    TextView resetPasswordEmail;
    Button resetPasswordSendButton;
    ResetPasswordPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        initViews();

        presenter = new ResetPasswordPresenter(this);

        resetPasswordSendButton.setOnClickListener(this);
        resetPasswordSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void initViews() {
        mToolbar = findViewById(R.id.forget_password_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Reset Password");

        resetPasswordEmail = findViewById(R.id.reset_password_email);
        resetPasswordSendButton = findViewById(R.id.reset_password_send_button);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset_password_send_button:
                String userEmail = resetPasswordEmail.getText().toString();
                presenter.resetPassword(userEmail);
                break;
        }
    }

    @Override
    public void sendUserToLoginActivity() {
        startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
    }
}
