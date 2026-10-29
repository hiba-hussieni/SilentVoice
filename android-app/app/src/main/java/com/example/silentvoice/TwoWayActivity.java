package com.example.silentvoice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class TwoWayActivity extends AppCompatActivity {

    LinearLayout mainLayout, resultCard, btnMic;

    TextView btnBack, tvSpeak, tvDesc, tvRecognized, tvSpeechResult, tvTap;
    TextView btnThemeTwoWay, profileCircle, tvGreetingTwoWay, tvUserNameTwoWay;

    boolean isDarkMode;

    ActivityResultLauncher<Intent> speechLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_way);
        isDarkMode = ThemeManager.isDarkMode(this);

        initViews();
        loadProfileData();
        applyTheme();

        btnBack.setOnClickListener(v -> finish());
        btnMic.setOnClickListener(v -> startSpeechRecognition());
        btnThemeTwoWay.setOnClickListener(v -> {

            isDarkMode = !isDarkMode;

            ThemeManager.saveTheme(
                    TwoWayActivity.this,
                    isDarkMode
            );

            applyTheme();
        });

        profileCircle.setOnClickListener(v -> {
            Intent intent = new Intent(TwoWayActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        speechLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                ArrayList<String> results =
                                        result.getData().getStringArrayListExtra(
                                                RecognizerIntent.EXTRA_RESULTS
                                        );

                                if (results != null && !results.isEmpty()) {
                                    tvSpeechResult.setText(results.get(0));
                                }
                            }
                        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadProfileData();

        isDarkMode = ThemeManager.isDarkMode(this);

        applyTheme();
    }

    private void initViews() {
        mainLayout = findViewById(R.id.mainLayout);
        resultCard = findViewById(R.id.resultCard);
        btnMic = findViewById(R.id.btnMic);
        btnBack = findViewById(R.id.btnBack);
        tvSpeak = findViewById(R.id.tvSpeak);
        tvDesc = findViewById(R.id.tvDesc);
        tvRecognized = findViewById(R.id.tvRecognized);
        tvSpeechResult = findViewById(R.id.tvSpeechResult);
        tvTap = findViewById(R.id.tvTap);
        btnThemeTwoWay = findViewById(R.id.btnThemeTwoWay);
        profileCircle = findViewById(R.id.profileCircle);
        tvGreetingTwoWay = findViewById(R.id.tvGreetingTwoWay);
        tvUserNameTwoWay = findViewById(R.id.tvUserNameTwoWay);
    }

    private void loadProfileData() {
        SharedPreferences profilePrefs =
                getSharedPreferences("USER_PROFILE", MODE_PRIVATE);

        SharedPreferences userPrefs =
                getSharedPreferences("USER_DATA", MODE_PRIVATE);

        String name = profilePrefs.getString(
                "name",
                userPrefs.getString("username", "Guest")
        );

        String email = profilePrefs.getString(
                "email",
                userPrefs.getString("email", "hiba@test.com")
        );

        tvGreetingTwoWay.setText("Good Afternoon");

        if (email != null && !email.trim().isEmpty()) {
            tvUserNameTwoWay.setText(email);
        } else {
            tvUserNameTwoWay.setText(name);
        }

        if (name != null && !name.trim().isEmpty() && !name.equals("Guest")) {
            profileCircle.setText(name.substring(0, 1).toUpperCase());
        } else if (email != null && !email.trim().isEmpty()) {
            profileCircle.setText(email.substring(0, 1).toUpperCase());
        } else {
            profileCircle.setText("👤");
        }
    }

    private void startSpeechRecognition() {
        Intent intent =
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );

        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
        );

        intent.putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                "Speak now..."
        );

        speechLauncher.launch(intent);
    }

    private void applyTheme() {
        int bg, card, stroke, primary, secondary;

        if (isDarkMode) {
            bg = Color.parseColor("#0D0F1A");
            card = Color.parseColor("#1A1D2E");
            stroke = Color.parseColor("#2B2F45");
            primary = Color.WHITE;
            secondary = Color.parseColor("#A0A3BD");

            btnThemeTwoWay.setText("☀");
            btnThemeTwoWay.setTextColor(Color.WHITE);
            btnThemeTwoWay.setBackground(round(Color.parseColor("#1A1D2E"), Color.parseColor("#2B2F45")));

            btnBack.setTextColor(Color.WHITE);
            btnBack.setBackground(round(Color.BLACK, Color.BLACK));

        } else {
            bg = Color.WHITE;
            card = Color.WHITE;
            stroke = Color.parseColor("#E5E7EB");
            primary = Color.BLACK;
            secondary = Color.parseColor("#666666");

            btnThemeTwoWay.setText("☾");
            btnThemeTwoWay.setTextColor(Color.BLACK);
            btnThemeTwoWay.setBackground(round(Color.WHITE, Color.parseColor("#E5E7EB")));

            btnBack.setTextColor(Color.BLACK);
            btnBack.setBackground(round(Color.WHITE, Color.parseColor("#E5E7EB")));
        }

        mainLayout.setBackgroundColor(bg);
        resultCard.setBackground(round(card, stroke));

        tvSpeak.setTextColor(primary);
        tvDesc.setTextColor(secondary);
        tvGreetingTwoWay.setTextColor(secondary);
        tvUserNameTwoWay.setTextColor(primary);
        tvSpeechResult.setTextColor(primary);

        tvRecognized.setTextColor(Color.parseColor("#9B5CFF"));
        tvTap.setTextColor(Color.parseColor("#9B5CFF"));
    }

    private GradientDrawable round(int color, int strokeColor) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(26 * getResources().getDisplayMetrics().density);
        d.setStroke(2, strokeColor);
        return d;
    }
}