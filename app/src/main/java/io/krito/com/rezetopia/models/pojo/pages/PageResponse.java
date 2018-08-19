package io.krito.com.rezetopia.models.pojo.pages;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.krito.com.rezetopia.models.pojo.friends.Friend;
import io.krito.com.rezetopia.models.pojo.post.Post;
import io.krito.com.rezetopia.models.pojo.post.Pp;

/**
 * Created by Ahmed Ali on 8/11/2018.
 */

public class PageResponse implements Serializable{

    @SerializedName("next_cursor")
    @Expose
    private String nextCursor;

    @SerializedName("error")
    @Expose
    private boolean error;

    @SerializedName("posts")
    @Expose
    private Post[] posts;

    @SerializedName("liked")
    @Expose
    private boolean liked;

    @SerializedName("now")
    @Expose
    private long now;

    @SerializedName("verified")
    @Expose
    private String verified;

    @SerializedName("cover")
    @Expose
    private String cover;

    @SerializedName("profile_picture")
    @Expose
    private String pp;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("about")
    @Expose
    private About about;

    @Expose
    @SerializedName("admins")
    private Admin[] admins;

    public Admin[] getAdmins() {
        return admins;
    }

    public void setAdmins(Admin[] admins) {
        this.admins = admins;
    }

    public About getAbout() {
        return about;
    }

    public void setAbout(About about) {
        this.about = about;
    }

    public String getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(String nextCursor) {
        this.nextCursor = nextCursor;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public Post[] getPosts() {
        return posts;
    }

    public void setPosts(Post[] posts) {
        this.posts = posts;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public long getNow() {
        return now;
    }

    public void setNow(long now) {
        this.now = now;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getPp() {
        return pp;
    }

    public void setPp(String pp) {
        this.pp = pp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
