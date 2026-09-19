package com.example.recentsblocker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = findViewById(R.id.status);

        Button openSettings = findViewById(R.id.openAccessibility);
        openSettings.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                // Send the user to the Accessibility settings to turn the
                // service on. Android requires the user to enable it manually.
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            }
        });

        SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        Switch toggle = findViewById(R.id.blockToggle);
        toggle.setChecked(prefs.getBoolean("blocking_enabled", true));
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton b, boolean checked) {
                prefs.edit().putBoolean("blocking_enabled", checked).apply();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatus();
    }

    private void updateStatus() {
        if (isServiceEnabled()) {
            status.setText(R.string.status_on);
        } else {
            status.setText(R.string.status_off);
        }
    }

    private boolean isServiceEnabled() {
        String enabled = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        return enabled != null && enabled.contains(getPackageName());
    }
}
