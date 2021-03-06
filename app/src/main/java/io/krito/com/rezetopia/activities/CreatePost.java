package io.krito.com.rezetopia.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.andrognito.flashbar.Flashbar;
import com.andrognito.flashbar.anim.FlashAnim;
import com.andrognito.flashbar.anim.FlashAnimBarBuilder;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.apradanas.prismoji.PrismojiEditText;
import com.apradanas.prismoji.PrismojiPopup;
import com.apradanas.prismoji.listeners.OnSoftKeyboardCloseListener;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.squareup.picasso.Picasso;
import com.tangxiaolv.telegramgallery.GalleryActivity;
import com.tangxiaolv.telegramgallery.GalleryConfig;
import com.taskail.googleplacessearchdialog.SimplePlacesSearchDialog;
import com.taskail.googleplacessearchdialog.SimplePlacesSearchDialogBuilder;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
//import com.taskail.googleplacessearchdialog.SimplePlacesSearchDialog;l
//import com.taskail.googleplacessearchdialog.SimplePlacesSearchDialogBuilder;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import id.arieridwan.lib.PageLoader;
import io.krito.com.rezetopia.R;
import io.krito.com.rezetopia.application.AppConfig;
import io.krito.com.rezetopia.application.RezetopiaApp;
import io.krito.com.rezetopia.helper.EncodeBase64;
import io.krito.com.rezetopia.helper.ListPopupWindowAdapter;
import io.krito.com.rezetopia.helper.MenuCustomItem;
import io.krito.com.rezetopia.helper.Upload;
import io.krito.com.rezetopia.helper.VolleyMultipartRequest;
import io.krito.com.rezetopia.models.pojo.friends.Friend;
import io.krito.com.rezetopia.models.pojo.post.Attachment;
import io.krito.com.rezetopia.models.pojo.post.Media;
import io.krito.com.rezetopia.models.pojo.post.PostResponse;
import io.krito.com.rezetopia.receivers.ConnectivityReceiver;
import io.krito.com.rezetopia.views.CustomTextView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.whalemare.sheetmenu.SheetMenu;

public class CreatePost extends AppCompatActivity implements View.OnClickListener {

    private static final int TYPE_TEXT = 0;
    private static final int TYPE_VIDEO = 1;
    private static final int TYPE_TEXT_VIDEO = 2;
    private static final int PICK_IMAGE_REQUEST_CODE = 1006;
    private static final int TAGS_REQUEST_CODE = 2009;

    TextView createPost;
    TextView privacyText;
    ImageView privacyIcon;
    ImageView back;
    PrismojiEditText postText;
    TextView image;
    //CustomTextView video;
    TextView location;
    TextView tag;
    PageLoader loader;
    RecyclerView imagesVideoRecView;
    RelativeLayout createPostNav;
    ImageView emoView;
    Button postButton;
    TextView actualLocationView;
    FrameLayout deleteLocationView;
    FrameLayout createPostRoot;
    RecyclerView addedTagsRecyclerView;


    private String decodedVideo;
    private String userId;
    private List<String> selectedImages;
    private String selectedVideo;
    List<MediaType> media;
    private ArrayList<String> encodedImages;
    private RecyclerView.Adapter imageVideoAdapter;
    private int postType;
    private String privacy = "public";
    private StringRequest stringRequest;
    private PrismojiPopup prismojiPopup;
    private ArrayList<Friend> tags;
    private AddedTagsRecyclerAdapter tagsRecyclerAdapter;
    private String apiUrl;
    private int pageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        networkListener();
        
