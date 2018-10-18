package com.example.android.sunnysideup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

public class DetailedWeatherActivity extends AppCompatActivity {

    private TextView title, summary, visibility, wind, humidity, precipProb, pressure, dew_point, uv_index, cloud_cover, ozone, temperature;
    private ImageView summary_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_weather);

        Objects.requireNonNull(getSupportActionBar()).hide();

        initializeViews();
        populateViews(Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).getBundle("bundle")),
                getIntent().getExtras().getString("title"));

    }

    private void initializeViews(){
        title = findViewById(R.id.title);
        summary = findViewById(R.id.summary);
        summary_icon = findViewById(R.id.h_summary_icon);
        visibility = findViewById(R.id.visibility);
        wind = findViewById(R.id.wind_speed);
        humidity = findViewById(R.id.humidity);
        precipProb = findViewById(R.id.precip_prob);
        pressure = findViewById(R.id.pressure);
        dew_point = findViewById(R.id.dew_point);
        uv_index = findViewById(R.id.uv_index);
        cloud_cover = findViewById(R.id.cloud_cover);
        ozone = findViewById(R.id.ozone);
        temperature = findViewById(R.id.summary_temp);
    }

    private void populateViews(Bundle bundle, String titleName){

        title.setText(titleName);
        summary.setText(bundle.getString("summary"));
        summary_icon.setImageResource(bundle.getInt("iconID"));
        visibility.setText(String.valueOf(bundle.getDouble("visibility"))+" km");
        wind.setText(String.valueOf(bundle.getDouble("windSpeed"))+ " m/s");
        humidity.setText(String.valueOf(bundle.getInt("humidity")) + "%");
        precipProb.setText(String.valueOf(bundle.getInt("precipProbability"))+ "%");
        dew_point.setText(String.valueOf(bundle.getInt("dewPoint"))+"°");
        uv_index.setText(String.valueOf(bundle.getInt("uvIndex")));
        cloud_cover.setText(String.valueOf(bundle.getInt("cloudCover"))+"%");
        ozone.setText(String.valueOf(bundle.getDouble("ozone")));
        temperature.setText(String.valueOf(bundle.getInt("temperature")) +"°");
    }
}
