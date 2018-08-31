package com.example.kokolo.socialnetwork.views.main;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.kokolo.socialnetwork.R;
import com.example.kokolo.socialnetwork.contracts.MainContract;
import com.example.kokolo.socialnetwork.models.main.Posts;
import com.example.kokolo.socialnetwork.presenters.main.MainPresenter;
import com.example.kokolo.socialnetwork.views.friendslist.FriendsListActivity;
import com.example.kokolo.socialnetwork.views.post.PostActivity;
import com.example.kokolo.socialnetwork.views.profile.ProfileActivity;
import com.example.kokolo.socialnetwork.views.setting.SettingsActivity;
import com.example.kokolo.socialnetwork.views.setup.SetupActivity;
import com.example.kokolo.socialnetwork.views.findfriend.FindFriendsActivity;
import com.example.kokolo.socialnetwork.views.login.LoginActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements MainContract.View,View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;
    DrawerLayout drawerLayout;
    RecyclerView postList;
    Toolbar mToolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    TextView navProfileUserName;
    CircleImageView navProfileImage;
    ImageButton AddNewPostButton;

    MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new MainPresenter(this);

        initViews();

        AddNewPostButton.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);

        presenter.displayAllUsersPosts();
    }

    private void initViews() {
        // add toolbar
        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");

        //add drawerlayout
        drawerLayout = findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.navigation_view);

        //add display post list
        postList = findViewById(R.id.all_users_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        // add navigation view
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        navProfileImage = navView.findViewById(R.id.nav_profile_image);
        navProfileUserName = navView.findViewById(R.id.nav_user_full_name);

        //add post button
        AddNewPostButton = findViewById(R.id.add_new_post_button);
    }


    @Override
    protected void onStart() {
        super.onStart();

        presenter.validateUser();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.nav_post:
                sendUserToPostActivity();
                break;
            case R.id.nav_friends:
                SendUserToFriendsActivity();
                break;
            case R.id.nav_profile:
                presenter.sendUserToOwnProfile();
                break;
            case R.id.nav_home:
                DrawerLayout mDrawerLayout;
                mDrawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
                mDrawerLayout.closeDrawers();
                break;

            case R.id.nav_find_friend:
                sendUserToFindFriendsActivity();
                break;
            case R.id.nav_messages:
                SendUserToFriendsActivity();
                break;
            // Quang code
            case R.id.nav_setting:
                SendUserToSettingsActivity();
                break;
            case R.id.nav_logout:
                presenter.signOut();
                sendUserToLoginActivity();
                break;
        }
    }

    private void SendUserToFriendsActivity() {
        Intent friendsIntent = new Intent(MainActivity.this, FriendsListActivity.class);
        startActivity(friendsIntent);
    }

    public void sendUserToSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    @Override
    public void sendUserToProfileActivity(String userId) {
        Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
        profileIntent.putExtra("userId", userId);
        startActivity(profileIntent);
    }

    public void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }


    private void sendUserToPostActivity() {
        Intent postIntent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(postIntent);
    }


    private void sendUserToFindFriendsActivity() {
        Intent findFriendsIntent = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(findFriendsIntent);
    }

    // Quang code
    private void SendUserToSettingsActivity() {
        Intent loginIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(loginIntent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_new_post_button:
                sendUserToPostActivity();
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        UserMenuSelector(item);
        return false;
    }

    @Override
    public void setPostListAdapter(FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter) {
        postList.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public void setNavProfileUserName(String fullname) {
        navProfileUserName.setText(fullname);
    }

    @Override
    public void setNavProfileImage(String myProfileImage) {
        Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(navProfileImage);
    }
}
