package com.prd.quizzoapp.model.service;

import android.util.Log;

import com.prd.quizzoapp.model.dto.QuizRequestDto;
import com.prd.quizzoapp.model.service.intf.ActionCallback;
import com.prd.quizzoapp.model.service.intf.DataActionCallback;
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
                .baseUrl("http://10.0.2.2:8085/")
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
    public void connectToSseServer(String roomUUID, DataActionCallback<String> callback) {
        if(roomUUID.isEmpty()){
            callback.onFailure(new Exception("Room UUID is empty"));
            return;
        }
        /*OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.MINUTES)
                .writeTimeout(10, TimeUnit.MINUTES)
                .build();

        Request request = new Request.Builder()
                .url("https://test-sse-backend.herokuapp.com/events")
                .header("Accept", "application/json; q=0.5")
                .addHeader("Accept", "text/event-stream")
                .build();*/
        /*OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.MINUTES)
                .writeTimeout(20, TimeUnit.MINUTES)
                .build();
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8085/sse/"+roomUUID+"/stream")
                .header("Accept", "application/json; q=0.5")
                .addHeader("Accept", "text/event-stream")
                .build();

        EventSourceListener eventSourceListener = new EventSourceListener() {
            @Override
            public void onOpen(EventSource eventSource, okhttp3.Response response) {
                super.onOpen(eventSource, response);
                Log.d(TAG, "Connection Opened");
            }

            @Override
            public void onClosed(EventSource eventSource) {
                super.onClosed(eventSource);
                Log.d(TAG, "Connection Closed");
                disconnectSseServer();
            }

            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                super.onEvent(eventSource, id, type, data);
                Log.d(TAG, "On Event Received! Data: " + data);
                //eventSource.cancel();
                callback.onSuccess(data);
            }

            @Override
            public void onFailure(EventSource eventSource, Throwable t, okhttp3.Response response) {
                super.onFailure(eventSource, t, response);
                Log.d(TAG, "On Failure: " + (response != null ? response.body() : "Response is null"));
                if (t != null) {
                    Log.e(TAG, "Error", t);
                    callback.onFailure(new Exception(t));
                }else{
                    callback.onFailure(new Exception("Error desconocido"));
                }
            }
        };
        this.eventSource = EventSources.createFactory(client).newEventSource(request, eventSourceListener);*/
    }
    @Override
    public void disconnectSseServer() {
        if (eventSource != null) {
            eventSource.cancel();
            eventSource = null;
            Log.d(TAG, "Conexion cerrada");
        }
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
