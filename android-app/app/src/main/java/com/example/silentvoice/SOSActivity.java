package com.example.silentvoice;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import android.media.MediaPlayer;
import android.media.MediaPlayer;
import android.media.MediaPlayer;
import android.util.Log;
public class SOSActivity extends AppCompatActivity {

    ScrollView rootSOS;

    TextView btnThemeToggle;
    TextView btnBackSOS;

    TextView tvSosTitle;
    TextView tvEmergency;
    TextView tvHelp;

    LinearLayout cardLocation;
    LinearLayout cardAmbulance;
    LinearLayout card911;
    LinearLayout cardContacts;

    boolean isDarkMode;
    TextView tvLocationText, tvAmbulanceText, tv911Text, tvContactsText;
    LinearLayout sosCircle;
    TextView iconLocation, iconAmbulance, icon911, iconContacts;
    MediaPlayer alertPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sosactivity);
        isDarkMode = ThemeManager.isDarkMode(this);
        initViews();

        btnThemeToggle.setOnClickListener(v -> {

            isDarkMode = !isDarkMode;

            ThemeManager.saveTheme(
                    SOSActivity.this,
                    isDarkMode
            );

            applyTheme();

            alertPlayer = MediaPlayer.create(this, R.raw.alert);

            alertPlayer.start();
        });

        btnBackSOS.setOnClickListener(v -> finish());

        applyTheme();
        alertPlayer = MediaPlayer.create(this, R.raw.alert);

        if (alertPlayer != null) {
            Log.d("ALERT_SOUND", "sound works");
            alertPlayer.start();
        } else {
            Log.d("ALERT_SOUND", "player is null");
        }
        cardLocation.setOnClickListener(v -> {

            Uri gmmIntentUri = Uri.parse("geo:0,0?q=My+Location");

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

            mapIntent.setPackage("com.google.android.apps.maps");

            startActivity(mapIntent);

        });
        cardAmbulance.setOnClickListener(v -> {

            Intent callIntent = new Intent(Intent.ACTION_CALL);

            callIntent.setData(Uri.parse("tel:140"));

            if (ActivityCompat.checkSelfPermission(
                    SOSActivity.this,
                    Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        SOSActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        1);

                return;
            }

            startActivity(callIntent);

        });
        card911.setOnClickListener(v -> {

            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse("tel:911"));

            startActivity(dialIntent);
        });
        cardContacts.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_VIEW);

            intent.setType("vnd.android.cursor.dir/contact");

            startActivity(intent);

        });
    }

    private void initViews() {

        rootSOS = findViewById(R.id.rootSOS);

        btnThemeToggle = findViewById(R.id.btnThemeToggle);
        btnBackSOS = findViewById(R.id.btnBackSOS);

        tvSosTitle = findViewById(R.id.tvSosTitle);
        tvEmergency = findViewById(R.id.tvEmergency);
        tvHelp = findViewById(R.id.tvHelp);

        cardLocation = findViewById(R.id.cardLocation);
        cardAmbulance = findViewById(R.id.cardAmbulance);
        card911 = findViewById(R.id.card911);
        cardContacts = findViewById(R.id.cardContacts);
        tvLocationText = findViewById(R.id.tvLocationText);
        tvAmbulanceText = findViewById(R.id.tvAmbulanceText);
        tv911Text = findViewById(R.id.tv911Text);
        tvContactsText = findViewById(R.id.tvContactsText);
        sosCircle = findViewById(R.id.sosCircle);
        iconLocation = findViewById(R.id.iconLocation);
        iconAmbulance = findViewById(R.id.iconAmbulance);
        icon911 = findViewById(R.id.icon911);
        iconContacts = findViewById(R.id.iconContacts);
    }

    private void applyTheme() {

        int bg;
        int card;
        int stroke;
        int textPrimary;
        int textSecondary;

        int accent = Color.parseColor("#FF7A7A");
        sosCircle.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(accent)
        );// اللون الأحمر/كورال متل الصورة

        if (isDarkMode) {
            bg = Color.parseColor("#0D0F1A");
            card = Color.parseColor("#1A1D2E");
            stroke = Color.parseColor("#3A3F55");
            textPrimary = Color.WHITE;
            textSecondary = Color.parseColor("#D1D5DB");

            btnThemeToggle.setText("☀");
            btnThemeToggle.setTextColor(Color.WHITE);
            btnThemeToggle.setBackground(round(Color.BLACK, Color.TRANSPARENT));

        } else {
            bg = Color.WHITE;
            card = Color.WHITE;
            stroke = Color.parseColor("#E5E7EB");
            textPrimary = Color.parseColor("#111111");
            textSecondary = Color.parseColor("#666666");

            btnThemeToggle.setText("☾");
            btnThemeToggle.setTextColor(Color.BLACK);
            btnThemeToggle.setBackground(round(Color.WHITE, Color.TRANSPARENT));
        }

        rootSOS.setBackgroundColor(bg);

        tvSosTitle.setTextColor(textPrimary);
        tvEmergency.setTextColor(accent);
        tvHelp.setTextColor(textSecondary);

        btnBackSOS.setTextColor(textPrimary);

        cardLocation.setBackground(round(card, stroke));
        cardAmbulance.setBackground(round(card, stroke));
        card911.setBackground(round(card, stroke));
        cardContacts.setBackground(round(card, stroke));

        tvLocationText.setTextColor(textPrimary);
        tvAmbulanceText.setTextColor(textPrimary);
        tv911Text.setTextColor(textPrimary);
        tvContactsText.setTextColor(textPrimary);
        iconLocation.setTextColor(accent);
        iconAmbulance.setTextColor(accent);
        icon911.setTextColor(accent);
        iconContacts.setTextColor(accent);

        iconLocation.setAlpha(1f);
        iconAmbulance.setAlpha(1f);
        icon911.setAlpha(1f);
        iconContacts.setAlpha(1f);

    }
    private GradientDrawable round(int color, int strokeColor) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(22 * getResources().getDisplayMetrics().density);
        d.setStroke(2, strokeColor);
        return d;
    }
    @Override
    protected void onResume() {
        super.onResume();

        isDarkMode = ThemeManager.isDarkMode(this);

        applyTheme();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (alertPlayer != null) {
            alertPlayer.release();
            alertPlayer = null;
        }
    }
}