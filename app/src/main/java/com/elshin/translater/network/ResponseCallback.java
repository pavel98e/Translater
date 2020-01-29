package com.elshin.translater.network;

public interface ResponseCallback<R> {
    void onEnd(R apiResponse);
}
