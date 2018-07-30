package io.krito.com.rezetopia.helper;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.krito.com.rezetopia.R;
import io.krito.com.rezetopia.activities.Profile;
import io.krito.com.rezetopia.models.operations.ProfileOperations;
import io.krito.com.rezetopia.models.pojo.friends.Friend;

/**
 * Created by Ahmed Ali on 7/23/2018.
 */

public class FriendsRecyclerAdapter extends RecyclerView.Adapter<FriendsRecyclerAdapter.FriendsViewHolder> {

    public static final int SUGGESTED_TYPE = 0;
    public static final int FRIENDS_TYPE = 1;

    private Context context;
    private ArrayList<Friend> friends;
    private String userId;
    private int type;
    private FriendsOperationCallback callback;

    public FriendsRecyclerAdapter(Context context, ArrayList<Friend> friends, String userId, int type, FriendsOperationCallback callback) {
        this.context = context;
        this.friends = friends;
        this.userId = userId;
        this.callback = callback;
        this.type = type;
    }

    public void setCallback(FriendsOperationCallback callback) {
        this.callback = callback;
    }

    public void setFriends(ArrayList<Friend> friends) {
        this.friends = friends;
    }

    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (type == SUGGESTED_TYPE){
            view = LayoutInflater.from(context).inflate(R.layout.suggested_friends_card, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.view_friends_card, parent, false);
        }

        return new FriendsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsViewHolder holder, int position) {
        holder.bind(friends.get(position), position);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }


    public class FriendsViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView usernameView;
        Button addDeleteButton;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.suggestedImageView);
            usernameView = itemView.findViewById(R.id.suggestedUsernameView);
            addDeleteButton = itemView.findViewById(R.id.suggestedAddButton);
        }

        public void bind(Friend friend, int position){

            itemView.setOnClickListener(v -> {
                Intent intent = Profile.createIntent(String.valueOf(friend.getId()), context);
                context.startActivity(intent);
            });


            if (friend.getImageUrl() != null){
                Picasso.with(context).load(friend.getImageUrl()).into(imageView);
            }

            if (friend.getUsername() != null){
                usernameView.setText(friend.getUsername());
            }

            itemView.setOnClickListener(v -> {
                Intent intent = Profile.createIntent(String.valueOf(friend.getId()), context);
                context.startActivity(intent);
            });

            addDeleteButton.setOnClickListener(v -> {

                if (type == SUGGESTED_TYPE) {
                    //friends.remove(position);
                    if (callback != null)
                        callback.onItemRemoved(position);
                    //notifyItemRemoved(position);


                    Log.i("friendsIds", "bind: " + userId + " - " + friend.getId());
                    ProfileOperations.sendFriendRequest(userId, String.valueOf(friend.getId()));
                    ProfileOperations.setFriendRequestCallback(new ProfileOperations.SendFriendRequestCallback() {
                        @Override
                        public void onSuccess(boolean result) {
                            Log.i("addFriendSuccess", "onSuccess: ");
                        }

                        @Override
                        public void onError(int error) {
                            Log.i("addFriendFailure", "onError: ");
                        }
                    });
                } else {
                    Log.i("IDS", "bind: mine->" + userId + " -- " + "outer->" + friend.getId());
                    friends.remove(position);
                    notifyDataSetChanged();
                    ProfileOperations.cancelFriendRequest(userId, String.valueOf(friend.getId()));
                    ProfileOperations.setCancelDeleteFriendRequestCallback(new ProfileOperations.CancelDeleteFriendRequestCallback() {
                        @Override
                        public void onSuccess(boolean result) {
                            Log.i("deleteFriendSuccess", "onSuccess: ");
                        }

                        @Override
                        public void onError(int error) {
                            Log.i("deleteFriendFailure", "onError: ");
                        }
                    });
                }
            });
        }
    }

    public void filter(String text) {
        ArrayList<Friend> copy = friends;
        friends.clear();
        if(text.isEmpty()){
            friends.addAll(copy);
        } else {
            text = text.toLowerCase();
            for(Friend item: copy){
                if(item.getUsername().toLowerCase().contains(text)){
                    friends.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public interface FriendsOperationCallback {
        void onItemRemoved(int position);
    }
}
