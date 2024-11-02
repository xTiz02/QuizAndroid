package com.prd.quizzoapp.model.service;

import android.util.Log;

import com.prd.quizzoapp.model.dto.QuizRequestDto;
import com.prd.quizzoapp.model.entity.Question;
import com.prd.quizzoapp.model.service.intf.DataActionCallback;
import com.prd.quizzoapp.model.service.intf.QuizServerService;
import com.prd.quizzoapp.model.service.intf.RetroApiService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuizServerImpl implements QuizServerService {
    private final RetroApiService retroApiService;

    public QuizServerImpl() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8085/")
                .client(new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retroApiService = retrofit.create(RetroApiService.class);
    }

    @Override
    public void generateQuestions(QuizRequestDto quizRequestDto, DataActionCallback<List<Question>> callback) {
        retroApiService.getQuestions(quizRequestDto).enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                Log.d("QuizServerImpl", "onFailure: en generar las preguntas" + t);
                callback.onFailure(new Exception(t));
            }
        });
    }
}
