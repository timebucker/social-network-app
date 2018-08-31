package com.example.kokolo.socialnetwork.views.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.kokolo.socialnetwork.R;
import com.example.kokolo.socialnetwork.contracts.LoginContract;
import com.example.kokolo.socialnetwork.presenters.login.LoginPresenter;
import com.example.kokolo.socialnetwork.views.main.MainActivity;
import com.example.kokolo.socialnetwork.views.register.RegisterActivity;
import com.example.kokolo.socialnetwork.views.resetpassword.ResetPasswordActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity implements LoginContract.View, View.OnClickListener{
    private Button LoginButton;
    private ImageView googleSignInButton;
    private EditText UserEmail, UserPassword;
    private TextView NeedNewAccountLink, ForgetPasswordLink;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;

    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleSignInClient;
    private static final String TAG = "LoginActivity";

    LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        presenter = new LoginPresenter(this);
        mAuth = FirebaseAuth.getInstance();
        initViews();

        LoginButton.setOnClickListener(this);
        googleSignInButton.setOnClickListener(this);
        NeedNewAccountLink.setOnClickListener(this);
        ForgetPasswordLink.setOnClickListener(this);
        configureGoogleSignIn();
    }


    private void initViews() {
        NeedNewAccountLink = (TextView) findViewById(R.id.register_account_link);
        ForgetPasswordLink = findViewById(R.id.forget_password_link);
        UserEmail = (EditText) findViewById(R.id.login_email);
        UserPassword = (EditText) findViewById(R.id.login_password);
        LoginButton = (Button) findViewById(R.id.login_button);
        googleSignInButton = (ImageView) findViewById(R.id.google_signin_button);
        loadingBar = new ProgressDialog(this);
    }

    // Quang code


    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null)
        {
            sendUserToMainActivity();
        }
    }


    public void sendUserToMainActivity()
    {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    // Quang code
    public void sendUserToLoginActivity()
    {
        Intent mainIntent = new Intent(LoginActivity.this, LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void sendUserToRegisterActivity()
    {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    private void sendUserToResetPasswordActivity() {
        startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
    }

    @Override
    public void loginFailed(String errorMassage) {
        Toast.makeText(LoginActivity.this, "Login failed: " + errorMassage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loginSuccess() {
        sendUserToMainActivity();
        Toast.makeText(LoginActivity.this, "You are Logged In successfully.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoadingBar(String title, String message) {
        loadingBar.setTitle(title);
        loadingBar.setMessage(message);
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
    }

    @Override
    public void dismissLoadingBar() {
        loadingBar.dismiss();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_button:
                String email = UserEmail.getText().toString();
                String password = UserPassword.getText().toString();
                presenter.login(email, password);
                break;
            case R.id.register_account_link:
                sendUserToRegisterActivity();
                break;
            case R.id.google_signin_button:
                signIn();
                break;
            case R.id.forget_password_link:
                sendUserToResetPasswordActivity();
        }
    }




    private void configureGoogleSignIn() {
        // Quang code
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
                    {
                        Toast.makeText(LoginActivity.this, "Connection to Google Sign in failed...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN)
        {
            showLoadingBar("google Sign In", "Please wait, while we are allowing you to login using your Google Account...");

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess())
            {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                Toast.makeText(this, "Please wait, while we are getting your auth result...", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Can't get Auth result.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        final Task<AuthResult> authResultTask = mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            sendUserToMainActivity();
                            loadingBar.dismiss();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            String message = task.getException().toString();
                            sendUserToLoginActivity();
                            Toast.makeText(LoginActivity.this, "Not Authenticated : " + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
    }

    public void signIn()
    {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        //mView.startActivityForResult(signInIntent, RC_SIGN_IN);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

}

