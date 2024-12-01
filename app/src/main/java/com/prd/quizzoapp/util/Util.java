package com.prd.quizzoapp.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prd.quizzoapp.model.service.LoadingService;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Util {
    private static Gson gson;
    public static final String default_img= "https://firebasestorage.googleapis.com/v0/b/ecommerce-web-fea18.appspot.com/o/profile_pic.png?alt=media&token=3878a128-c57b-4379-8b0c-dfbc15ec0b97";
    public static final String ROOM_UUID_KEY = "roomUUID";
    public static final String IS_ADMIN_KEY = "isAdmin";
    public static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private static final Map<String, String> usersImg = new ConcurrentHashMap<>();

    private Util() {} // Constructor privado para evitar instancias

    public static Map<String, String> getImages() {
        return usersImg;
    }






    //DatabaseReference singleton
    //192.168.232.2 (port 41324)

    public static void showToastLog(String message,Context context){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public static void showLog(String tag, String message){
        Log.e(tag, message);
    }

    public static String generateRoomCode() {
        //Jk9N0M

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder roomCode = new StringBuilder();
        Random rnd = new Random();
        while (roomCode.length() < 6) { // length of the random string.
            int index = (int) (rnd.nextFloat() * characters.length());
            roomCode.append(characters.charAt(index));
        }
        return roomCode.toString();
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

    public static void delay(String message,Context context,Runnable run){
        new Handler(Looper.getMainLooper()).post(() -> {
            LoadingService loadingService = new LoadingService(context);
            loadingService.showLoading(message);
            run.run();
            loadingService.hideLoading();
        });
    }

    public static void delay(Context context,Runnable run){
        new Handler(Looper.getMainLooper()).post(() -> {
            run.run();
        });
    }


}



