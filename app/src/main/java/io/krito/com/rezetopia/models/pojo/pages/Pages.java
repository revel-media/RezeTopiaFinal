package io.krito.com.rezetopia.models.pojo.pages;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ahmed Ali on 8/11/2018.
 */

public class Pages implements Serializable {

    @Expose
    @SerializedName("error")
    private boolean error;

    @Expose
    @SerializedName("pages")
    private Page[] pages;

    public Page[] getPages() {
        return pages;
    }

    public void setPages(Page[] pages) {
        this.pages = pages;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
