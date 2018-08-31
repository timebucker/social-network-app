package com.example.kokolo.socialnetwork.views.setting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kokolo.socialnetwork.R;
import com.example.kokolo.socialnetwork.contracts.SettingContract;
import com.example.kokolo.socialnetwork.presenters.setting.SettingPresenter;
import com.example.kokolo.socialnetwork.views.main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity implements SettingContract.View, View.OnClickListener {

    Toolbar mToolbar;

    EditText userName, userProfName, userStatus, userCountry, userGender, userRelation, userDOB;
    Button updateAccountSettingsButton;
    CircleImageView userProfImage;

    SettingPresenter presenter;

    final static int Gallery_Pick = 1;
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        presenter = new SettingPresenter(this);

        initViews();

        presenter.loadDataToViews();

        userProfImage.setOnClickListener(this);
        updateAccountSettingsButton.setOnClickListener(this);
    }

    private void initViews() {
        mToolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userName = findViewById(R.id.settings_username);
        userProfName = findViewById(R.id.settings_profile_full_name);
        userStatus = findViewById(R.id.settings_status);
        userCountry = findViewById(R.id.settings_country);
        userGender = findViewById(R.id.settings_gender);
        userRelation = findViewById(R.id.settings_relationship_status);
        userDOB = findViewById(R.id.settings_dob);
        userProfImage = findViewById(R.id.settings_profile_image);

        updateAccountSettingsButton = findViewById(R.id.update_account_settings_button);

        loadingBar = new ProgressDialog(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK)
            {
                showLoadingBar("Profile Image", "Please wait, while we updating your profile image...");

                Uri resultUri = result.getUri();
                presenter.storeImageToStorage(resultUri);
            }
            else
            {
                Toast.makeText(this, "Error Occured: Image can not be cropped. Try Again.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }

    private void SendUserToMainActivity() {
        Intent setupIntent = new Intent(SettingsActivity.this, MainActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    @Override
    public void loadViewData(String myProfileImage, String myUserName, String myProfileName, String myCountry, String myProfileStatus, String myDOB, String myGender, String myRelationshipStatus) {

        Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfImage);

        userName.setText(myUserName);
        userProfName.setText(myProfileName);
        userCountry.setText(myCountry);
        if (!myProfileStatus.equals("none")){
            userStatus.setText(myProfileStatus);
        }
        if (!myDOB.equals("none")){
            userDOB.setText(myDOB);
        }
        if (!myGender.equals("none")){
            userGender.setText(myGender);
        }
        if (!myRelationshipStatus.equals("none")){
            userRelation.setText(myRelationshipStatus);
        }
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
    public void onUpdateSuccess() {
        SendUserToMainActivity();
        Toast.makeText(SettingsActivity.this, "Account Settings Updated Successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateFailed() {
        Toast.makeText(SettingsActivity.this, "Error Occured, while updating account settings information.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setViewError(String ViewName) {
        if (ViewName.equals("username")){
            userName.setError("Your username must be not null");
        }
        else if (ViewName.equals("userprofName")){
            userProfName.setError("Your full name must be not null");
        }
        else if (ViewName.equals("usercountry")){
            userCountry.setError("Your country must be not null");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.settings_profile_image:
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
                break;
            case R.id.update_account_settings_button:
                String username = userName.getText().toString();
                String profname = userProfName.getText().toString();
                String status = userStatus.getText().toString();
                String dob = userDOB.getText().toString();
                String country = userCountry.getText().toString();
                String gender = userGender.getText().toString();
                String relation = userRelation.getText().toString();
                presenter.validateAccountInfo(username, profname, status, dob, country, gender, relation);
                break;
        }
    }
}
