package hr.franjkovic.ivan.myway.database;

import android.app.Application;
import android.arch.persistence.room.Room;

public class MyWayApp extends Application {

    public static AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();

        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "MyWayDb")
                .allowMainThreadQueries()
                .build();

        database.trackDao().insertAll();
    }
}
