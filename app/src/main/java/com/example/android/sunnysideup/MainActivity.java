package com.example.android.sunnysideup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static SharedPreferences settingsPref;
    public static Context ctx;

    private static String baseUrl = "https://api.darksky.net/forecast";

    private static final String API_KEY = "71bac57cbbb13e207ffd6f69f7d1e734";

    private static String location;
    public static Bundle currentWeather = new Bundle();
    public static Bundle hourSummary = new Bundle();
    public static Bundle dailySummary = new Bundle();
    public static ArrayList<Bundle> hourlyWeather = new ArrayList<>();
    public static ArrayList<Bundle> dailyWeather = new ArrayList<>();


    static double latitude = 28.7041;
    static double longitude = 77.1025;

    Double lat_lang[] = new Double[2];
    public static String Url = baseUrl + "/" + API_KEY + "/" + latitude + "," + longitude + "?exclude=minutely,alerts,flags&units=si";

    private static final int AUTOCOMPLETE_ACTIVITY_REQUEST_CODE = 101;
    private static final int COORDINATES_REQUEST_CODE = 201;

    private static TextView temp, appTemp, locationView, dateView, timeView, minTemp, maxTemp;
    private static ImageView iconImg, background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temp = findViewById(R.id.temp_main);
        appTemp = findViewById(R.id.appTemp_main);
        iconImg = findViewById(R.id.icon_main);
        locationView = findViewById(R.id.location_main);
        dateView = findViewById(R.id.date_main);
        timeView = findViewById(R.id.time_main);
        minTemp = findViewById(R.id.minTemp_main);
        maxTemp = findViewById(R.id.maxTemp_main);
        background = findViewById(R.id.flag);

        try {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHideOnContentScrollEnabled(true);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        }
        catch(Exception e){
            e.printStackTrace();
        }

        setInitialState();
        setMainActivityView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.action_settings: startActivity(new Intent(this, SettingsActivity.class));
                                        return true;

            case R.id.action_search: Intent intent = new Intent(this, PlaceAutocompleteActivity.class)
                                        .putExtra("call_code", 0);
                                     startActivityForResult(intent, AUTOCOMPLETE_ACTIVITY_REQUEST_CODE);
                                     return true;

        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle intentBundle = data.getBundleExtra("LOCATION_BUNDLE");
                latitude = intentBundle.getDouble("LATITUDE");
                longitude = intentBundle.getDouble("LONGITUDE");
                location = intentBundle.getString("NAME");

                lat_lang[0] =  latitude;
                lat_lang[1] = longitude;

            }
            else
                Log.i("Message", "Autocomplete onActivityResult from main RESULT_CODE not okay");
        }
        else if (requestCode == COORDINATES_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Bundle intentBundle = data.getBundleExtra("LOCATION_BUNDLE");
                latitude = intentBundle.getDouble("LATITUDE");
                longitude = intentBundle.getDouble("LONGITUDE");
                location = intentBundle.getString("LOCATION");

                if (getIntent().getExtras().getInt("call_code") == 1) {
                    SharedPreferences.Editor editor = MainActivity.settingsPref.edit();
                    editor.putString("location", intentBundle.getString("NAME"));
                    editor.apply();
                    SharedPreferences sh = getSharedPreferences("MyLocationPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor shEditor = sh.edit();
                    shEditor.putFloat("latitude", (float)latitude);
                    shEditor.putFloat("longitude", (float)longitude);
                    shEditor.apply();
                }

                lat_lang[0] =  latitude;
                lat_lang[1] = longitude;

            }

        }

        JsonTask getJsonTask = new JsonTask(this);
        getJsonTask.execute(lat_lang);

    }

    private void setInitialState(){
        ctx = this;
        settingsPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences sh = getSharedPreferences("MyLocationPreferences", MODE_PRIVATE);

        if(settingsPref.getString("location", "").equals("")) {
            Intent intent = new Intent(this, LocationActivity.class);
            intent.putExtra("call_code", 1);
            startActivityForResult(intent, COORDINATES_REQUEST_CODE);
        }
        else{
            latitude = sh.getFloat("latitude", 22);
            longitude = sh.getFloat("longitude", 22);
            location = sh.getString("location", "");
            lat_lang[0] =  latitude;
            lat_lang[1] = longitude;

            JsonTask getJsonTask = new JsonTask(this);
            getJsonTask.execute(lat_lang);

        }
    }

    public static void setMainActivityView(){

        background.setImageResource(currentWeather.getInt("background"));
        temp.setText(String.valueOf(currentWeather.getInt("temperature"))+"°C");
        appTemp.setText("Feels like "+ String.valueOf(currentWeather.getInt("apparentTemp")) + "°C");
        iconImg.setImageResource(currentWeather.getInt("iconID"));
        locationView.setText(location);
        dateView.setText(currentWeather.getString("date"));
        timeView.setText(currentWeather.getString("time"));
        minTemp.setText(String.valueOf(dailySummary.getInt("minTemp"))+"°");
        maxTemp.setText(String.valueOf(dailySummary.getInt("maxTemp"))+"°");
    }

    public void onCurrentClick(View view) {
        Intent intent = new Intent(this, DetailedWeatherActivity.class);
        intent.putExtra("bundle", currentWeather);
        intent.putExtra("title", "TODAY'S WEATHER");
        startActivity(intent);
    }

    public void onDetailClick(View view) {

        Intent intent = new Intent(this, HourWeatherActivity.class);
        switch(view.getId()){

            case R.id.hourlyWeather:    intent.putExtra("summary_bundle", hourSummary);
                                        intent.putExtra("weather_list", hourlyWeather);
                                        intent.putExtra("title", "HOURLY WEATHER REPORT");
                                        intent.putExtra("call_code", 1);
                                        break;

            case R.id.dailyWeather:     intent.putExtra("summary_bundle", dailySummary);
                                        intent.putExtra("weather_list", dailyWeather);
                                        intent.putExtra("title", "DAILY WEATHER REPORT");
                                        intent.putExtra("call_code", 2);
                                        break;
        }

        startActivity(intent);
    }
}
