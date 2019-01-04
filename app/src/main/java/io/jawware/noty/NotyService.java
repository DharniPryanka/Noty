package io.jawware.noty;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class NotyService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startid) {

        Timer timer = new Timer ();
        TimerTask hourlyTask =
                new TimerTask () {
            @Override
            public void run () {
                new Request().execute();

            }
        };

// schedule the task to run starting now and then every 12 hours...
        timer.schedule (hourlyTask, 0l, 43200000);
//        checks for every 3 minutes...
//        timer.schedule (hourlyTask, 0l, 180000);
//        timer.schedule (hourlyTask, 0l, );
//        Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();
    }
}


