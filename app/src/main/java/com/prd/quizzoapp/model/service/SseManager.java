package com.prd.quizzoapp.model.service;

import androidx.annotation.NonNull;

import com.prd.quizzoapp.model.dto.QuestionsListDto;
import com.prd.quizzoapp.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

public class SseManager {
    private static SseManager instance;
    private OkHttpClient client;
    private EventSource eventSource;
    private final List<SseListener> listeners = new ArrayList<>();

    public interface SseListener {
        void onMessageReceived(QuestionsListDto questions);
        void onConnectionError(Throwable t);
        void onClosed();
    }

    public static synchronized SseManager getInstance() {
        if (instance == null) {
            instance = new SseManager();
        }
        return instance;
    }

    private SseManager() {
        client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.MINUTES)
                .writeTimeout(20, TimeUnit.MINUTES)
                .build();
    }

    public void connect(String roomUUID) {
        if (eventSource != null) {
            Util.showLog("SseManager", "Ya esta conectado");
            return; // Ya conectado
        }

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8085/sse/" + roomUUID + "/stream")
                .header("Accept", "application/json; q=0.5")
                .addHeader("Accept", "text/event-stream")
                .build();

        eventSource = EventSources.createFactory(client).newEventSource(request, new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                Util.showLog("SseManager", "Mensaje recibido: " + data);
                //parse data to QuestionsListDto class using gson
                QuestionsListDto questionsListDto = Util.getGson().fromJson(data, QuestionsListDto.class);

                for (SseListener listener : listeners) {
                    listener.onMessageReceived(questionsListDto);
                }
            }

            @Override
            public void onFailure(EventSource eventSource, Throwable t, okhttp3.Response response) {
                Util.showLog("SseManager", "Error general en la conexión: " + t.getMessage());
                if (t != null) {
                    t.printStackTrace();
                }
                disconnect();
                for (SseListener listener : listeners) {
                    listener.onConnectionError(t);
                }
            }

            @Override
            public void onClosed(EventSource eventSource) {
                Util.showLog("SseManager", "Conexión cerrada por el servidor");
                disconnect();
                for (SseListener listener : listeners) {
                    listener.onClosed();
                }

            }

            @Override
            public void onOpen(@NonNull EventSource eventSource, @NonNull Response response) {
                super.onOpen(eventSource, response);
                Util.showLog("SseManager", "Conexión abierta");
            }
        });
    }

    public void disconnect() {
        if (eventSource != null) {
            eventSource.cancel();
            eventSource = null;
            Util.showLog("SseManager", "Se desconecto del servidor de eventos");
        }
    }

    public void addListener(SseListener listener) {
        if (!listeners.contains(listener)) {
            Util.showLog("SseManager", "Se agrego el listener Main");
            listeners.add(listener);
        }
    }

    public void removeListener(SseListener listener) {
        listeners.remove(listener);
    }
}
