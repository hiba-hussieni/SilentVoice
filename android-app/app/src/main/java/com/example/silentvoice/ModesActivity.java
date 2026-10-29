package com.example.silentvoice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ModesActivity extends AppCompatActivity {

    boolean isDark;

    ScrollView rootModes;

    TextView btnTheme, btnBackModes;

    LinearLayout cardDiscover, cardAdd, cardDelete, cardTwoWay;

    TextView titleDiscover, subDiscover,
            titleAdd, subAdd,
            titleDelete, subDelete;

    TextView titleTwoWay, subTwoWay;

    TextView profileIcon, txtGreeting, txtUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modes);

        isDark = ThemeManager.isDarkMode(this);

        rootModes = findViewById(R.id.rootModes);

        btnTheme = findViewById(R.id.btnTheme);
        btnBackModes = findViewById(R.id.btnBackModes);

        cardDiscover = findViewById(R.id.cardDiscover);
        cardAdd = findViewById(R.id.cardAdd);
        cardDelete = findViewById(R.id.cardDelete);
        cardTwoWay = findViewById(R.id.cardTwoWay);

        titleDiscover = findViewById(R.id.titleDiscover);
        subDiscover = findViewById(R.id.subDiscover);

        titleAdd = findViewById(R.id.titleAdd);
        subAdd = findViewById(R.id.subAdd);

        titleDelete = findViewById(R.id.titleDelete);
        subDelete = findViewById(R.id.subDelete);

        titleTwoWay = findViewById(R.id.titleTwoWay);
        subTwoWay = findViewById(R.id.subTwoWay);

        profileIcon = findViewById(R.id.profileIcon);
        txtGreeting = findViewById(R.id.txtGreeting);
        txtUserEmail = findViewById(R.id.txtUserEmail);

        setupUserProfile();

        profileIcon.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            ModesActivity.this,
                            ProfileActivity.class
                    )
            );
        });

        cardDiscover.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            ModesActivity.this,
                            DiscoverActivity.class
                    )
            );
        });

        cardAdd.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            ModesActivity.this,
                            AddModeActivity.class
                    )
            );
        });

        cardDelete.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            ModesActivity.this,
                            DeleteModeActivity.class
                    )
            );
        });

        cardTwoWay.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            ModesActivity.this,
                            TwoWayActivity.class
                    )
            );
        });

        btnTheme.setOnClickListener(v -> {

            isDark = !isDark;

            ThemeManager.saveTheme(
                    ModesActivity.this,
                    isDark
            );

            applyModeTheme();
        });

        btnBackModes.setOnClickListener(v -> finish());

        applyModeTheme();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setupUserProfile();

        isDark = ThemeManager.isDarkMode(this);

        applyModeTheme();
    }

    private void setupUserProfile() {

        SharedPreferences profilePrefs =
                getSharedPreferences(
                        "USER_PROFILE",
                        MODE_PRIVATE
                );

        SharedPreferences userPrefs =
                getSharedPreferences(
                        "USER_DATA",
                        MODE_PRIVATE
                );

        String savedName =
                profilePrefs.getString(
                        "name",
                        userPrefs.getString(
                                "username",
                                "Guest"
                        )
                );

        String savedEmail =
                profilePrefs.getString(
                        "email",
                        userPrefs.getString(
                                "email",
                                ""
                        )
                );

        txtGreeting.setText("Good Afternoon");

        if (savedEmail != null
                && !savedEmail.trim().isEmpty()) {

            txtUserEmail.setText(savedEmail);

            if (savedName != null
                    && !savedName.trim().isEmpty()
                    && !savedName.equals("Guest")) {

                profileIcon.setText(
                        savedName.substring(0, 1)
                                .toUpperCase()
                );

            } else {

                profileIcon.setText(
                        savedEmail.substring(0, 1)
                                .toUpperCase()
                );
            }

        } else if (savedName != null
                && !savedName.trim().isEmpty()
                && !savedName.equals("Guest")) {

            txtUserEmail.setText(savedName);

            profileIcon.setText(
                    savedName.substring(0, 1)
                            .toUpperCase()
            );

        } else {

            txtUserEmail.setText("Guest");

            profileIcon.setText("👤");
        }
    }

    private void applyModeTheme() {

        if (isDark) {

            rootModes.setBackgroundColor(
                    Color.parseColor("#0D0F1A")
            );

            btnTheme.setText("☀");

            btnTheme.setTextColor(Color.WHITE);

            btnTheme.setBackground(
                    round("#000000", "#000000")
            );

            btnBackModes.setTextColor(Color.WHITE);

            btnBackModes.setBackground(
                    round("#000000", "#000000")
            );

            txtGreeting.setTextColor(
                    Color.parseColor("#CCCCCC")
            );

            txtUserEmail.setTextColor(Color.WHITE);

            profileIcon.setTextColor(Color.WHITE);

            profileIcon.setBackgroundResource(
                    R.drawable.bg_circle
            );

            cardDiscover.setBackground(
                    round("#0FA3A0", "#0FA3A0")
            );

            cardAdd.setBackground(
                    round("#B999F2", "#B999F2")
            );

            cardDelete.setBackground(
                    round("#F7D84A", "#F7D84A")
            );

            setTexts(
                    Color.WHITE,
                    Color.parseColor("#F1F1F1")
            );

            titleTwoWay.setTextColor(Color.WHITE);

            subTwoWay.setTextColor(Color.WHITE);

        } else {

            rootModes.setBackgroundColor(Color.WHITE);

            btnTheme.setText("☾");

            btnTheme.setTextColor(Color.BLACK);

            btnTheme.setBackground(
                    round("#FFFFFF", "#E5E7EB")
            );

            btnBackModes.setTextColor(Color.BLACK);

            btnBackModes.setBackground(
                    round("#FFFFFF", "#E5E7EB")
            );

            txtGreeting.setTextColor(
                    Color.parseColor("#666666")
            );

            txtUserEmail.setTextColor(
                    Color.parseColor("#111111")
            );

            profileIcon.setTextColor(Color.WHITE);

            profileIcon.setBackgroundResource(
                    R.drawable.bg_circle
            );

            cardDiscover.setBackground(
                    round("#0FA3A0", "#0FA3A0")
            );

            cardAdd.setBackground(
                    round("#C7A7F5", "#C7A7F5")
            );

            cardDelete.setBackground(
                    round("#F9DF55", "#F9DF55")
            );

            setTexts(
                    Color.parseColor("#111111"),
                    Color.parseColor("#444444")
            );

            titleTwoWay.setTextColor(Color.BLACK);

            subTwoWay.setTextColor(Color.BLACK);
        }
    }

    private void setTexts(
            int titleColor,
            int subColor
    ) {

        titleDiscover.setTextColor(titleColor);
        subDiscover.setTextColor(subColor);

        titleAdd.setTextColor(titleColor);
        subAdd.setTextColor(subColor);

        titleDelete.setTextColor(titleColor);
        subDelete.setTextColor(subColor);
    }

    private GradientDrawable round(
            String color,
            String stroke
    ) {

        GradientDrawable d =
                new GradientDrawable();

        d.setColor(Color.parseColor(color));

        d.setCornerRadius(
                24 * getResources()
                        .getDisplayMetrics()
                        .density
        );

        d.setStroke(
                1,
                Color.parseColor(stroke)
        );

        return d;
    }
}