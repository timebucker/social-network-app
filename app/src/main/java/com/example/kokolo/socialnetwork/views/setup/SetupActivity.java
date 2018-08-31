package com.example.kokolo.socialnetwork.views.setup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kokolo.socialnetwork.R;
import com.example.kokolo.socialnetwork.contracts.SetupContract;
import com.example.kokolo.socialnetwork.presenters.setup.SetupPresenter;
import com.example.kokolo.socialnetwork.views.main.MainActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity implements SetupContract.View,View.OnClickListener {

    EditText UserName, FullName, Country;
    Button SaveInfomationButton;
    CircleImageView profileImage;
    ProgressDialog loadingBar;
    CropImage.ActivityResult imageCropResult;

    final static int Gallery_Pick = 1;

    SetupPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        presenter = new SetupPresenter(this);

        initViews();

        SaveInfomationButton.setOnClickListener(this);
        profileImage.setOnClickListener(this);

    }

    private void initViews() {
        loadingBar = new ProgressDialog(this);
        UserName = findViewById(R.id.setup_username);
        FullName = findViewById(R.id.setup_full_name);
        Country = findViewById(R.id.setup_country);
        SaveInfomationButton = findViewById(R.id.setup_information_button);
        profileImage = findViewById(R.id.setup_profile_image);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                imageCropResult = CropImage.getActivityResult(data);
                profileImage.setImageURI(imageCropResult.getUri());
            }
        }
    }

    private void setAccountSetupInformation() {
        String name = UserName.getText().toString();
        String fullName = FullName.getText().toString();
        String country = Country.getText().toString();

        presenter.setAccountSetupInformation(name, fullName, country);
    }


    private void sendUserToMainActivity() {
        Intent mainIntetn = new Intent(SetupActivity.this, MainActivity.class);
        mainIntetn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntetn);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setup_information_button:
                setAccountSetupInformation();
                break;
            case R.id.setup_profile_image:
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
                break;
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
    public void onSetupSuccess() {
        Toast.makeText(SetupActivity.this, "Setup success!", Toast.LENGTH_SHORT).show();
        sendUserToMainActivity();
    }

    @Override
    public void onSetupFailed(String errorMassage) {
        //Toast.makeText(SetupActivity.this, "Setup failed!: "+errorMassage, Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean isSetImageProfile() {
        return imageCropResult != null;
    }

    @Override
    public CropImage.ActivityResult getImageCropResult() {
        return this.imageCropResult;
    }
}
