package io.krito.com.rezetopia.models.pojo.pages;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ahmed Ali on 8/12/2018.
 */

public class Admins implements Serializable{

    @Expose
    @SerializedName("error")
    private boolean error;


    @Expose
    @SerializedName("admins")
    private Admin[] admins;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public Admin[] getAdmins() {
        return admins;
    }

    public void setAdmins(Admin[] admins) {
        this.admins = admins;
    }
}
