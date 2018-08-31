package com.example.kokolo.socialnetwork.views.post;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kokolo.socialnetwork.R;
import com.example.kokolo.socialnetwork.contracts.PostContract;
import com.example.kokolo.socialnetwork.contracts.SetupContract;
import com.example.kokolo.socialnetwork.presenters.post.PostPresenter;
import com.example.kokolo.socialnetwork.views.main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class PostActivity extends AppCompatActivity implements PostContract.View, View.OnClickListener {

    Toolbar mToolbar;
    ImageButton SelectPostImage;
    Button UpdatePostButton;
    EditText PostDescription;

    static final int Gallery_Pick = 1;
    Uri imageUri;

    Bitmap BitMapPostImage;

    ProgressDialog loadingBar;

    PostPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        presenter = new PostPresenter(this);
        initViews();

        SelectPostImage.setOnClickListener(this);
        UpdatePostButton.setOnClickListener(this);

    }

    private void initViews() {
        mToolbar = findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Update Post");

        loadingBar = new ProgressDialog(this);

        SelectPostImage = findViewById(R.id.select_post_image);
        UpdatePostButton = findViewById(R.id.update_post_button);
        PostDescription = findViewById(R.id.post_description);
    }


    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null){

            try {
                imageUri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                BitMapPostImage = presenter.resizeBitMap(bitmap, 550, 450);
                SelectPostImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                SelectPostImage.setImageBitmap(BitMapPostImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            sendUserToMainActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendUserToMainActivity() {
        Intent mainIntetn = new Intent(PostActivity.this, MainActivity.class);
        startActivity(mainIntetn);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.select_post_image:
                openGallery();
                break;
            case R.id.update_post_button:
                presenter.validatePostInfo(this.PostDescription, this.BitMapPostImage);
                break;
        }
    }

    @Override
    public void onPostFailed(String errorMessage) {
        Toast.makeText(PostActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPostSuccess() {
        sendUserToMainActivity();
        Toast.makeText(PostActivity.this, "Post Successfully!", Toast.LENGTH_SHORT).show();
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

}
