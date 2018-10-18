package com.example.android.sunnysideup;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JsonTask extends AsyncTask<Double, Void, JSONObject> {

    private ProgressDialog pd;
    private Context ctx;

    private static final String baseUrl = "https://api.darksky.net/forecast";

    private static final String API_KEY = "71bac57cbbb13e207ffd6f69f7d1e734";

    private double offset;


    public JsonTask(Context context) {
        super();
        ctx = context;
        MainActivity.hourlyWeather.clear();
        MainActivity.dailyWeather.clear();
    }

    @Override
    protected void onPreExecute() {

        pd = new ProgressDialog(ctx);
        pd.setMessage("Clearing skies, please wait...");
        pd.setIcon(R.drawable.clearing_skies);
        pd.setCancelable(false);
        pd.show();

    }

    @Override
    protected JSONObject doInBackground(Double... lat_lang) {

        HttpURLConnection urlConn;
        BufferedReader bufferedReader = null;

        String Url = baseUrl + "/" + API_KEY + "/" + lat_lang[0] + "," + lat_lang[1] + "?exclude=minutely,alerts,flags&units=si";

        try{
            URL url = new URL(Url);
            urlConn = (HttpURLConnection) url.openConnection();
            InputStreamReader inputStreamReader = new InputStreamReader(urlConn.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line;

            while((line=bufferedReader.readLine())!=null){
                stringBuffer.append(line);
            }

            try {
                return new JSONObject(stringBuffer.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if (bufferedReader!=null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {

        pd.dismiss();
        if(jsonObject!=null){
            JSONObject currently = new JSONObject();
            JSONObject hourly = new JSONObject();
            JSONObject daily = new JSONObject();


            try {
                currently = jsonObject.getJSONObject("currently");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                hourly = jsonObject.getJSONObject("hourly");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                daily = jsonObject.getJSONObject("daily");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                offset = jsonObject.getDouble("offset");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            /**
             * Setting current weather
             */
            try {
                MainActivity.currentWeather = jsonParser(currently, 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            /**
             * Setting hourly summary
             */
            String iconNameH = null;
            try {
                iconNameH = getIconName(hourly.getString("icon"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int iconID_h = ctx.getResources().getIdentifier(iconNameH, "drawable", ctx.getPackageName());
            MainActivity.hourSummary.putInt("icon", iconID_h);
            try {
                MainActivity.hourSummary.putString("summary", hourly.getString("summary"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            /**
             * Setting daily summary
             */
            String iconNameD = null;
            try {
                iconNameD = getIconName(daily.getString("icon"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int iconID_d = ctx.getResources().getIdentifier(iconNameD, "drawable", ctx.getPackageName());
            MainActivity.dailySummary.putInt("icon", iconID_d);
            try {
                MainActivity.dailySummary.putString("summary", daily.getString("summary"));
                MainActivity.dailySummary.putInt("minTemp", (int) daily.getJSONArray("data").getJSONObject(0).
                        getDouble("temperatureMin"));
                MainActivity.dailySummary.putInt("maxTemp", (int) daily.getJSONArray("data").getJSONObject(0).
                        getDouble("temperatureMax"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            /**
             * Setting hourly weather data list
             */
            try {
                JSONArray hourData = hourly.getJSONArray("data");
                int length = hourData.length();
                for(int i = 0; i< length; i++){
                    MainActivity.hourlyWeather.add(jsonParser(hourData.getJSONObject(i),0));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            /**
             * Setting daily weather data list
             */
            try {
                JSONArray dailyData = daily.getJSONArray("data");
                int length = dailyData.length();
                for(int i = 0; i< length; i++){
                    MainActivity.dailyWeather.add(jsonParser(dailyData.getJSONObject(i),1));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else{
            Toast.makeText(ctx, "Set default location or turn on the internet.", Toast.LENGTH_SHORT).show();
        }

        MainActivity.setMainActivityView();
    }

    private Bundle jsonParser(JSONObject weatherJson, int code) throws JSONException {
        String[] retValue = new String[3];

        try {
            retValue = ConversionUtils.unixToDateTimeFormatter(weatherJson.getLong("time"), offset);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Bundle retBundle = new Bundle();

        String date = retValue[0];
        String time = retValue[1];
        String dayNight = retValue[2];

        //clear-day, clear-night, rain, snow, sleet, wind, fog, cloudy, partly-cloudy-day, or partly-cloudy-night,hail, thunderstorm, or tornado
        String iconName = getIconName(weatherJson.getString("icon"));

        int iconID = ctx.getResources().getIdentifier(iconName, "drawable", ctx.getPackageName());
        int backgroundID = ctx.getResources().getIdentifier(dayNight, "drawable", ctx.getPackageName());

        int temperature, apparentTemp;
        if(code!=1) {
            temperature = (int) weatherJson.getDouble("temperature");
            apparentTemp = (int) weatherJson.getDouble("apparentTemperature");
        }
        else {
            temperature = (int) (weatherJson.getDouble("temperatureMin") + weatherJson.getDouble("temperatureMax")) / 2;
            apparentTemp = temperature;
        }

        String summary = weatherJson.getString("summary");
        double visibility = weatherJson.getDouble("visibility");
        double windSpeed = weatherJson.getDouble("windSpeed");
        int humidity = (int) (weatherJson.getDouble("humidity")*100);
        int precipProbability = (int) (weatherJson.getDouble("precipProbability")*100);
        int pressure = (int) weatherJson.getDouble("pressure");
        int dewPoint = (int) weatherJson.getDouble("dewPoint");
        int uvIndex = weatherJson.getInt("uvIndex");
        int cloudCover = (int) (weatherJson.getDouble("cloudCover")*100);
        double ozone = weatherJson.getDouble("ozone");

        retBundle.putString("date", date);
        retBundle.putString("time", time);
        retBundle.putInt("background", backgroundID);
        retBundle.putInt("iconID", iconID);
        retBundle.putInt("temperature", temperature);
        retBundle.putInt("apparentTemp", apparentTemp);
        retBundle.putString("summary", summary);
        retBundle.putDouble("visibility", visibility);
        retBundle.putDouble("windSpeed", windSpeed);
        retBundle.putInt("humidity", humidity);
        retBundle.putInt("precipProbability", precipProbability);
        retBundle.putInt("pressure", pressure);
        retBundle.putInt("dewPoint", dewPoint);
        retBundle.putInt("uvIndex", uvIndex);
        retBundle.putInt("cloudCover", cloudCover);
        retBundle.putDouble("ozone", ozone);

        return retBundle;

    }

    private String getIconName(String jsonName){

        String iconName;
        switch (jsonName){
            case "clear-day":   iconName = "clear_day";
                break;
            case "clear-night": iconName = "clear_night";
                break;
            case "partly-cloudy-day":iconName = "partly_cloudy_day";
                break;
            case "partly-cloudy-night": iconName = "partly_cloudy_night";
                break;
            default: iconName =  jsonName;
                break;
        }

        return iconName;
    }


}
