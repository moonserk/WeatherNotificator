package com.moonserk.weathernotificator;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public static final String APP_SETTINGS = "mysettings";
    public static final String APP_SETTINGS_CITY = "city";
    public static final String APP_SETTINGS_DIFFIRENCE = "difference";

    public static SharedPreferences mSettings;

    private TextView todayTemp;
    private TextView tomorrowTemp;
    public static WeatherParser weatherParser;
    private TextView cityView;
    private TextView diffView;

    private Intent settingsIntent;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        // Операции для выбранного пункта меню
        switch (id) {
            case R.id.action_settings:
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settingsIntent = new Intent(this, Settings.class);

        mSettings = getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);

        try {
            weatherParser = new WeatherParser("Sarapul", "metric");
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Таймер переодического обновления   << -- переделать
//        Timer watchDog = new Timer();
//        watchDog.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                System.out.println("Timer");
//                try {
//                    weatherParser.reGetWeather();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, 1000, (60 * 1000) * 2);

        initUI();
        initTemperature();
    }

    @Override
    protected void onPause(){
        super.onPause();
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_SETTINGS_CITY, weatherParser.getCity());
        editor.putInt(APP_SETTINGS_DIFFIRENCE, weatherParser.getDifference());
        editor.apply();
    }

    @Override
    protected  void onResume(){
        super.onResume();


        if(mSettings.contains(APP_SETTINGS_CITY)){
            weatherParser.setCity(mSettings.getString(APP_SETTINGS_CITY, "Sarapul"));

            try {
                weatherParser.reGetWeather();
                initTemperature();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if(mSettings.contains(APP_SETTINGS_DIFFIRENCE)){
            weatherParser.setDifference(mSettings.getInt(APP_SETTINGS_DIFFIRENCE, 10));

            try {
                weatherParser.reGetWeather();
                initTemperature();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        initTemperature();
    }

    public void initTemperature(){
        todayTemp.setText(weatherParser.getCurrentTemp());
        tomorrowTemp.setText(weatherParser.getNextTemp());
        cityView.setText(weatherParser.getCity());
        diffView.setText(weatherParser.getDifferenceTemp());

        if(Double.parseDouble(weatherParser.getDifferenceTemp()) > weatherParser.getDifference()) {
            getNotification(weatherParser.getDifferenceTemp());
        }

    }

    private void initUI(){
        todayTemp = (TextView) findViewById(R.id.textView);
        tomorrowTemp = (TextView) findViewById(R.id.textView2);
        cityView = (TextView) findViewById(R.id.textView6);
        diffView = (TextView) findViewById(R.id.textView5);
    }

    private void getNotification(String content){
        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle("WeatherNotification")
                        .setContentText("Temperature difference is " + content + " degree");

        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(1, mBuilder.build());
    }
}
