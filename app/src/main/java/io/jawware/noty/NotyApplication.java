package io.jawware.noty;

import android.app.Application;
import android.content.Context;

import com.evernote.android.job.JobManager;

public class NotyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        NotyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return NotyApplication.context;
    }
}
