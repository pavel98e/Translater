package com.elshin.translater.network.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LanguagesResponse {

    @SerializedName("dirs")
    List<String> dirs = null;

    public List<String> getDirs() {
        return dirs;
    }
}
