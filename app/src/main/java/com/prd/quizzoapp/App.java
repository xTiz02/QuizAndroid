package com.prd.quizzoapp;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.prd.quizzoapp.util.Util;

public class App extends Application {
    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

            @Override
            public void onActivityStarted(Activity activity) {
                activityReferences++;
                Util.showLog("App", "Actualmente hay " + activityReferences + " referencias a la actividad");
            }

            @Override
            public void onActivityStopped(Activity activity) {
                isActivityChangingConfigurations = activity.isChangingConfigurations();
                if (activityReferences == 1 && !isActivityChangingConfigurations) {
                    clearSharedPreferences();
                }
                activityReferences--;
                Util.showLog("App", "Actualmente hay " + activityReferences + " referencias a la actividad");
            }

            @Override
            public void onActivityResumed(Activity activity) {}

            @Override
            public void onActivityPaused(Activity activity) {}

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

            @Override
            public void onActivityDestroyed(Activity activity) {}

            private void clearSharedPreferences() {
                //Util.showLog("App", "Actualmente hay " + activityReferences + " referencias a la actividad");
            }
        });
    }
}
