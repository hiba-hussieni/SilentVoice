package com.example.silentvoice;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class IntroActivity extends AppCompatActivity {

    private Button btnStart;
    private TextView btnTheme;
    private ImageView imgLogo;
    private ConstraintLayout root;

    private boolean isDark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        isDark = ThemeManager.isDarkMode(this);

        Intent serviceIntent =
                new Intent(this, SOSMonitorService.class);
        serviceIntent.putExtra("enable_sos_monitor", true);
        startService(serviceIntent);
        root = findViewById(R.id.rootLayout);
        btnStart = findViewById(R.id.btnStart);
        btnTheme = findViewById(R.id.btnTheme);
        imgLogo = findViewById(R.id.imgLogo);

        applyTheme();

        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(IntroActivity.this, WelcomeActivity.class);
            startActivity(intent);
        });

        btnTheme.setOnClickListener(v -> {

            isDark = !isDark;

            ThemeManager.saveTheme(
                    IntroActivity.this,
                    isDark
            );

            applyTheme();
        });
    }

    private void applyTheme() {

        if (isDark) {
            root.setBackgroundColor(Color.parseColor("#050B16"));

            btnTheme.setText("☀");
            btnTheme.setTextColor(Color.WHITE);
            btnTheme.setBackgroundResource(R.drawable.bg_theme_toggle);

            imgLogo.setImageResource(R.drawable.logo_silentvoice_dark);

            btnStart.setBackgroundResource(R.drawable.bg_button_dark_glow);
            btnStart.setTextColor(Color.WHITE);
            btnStart.setElevation(18f);

        } else {
            root.setBackgroundColor(Color.WHITE);

            btnTheme.setText("☾");
            btnTheme.setTextColor(Color.parseColor("#111827"));
            btnTheme.setBackgroundResource(R.drawable.bg_theme_toggle_light);

            imgLogo.setImageResource(R.drawable.logo_silentvoice_light);

            btnStart.setBackgroundResource(R.drawable.bg_gradient_button);
            btnStart.setTextColor(Color.WHITE);
            btnStart.setElevation(8f);
        }
    }
}