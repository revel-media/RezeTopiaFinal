package io.krito.com.rezetopia.helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andrognito.flashbar.Flashbar;
import com.andrognito.flashbar.anim.FlashAnimBarBuilder;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apradanas.prismoji.PrismojiTextView;
import com.github.aakira.expandablelayout.ExpandableLayout;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.krito.com.rezetopia.R;
import io.krito.com.rezetopia.activities.AboutProfile;
import io.krito.com.rezetopia.activities.Albums;
import io.krito.com.rezetopia.activities.Friends;
import io.krito.com.rezetopia.activities.ImageViewer;
import io.krito.com.rezetopia.activities.Profile;
import io.krito.com.rezetopia.models.operations.HomeOperations;
import io.krito.com.rezetopia.models.operations.ProfileOperations;
import io.krito.com.rezetopia.models.pojo.User;
import io.krito.com.rezetopia.models.pojo.friends.FriendsResponse;
import io.krito.com.rezetopia.models.pojo.news_feed.NewsFeedItem;
import io.krito.com.rezetopia.views.CustomTextView;
import ru.whalemare.sheetmenu.SheetMenu;

public class ProfileRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_HEADER = 1;
    private static final int VIEW_POST_1 = 2;
    private static final int VIEW_PROGRESS = 3;
    private static final int VIEW_POST_2 = 4;
    private static final int VIEW_POST_3 = 5;
    private static final int VIEW_POST_4 = 6;
    private static final int VIEW_POST_5 = 7;
    private static final int VIEW_PP = 8;

    private Context context;
    private ArrayList<NewsFeedItem> items;
    private long now;
    private String loggedInUserId;
    private AdapterCallback callback;
    private String profileOwnerUserId;
    private boolean isFriend;
    private boolean friendState;
    private User user;
    private boolean updatePp = false;
    private boolean updateCover = false;
    private boolean havePosts = true;


    public ProfileRecyclerAdapter(Context context, ArrayList<NewsFeedItem> items, long now, String loggedInUserId, String profileOwnerId,
                                  boolean isFriend, boolean friendState, User user, boolean havePosts) {
        this.context = context;
        this.items = items;
        this.now = now;
        this.loggedInUserId = loggedInUserId;
        this.profileOwnerUserId = profileOwnerId;
        this.isFriend = isFriend;
        this.friendState = friendState;
        this.user = user;
        this.havePosts = havePosts;
    }

    public ArrayList<NewsFeedItem> getItems() {
        return items;
    }

    public void setItem(NewsFeedItem item, int index) {
        items.set(index, item);
        notifyDataSetChanged();
        //callback.onSetItem(index);
    }

    public void addItem() {
        NewsFeedItem item = new NewsFeedItem();
        item.setType(1009);
        items.add(item);
        //notifyItemInserted(items.size() - 1);
        callback.onItemAdded(items.size());
    }

    public int removeLastItem() {
        //items.remove(items.size() - 1);
        //notifyItemRemoved(items.size() - 1);

        if (items != null && items.size() > 0) {
            for (NewsFeedItem item : items) {
                if (item.getType() == 1009) {
                    items.remove(items.size() - 1);
                    callback.onItemRemoved(items.size());
                    return items.size();
                }
            }
        }
        //callback.onItemRemoved(items.size());
        return 0;
    }

    public void addCommentItem(int postId, int commentSize) {
        for (int i = 0; i < items.size(); i++) {
            Log.e("feed_size", String.valueOf(items.size()));
            Log.e("postIDs", String.valueOf(items.get(i).getId()));
            if (items.get(i).getType() == NewsFeedItem.POST_TYPE) {
                if (items.get(i).getId() == postId) {
                    items.get(i).setCommentSize(commentSize);
                    notifyItemChanged(i);
                }
            }
        }
    }

    public void setCallback(AdapterCallback adapterCallback) {
        this.callback = adapterCallback;
    }

    public void addPostToTop(NewsFeedItem item) {
        items.add(0, item);
        /*if (item.getPostAttachment().getImages() != null){
            Log.i("Post_Images", "addPostToTop: " + item.getPostAttachment().getImages()[0].getPath());
        }*/
        //callback.onItemAdded(1);
        if (!havePosts){
            havePosts = true;
            notifyItemChanged(1);
        }
        notifyDataSetChanged();
    }

    public void addPpToTop(NewsFeedItem item) {
        items.add(0, item);

        callback.onItemAdded(1);
    }

    public void updatePp(String url) {
        user.setImageUrl(url);
        updatePp = true;
        notifyItemChanged(0);
    }

    public void updateCover(String url) {
        user.setCover(url);
        updateCover = true;
        notifyItemChanged(0);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.profile_header, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == VIEW_POST_1) {
            View view = LayoutInflater.from(context).inflate(R.layout.post_card_1, parent, false);
            return new PostViewHolder(view);
        } else if (viewType == VIEW_POST_2) {
            View view = LayoutInflater.from(context).inflate(R.layout.post_card_2, parent, false);
            return new PostViewHolder(view);
        } else if (viewType == VIEW_POST_3) {
            View view = LayoutInflater.from(context).inflate(R.layout.post_card_3, parent, false);
            return new PostViewHolder(view);
        } else if (viewType == VIEW_POST_4) {
            View view = LayoutInflater.from(context).inflate(R.layout.post_card_4, parent, false);
            return new PostViewHolder(view);
        } else if (viewType == VIEW_POST_5) {
            View view = LayoutInflater.from(context).inflate(R.layout.post_card_5, parent, false);
            return new PostViewHolder(view);
        } else if (viewType == VIEW_PP) {
            View view = LayoutInflater.from(context).inflate(R.layout.pp_card, parent, false);
            return new PpViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.progress, parent, false);
            return new ProgressViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PostViewHolder) {
            PostViewHolder pHolder = (PostViewHolder) holder;
            pHolder.bind(items.get(position - 1), position - 1);
        } else if (holder instanceof PpViewHolder) {
            PpViewHolder pHolder = (PpViewHolder) holder;
            pHolder.bind(items.get(position - 1), position - 1);
        }
    }

    @Override
    public int getItemCount() {
        return items.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return VIEW_HEADER;
        } else if (items.get(position - 1).getType() == 1009) {
            return VIEW_PROGRESS;
        } else if (items.get(position - 1).getPpUrl() != null) {
            return VIEW_PP;
        } else if (items.get(position - 1).getCoverUrl() != null) {
            return VIEW_PP;
        } else if (items.get(position - 1).getPostAttachment() != null && items.get(position - 1).getPostAttachment().getImages() != null && items.get(position - 1).getPostAttachment().getImages().length > 0) {
            if (items.get(position - 1).getPostAttachment().getImages().length == 2) {
                return VIEW_POST_2;
            } else if (items.get(position - 1).getPostAttachment().getImages().length == 3) {
                return VIEW_POST_3;
            } else if (items.get(position - 1).getPostAttachment().getImages().length == 4) {
                return VIEW_POST_4;
            } else if (items.get(position - 1).getPostAttachment().getImages().length == 5) {
                return VIEW_POST_5;
            }
        }

        return VIEW_POST_1;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout createPostLayout;
        CircleImageView ppView;
        TextView usernameView;
        ImageView coverView;
        TextView addFriendBtn;
        //CustomTextView skillsView;
        CustomTextView photosView;
        CustomTextView aboutView;
        RelativeLayout operationsLayout;
        TextView dontHavePosts;
        CustomTextView friendsView;
        ExpandableLinearLayout expandableLayout;
        FriendsRecyclerAdapter adapter;
        RecyclerView friendsRecView;


        public HeaderViewHolder(View itemView) {
            super(itemView);

            createPostLayout = itemView.findViewById(R.id.createPostLayout);
            ppView = itemView.findViewById(R.id.ppView);
            usernameView = itemView.findViewById(R.id.usernameView);
            coverView = itemView.findViewById(R.id.coverView);
            addFriendBtn = itemView.findViewById(R.id.addFriendBtn);
            //skillsView = itemView.findViewById(R.id.skills);
            photosView = itemView.findViewById(R.id.profilePhotos);
            aboutView = itemView.findViewById(R.id.about);
            operationsLayout = itemView.findViewById(R.id.operationsLayout);
            dontHavePosts = itemView.findViewById(R.id.dontHavePosts);
            friendsView = itemView.findViewById(R.id.friends);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            friendsRecView = itemView.findViewById(R.id.friendsRecView);

//            if (havePosts){
//                dontHavePosts.setVisibility(View.GONE);
//                usernameView.setText("حسبالاه");
//            } else {
//                dontHavePosts.setVisibility(View.VISIBLE);
//            }

            if (loggedInUserId.contentEquals(profileOwnerUserId)) {
                operationsLayout.setVisibility(View.GONE);
            } else {
                createPostLayout.setVisibility(View.GONE);
            }

            aboutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Intent intent = AboutProfile.createIntent(context, profileOwnerUserId);
                    Intent intent = new Intent(context, AboutProfile.class);
                    intent.putExtra("id", profileOwnerUserId);
                    intent.putExtra("name", user.getName());
                    context.startActivity(intent);
                }
            });

            photosView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Intent intent = Albums.createIntent(context, profileOwnerUserId);
                    Intent intent = new Intent(context, Albums.class);
                    intent.putExtra("id", profileOwnerUserId);
                    intent.putExtra("name", user.getName());
                    context.startActivity(intent);
                }
            });

            friendsView.setOnClickListener(v -> {

                Intent intent = new Intent(context, Friends.class);
                context.startActivity(intent);
//                if (expandableLayout.isExpanded()){
//                    expandableLayout.toggle();
//                }

//                if (adapter == null){
//                    ProfileOperations.fetchFriends(loggedInUserId, "0");
//                    ProfileOperations.setFriendsCallback(new ProfileOperations.FriendsCallback() {
//                        @Override
//                        public void onSuccess(FriendsResponse response) {
//                            expandableLayout.toggle();
//                            Log.i("getFriendsSuccess", "onSuccess: " + response.getFriends().length);
//                            adapter = new FriendsRecyclerAdapter(context, new ArrayList<>(Arrays.asList(response.getFriends())), loggedInUserId, position -> {
//
//                            });
//
//                            friendsRecView.setAdapter(adapter);
//                            friendsRecView.setLayoutManager(new CustomLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
//                            //expandableLayout.expand();
//                        }
//
//                        @Override
//                        public void onError(int error) {
//                            Log.i("getFriendsFailure", "onError: ");
//                        }
//
//                        @Override
//                        public void onEmptyResult() {
//                            Log.i("getFriendsEmptyResult", "onEmptyResult: ");
//                            callback.onEmptyFriends();
//                        }
//                    });
//
//
//                } else {
//
//                }

            });

            if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
                if (!updatePp) {
                    Picasso.with(context).load(user.getImageUrl()).into(ppView);
                } else {
                    Picasso.with(context).load(user.getImageUrl()).into(ppView);
                }
                Log.i("updatePp", "HeaderViewHolder: " + user.getImageUrl());
            }

            if (user.getCover() != null && !user.getCover().isEmpty()) {
                if (!updateCover) {
                    Picasso.with(context).load(user.getCover() + ".JPG").into(coverView);
                } else {
                    Picasso.with(context).load(user.getCover()).into(coverView);
                }

                Log.i("updateCover", "HeaderViewHolder: " + user.getCover());
            } else {
                coverView.setBackground(context.getResources().getDrawable(R.drawable.cover));
            }

            usernameView.setText(user.getName());

            if (isFriend && friendState) {
                addFriendBtn.setText(context.getResources().getString(R.string.remove_friend));
            } else if (isFriend && !friendState) {
                addFriendBtn.setText(context.getResources().getString(R.string.respond_friend_request));
            } else {
                addFriendBtn.setText(context.getResources().getString(R.string.add));
            }

            createPostLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onStartCreatePost();
                }
            });

            addFriendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isFriend) {
                        if (friendState) {
                            cancelDeleteFriendship();
                        } else {
                             SheetMenu.with(context)
                                    .setMenu(R.menu.respond_friend_request)
                                    .setAutoCancel(true)
                                    .setClick(new MenuItem.OnMenuItemClickListener() {
                                        @Override
                                        public boolean onMenuItemClick(MenuItem item) {
                                            switch (item.getItemId()) {
                                                case R.id.acceptFriendRequest:
                                                    acceptFriendship();
                                                    break;
                                                case R.id.removeFriendRequest:
                                                    cancelDeleteFriendship();
                                                    break;
                                            }
                                            return false;
                                        }
                                    }).show();
                        }

                    } else {
                        sendFriendRequest();
                    }
                }
            });

            /*skillsView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, Skills.class));
                }
            });*/

            ppView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (profileOwnerUserId.contentEquals(loggedInUserId)) {
                        SheetMenu.with(context)
                                .setMenu(R.menu.image_view_menu_owner)
                                .setAutoCancel(true)
                                .setClick(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        switch (item.getItemId()) {
                                            case R.id.viewImage:
                                                if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
                                                    Intent intent = ImageViewer.createIntent(user.getImageUrl(), ImageViewer.PP_MODE, context);
                                                    context.startActivity(intent);
                                                } else {
                                                    callback.showSnackBar(context.getResources().getString(R.string.no_image));
                                                }

                                                return true;
                                            case R.id.updatePp:
                                                callback.onStartPickPp();
                                                return true;
                                        }
                                        return false;
                                    }
                                }).show();
                    } else {
                        Intent intent = ImageViewer.createIntent(user.getImageUrl(), ImageViewer.PP_MODE, context);
                        context.startActivity(intent);
                    }
                }
            });

            coverView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (profileOwnerUserId.contentEquals(loggedInUserId)) {
                        SheetMenu.with(context)
                                .setMenu(R.menu.cover_view_menu_owner)
                                .setAutoCancel(true)
                                .setClick(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        switch (item.getItemId()) {
                                            case R.id.viewImage:
                                            /*if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()){
                                                Intent intent = ImageViewer.createIntent(user.getImageUrl(), ImageViewer.PP_MODE, context);
                                                context.startActivity(intent);
                                            } else {
                                                callback.showSnackBar(context.getResources().getString(R.string.no_image));
                                            }
*/
                                                return true;
                                            case R.id.updateCover:
                                                callback.onStartPickCover();
                                                return true;
                                        }
                                        return false;
                                    }
                                }).show();
                    } else {

                    }
                }
            });
        }

        private void sendFriendRequest() {
            addFriendBtn.setEnabled(false);
            ProfileOperations.sendFriendRequest(loggedInUserId, profileOwnerUserId);
            ProfileOperations.setFriendRequestCallback(new ProfileOperations.SendFriendRequestCallback() {
                @Override
                public void onSuccess(boolean result) {
                    addFriendBtn.setEnabled(true);
                    if (result) {
                        addFriendBtn.setText(context.getResources().getString(R.string.cancel_friend_request));
                        isFriend = true;
                    } else {

                    }
                }

                @Override
                public void onError(int error) {
                    addFriendBtn.setEnabled(true);
                }
            });
        }

        private void cancelDeleteFriendship() {
            addFriendBtn.setEnabled(false);
            ProfileOperations.cancelFriendRequest(loggedInUserId, profileOwnerUserId);
            ProfileOperations.setCancelDeleteFriendRequestCallback(new ProfileOperations.CancelDeleteFriendRequestCallback() {
                @Override
                public void onSuccess(boolean result) {
                    addFriendBtn.setText(context.getString(R.string.add));
                    addFriendBtn.setEnabled(true);
                    isFriend = false;
                }

                @Override
                public void onError(int error) {
                    addFriendBtn.setEnabled(true);
                }
            });
        }

        private void acceptFriendship() {
            addFriendBtn.setEnabled(false);
            ProfileOperations.acceptFriendRequest(loggedInUserId, profileOwnerUserId);
            ProfileOperations.setAcceptCallback(new ProfileOperations.AcceptFriendRequestCallback() {
                @Override
                public void onSuccess(boolean result) {
                    addFriendBtn.setText(context.getString(R.string.remove_friend));
                    addFriendBtn.setEnabled(true);
                    isFriend = false;
                }

                @Override
                public void onError(int error) {
                    addFriendBtn.setEnabled(true);
                }
            });
        }

        private void getFriends(){

        }
    }

    private class ProgressViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.homeProgress);

            progressBar.getIndeterminateDrawable().setColorFilter(context.getResources()
                    .getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);

        }
    }

    private class PostViewHolder extends RecyclerView.ViewHolder {

        PrismojiTextView postTextView;
        Button likeButton;
        Button commentButton;
        TextView dateView;
        TextView usernameView;
        ImageView ppView;
        ImageView postSideMenu;
        Button postShareButton;
        ImageView verifyView;
        TextView atLocation;
        TextView at;
        ImageView image1;
        ImageView image2;
        ImageView image3;
        ImageView image4;
        ImageView image5;

        public PostViewHolder(final View itemView) {
            super(itemView);

            postTextView = itemView.findViewById(R.id.postTextView);
            likeButton = itemView.findViewById(R.id.postLikeButton);
            commentButton = itemView.findViewById(R.id.postCommentButton);
            dateView = itemView.findViewById(R.id.postDateView);
            usernameView = itemView.findViewById(R.id.postUserName);
            ppView = itemView.findViewById(R.id.ppView);
            postSideMenu = itemView.findViewById(R.id.postSideMenu);
            postShareButton = itemView.findViewById(R.id.postShareButton);
            verifyView = itemView.findViewById(R.id.verifyView);
            atLocation = itemView.findViewById(R.id.atLocation);
            at = itemView.findViewById(R.id.at);
            image1 = itemView.findViewById(R.id.postImage1);
            image2 = itemView.findViewById(R.id.postImage2);
            image3 = itemView.findViewById(R.id.postImage3);
            image4 = itemView.findViewById(R.id.postImage4);
            image5 = itemView.findViewById(R.id.postImage5);



        }

        public void bind(final NewsFeedItem item, final int pos) {

            if (item.getItemImage() != null) {
                Picasso.with(context).load(item.getItemImage()).into(ppView);
            } else {
                ppView.setImageDrawable(context.getResources().getDrawable(R.drawable.default_avatar));
            }

            postShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onStartShare(item);
                }
            });

            if (item.getLocation() != null && !item.getLocation().isEmpty()){
                atLocation.setText(item.getLocation());
                atLocation.setVisibility(View.VISIBLE);
                at.setVisibility(View.VISIBLE);
            } else {
                atLocation.setVisibility(View.GONE);
                at.setVisibility(View.GONE);
            }

            if (item.getPostAttachment() != null && item.getPostAttachment().getImages() != null && item.getPostAttachment().getImages().length > 0) {
                if (item.getPostAttachment().getImages().length == 1) {
                    Log.i("IMAGE1_POST_1", "bind: " + item.getPostAttachment().getImages()[0].getPath());
                    image1.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[0].getPath()).into(image1);
                } else if (item.getPostAttachment().getImages().length == 2) {
                    Log.i("IMAGE1", "bind: " + item.getPostAttachment().getImages()[0].getPath());
                    Log.i("IMAGE2", "bind: " + item.getPostAttachment().getImages()[1].getPath());
                    image1.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[0].getPath()).into(image1);
                    image2.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[1].getPath()).into(image2);
                } else if (item.getPostAttachment().getImages().length == 3) {
                    image1.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[0].getPath()).into(image1);
                    image2.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[1].getPath()).into(image2);
                    image3.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[2].getPath()).into(image3);
                } else if (item.getPostAttachment().getImages().length == 4) {
                    image1.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[0].getPath()).into(image1);
                    image2.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[1].getPath()).into(image2);
                    image3.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[2].getPath()).into(image3);
                    image4.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[3].getPath()).into(image4);
                } else if (item.getPostAttachment().getImages().length >= 5) {
                    image1.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[0].getPath()).into(image1);
                    image2.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[1].getPath()).into(image2);
                    image3.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[2].getPath()).into(image3);
                    image4.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[3].getPath()).into(image4);
                    image5.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[4].getPath()).into(image5);
                } else {
                    image1.setVisibility(View.GONE);
                    if (image2 != null) {
                        image2.setVisibility(View.GONE);
                    }

                    if (image3 != null) {
                        image3.setVisibility(View.GONE);
                    }

                    if (image4 != null) {
                        image4.setVisibility(View.GONE);
                    }

                    if (image5 != null) {
                        image5.setVisibility(View.GONE);
                    }
                }
            } else {
                image1.setVisibility(View.GONE);
                if (image2 != null) {
                    image2.setVisibility(View.GONE);
                }

                if (image3 != null) {
                    image3.setVisibility(View.GONE);
                }

                if (image4 != null) {
                    image4.setVisibility(View.GONE);
                }

                if (image5 != null) {
                    image5.setVisibility(View.GONE);
                }
            }

            String postText = null;
            if (item.getOwnerName() != null) {
                usernameView.setText(item.getOwnerName());
            }

            if (item.getCreatedAt() != null) {
                Date date = null;
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).parse(item.getCreatedAt());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long milliseconds = date.getTime();
                long millisecondsFromNow = milliseconds - now;
                dateView.setText(DateUtils.getRelativeDateTimeString(context, milliseconds, millisecondsFromNow, DateUtils.DAY_IN_MILLIS, 0));
            }

            if (!(item.getPostText() == null || item.getPostText().contentEquals("null"))){
                postTextView.setText(item.getPostText());
            } else {
                postTextView.setText("");
            }

            String likeString = context.getResources().getString(R.string.like);
            if (item.getLikes() != null && item.getLikes().length > 0) {

                likeButton.setText(item.getLikes().length + " " + likeString);
                Log.e("post_like ->> " + item.getPostText(), item.getLikes().length + " " + likeString);

                //Log.e("loggedInUserId", loggedInUserId);
                for (int id : item.getLikes()) {
                    //Log.e("likesUserId", String.valueOf(id));
                    if (String.valueOf(id).contentEquals(String.valueOf(loggedInUserId))) {
                        likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_holo_star, 0, 0, 0);
                        break;
                    } else {
                        likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star, 0, 0, 0);
                    }
                }
            } else {
                likeButton.setText(likeString);
                likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star, 0, 0, 0);
            }
            String commentString = context.getResources().getString(R.string.comment);
            if (item.getCommentSize() > 0) {

                commentButton.setText(item.getCommentSize() + " " + commentString);
                Log.e("post_comment ->> " + item.getPostText(), (item.getCommentSize() + " " + commentString));
            } else {
                commentButton.setText(commentString);
            }

            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onStartComment(item, now);
                }
            });

            //todo
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String likeString = context.getResources().getString(R.string.like);

                    if (item.getLikes() != null) {
                        for (int i = 0; i < item.getLikes().length; i++) {
                            if (item.getLikes()[i] == Integer.parseInt(loggedInUserId)) {

                                if (item.getLikes().length > 1) {
                                    likeButton.setText((item.getLikes().length - 1) + " " + likeString);
                                } else {
                                    likeButton.setText(likeString);
                                }
                                likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star, 0, 0, 0);
                                reverseLike(item);
                                return;
                            }
                        }
                    }


                    if (item.getLikes() != null && item.getLikes().length > 0) {
                        likeButton.setText((item.getLikes().length + 1) + " " + likeString);
                    } else {
                        likeButton.setText(("1 " + likeString));
                    }

                    likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_holo_star, 0, 0, 0);
                    performLike(item);

                }
            });

            usernameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getOwnerId().contentEquals(loggedInUserId)) {
                        //todo start profile activity
                    } else {
                        startOtherProfile(item);
                    }
                }
            });

            ppView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getOwnerId().contentEquals(loggedInUserId)) {
                        //todo start profile activity
                    } else {
                        startOtherProfile(item);
                    }
                }
            });

            postSideMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*if (String.valueOf(item.getOwnerId()).contentEquals(String.valueOf(loggedInUserId))) {
                        showPostPopupWindow(postSideMenu, true, item.getId(), item.getOwnerId());
                    } else {
                        showPostPopupWindow(postSideMenu, false, item.getId(), item.getOwnerId());
                    }*/
                    Log.i("invalidPostId", "onClick: "+  item.getId() + "-" + pos);
                    createSheetMenu(item, Integer.parseInt(item.getPostId()), item.getOwnerId(), pos);
                }
            });
        }

        private void startOtherProfile(NewsFeedItem item) {
            Intent intent = Profile.createIntent(item.getOwnerId(), context);
            context.startActivity(intent);
        }

        private void performLike(final NewsFeedItem item) {
            HomeOperations homeOperations = new HomeOperations();
            homeOperations.postLike("add_like", loggedInUserId, item.getOwnerId(), item.getPostId());
            homeOperations.setLikeCallback(new HomeOperations.LikeCallback() {
                @Override
                public void onSuccess() {
                    int[] likes = new int[item.getLikes().length + 1];
                    for (int i = 0; i < item.getLikes().length; i++) {
                        likes[i] = item.getLikes()[i];
                    }

                    likes[likes.length - 1] = Integer.parseInt(loggedInUserId);
                    item.setLikes(likes);
                }

                @Override
                public void onError(int error) {

                }
            });
        }

        private void reverseLike(final NewsFeedItem item) {
            HomeOperations homeOperations = new HomeOperations();
            homeOperations.postUnlike("remove_like", loggedInUserId, item.getOwnerId(), item.getPostId());
            homeOperations.setLikeCallback(new HomeOperations.LikeCallback() {
                @Override
                public void onSuccess() {
                    ArrayList<Integer> likesList = new ArrayList<>();
                    for (int id : item.getLikes()) {
                        if (id != Integer.parseInt(loggedInUserId)) {
                            likesList.add(id);
                        }
                    }

                    int[] likes = new int[likesList.size()];

                    for (int i = 0; i < likesList.size(); i++) {
                        likes[i] = likesList.get(i);
                    }

                    item.setLikes(likes);
                }

                @Override
                public void onError(int error) {

                }
            });
        }
    }

    private class PpViewHolder extends RecyclerView.ViewHolder {

        TextView postTextView;
        Button likeButton;
        Button commentButton;
        TextView dateView;
        TextView usernameView;
        ImageView ppView;
        ImageView postSideMenu;
        Button postShareButton;
        ImageView verifyView;
        ImageView image1;
        TextView ppCoverHint;

        public PpViewHolder(final View itemView) {
            super(itemView);

            postTextView = itemView.findViewById(R.id.postTextView);
            likeButton = itemView.findViewById(R.id.postLikeButton);
            commentButton = itemView.findViewById(R.id.postCommentButton);
            dateView = itemView.findViewById(R.id.postDateView);
            usernameView = itemView.findViewById(R.id.postUserName);
            ppView = itemView.findViewById(R.id.ppView);
            postSideMenu = itemView.findViewById(R.id.postSideMenu);
            postShareButton = itemView.findViewById(R.id.postShareButton);
            verifyView = itemView.findViewById(R.id.verifyView);
            image1 = itemView.findViewById(R.id.postImage1);
            ppCoverHint = itemView.findViewById(R.id.pp_cover_hint);


            postShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    AlertFragment fragment = AlertFragment.createFragment("Post share is coming soon");
//                    fragment.show(context.getFragmentManager(), null);
                }
            });
        }

        public void bind(final NewsFeedItem item, final int pos) {

            if (item.getItemImage() != null) {
                Picasso.with(context).load(item.getItemImage()).into(ppView);
            } else {
                ppView.setImageDrawable(context.getResources().getDrawable(R.drawable.default_avatar));
            }

            if (item.getPpUrl() != null) {
                Picasso.with(context).load(item.getPpUrl()).into(image1);
                image1.setVisibility(View.VISIBLE);
                ppCoverHint.setText(R.string.change_pp);
            }

            if (item.getCoverUrl() != null){
                Picasso.with(context).load(item.getCoverUrl()).into(image1);
                image1.setVisibility(View.VISIBLE);
                ppCoverHint.setText(R.string.change_cover);
            }


            String postText = null;
            if (item.getOwnerName() != null) {
                usernameView.setText(item.getOwnerName());
            }

            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).parse(item.getCreatedAt());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long milliseconds = date.getTime();
            long millisecondsFromNow = milliseconds - now;
            dateView.setText(DateUtils.getRelativeDateTimeString(context, milliseconds, millisecondsFromNow, DateUtils.DAY_IN_MILLIS, 0));

            if (item.getPostText() != null && !item.getPostText().isEmpty()) {
                postTextView.setText(item.getPostText());
            } else {
                postTextView.setText("");
            }


            String likeString = context.getResources().getString(R.string.like);
            if (item.getLikes() != null && item.getLikes().length > 0) {

                likeButton.setText(item.getLikes().length + " " + likeString);
                Log.e("post_like ->> " + item.getPostText(), item.getLikes().length + " " + likeString);

                //Log.e("loggedInUserId", loggedInUserId);
                for (int id : item.getLikes()) {
                    //Log.e("likesUserId", String.valueOf(id));
                    if (String.valueOf(id).contentEquals(String.valueOf(loggedInUserId))) {
                        likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_holo_star, 0, 0, 0);
                        break;
                    } else {
                        likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star, 0, 0, 0);
                    }
                }
            } else {
                likeButton.setText(likeString);
                likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star, 0, 0, 0);
            }
            String commentString = context.getResources().getString(R.string.comment);
            if (item.getCommentSize() > 0) {

                commentButton.setText(item.getCommentSize() + " " + commentString);
                Log.e("post_comment ->> " + item.getPostText(), (item.getCommentSize() + " " + commentString));
            } else {
                commentButton.setText(commentString);
            }

            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onStartComment(item, now);
                }
            });

            //todo
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String likeString = context.getResources().getString(R.string.like);

                    if (item.getLikes() != null) {
                        for (int i = 0; i < item.getLikes().length; i++) {
                            if (item.getLikes()[i] == Integer.parseInt(loggedInUserId)) {

                                if (item.getLikes().length > 1) {
                                    likeButton.setText((item.getLikes().length - 1) + " " + likeString);
                                } else {
                                    likeButton.setText(likeString);
                                }
                                likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star, 0, 0, 0);
                                reverseLike(item);
                                return;
                            }
                        }
                    }


                    if (item.getLikes() != null && item.getLikes().length > 0) {
                        likeButton.setText((item.getLikes().length + 1) + " " + likeString);
                    } else {
                        likeButton.setText(("1 " + likeString));
                    }

                    likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_holo_star, 0, 0, 0);
                    performLike(item);

                }
            });

            usernameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getOwnerId().contentEquals(loggedInUserId)) {
                        //todo start profile activity
                    } else {
                        startProfile(item);
                    }
                }
            });

            ppView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getOwnerId().contentEquals(loggedInUserId)) {
                        //todo start profile activity
                    } else {
                        startProfile(item);
                    }
                }
            });

            postSideMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*if (String.valueOf(item.getOwnerId()).contentEquals(String.valueOf(loggedInUserId))) {
                        showPostPopupWindow(postSideMenu, true, item.getId(), item.getOwnerId());
                    } else {
                        showPostPopupWindow(postSideMenu, false, item.getId(), item.getOwnerId());
                    }*/
                    createSheetMenu(item, Integer.parseInt(item.getPostId()), item.getOwnerId(), pos);
                }
            });
        }

        private void startProfile(NewsFeedItem item) {
            Intent intent = Profile.createIntent(item.getOwnerId(), context);
            context.startActivity(intent);
        }

        private void performLike(final NewsFeedItem item) {
            HomeOperations homeOperations = new HomeOperations();
            homeOperations.postLike("add_like", loggedInUserId, item.getOwnerId(), item.getPostId());
            homeOperations.setLikeCallback(new HomeOperations.LikeCallback() {
                @Override
                public void onSuccess() {
                    int[] likes = new int[item.getLikes().length + 1];
                    for (int i = 0; i < item.getLikes().length; i++) {
                        likes[i] = item.getLikes()[i];
                    }

                    likes[likes.length - 1] = Integer.parseInt(loggedInUserId);
                    item.setLikes(likes);
                }

                @Override
                public void onError(int error) {

                }
            });
        }

        private void reverseLike(final NewsFeedItem item) {
            HomeOperations homeOperations = new HomeOperations();
            homeOperations.postUnlike("remove_like", loggedInUserId, item.getOwnerId(), item.getPostId());
            homeOperations.setLikeCallback(new HomeOperations.LikeCallback() {
                @Override
                public void onSuccess() {
                    ArrayList<Integer> likesList = new ArrayList<>();
                    for (int id : item.getLikes()) {
                        if (id != Integer.parseInt(loggedInUserId)) {
                            likesList.add(id);
                        }
                    }

                    int[] likes = new int[likesList.size()];

                    for (int i = 0; i < likesList.size(); i++) {
                        likes[i] = likesList.get(i);
                    }

                    item.setLikes(likes);
                }

                @Override
                public void onError(int error) {

                }
            });
        }
    }

    private void showPostPopupWindow(View anchor, final boolean owner, final int postId, final String postOwnerId) {
        final ListPopupWindow popupWindow = new ListPopupWindow(context);

        List<MenuCustomItem> itemList = new ArrayList<>();
        if (owner) {
            itemList.add(new MenuCustomItem(context.getResources().getString(R.string.edit), R.drawable.ic_edit));
            itemList.add(new MenuCustomItem(context.getResources().getString(R.string.save_post), R.drawable.ic_save));
            itemList.add(new MenuCustomItem(context.getResources().getString(R.string.remove), R.drawable.ic_remove));
        } else {
            itemList.add(new MenuCustomItem(context.getResources().getString(R.string.save_post), R.drawable.ic_save));
            itemList.add(new MenuCustomItem(context.getResources().getString(R.string.report_post), R.drawable.ic_report));
        }


        ListAdapter adapter = new ListPopupWindowAdapter(context, itemList, R.layout.custom_menu);
        popupWindow.setAnchorView(anchor);
        popupWindow.setAdapter(adapter);
        popupWindow.setWidth(400);
        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (String.valueOf(postOwnerId).contentEquals(String.valueOf(loggedInUserId))) {
                    if (i == 0) {
                        //todo edit post
                    } else if (i == 1) {
                        savePost(postId);
                    } else if (i == 2) {
                        removePost(postId);
                    }
                } else {
                    if (i == 0) {
                        savePost(postId);
                    } else if (i == 1) {
                        reportPost(postId);
                    }
                }
                popupWindow.dismiss();
            }
        });
        popupWindow.show();
    }

    private void createSheetMenu(final NewsFeedItem it, final int postId, final String postOwnerId, final int index) {
        int menu;
        Log.i("ownerAndViewer", "createSheetMenu: " + postOwnerId + "-" + profileOwnerUserId);
        menu = (String.valueOf(postOwnerId).contentEquals(String.valueOf(loggedInUserId))) ? R.menu.profile_menu : R.menu.viewer_post_menu;
        SheetMenu.with(context)
                .setMenu(menu)
                .setAutoCancel(true)
                .setClick(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.save_post_menu:
                                savePost(postId);
                                break;
                            case R.id.edit_post_menu:
                                callback.onStartEditPost(it, index);
                                break;
                            case R.id.remove_post_menu:
                                removePost(postId);
                                break;
                            case R.id.report_post_menu:
                                reportPost(postId);
                                break;
                        }
                        return false;
                    }
                }).show();
    }

    private void removePost(final int postId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://rezetopia.com/Apis/posts/delete/post",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("remove_post", response);
                        for (int i = 0; i < items.size(); i++) {
                            if (items.get(i).getPostId().contentEquals(String.valueOf(postId))
                                    && items.get(i).getType() == NewsFeedItem.POST_TYPE) {
                                items.remove(i);
                                items = new ArrayList<NewsFeedItem>(items);
                                //adapter.notifyDataSetChanged();
                                callback.onItemRemoved(i + 1);
                                break;
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("volley error", "onErrorResponse: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();

                map.put("method", "remove_post");
                map.put("user_id", loggedInUserId);
                map.put("post_id", String.valueOf(postId));

                return map;
            }
        };

        Volley.newRequestQueue(context).add(stringRequest);
    }

    private void savePost(final int postId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://rezetopia.com/Apis/posts/save/post/save",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("save_post", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")){
                                callback.onPostSaved(false);
                            } else {
                                callback.onPostSaved(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("volley error", "onErrorResponse: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();

                map.put("method", "save_post");
                map.put("user_id", loggedInUserId);
                map.put("post_id", String.valueOf(postId));

                return map;
            }
        };

        Volley.newRequestQueue(context).add(stringRequest);
    }

    private void reportPost(final int postId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/reze/user_post.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("report_post", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("volley error", "onErrorResponse: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();

                map.put("method", "report_post");
                map.put("user_id", loggedInUserId);
                map.put("post_id", String.valueOf(postId));

                return map;
            }
        };

        Volley.newRequestQueue(context).add(stringRequest);
    }

    public interface AdapterCallback {
        void onStartComment(NewsFeedItem item, long now);

        void onStartCreatePost();

        void onItemAdded(int position);

        void onItemRemoved(int position);

        void showSnackBar(String message);

        void onStartPickPp();

        void onStartPickCover();

        void onStartEditPost(NewsFeedItem item, int index);

        void onStartShare(NewsFeedItem item);

        void onPostSaved(boolean error);

        void onEmptyFriends();
    }
}