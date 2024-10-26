package com.prd.quizzoapp.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Util {

    public static final String default_img= "https://firebasestorage.googleapis.com/v0/b/ecommerce-web-fea18.appspot.com/o/profile_pic.png?alt=media&token=3878a128-c57b-4379-8b0c-dfbc15ec0b97";

    public static void showLog(String tag, String message, Context context,Exception e){
        Log.e(tag, message, e);
        Toast.makeText(context, "Algo salió mal", Toast.LENGTH_SHORT).show();
    }


}