        userId = getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, null);

        createPost = findViewById(R.id.createPostView);
        privacyText = findViewById(R.id.privacy);
        privacyIcon = findViewById(R.id.privacyIcon);
        back = findViewById(R.id.backView);
        postText = findViewById(R.id.postText);
        image = findViewById(R.id.pickImageVideo);
        //video = findViewById(R.id.pickVideo);
        location = findViewById(R.id.pickLocation);
        tag = findViewById(R.id.tagFriend);
        loader = findViewById(R.id.pageLoader);
        imagesVideoRecView = findViewById(R.id.imagesRecView);
        createPostNav = findViewById(R.id.createPostNav);
        emoView = findViewById(R.id.emoView);
        postButton = findViewById(R.id.postButton);
        actualLocationView = findViewById(R.id.actualLocationView);
        deleteLocationView = findViewById(R.id.deleteLocationView);
        createPostRoot = findViewById(R.id.createPostRoot);
        addedTagsRecyclerView = findViewById(R.id.tagRecView);

        if (getIntent().getExtras() != null) {
            apiUrl = getIntent().getExtras().getString("url", null);
            pageId = getIntent().getExtras().getInt("pageId", 0);
        }

        createPost.setOnClickListener(this);
        privacyText.setOnClickListener(this);
        privacyIcon.setOnClickListener(this);
        back.setOnClickListener(this);
        image.setOnClickListener(this);
        emoView.setOnClickListener(this);
        postButton.setOnClickListener(this);
        location.setOnClickListener(this);
        deleteLocationView.setOnClickListener(this);
        postText.setOnClickListener(this);
        tag.setOnClickListener(this);

//        emojiPopup = EmojiPopup.Builder.fromRootView(createPostRoot).build(postText);
        prismojiPopup = PrismojiPopup.Builder
                .fromRootView(createPostRoot)
                .into(postText)
                .setOnSoftKeyboardCloseListener(new OnSoftKeyboardCloseListener() {
                    @Override
                    public void onKeyboardClose() {
                        prismojiPopup.dismiss();
                    }
                })
                .build();
    }

    private void showPostPopupWindow(View anchor) {
        final ListPopupWindow popupWindow = new ListPopupWindow(this);

        final List<MenuCustomItem> itemList = new ArrayList<>();
        itemList.add(new MenuCustomItem(getResources().getString(R.string.public_), R.drawable.ic_earth_grey600_24dp));
        itemList.add(new MenuCustomItem(getResources().getString(R.string.friends), R.drawable.ic_account_check_grey600_24dp));
        itemList.add(new MenuCustomItem(getResources().getString(R.string.friends_of_friends), R.drawable.ic_account_multiple_check_grey600_24dp));
        itemList.add(new MenuCustomItem(getResources().getString(R.string.only_me), R.drawable.ic_lock_grey600_24dp));


        ListAdapter adapter = new ListPopupWindowAdapter(this, itemList, R.layout.custom_menu);
        popupWindow.setAnchorView(anchor);
        popupWindow.setAdapter(adapter);
        popupWindow.setWidth(500);
        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    privacy = "public";
                    privacyText.setText(R.string.public_);
                    privacyIcon.setBackground(getResources().getDrawable(R.drawable.earth));
                } else if (i == 1) {
                    privacy = "friends";
                    privacyText.setText(R.string.friends);
                    privacyIcon.setBackground(getResources().getDrawable(R.drawable.account_check));
                } else if (i == 2) {
                    privacy = "friends_of_friends";
                    privacyText.setText(R.string.friends_of_friends);
                    privacyIcon.setBackground(getResources().getDrawable(R.drawable.account_multiple_check));
                } else if (i == 3) {
                    privacy = "only_me";
                    privacyText.setText(R.string.only_me);
                    privacyIcon.setBackground(getResources().getDrawable(R.drawable.lock));
                }


                popupWindow.dismiss();
            }
        });
        popupWindow.show();
    }

    private void createSheet() {
        SheetMenu.with(this)
                .setMenu(R.menu.post_privacy_menu)
                .setAutoCancel(true)
                .setClick(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.publicId:
                                privacy = "public";
                                privacyText.setText(R.string.public_);
                                privacyIcon.setBackground(getResources().getDrawable(R.drawable.earth));
                                break;
                            case R.id.friendsId:
                                privacy = "friends";
                                privacyText.setText(R.string.friends);
                                privacyIcon.setBackground(getResources().getDrawable(R.drawable.account_check));
                                break;
                            case R.id.friends_of_friendsId:
                                privacy = "friends_of_friends";
                                privacyText.setText(R.string.friends_of_friends);
                                privacyIcon.setBackground(getResources().getDrawable(R.drawable.account_multiple_check));
                                break;
                            case R.id.only_meId:
                                privacy = "only_me";
                                privacyText.setText(R.string.only_me);
                                privacyIcon.setBackground(getResources().getDrawable(R.drawable.lock));
                                break;
                        }
                        return false;
                    }
                }).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createPostView:
                if (ConnectivityReceiver.isConnected(CreatePost.this)) {
//                    if (selectedVideo != null && !selectedVideo.isEmpty()){
//                        if (decodeVideo()){
//                            loader.startProgress();
//                            performUserUpload();
//                            return;
//                        }
//                    }
                    if (validPost()) {
                        loader.startProgress();
                        performUserUpload();
                    } else {
                        new Flashbar.Builder(CreatePost.this)
                                .gravity(Flashbar.Gravity.TOP)
                                .duration(500)
                                .message(R.string.empty_post)
                                .messageTypeface(Typeface.createFromAsset(getAssets(), "CoconNextArabic-Regular.otf"))
                                .backgroundColorRes(R.color.colorPrimaryDark)
                                .enterAnimation(FlashAnim.with(CreatePost.this)
                                        .animateBar()
                                        .duration(750)
                                        .alpha()
                                        .overshoot())
                                .exitAnimation(FlashAnim.with(CreatePost.this)
                                        .animateBar()
                                        .duration(400)
                                        .accelerateDecelerate())
                                .build();
                    }
                } else {
                    new Flashbar.Builder(CreatePost.this)
                            .gravity(Flashbar.Gravity.TOP)
                            .duration(500)
                            .message(R.string.connection_error)
                            .messageTypeface(Typeface.createFromAsset(getAssets(), "CoconNextArabic-Regular.otf"))
                            .backgroundColorRes(R.color.colorPrimaryDark)
                            .enterAnimation(FlashAnim.with(CreatePost.this)
                                    .animateBar()
                                    .duration(750)
                                    .alpha()
                                    .overshoot())
                            .exitAnimation(FlashAnim.with(CreatePost.this)
                                    .animateBar()
                                    .duration(400)
                                    .accelerateDecelerate())
                            .build();
                }
                break;
            case R.id.privacy:
                createSheet();
                //showPostPopupWindow(privacyText);
                break;
            case R.id.privacyIcon:
                createSheet();
                //showPostPopupWindow(privacyText);
                break;
            case R.id.backView:
                onBackPressed();
                break;
            case R.id.pickImageVideo:
                openImagePicker();
                break;
