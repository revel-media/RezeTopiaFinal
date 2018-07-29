package io.krito.com.rezetopia.models.pojo.post;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Media implements Serializable {

    public static final int IMAGE_TYPE = 0;
    public static final int VIDEO_TYPE = 1;

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("path")
    @Expose
    private String path;

    @SerializedName("type")
    @Expose
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
