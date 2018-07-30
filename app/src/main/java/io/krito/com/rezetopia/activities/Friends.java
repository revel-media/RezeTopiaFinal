package io.krito.com.rezetopia.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.andrognito.flashbar.Flashbar;
import com.andrognito.flashbar.anim.FlashAnimBarBuilder;

import java.util.ArrayList;
import java.util.Arrays;

import io.krito.com.rezetopia.R;
import io.krito.com.rezetopia.application.AppConfig;
import io.krito.com.rezetopia.helper.CustomLinearLayoutManager;
import io.krito.com.rezetopia.helper.FriendsRecyclerAdapter;
import io.krito.com.rezetopia.models.operations.ProfileOperations;
import io.krito.com.rezetopia.models.pojo.friends.FriendsResponse;

public class Friends extends AppCompatActivity implements View.OnClickListener{

    RecyclerView recyclerView;
    ImageView backFriends;
    EditText searchFriendsView;
    ProgressBar progressBar;

    FriendsRecyclerAdapter adapter;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        recyclerView = findViewById(R.id.friendsRecView);
        backFriends = findViewById(R.id.backFriends);
        searchFriendsView = findViewById(R.id.searchFriendsView);
        progressBar = findViewById(R.id.friendsProgress);


        /*userId = getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, null);*/

        userId = getIntent().getExtras().getString("owner_id");

        searchFriendsView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null){
                    adapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        updateUi();
    }

    private void updateUi(){
        if (adapter == null){
            ProfileOperations.fetchFriends(userId, "0");
            ProfileOperations.setFriendsCallback(new ProfileOperations.FriendsCallback() {
                @Override
                public void onSuccess(FriendsResponse response) {
                    Log.i("getFriendsSuccess", "onSuccess: " + response.getFriends().length);
                    progressBar.setVisibility(View.GONE);
                    adapter = new FriendsRecyclerAdapter(Friends.this, new ArrayList<>(Arrays.asList(response.getFriends())), userId, FriendsRecyclerAdapter.FRIENDS_TYPE, null);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(Friends.this));
                }

                @Override
                public void onError(int error) {
                    progressBar.setVisibility(View.GONE);
                    Log.i("getFriendsFailure", "onError: ");

                    Flashbar.Builder builder = new Flashbar.Builder(Friends.this);
                    builder.gravity(Flashbar.Gravity.BOTTOM)
                            .backgroundColor(R.color.red2)
                            .enableSwipeToDismiss()
                            .message(R.string.empty_friends)
                            .enterAnimation(new FlashAnimBarBuilder(Friends.this).slideFromRight().duration(200))
                            .build().show();
                }

                @Override
                public void onEmptyResult() {
                    progressBar.setVisibility(View.GONE);
                    Log.i("getFriendsEmptyResult", "onEmptyResult: ");
                    Log.i("friends_owner", "HeaderViewHolder: " + userId);

                    Flashbar.Builder builder = new Flashbar.Builder(Friends.this);
                    builder.gravity(Flashbar.Gravity.BOTTOM)
                            .backgroundColor(R.color.red2)
                            .enableSwipeToDismiss()
                            .message(R.string.empty_friends)
                            .enterAnimation(new FlashAnimBarBuilder(Friends.this).slideFromRight().duration(200))
                            .build().show();
                }
            });
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backFriends:
                break;
        }
    }
}
