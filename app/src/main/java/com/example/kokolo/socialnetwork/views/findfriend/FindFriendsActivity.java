package com.example.kokolo.socialnetwork.views.findfriend;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.kokolo.socialnetwork.R;
import com.example.kokolo.socialnetwork.contracts.FindFriendContract;
import com.example.kokolo.socialnetwork.models.findfriend.FindFriends;
import com.example.kokolo.socialnetwork.presenters.findfriend.FindFriendPresenter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity implements FindFriendContract.View,View.OnClickListener {

    Toolbar mToolbar;

    ImageButton searchButton;
    EditText searchInputText;

    RecyclerView searchResultList;

    FindFriendPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        presenter = new FindFriendPresenter(this);
        initViews();

        searchButton.setOnClickListener(this);
    }

    private void initViews() {
        mToolbar = findViewById(R.id.find_friends_app_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

        searchResultList = findViewById(R.id.search_result_list);
        searchResultList.setHasFixedSize(true);
        searchResultList.setLayoutManager(new LinearLayoutManager(this));

        searchButton = findViewById(R.id.search_friends_button);
        searchInputText = findViewById(R.id.search_box_input);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_friends_button:
                String searchBoxInput = searchInputText.getText().toString();
                presenter.searchFriends(searchBoxInput);
                break;
        }
    }

    @Override
    public void setSearchResultAdapter(FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder> firebaseRecyclerAdapter) {
        this.searchResultList.setAdapter(firebaseRecyclerAdapter);
    }
}
