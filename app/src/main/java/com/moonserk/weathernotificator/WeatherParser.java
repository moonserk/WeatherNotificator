package com.moonserk.weathernotificator;

import android.os.StrictMode;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class WeatherParser {

    private ArrayList<String> weather = new ArrayList<>();
    private String city;
    private String unit;
    private String sUrl;
    private int difference = 10;

    public WeatherParser(String city, String unit) throws IOException {
        this.city = city;
        this.unit = unit;
        //sUrl = "http://api.openweathermap.org/data/2.5/weather?q=" + this.city + "&units=" + this.unit + "&appid=79307a8fc88d83096f2694e47e3659b3";
        sUrl  = "http://api.openweathermap.org/data/2.5/forecast?q=" + this.city + "&units=" + this.unit + "&mode=json&appid=79307a8fc88d83096f2694e47e3659b3";
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        addWeatherArray();
    }

    public void setDifference(int difference){
        this.difference = difference;
    }

    public int getDifference(){
        return difference;
    }

    public String getCity(){
        return this.city;
    }
    public void reGetWeather() throws IOException {
        sUrl  = "http://api.openweathermap.org/data/2.5/forecast?q=" + this.city + "&units=" + this.unit + "&mode=json&appid=79307a8fc88d83096f2694e47e3659b3";
        weather.clear();
        addWeatherArray();
    }

    public String getCurrentTemp(){
        return weather.get(0);
    }

    public String getNextTemp(){
        return weather.get(5);
    }

    public String getDifferenceTemp(){
        double w1 = Double.parseDouble(getCurrentTemp());
        double w2 = Double.parseDouble(getNextTemp());
        double result  = new BigDecimal(Math.abs(w1 - w2)).setScale(2, RoundingMode.UP).doubleValue();
        return Double.toString(result);
    }

    public void setCity(String city){
        this.city = city;
    }


    public void addWeatherArray() throws IOException {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(sUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();
                    JsonParser jp = new JsonParser(); //from gson
                    JsonElement root = jp.parse(new InputStreamReader((InputStream) conn.getContent())); //Convert the input stream to a json element
                    JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
                    JsonArray arra = rootobj.get("list").getAsJsonArray();
                    for(JsonElement a : arra){
                        weather.add(a.getAsJsonObject().get("main").getAsJsonObject().get("temp").toString());
                    }


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        t.run();
    }
}
