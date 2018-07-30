package io.krito.com.rezetopia.models.pojo.friends;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Mona Abdallh on 7/23/2018.
 */

public class FriendsResponse implements Serializable {

    @SerializedName("error")
    @Expose
    private boolean error;

    @SerializedName("friends")
    @Expose
    private Friend[] friends;

    @SerializedName("next_cursor")
    @Expose
    private String nextCursor;

    @SerializedName("message")
    @Expose
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public Friend[] getFriends() {
        return friends;
    }

    public void setFriends(Friend[] friends) {
        this.friends = friends;
    }

    public String getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(String nextCursor) {
        this.nextCursor = nextCursor;
    }
}
