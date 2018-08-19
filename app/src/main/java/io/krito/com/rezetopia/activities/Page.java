package io.krito.com.rezetopia.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.andrognito.flashbar.Flashbar;
import com.andrognito.flashbar.anim.FlashAnimBarBuilder;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Arrays;

import io.krito.com.rezetopia.R;
import io.krito.com.rezetopia.application.AppConfig;
import io.krito.com.rezetopia.application.RezetopiaApp;
import io.krito.com.rezetopia.fragments.Home;
import io.krito.com.rezetopia.fragments.Share;
import io.krito.com.rezetopia.helper.PageRecyclerAdapter;
import io.krito.com.rezetopia.helper.PostRecyclerAdapter;
import io.krito.com.rezetopia.helper.VolleyCustomRequest;
import io.krito.com.rezetopia.models.pojo.news_feed.NewsFeed;
import io.krito.com.rezetopia.models.pojo.news_feed.NewsFeedItem;
import io.krito.com.rezetopia.models.pojo.pages.PageResponse;
import io.krito.com.rezetopia.models.pojo.post.Post;
import io.krito.com.rezetopia.models.pojo.post.PostResponse;

public class Page extends AppCompatActivity {

    private static final int COMMENT_ACTIVITY_RESULT = 1001;
    private static final int CREATE_POST_RESULT = 1002;
    private static final int EDIT_POST_RESULT = 1003;
    private static final int SETTING_RESULT = 1004;


    SwipeRefreshLayout homeSwipeView;
    FrameLayout homeHeader;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ProgressBar progressBar;
    String cursor = "0";
    int pastVisibleItems;
    NewsFeed newsFeed;

    PageResponse page;
    String userId;
    String pageName;
    int pageId;
    PageRecyclerAdapter adapter;

