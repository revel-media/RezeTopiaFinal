package io.krito.com.rezetopia.models.pojo.post;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ahmed Ali on 7/22/2018.
 */

public class ShareResponse {

    @SerializedName("error")
    @Expose
    private boolean error;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("image_url")
    @Expose
    private String imageUrl;

    @SerializedName("post_id")
    @Expose
    private int postId;

    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("create_at")
    @Expose
    private String createAt;

    @SerializedName("location")
    @Expose
    private String location;

    @SerializedName("shared_post")
    @Expose
    private Post post;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