//            case R.id.pickVideo:
//                break;
            case R.id.pickLocation:
                SimplePlacesSearchDialog searchDialog = new SimplePlacesSearchDialogBuilder(this)
                        .setLocationListener(place -> {
                            actualLocationView.setText(place.getName());
                            deleteLocationView.setVisibility(View.VISIBLE);
                        }).build();

                searchDialog.show();
                break;
            case R.id.deleteLocationView:
                actualLocationView.setText("");
                deleteLocationView.setVisibility(View.GONE);
                break;
            case R.id.tagFriend:
                Intent intent = new Intent(CreatePost.this, FriendsTag.class);
                startActivityForResult(intent, TAGS_REQUEST_CODE);
                break;
            case R.id.emoView:
                if (prismojiPopup.isShowing()){
                    prismojiPopup.dismiss();
                } else {
                    prismojiPopup.toggle();
                }
                break;
            case R.id.postButton:
                if (ConnectivityReceiver.isConnected(CreatePost.this)) {
                    if (validPost()) {
                        loader.startProgress();
                        //performUserUpload();
                        createPostMultiPart();
                    } else {
                        new Flashbar.Builder(CreatePost.this)
                                .gravity(Flashbar.Gravity.TOP)
                                .duration(500)
                                .message(R.string.empty_post)
                                .messageTypeface(Typeface.createFromAsset(getAssets(), "CoconNextArabic-Regular.otf"))
                                .backgroundColorRes(R.color.colorPrimaryDark)
                                .enterAnimation(FlashAnim.with(CreatePost.this)
                                        .animateBar()
                                        .duration(750)
                                        .alpha()
                                        .overshoot())
                                .exitAnimation(FlashAnim.with(CreatePost.this)
                                        .animateBar()
                                        .duration(400)
                                        .accelerateDecelerate())
                                .build();
                    }
                } else {
                    new Flashbar.Builder(CreatePost.this)
                            .gravity(Flashbar.Gravity.TOP)
                            .duration(500)
                            .message(R.string.connection_error)
                            .messageTypeface(Typeface.createFromAsset(getAssets(), "CoconNextArabic-Regular.otf"))
                            .backgroundColorRes(R.color.colorPrimaryDark)
                            .enterAnimation(FlashAnim.with(CreatePost.this)
                                    .animateBar()
                                    .duration(750)
                                    .alpha()
                                    .overshoot())
                            .exitAnimation(FlashAnim.with(CreatePost.this)
                                    .animateBar()
                                    .duration(400)
                                    .accelerateDecelerate())
                            .build();
                }
                break;

            default:
                break;
        }
    }

    private boolean validPost() {
        if (!postText.getText().toString().isEmpty() || (selectedImages != null && selectedImages.size() > 0)
                || (selectedVideo != null && !selectedVideo.isEmpty())) {
            postType = TYPE_TEXT;

            if (selectedVideo != null && !selectedVideo.isEmpty()) {
                postType = TYPE_VIDEO;
            }

            if (selectedVideo != null && !selectedVideo.isEmpty() && !postText.getText().toString().isEmpty()) {
                postType = TYPE_TEXT_VIDEO;
            }

            return true;
        } else if (postText.getText().toString().isEmpty()) {
            Snackbar.make(createPostNav, R.string.empty_post, BaseTransientBottomBar.LENGTH_SHORT).show();
            return false;
        }

        return false;
    }

    private void openImagePicker() {
        GalleryConfig config = new GalleryConfig.Build()
                .singlePhoto(false)
                .hintOfPick("Pick image/s")
                //.filterMimeTypes(new String[]{"image/jpeg"})
                .build();
        GalleryActivity.openActivity(CreatePost.this, PICK_IMAGE_REQUEST_CODE, config);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST_CODE && data != null) {
            selectedImages = (List<String>) data.getSerializableExtra(GalleryActivity.PHOTOS);
            selectedVideo = data.getExtras().getString(GalleryActivity.VIDEO);
            media = new ArrayList<>();

            if (selectedImages != null && selectedImages.size() > 0) {
                Log.i("ImageSize", "onActivityResult: " + selectedImages.size());
                imagesVideoRecView.setVisibility(View.VISIBLE);
                for (String path : selectedImages) {
                    MediaType mediaType = new MediaType();
                    mediaType.path = path;
                    mediaType.type = MediaType.IMAGE_TYPE;
                    media.add(mediaType);
                }
            }

            if (selectedVideo != null && !selectedVideo.isEmpty()) {
                imagesVideoRecView.setVisibility(View.VISIBLE);
                Log.i("videoPath", "onActivityResult: " + selectedVideo);
                MediaType mediaType = new MediaType();
                mediaType.path = selectedVideo;
                mediaType.type = MediaType.VIDEO_TYPE;
                media.add(mediaType);
            }

            if (media != null && media.size() > 0) {
                updateImageHolder();
            }
        } else if (requestCode == TAGS_REQUEST_CODE && data != null){
            if (data.getSerializableExtra("tags") != null) {
                tags = (ArrayList<Friend>) data.getSerializableExtra("tags");
                if (tags != null && tags.size() > 0){
                    updateAddedTagsList();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void performUserUpload() {
        encodedImages = new ArrayList<>();
        if (selectedImages != null) {
            if (selectedImages.size() > 0) {
                for (String path : selectedImages) {
                    Bitmap bm = null;
                    bm = BitmapFactory.decodeFile(path);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    String encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                    encodedImages.add(encodedImage);
                }
            }
        }

        createPostMultiPart();

        /*if (postType == TYPE_TEXT) {
            //createPostMultiPart();
            createPost();
            RezetopiaApp.getInstance().getRequestQueue().add(stringRequest);
        } else
            createPostMultiPart();
        //uploadVideo(postText.getText().toString());
        //uploadMultipart(postText.getText().toString());*/
    }

    private void createPostMultiPart() {
        String url = (apiUrl == null || apiUrl.isEmpty()) ? "https://rezetopia.com/Apis/posts" : apiUrl;
        VolleyMultipartRequest request = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        PostResponse postResponse = new PostResponse();
                        try {
                            String utf8String = new String(response.data, "UTF-8");
                            Log.i("responseString", "onResponse: " + utf8String);
                            JSONObject jsonObject = new JSONObject(utf8String);

                            if (jsonObject.getInt("post_id") > 0) {
                                postResponse.setUserId(userId);
                                postResponse.setPostId(jsonObject.getInt("post_id"));
                                postResponse.setUsername(jsonObject.getString("username"));
                                postResponse.setCreatedAt(jsonObject.getString("created_at"));
                                postResponse.setLocation(jsonObject.getString("location"));
                                if (jsonObject.getString("text") != null && !jsonObject.getString("text").isEmpty()) {
                                    postResponse.setText(jsonObject.getString("text"));
                                }

                                Attachment attachmentResponse = new Attachment();
                                if (jsonObject.getJSONArray("image_url") != null && jsonObject.getJSONArray("image_url").length() > 0) {
                                    JSONArray urls = jsonObject.getJSONArray("image_url");

                                    Media[] mediaArray = new Media[urls.length()];
                                    for (int i = 0; i < urls.length(); i++) {
                                        Log.i("CreatePostResponse", "onResponse: " + urls.getString(i));
                                        Media media = new Media();
                                        media.setPath(urls.getString(i));
                                        mediaArray[i] = media;
                                    }
                                    attachmentResponse.setImages(mediaArray);
                                    postResponse.setAttachment(attachmentResponse);
                                }

                                if (jsonObject.getString("video_url") != null && jsonObject.getString("video_url").length() > 0){
                                    Media media = new Media();
                                    media.setPath(jsonObject.getString("video_url"));
                                    attachmentResponse.setVideos(new Media[]{media});
                                    postResponse.setAttachment(attachmentResponse);
                                }
                            } else {
                                loader.stopProgress();
                            }

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (postResponse.getPostId() > 0) {
                            Intent intent = new Intent();
                            intent.putExtra("post", postResponse);
                            setResult(RESULT_OK, intent);
                            onBackPressed();
                        } else {
                            loader.stopProgress();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("responseStringError", "onResponse: " + error.getMessage());
                loader.stopProgress();

                if (error instanceof NetworkError) {
                    //message = String.valueOf();
                    Log.i("uploadError", "onErrorResponse: " + getResources().getString(R.string.checkingNetwork));
                } else if (error instanceof ServerError) {
                    Log.i("uploadError", "onErrorResponse: " + getResources().getString(R.string.server_error));
                } else if (error instanceof AuthFailureError) {
                    Log.i("uploadError", "onErrorResponse: " + getResources().getString(R.string.connection_error));
                } else if (error instanceof ParseError) {
                    Log.i("uploadError", "onErrorResponse: " + getResources().getString(R.string.parsing_error));
                } else if (error instanceof NoConnectionError) {
                    Log.i("uploadError", "onErrorResponse: " + getResources().getString(R.string.connection_error));
                } else if (error instanceof TimeoutError) {
                    Log.i("uploadError", "onErrorResponse: " + getResources().getString(R.string.time_out));
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Log.i("uploadTest", "getParams: data");
                Map<String, String> params = new HashMap<>();
                params.put("method", "multipart_image_post");
                if (apiUrl != null && !apiUrl.isEmpty()){
                    params.put("page_id", String.valueOf(pageId));
                }

                params.put("userId", userId);
                //params.put("description", postText.getText().toString());
                try {
                    params.put("description", URLEncoder.encode(postText.getText().toString(), "UTF-8"));
                    Log.i("postDescriptionEncode", "getParams: " + URLEncoder.encode(postText.getText().toString(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                params.put("privacy", privacy);
                if (actualLocationView.getText() != null && !actualLocationView.getText().toString().isEmpty()){
                    try {
                        params.put("location", URLEncoder.encode(actualLocationView.getText().toString(), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                if (selectedImages != null && selectedImages.size() > 0){
                    params.put("imageSize", String.valueOf(selectedImages.size()));

                }

                if (tags != null && tags.size() > 0){
                    params.put("tags_size", String.valueOf(tags.size()));

                    for (int i=0; i < tags.size(); i++){
                        params.put("tag" + i, String.valueOf(tags.get(i).getId()));
                    }
                }

                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Log.i("uploadTest", "getParams: bytes");
                Map<String, DataPart> params = new HashMap<>();
                if (selectedVideo != null){
                    params.put("video", new DataPart("video.mp4", convert(selectedVideo), "video/mp4"));
                }

                if (selectedImages != null){
                    for (int i = 0; i < selectedImages.size(); i++) {
                        params.put("image" + i, new DataPart("image" + i + ".jpg", convert(selectedImages.get(i)), "image/jpeg"));
                    }
                }

                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RezetopiaApp.getInstance().getRequestQueue().add(request);
    }

    private class ImageVideoViewHolder extends RecyclerView.ViewHolder {

        ImageView close;
        ImageView image;
        VideoView video;

        public ImageVideoViewHolder(View itemView) {
            super(itemView);

            close = itemView.findViewById(R.id.closeView);
            image = itemView.findViewById(R.id.imageView);
            video = itemView.findViewById(R.id.videoView);
        }

        public void bind(final MediaType i) {
            if (i.type == MediaType.IMAGE_TYPE) {
                video.setVisibility(View.GONE);
                Bitmap bm = BitmapFactory.decodeFile(i.path);
                image.setVisibility(View.VISIBLE);
                image.setImageBitmap(bm);
            } else {
                image.setVisibility(View.GONE);
                video.setVisibility(View.VISIBLE);
                video.setVideoPath(i.path);
                video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setVolume(0, 0);
                    }
                });
                video.start();

            }

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    media.remove(i);
                    imageVideoAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private class ImageVideoRecyclerAdapter extends RecyclerView.Adapter<ImageVideoViewHolder> {

        @NonNull
        @Override
        public ImageVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(CreatePost.this).inflate(R.layout.image_card, parent, false);
            return new ImageVideoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageVideoViewHolder holder, int position) {
            holder.bind(media.get(position));
        }

        @Override
        public int getItemCount() {
            return media.size();
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
                tags.remove(pos);
                updateAddedTagsList();

                if (tags.size() == 0){
                    addedTagsRecyclerView.setVisibility(View.GONE);
                }
            });
        }
    }

    private class AddedTagsRecyclerAdapter extends RecyclerView.Adapter<AddedTagsViewHolder>{

        @NonNull
        @Override
        public AddedTagsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(CreatePost.this).inflate(R.layout.added_tags_card, parent, false);
            return new AddedTagsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AddedTagsViewHolder holder, int position) {
            holder.bind(tags.get(position), position);
        }

        @Override
        public int getItemCount() {
            return tags.size();
        }
    }

    private void updateImageHolder() {
        if (imageVideoAdapter == null) {
            imageVideoAdapter = new ImageVideoRecyclerAdapter();
            imagesVideoRecView.setAdapter(imageVideoAdapter);
            imagesVideoRecView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        } else {
            imageVideoAdapter.notifyDataSetChanged();
        }
    }

    private class MediaType {
        public static final int IMAGE_TYPE = 0;
        public static final int VIDEO_TYPE = 1;

        public int type;
        public String path;
    }

    public byte[] convert(String path) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];

        try {
            for (int readNum; (readNum = fis.read(b)) != -1; ) {
                bos.write(b, 0, readNum);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] bytes = bos.toByteArray();

        return bytes;
    }

    private void networkListener(){
        ReactiveNetwork.observeNetworkConnectivity(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(connectivity -> {
                    if (connectivity.getState() == NetworkInfo.State.CONNECTED){
                        Log.i("internetC", "onNext: " + "Connected");
                    } else if (connectivity.getState() == NetworkInfo.State.SUSPENDED){
                        Log.i("internetC", "onNext: " + "LowNetwork");
                    } else {
                        Log.i("internetC", "onNext: " + "NoInternet");
                        Flashbar.Builder builder = new Flashbar.Builder(this);
                        builder.gravity(Flashbar.Gravity.BOTTOM)
                                .backgroundColor(R.color.red2)
                                .enableSwipeToDismiss()
                                .message(R.string.checkingNetwork)
                                .enterAnimation(new FlashAnimBarBuilder(CreatePost.this).slideFromRight().duration(200))
                                .build().show();
                    }
                });
    }

    private void updateAddedTagsList(){
        if (tagsRecyclerAdapter == null) {
            addedTagsRecyclerView.setVisibility(View.VISIBLE);
            tagsRecyclerAdapter = new AddedTagsRecyclerAdapter();
            addedTagsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            addedTagsRecyclerView.setAdapter(tagsRecyclerAdapter);
        } else {
            if (tags.size() > 0){
                addedTagsRecyclerView.setVisibility(View.VISIBLE);
            }
            tagsRecyclerAdapter.notifyDataSetChanged();
        }
    }
}
