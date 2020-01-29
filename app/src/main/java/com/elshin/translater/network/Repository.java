package com.elshin.translater.network;

import com.elshin.translater.network.pojo.LanguagesResponse;
import com.elshin.translater.network.pojo.TextResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {
    private static Repository mInstance;

    public static Repository getInstance() {
        if (mInstance == null) {
            mInstance = new Repository();
        }
        return mInstance;
    }

    public void getLanguages(final ResponseCallback<LanguagesResponse> responseCallback) {
        NetworkService.getInstance().getJSONApi().getLanguages().enqueue(new Callback<LanguagesResponse>() {//асинхронный запрос на получение списка направлений перевода

            @Override
            public void onResponse(Call<LanguagesResponse> call, Response<LanguagesResponse> response) {
                if (response.isSuccessful()) {
                    LanguagesResponse languagesResponse = response.body();

                    if (languagesResponse != null) {
                        responseCallback.onEnd(languagesResponse);
                    }
                }
            }

            @Override
            public void onFailure(Call<LanguagesResponse> call, Throwable t) {

            }
        });
    }

    public void getText(final ResponseCallback<TextResponse> responseCallback, String sourceText, String lang){
        NetworkService.getInstance().getJSONApi().getText(sourceText, lang).enqueue(new Callback<TextResponse>() {//асинхронный запрос на перевод текста (параметр sourceText - текст, который надо перевести, lang - направление перевода)
            @Override
            public void onResponse(Call<TextResponse> call, Response<TextResponse> response) {
                if (response.isSuccessful()) {
                    TextResponse textResponse = response.body();

                    if (textResponse != null) {
                        responseCallback.onEnd(textResponse);
                    }
                }
            }

            @Override
            public void onFailure(Call<TextResponse> call, Throwable t) {

            }
        });
    }
}
