package com.example.silentvoice;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager2.widget.ViewPager2;

import java.util.Arrays;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    LinearLayout layoutDots;
    TextView btnTheme;
    ConstraintLayout root;
    Button btnNext;
    TextView txtSignIn;

    boolean isDark;

    ViewPager2 viewPager;
    OnboardingAdapter adapter;

    List<String> descriptions;
    List<Integer> lightImages;
    List<Integer> darkImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        isDark = ThemeManager.isDarkMode(this);

        root = findViewById(R.id.rootWelcome);
        btnTheme = findViewById(R.id.btnTheme);
        viewPager = findViewById(R.id.viewPager);

        layoutDots = findViewById(R.id.layoutDots);
        btnNext = findViewById(R.id.btnNext);
        txtSignIn = findViewById(R.id.txtSignIn);

        txtSignIn.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            WelcomeActivity.this,
                            LoginActivity.class
                    );

            startActivity(intent);
        });

        btnNext.setOnClickListener(v -> {

            if (viewPager.getCurrentItem() == descriptions.size() - 1) {

                Intent intent =
                        new Intent(
                                WelcomeActivity.this,
                                SignInActivity.class
                        );

                startActivity(intent);

                finish();

            } else {

                viewPager.setCurrentItem(
                        viewPager.getCurrentItem() + 1,
                        true
                );
            }
        });

        lightImages = Arrays.asList(
                R.drawable.onboard_light_1,
                R.drawable.onboard_light_2,
                R.drawable.onboard_light_3
        );

        darkImages = Arrays.asList(
                R.drawable.onboard_dark_1,
                R.drawable.onboard_dark_2,
                R.drawable.onboard_dark_3
        );

        descriptions = Arrays.asList(
                "Turn hand gestures into clear voice and meaning.",
                "SilentVoice recognizes sign language in real-time and helps you understand without barriers.",
                "Join SilentVoice today and unlock a new way to communicate, understand, and connect."
        );

        applyTheme();

        adapter = new OnboardingAdapter(
                lightImages,
                darkImages,
                descriptions,
                isDark
        );

        viewPager.setAdapter(adapter);

        viewPager.setOrientation(
                ViewPager2.ORIENTATION_HORIZONTAL
        );

        viewPager.setUserInputEnabled(true);

        viewPager.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {

                    @Override
                    public void onPageSelected(int position) {

                        super.onPageSelected(position);

                        updateDots(position);

                        if (position == descriptions.size() - 1) {

                            btnNext.setText("Get Started →");

                        } else {

                            btnNext.setText("Next →");
                        }
                    }
                });

        btnTheme.setOnClickListener(v -> {

            isDark = !isDark;

            ThemeManager.saveTheme(
                    WelcomeActivity.this,
                    isDark
            );

            int currentPosition = viewPager.getCurrentItem();

            applyTheme();

            adapter = new OnboardingAdapter(
                    lightImages,
                    darkImages,
                    descriptions,
                    isDark
            );

            viewPager.setAdapter(adapter);

            viewPager.setCurrentItem(
                    currentPosition,
                    false
            );
        });
    }

    private void applyTheme() {

        if (isDark) {

            root.setBackgroundColor(
                    Color.parseColor("#050B16")
            );

            btnTheme.setText("☀");

            btnTheme.setTextColor(Color.WHITE);

            btnTheme.setBackgroundResource(
                    R.drawable.bg_theme_toggle
            );

        } else {

            root.setBackgroundColor(Color.WHITE);

            btnTheme.setText("☾");

            btnTheme.setTextColor(
                    Color.parseColor("#111827")
            );

            btnTheme.setBackgroundResource(
                    R.drawable.bg_theme_toggle_light
            );
        }
    }

    private void updateDots(int position) {

        for (int i = 0; i < layoutDots.getChildCount(); i++) {

            View dot = layoutDots.getChildAt(i);

            if (i == position) {

                dot.setBackgroundResource(
                        R.drawable.dot_active
                );

            } else {

                dot.setBackgroundResource(
                        R.drawable.dot_inactive
                );
            }
        }
    }
}