package io.krito.com.rezetopia.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

import io.krito.com.rezetopia.R;
import io.krito.com.rezetopia.application.AppConfig;
import io.krito.com.rezetopia.application.RezetopiaApp;
import io.krito.com.rezetopia.helper.VolleyCustomRequest;
import io.krito.com.rezetopia.models.pojo.friends.Friend;
import io.krito.com.rezetopia.models.pojo.friends.FriendsResponse;

public class FriendsTag extends AppCompatActivity {

    EditText search;
    RecyclerView addedTagsRecyclerView;
    RecyclerView friendsRecyclerView;
    TextView doneTagView;
    ImageView back;

    String userId;

    ArrayList<Friend> friendsResult;
    ArrayList<Friend> addedTagsFriends;
    FriendsRecyclerAdapter friendsRecyclerAdapter;
    AddedTagsRecyclerAdapter tagsRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tags);

        userId = getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, null);

        search = findViewById(R.id.searchFriendsView);
        addedTagsRecyclerView = findViewById(R.id.addedTags);
        friendsRecyclerView = findViewById(R.id.searchTags);
        doneTagView = findViewById(R.id.doneTagView);
        back = findViewById(R.id.backFriends);

        doneTagView.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("tags", addedTagsFriends);
            setResult(RESULT_OK, intent);
            onBackPressed();
        });

        back.setOnClickListener(v -> {
            onBackPressed();
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performGetTags(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private class FriendsViewHolder extends RecyclerView.ViewHolder{

        ImageView ppView;
        TextView usernameView;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            ppView = itemView.findViewById(R.id.suggestedImageView);
            usernameView = itemView.findViewById(R.id.suggestedUsernameView);
        }

        public void bind(Friend friend){
            if (friend.getImageUrl() != null){
                Picasso.with(FriendsTag.this).load(friend.getImageUrl()).into(ppView);
            }

            if (friend.getUsername() != null){
                usernameView.setText(friend.getUsername());
            }

            itemView.setOnClickListener(v -> {
                if (addedTagsFriends == null){
                    addedTagsFriends = new ArrayList<>();
                }

                addedTagsFriends.add(friend);
                updateAddedTagsList();
            });
        }
    }

    private class FriendsRecyclerAdapter extends RecyclerView.Adapter<FriendsViewHolder>{

        @NonNull
        @Override
        public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(FriendsTag.this).inflate(R.layout.tag_friend_card, parent, false);
            return new FriendsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FriendsViewHolder holder, int position) {
            holder.bind(friendsResult.get(position));
        }

        @Override
        public int getItemCount() {
            return friendsResult.size();
        }
    }

    private class AddedTagsViewHolder extends RecyclerView.ViewHolder{

        TextView usernameView;

        public AddedTagsViewHolder(View itemView) {
            super(itemView);

            usernameView = itemView.findViewById(R.id.addedTagNameView);
        }

        public void bind(Friend friend, int pos){
            usernameView.setText(friend.getUsername());

            itemView.setOnClickListener(v -> {
                addedTagsFriends.remove(pos);
                updateAddedTagsList();
            });
        }
    }

    private class AddedTagsRecyclerAdapter extends RecyclerView.Adapter<AddedTagsViewHolder>{

        @NonNull
        @Override
        public AddedTagsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(FriendsTag.this).inflate(R.layout.added_tags_card, parent, false);
            return new AddedTagsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AddedTagsViewHolder holder, int position) {
            holder.bind(addedTagsFriends.get(position), position);
        }

        @Override
        public int getItemCount() {
            return addedTagsFriends.size();
        }
    }


    private void performGetTags(String q){
        String url = "https://rezetopia.com/Apis/tag/search?user_id=" + userId + "&keyword=" + q;

        VolleyCustomRequest request = new VolleyCustomRequest(Request.Method.GET, url, FriendsResponse.class,
                new Response.Listener<FriendsResponse>() {
                    @Override
                    public void onResponse(FriendsResponse response) {
                        if (!response.isError()) {
                            if (response.getFriends() != null && response.getFriends().length > 0){
                                Log.i("friends_cursor", "onResponse: ");
                                friendsResult = new ArrayList<>(Arrays.asList(response.getFriends()));
                                updateResultList();
                            }
                        } else if (response.isError()) {

                        } else {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("friends_cursor_error", "onErrorResponse: ");
            }
        });

        RezetopiaApp.getInstance().getRequestQueue().add(request);
    }

    private void updateResultList(){
        if (friendsRecyclerAdapter == null) {
            friendsRecyclerAdapter = new FriendsRecyclerAdapter();
            friendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            friendsRecyclerView.setAdapter(friendsRecyclerAdapter);
        } else {
            friendsRecyclerAdapter.notifyDataSetChanged();
        }
    }

    private void updateAddedTagsList(){
        if (tagsRecyclerAdapter == null) {
            tagsRecyclerAdapter = new AddedTagsRecyclerAdapter();
            addedTagsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            addedTagsRecyclerView.setAdapter(tagsRecyclerAdapter);
        } else {
            tagsRecyclerAdapter.notifyDataSetChanged();
        }
    }
}
