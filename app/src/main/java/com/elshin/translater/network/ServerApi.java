package com.elshin.translater.network;

import com.elshin.translater.network.pojo.LanguagesResponse;
import com.elshin.translater.network.pojo.TextResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServerApi {
    @GET("getLangs?key=trnsl.1.1.20191218T183120Z.91006b232e1a2619.7dd95af3c748732ac32ec557ff3c3c77641c6be7&ui=ru")
    Call<LanguagesResponse> getLanguages();//дополнение к BASE_URL

    @GET("translate?key=trnsl.1.1.20191218T183120Z.91006b232e1a2619.7dd95af3c748732ac32ec557ff3c3c77641c6be7&lang=ru-en")
    Call<TextResponse> getText(@Query("text") String text, @Query("lang") String lang);//дополнение к BASE_URL
}
