package com.elshin.translater.network.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TextResponse {

    @SerializedName("text")
    @Expose
    private List<String> text = null;

    public List<String> getText() {
        return text;
    }
}