    int start = 0, end = 0;
    boolean loadingData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);

        homeHeader = findViewById(R.id.homeHeader);
        recyclerView = findViewById(R.id.pageRecView);
        progressBar = findViewById(R.id.progressPage);
        homeSwipeView = findViewById(R.id.homeSwipeView);

        userId = getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
                .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, null);

        pageName = getIntent().getExtras().getString("name");
        pageId = getIntent().getExtras().getInt("id");

        if (savedInstanceState != null && savedInstanceState.getSerializable("feed") != null) {

            page = (PageResponse) savedInstanceState.getSerializable("feed");
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    updateUi(0, 0);
                    recyclerView.scrollToPosition(savedInstanceState.getInt("last"));
                    //adapter.notifyDataSetChanged();
                }
            });
        } else {
            fetchPage();
        }

        registerScrollListener();

        homeSwipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = null;
                adapter = null;
                cursor = "0";
                fetchPage();
            }
        });
    }


    private void registerScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
                    int lastVisibleItem = layoutManager.findLastVisibleItemPosition();


                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        //Snackbar.make(homeHeader, R.string.loading, BaseTransientBottomBar.LENGTH_LONG).show();
                        //adapter.notifyItemInserted(adapter.addItem());
                        if (!loadingData) {
                            Log.v("SCROLL_DOWN", "Last Item Wow !");
                            loadingData = true;
                            adapter.addItem();
                            fetchPage();
                        }
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void updateUi(int s, int e) {
        if (adapter == null) {
            adapter = new PageRecyclerAdapter(Page.this, newsFeed.getItems(), newsFeed.getNow(), userId, page, pageName, pageId,
                    new ArrayList<>(Arrays.asList(page.getAdmins())));
            recyclerView.setAdapter(adapter);
            layoutManager = new LinearLayoutManager(Page.this);
            recyclerView.setLayoutManager(layoutManager);

            adapter.setCallback(new PageRecyclerAdapter.AdapterCallback() {
                @Override
                public void onStartComment(NewsFeedItem item, long now) {
                    Intent intent = Comment.createIntent(item.getLikes(), Integer.parseInt(item.getPostId()), now, 0,
                            Page.this);

                    startActivityForResult(intent, COMMENT_ACTIVITY_RESULT);
                    Page.this.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                }

                @Override
                public void onItemAdded(final int position) {
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemInserted(position);
                        }
                    });
                }

                @Override
                public void onItemRemoved(final int position) {
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemRemoved(position);
                        }
                    });
                }

                @Override
                public void onStartShare(NewsFeedItem item) {
                    Share share = Share.createShareFragment(item);
                    share.setShareCallback(item1 -> {
                        adapter.addPostToTop(item);
                    });
                    share.show(Page.this.getFragmentManager(), null);
                }

                @Override
                public void onStartEditPost(NewsFeedItem item, int index) {
                    Intent intent = new Intent(Page.this, EditPost.class);
                    intent.putExtra("item", item);
                    intent.putExtra("index", index);
                    startActivityForResult(intent, EDIT_POST_RESULT);
                }

                @Override
                public void onSetItem(int index) {
                    adapter.notifyItemChanged(index);
                }

                @Override
                public void onPostSaved(boolean error) {
                    String saveMessage = error ? getResources().getString(R.string.post_save_failure) : getResources().getString(R.string.post_save_success);
                    Flashbar.Builder builder = new Flashbar.Builder(Page.this);
                    builder.gravity(Flashbar.Gravity.BOTTOM)
                            .backgroundColor(R.color.red2)
                            .enableSwipeToDismiss()
                            .message(saveMessage)
                            .enterAnimation(new FlashAnimBarBuilder(Page.this).slideFromRight().duration(200))
                            .build().show();
                }

                @Override
                public void onOpenSetting() {
                    Intent intent = new Intent(Page.this, PageSetting.class);
                    intent.putExtra("page_id", pageId);
                    startActivityForResult(intent, SETTING_RESULT);
                }

                @Override
                public void onUpdateHeader() {
                    recyclerView.post(() -> {
                        adapter.notifyDataSetChanged();
                    });
                }

                @Override
                public void onStartCreatePost() {
                    Intent intent = new Intent(Page.this, CreatePost.class);
                    intent.putExtra("url", "https://rezetopia.com/Apis/pages/posts");
                    intent.putExtra("pageId", pageId);
                    startActivityForResult(intent, CREATE_POST_RESULT);
                }
            });

        } else if (s > 0 && e > 0) {
            adapter.notifyItemRangeInserted(s, e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COMMENT_ACTIVITY_RESULT) {
            if (data != null) {
                int postId = data.getIntExtra("post_id", 0);
                int commentSize = data.getIntExtra("added_size", 0);
                adapter.addCommentItem(postId, commentSize);
            }
        } else if (requestCode == CREATE_POST_RESULT) {
            if (data != null) {
                PostResponse returnPost = (PostResponse) data.getSerializableExtra("post");
                if (returnPost != null) {
                    NewsFeedItem item = new NewsFeedItem();
                    item.setPostId(String.valueOf(returnPost.getPostId()));
                    item.setCreatedAt(returnPost.getCreatedAt());
                    item.setOwnerId(returnPost.getUserId());
                    item.setOwnerName(pageName);
                    item.setPostText(returnPost.getText());
                    item.setLocation(returnPost.getLocation());
                    item.setPostAttachment(returnPost.getAttachment());
                    item.setLikes(null);
                    item.setCommentSize(0);
                    item.setType(NewsFeedItem.POST_TYPE);
                    //todo add post to adapter
                    adapter.addPostToTop(item);
                    //adapter.addPostItem(item);
                }
            }
        } else if (requestCode == EDIT_POST_RESULT) {
            NewsFeedItem item = (NewsFeedItem) data.getExtras().getSerializable("item");
            int index = data.getExtras().getInt("index");
            adapter.setItem(item, index);
        } else if (requestCode == SETTING_RESULT){
            if (data != null){
                if (data.getExtras().getString("page_name") != null) {
                    adapter.setPageName(data.getExtras().getString("page_name"));
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("feed", page);
        outState.putInt("last", pastVisibleItems);
    }

    private void fetchPage(){
        Log.i("page_params", "fetchPage: " + pageId + " " + userId + " " + cursor);
        String url = "https://rezetopia.com/Apis/pages/show?page_id=" + pageId + "&user_id=" + userId + "&cursor=" + cursor;
        VolleyCustomRequest request = new VolleyCustomRequest(Request.Method.GET, url, PageResponse.class,
                new Response.Listener<PageResponse>() {
                    @Override
                    public void onResponse(PageResponse response) {
                        Log.i("fetch_page", "onResponse: " + response);

                        if (response != null && !response.isError()){
                            if (page != null){

                            } else {
                                page = response;
                                newsFeed = new NewsFeed();
                                newsFeed.setNextCursor(cursor);
                                newsFeed.setNow(response.getNow());
                                ArrayList<NewsFeedItem> items = new ArrayList<>();
                                if (response.getPosts() != null) {
                                    for (Post post : response.getPosts()) {
                                        NewsFeedItem item = new NewsFeedItem();
                                        item.setPostId(post.getPostId());
                                        item.setCreatedAt(post.getCreatedAt());
                                        item.setCommentSize(post.getCommentSize());
                                        item.setLocation(post.getLocation());
                                        item.setItemImage(post.getImageUrl());
                                        item.setLikes(post.getLikes());
                                        item.setOwnerId(post.getUserId());
                                        item.setOwnerName(post.getUsername());
                                        item.setPostText(post.getText());
                                        item.setPostAttachment(post.getAttachment());
                                        item.setPrivacyId(post.getPrivacyId());
                                        item.setTags(post.getTags());
                                        item.setType(NewsFeedItem.POST_TYPE);
                                        items.add(item);
                                    }

                                    newsFeed.setItems(items);
                                    updateUi(0, 0);
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    if (response.getMessage() != null && !response.getMessage().isEmpty()){

                                    }
                                }
                            }
                            cursor = response.getNextCursor();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("fetch_page", "onErrorResponse: " + error.getMessage());
            }
        });

        RezetopiaApp.getInstance().getRequestQueue().add(request);
    }
}
