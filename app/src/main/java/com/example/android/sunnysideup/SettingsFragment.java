package com.example.android.sunnysideup;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import static com.example.android.sunnysideup.MainActivity.ctx;
import static com.example.android.sunnysideup.MainActivity.settingsPref;

public class SettingsFragment extends PreferenceFragment {

    private static final int COORDINATES_REQUEST_CODE = 201;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);

        final Preference locationPref = findPreference("location");

        locationPref.setSummary(settingsPref.getString("location", ""));
        locationPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(ctx, LocationActivity.class);
                intent.putExtra("call_code", 1);
                startActivityForResult(intent, COORDINATES_REQUEST_CODE);

                return true;
            }
        });


    }


}
