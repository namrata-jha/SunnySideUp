package com.example.android.sunnysideup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;


public class HourWeatherActivity extends AppCompatActivity {

    private TextView title, summary;
    private ImageView summary_icon;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hour_weather);
        Objects.requireNonNull(getSupportActionBar()).hide();
        title = findViewById(R.id.h_title);
        summary = findViewById(R.id.h_summary);
        summary_icon = findViewById(R.id.h_summary_icon);
        recyclerView = findViewById(R.id.weatherRecycler);

        title.setText(Objects.requireNonNull(getIntent().getExtras()).getString("title"));
        summary.setText(getIntent().getBundleExtra("summary_bundle").getString("summary"));
        summary_icon.setImageResource(getIntent().getBundleExtra("summary_bundle").getInt("icon"));

        WeatherAdapter adapter = new WeatherAdapter(Objects.requireNonNull(getIntent().getExtras()).<Bundle>getParcelableArrayList("weather_list"),
                getBaseContext(), getIntent().getExtras().getInt("call_code"));

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
    }
}
