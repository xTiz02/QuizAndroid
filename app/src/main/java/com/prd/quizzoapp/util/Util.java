package com.prd.quizzoapp.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prd.quizzoapp.model.service.LoadingService;

import java.util.Random;

public class Util {
    private static Gson gson;
    public static final String default_img= "https://firebasestorage.googleapis.com/v0/b/ecommerce-web-fea18.appspot.com/o/profile_pic.png?alt=media&token=3878a128-c57b-4379-8b0c-dfbc15ec0b97";
    public static final String ROOM_UUID_KEY = "roomUUID";
    public static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    //192.168.232.2 (port 41324)

    public static void showToastLog(String message,Context context){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public static void showLog(String tag, String message){
        Log.e(tag, message);
    }

    public static String generateRoomCode() {
        Random random = new Random();

        StringBuilder letters = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            char letter = (char) (random.nextInt(26) + 'A');
            letters.append(letter);
        }
        int firstNumbers = random.nextInt(900) + 100;
        int secondNumbers = random.nextInt(900) + 100;
        return letters.toString() + "-" + firstNumbers + "-" + secondNumbers;
    }

    public static Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .setLenient() // Permite analizar JSON menos estricto, si es necesario para evitar errores
                    .create();
        }
        return gson;
    }

    public static void delay(int time,String message,Context context,Runnable beforeDelay,Runnable afterDelay){
        new Handler(Looper.getMainLooper()).post(() -> {
            LoadingService loadingService = new LoadingService(context);
            loadingService.showLoading(message);

            beforeDelay.run();

            new Handler().postDelayed(() -> {
                afterDelay.run();
                loadingService.hideLoading();
            }, time);
        });
    }


}



