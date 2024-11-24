package com.prd.quizzoapp.model.service;

import android.util.Log;

import com.prd.quizzoapp.model.dto.QuizRequestDto;
import com.prd.quizzoapp.model.service.intf.ActionCallback;
import com.prd.quizzoapp.model.service.intf.QuizServerService;
import com.prd.quizzoapp.model.service.intf.RetroApiService;
import com.prd.quizzoapp.util.Util;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.sse.EventSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class QuizServerImpl implements QuizServerService {
    private final RetroApiService retroApiService;
    private final String TAG = "QuizServerImpl";
    private EventSource eventSource;

    public QuizServerImpl() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://quizback-590131019426.us-central1.run.app/")
                .client(new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retroApiService = retrofit.create(RetroApiService.class);
    }

    @Override
    public void generateQuestions(QuizRequestDto quizRequestDto,String roomUUID, ActionCallback callback) {
        retroApiService.getQuestions(quizRequestDto,roomUUID).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                }else {
                    callback.onFailure(new Exception("Error al generar las preguntas"));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("QuizServerImpl", "onFailure: en generar las preguntas" + t);
                callback.onFailure(new Exception(t));
            }
        });
    }


    @Override
    public void deleteRoomSse(String roomUUID, ActionCallback callback) {
        retroApiService.deleteRoomSse(roomUUID).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    System.out.println(response.body().toString());
                    Log.d("QuizServerImpl", "onResponse: en eliminar la sala");
                    callback.onSuccess();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Util.showLog("QuizServerImpl", "onFailure: en eliminar la sala" + t);
                callback.onFailure(new Exception(t));
            }
        });
    }
}
