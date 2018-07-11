package io.krito.com.rezetopia.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrognito.flashbar.Flashbar;
import com.andrognito.flashbar.anim.FlashAnim;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import id.arieridwan.lib.PageLoader;
import io.krito.com.rezetopia.R;
import io.krito.com.rezetopia.application.AppConfig;
import io.krito.com.rezetopia.application.RezetopiaApp;
import io.krito.com.rezetopia.models.pojo.news_feed.NewsFeedItem;
import io.krito.com.rezetopia.models.pojo.post.Attachment;
import io.krito.com.rezetopia.models.pojo.post.Media;
import io.krito.com.rezetopia.models.pojo.post.PostResponse;
import io.krito.com.rezetopia.receivers.ConnectivityReceiver;
import ru.whalemare.sheetmenu.SheetMenu;

public class EditPost extends AppCompatActivity implements View.OnClickListener {

    private String userId;
    private NewsFeedItem item;
    private int index;
    String privacy;
    Boolean changed;

    TextView createPost;
    TextView privacyText;
    ImageView privacyIcon;
    ImageView back;
    EditText postText;
    PageLoader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        userId = getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, null);

        item = (NewsFeedItem) getIntent().getExtras().getSerializable("item");
        index = getIntent().getExtras().getInt("index");

        createPost = findViewById(R.id.createPostView);
        privacyText = findViewById(R.id.privacy);
        privacyIcon = findViewById(R.id.privacyIcon);
        back = findViewById(R.id.backView);
        postText = findViewById(R.id.postText);
        loader = findViewById(R.id.pageLoader);

        postText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    changed = true;
                    item.setPostText(s.toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (item.getPostText() != null && !item.getPostText().isEmpty()){
            postText.setText(item.getPostText());
        }


        switch (item.getPrivacyId()){
            case 1:
                privacy = getResources().getString(R.string.public_);
                privacyText.setText(R.string.public_);
                privacyIcon.setBackground(getResources().getDrawable(R.drawable.earth));
                break;
            case 2:
                privacy = getResources().getString(R.string.friends);
                privacyText.setText(R.string.friends);
                privacyIcon.setBackground(getResources().getDrawable(R.drawable.account_check));
                break;
            case 3:
                privacy = getResources().getString(R.string.friends_of_friends);
                privacyText.setText(R.string.friends_of_friends);
                privacyIcon.setBackground(getResources().getDrawable(R.drawable.account_multiple_check));
                break;
            case 4:
                privacy = getResources().getString(R.string.only_me);
                privacyText.setText(R.string.only_me);
                privacyIcon.setBackground(getResources().getDrawable(R.drawable.lock));
                break;
        }


        createPost.setOnClickListener(this);
        privacyText.setOnClickListener(this);
        privacyIcon.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.createPostView:
                if (ConnectivityReceiver.isConnected(EditPost.this)) {
                    if (validPost()) {
                        loader.startProgress();
                        editPost();
                    } else {
                        new Flashbar.Builder(EditPost.this)
                                .gravity(Flashbar.Gravity.TOP)
                                .duration(500)
                                .message(R.string.empty_post)
                                .messageTypeface(Typeface.createFromAsset(getAssets(), "CoconNextArabic-Regular.otf"))
                                .backgroundColorRes(R.color.colorPrimaryDark)
                                .enterAnimation(FlashAnim.with(EditPost.this)
                                        .animateBar()
                                        .duration(750)
                                        .alpha()
                                        .overshoot())
                                .exitAnimation(FlashAnim.with(EditPost.this)
                                        .animateBar()
                                        .duration(400)
                                        .accelerateDecelerate())
                                .build();
                    }
                }
                break;
            case R.id.privacyIcon:
                createSheet();
                break;
            case R.id.privacy:
                createSheet();
                break;
        }
    }


    private boolean validPost() {
        return changed;
    }

    private void editPost() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/reze/user_post.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("EditPostResponse", "onResponse: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")) {
                                Intent intent = new Intent();
                                intent.putExtra("item", item);
                                intent.putExtra("index", index);
                                setResult(RESULT_OK, intent);
                                onBackPressed();
                            } else {
                                loader.stopProgress();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.i("volley error", "onErrorResponse: " + error.getMessage());
                loader.stopProgress();
                if (error instanceof NetworkError) {
                    Log.i("CreateError", getResources().getString(R.string.connection_error));
                } else if (error instanceof ServerError) {
                    Log.i("CreateError", getResources().getString(R.string.server_error));
                } else if (error instanceof AuthFailureError) {
                    Log.i("CreateError", getResources().getString(R.string.connection_error));
                } else if (error instanceof ParseError) {
                    Log.i("CreateError", getResources().getString(R.string.parsing_error));
                } else if (error instanceof NoConnectionError) {
                    Log.i("CreateError", getResources().getString(R.string.connection_error));
                } else if (error instanceof TimeoutError) {
                    Log.i("CreateError", getResources().getString(R.string.time_out));
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();

                map.put("method", "edit_post");
                map.put("user_id", userId);
                map.put("post_id", item.getPostId());
                if (postText.getText().toString().length() > 0) {
                    map.put("text", postText.getText().toString());
                }

                if (item.getPrivacyId() > 0) {
                    map.put("privacy_id", String.valueOf(item.getPrivacyId()));
                }

                Log.i("EditData", "getParams: " + userId + " - " + item.getPostId() + " - " + postText.getText().toString() + " - " + item.getPrivacyId());

                return map;
            }
        };

        RezetopiaApp.getInstance().getRequestQueue().add(stringRequest);
    }

    private void createSheet() {
        SheetMenu.with(this)
                .setMenu(R.menu.post_privacy_menu)
                .setAutoCancel(true)
                .setClick(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem it) {
                        switch (it.getItemId()) {
                            case R.id.publicId:
                                privacy = "public";
                                privacyText.setText(R.string.public_);
                                privacyIcon.setBackground(getResources().getDrawable(R.drawable.earth));
                                item.setPrivacyId(1);
                                break;
                            case R.id.friendsId:
                                privacy = "friends";
                                privacyText.setText(R.string.friends);
                                privacyIcon.setBackground(getResources().getDrawable(R.drawable.account_check));
                                item.setPrivacyId(2);
                                break;
                            case R.id.friends_of_friendsId:
                                privacy = "friends_of_friends";
                                privacyText.setText(R.string.friends_of_friends);
                                privacyIcon.setBackground(getResources().getDrawable(R.drawable.account_multiple_check));
                                item.setPrivacyId(3);
                                break;
                            case R.id.only_meId:
                                privacy = "only_me";
                                privacyText.setText(R.string.only_me);
                                privacyIcon.setBackground(getResources().getDrawable(R.drawable.lock));
                                item.setPrivacyId(4);
                                break;
                        }
                        return false;
                    }
                }).show();
    }
}
