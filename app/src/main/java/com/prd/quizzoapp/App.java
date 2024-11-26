package com.prd.quizzoapp;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.prd.quizzoapp.model.service.RoomService;
import com.prd.quizzoapp.model.service.SseManager;
import com.prd.quizzoapp.util.DataSharedPreference;
import com.prd.quizzoapp.util.Util;

public class App extends Application {
    private RoomService roomService;
    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;

    @Override
    public void onCreate() {
        super.onCreate();
        roomService = new RoomService(this);

        //si el usuario ya ha iniciado sesion redirigir a la pantalla principal
        // Verificar si el usuario está autenticado
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Util.showLog("App", "No hay usuario autenticado");
            Toast.makeText(this, "No hay usuario autenticado", Toast.LENGTH_SHORT).show();
        } else {
            Util.showLog("App", "Usuario autenticado");
            // Si hay un usuario autenticado, redirigir a la pantalla principal
            Toast.makeText(this, "Bienvenido " , Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                if(activityReferences == 0){
                    changeUserRoomState(false);
                    Util.showLog("App", "La aplicación se abrió");
                }
                activityReferences++;

                Util.showLog("App", "Actualmente hay " + activityReferences + " referencias a la actividad i");
            }

            @Override
            public void onActivityStopped(Activity activity) {
                isActivityChangingConfigurations = activity.isChangingConfigurations();
                if (activityReferences == 1 && !isActivityChangingConfigurations) {//si la app se cierra y no se cambia de configuración
                    changeUserRoomState(true);
                    Util.showLog("App", "La aplicación se cerró");
                }
                activityReferences--;

                Util.showLog("App", "Actualmente hay " + activityReferences + " referencias a la actividad s");
            }

            @Override
            public void onActivityResumed(Activity activity) {}

            @Override
            public void onActivityPaused(Activity activity) {}

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

            @Override
            public void onActivityDestroyed(Activity activity) {}

            private void changeUserRoomState(boolean isPlaying) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if(auth.getCurrentUser() != null && DataSharedPreference.getData(Util.ROOM_UUID_KEY,getApplicationContext()) != null){
                    SseManager.getInstance().disconnect();
                    roomService.changePlayingState(DataSharedPreference.getData(Util.ROOM_UUID_KEY,getApplicationContext()),auth.getUid().toString(),isPlaying);
                }
            }
        });
    }
}
