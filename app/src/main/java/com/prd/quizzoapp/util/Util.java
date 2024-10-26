package com.prd.quizzoapp.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Util {

    public static final String default_img= "https://firebasestorage.googleapis.com/v0/b/ecommerce-web-fea18.appspot.com/o/profile_pic.png?alt=media&token=3878a128-c57b-4379-8b0c-dfbc15ec0b97";
    public static final String ROOM_UUID = "roomUUID";


    public static void showLog(String tag, String message, Context context,Exception e){
        Log.e(tag, message, e);
        Toast.makeText(context, "Algo salió mal", Toast.LENGTH_SHORT).show();
    }

    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (characters.length() * Math.random());
            randomString.append(characters.charAt(index));
        }
        return randomString.toString();
    }


}



