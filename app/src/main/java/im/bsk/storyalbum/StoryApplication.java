package im.bsk.storyalbum;


import android.app.Application;

import com.squareup.leakcanary.LeakCanary;


public class StoryApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        LeakCanary.install(this);
    }
}