package com.example.silentvoice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silentvoice.api.ApiService;
import com.example.silentvoice.api.ModeResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscoverActivity extends AppCompatActivity {

    boolean isDark;

    ScrollView rootDiscover;

    TextView btnBackDiscover;
    TextView btnThemeDiscover;
    TextView tvRecommended;

    TextView profileCircle;
    TextView txtGreeting;
    TextView txtUserName;

    LinearLayout modesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        isDark = ThemeManager.isDarkMode(this);

        rootDiscover = findViewById(R.id.rootDiscover);

        btnBackDiscover = findViewById(R.id.btnBackDiscover);

        btnThemeDiscover = findViewById(R.id.btnThemeDiscover);

        tvRecommended = findViewById(R.id.tvRecommended);

        profileCircle = findViewById(R.id.profileCircle);

        txtGreeting = findViewById(R.id.txtGreeting);

        txtUserName = findViewById(R.id.txtUserName);

        modesList = findViewById(R.id.modesList);

        setupUserProfile();

        profileCircle.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            DiscoverActivity.this,
                            ProfileActivity.class
                    );

            startActivity(intent);
        });

        btnBackDiscover.setOnClickListener(v -> finish());

        btnThemeDiscover.setOnClickListener(v -> {

            isDark = !isDark;

            ThemeManager.saveTheme(
                    DiscoverActivity.this,
                    isDark
            );

            applyDiscoverTheme();
        });

        applyDiscoverTheme();

        loadModes();

    }

    @Override
    protected void onResume() {
        super.onResume();

        setupUserProfile();

        isDark = ThemeManager.isDarkMode(this);

        applyDiscoverTheme();

        loadModes();
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

            txtUserName.setText(savedEmail);

            if (savedName != null
                    && !savedName.trim().isEmpty()
                    && !savedName.equals("Guest")) {

                profileCircle.setText(
                        savedName.substring(0, 1)
                                .toUpperCase()
                );

            } else {

                profileCircle.setText(
                        savedEmail.substring(0, 1)
                                .toUpperCase()
                );
            }

        } else if (savedName != null
                && !savedName.trim().isEmpty()
                && !savedName.equals("Guest")) {

            txtUserName.setText(savedName);

            profileCircle.setText(
                    savedName.substring(0, 1)
                            .toUpperCase()
            );

        } else {

            txtUserName.setText("Guest");

            profileCircle.setText("👤");
        }
    }

    private void goToMain(String mode, long modeId) {

        SharedPreferences userPrefs =
                getSharedPreferences("USER_DATA", MODE_PRIVATE);

        int userId = userPrefs.getInt("userId", -1);

        Intent intent =
                new Intent(
                        DiscoverActivity.this,
                        MainActivity.class
                );

        intent.putExtra("mode", mode);
        intent.putExtra("modeId", modeId);
        intent.putExtra("userId", userId);
        intent.putExtra("fromDiscover", true);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);

        finish();
    }

    private void loadModes() {

        SharedPreferences userPrefs =
                getSharedPreferences(
                        "USER_DATA",
                        MODE_PRIVATE
                );

        int userId = userPrefs.getInt("userId", -1);

        if (userId == -1) {

            modesList.removeAllViews();

            return;
        }

        ApiService apiService =
                RetrofitClientSpring
                        .getClient()
                        .create(ApiService.class);

        apiService.getUserModes(userId)
                .enqueue(new Callback<List<ModeResponse>>() {

                    @Override
                    public void onResponse(
                            Call<List<ModeResponse>> call,
                            Response<List<ModeResponse>> response
                    ) {

                        modesList.removeAllViews();

                        if (response.isSuccessful()
                                && response.body() != null) {

                            List<ModeResponse> modes =
                                    response.body();

                            if (modes.isEmpty()) {
                                return;
                            }

                            LinearLayout row = null;

                            for (int i = 0; i < modes.size(); i++) {

                                if (i % 3 == 0) {

                                    row = createRow();

                                    modesList.addView(row);
                                }

                                row.addView(
                                        createModeCard(modes.get(i))
                                );
                            }
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<ModeResponse>> call,
                            Throwable t
                    ) {

                        modesList.removeAllViews();
                    }
                });
    }

    private LinearLayout createRow() {

        LinearLayout row =
                new LinearLayout(
                        DiscoverActivity.this
                );

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        LinearLayout.LayoutParams rowParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        rowParams.setMargins(0, 0, 0, dp(12));

        row.setLayoutParams(rowParams);

        return row;
    }

    private LinearLayout createModeCard(ModeResponse mode) {

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setGravity(Gravity.CENTER);

        card.setPadding(
                dp(12),
                dp(12),
                dp(12),
                dp(12)
        );

        card.setBackground(
                round(getModeColor(mode.getName()))
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        0,
                        dp(135),
                        1
                );

        params.setMargins(
                dp(6),
                0,
                dp(6),
                0
        );

        card.setLayoutParams(params);

        ImageView icon = new ImageView(this);

        icon.setImageResource(
                getModeIcon(mode.getName())
        );

        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(
                        dp(45),
                        dp(45)
                );

        icon.setLayoutParams(iconParams);

        TextView title = new TextView(this);

        title.setText(mode.getName());

        title.setTextColor(Color.WHITE);

        title.setTextSize(14);

        title.setGravity(Gravity.CENTER);

        title.setTypeface(null, Typeface.BOLD);

        title.setMaxLines(2);

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        titleParams.setMargins(
                0,
                dp(10),
                0,
                0
        );

        title.setLayoutParams(titleParams);

        card.addView(icon);

        card.addView(title);

        card.setOnClickListener(v ->
                goToMain(
                        mode.getName(),
                        mode.getId()
                )
        );

        return card;
    }

    private String getModeColor(String modeName) {

        if (modeName == null)
            return "#7B61FF";

        String name = modeName.toLowerCase();

        if (name.contains("home"))
            return "#12A8A8";

        if (name.contains("emergency"))
            return "#F56C6C";

        if (name.contains("health"))
            return "#C7A7F5";

        if (name.contains("education")
                || name.contains("school"))
            return "#F5E663";

        if (name.contains("work"))
            return "#6D98D3";

        if (name.contains("food"))
            return "#9D7CFF";

        if (name.contains("fun"))
            return "#FF8CC8";

        if (name.contains("travel"))
            return "#6ED3CF";

        if (name.contains("default"))
            return "#7B61FF";

        return "#7B61FF";
    }

    private int getModeIcon(String modeName) {

        if (modeName == null)
            return R.drawable.ic_default;

        String name = modeName.toLowerCase();

        if (name.contains("home"))
            return R.drawable.ic_home;

        if (name.contains("emergency"))
            return R.drawable.ic_emergency;

        if (name.contains("health"))
            return R.drawable.ic_health;

        if (name.contains("education")
                || name.contains("school"))
            return R.drawable.ic_education;

        if (name.contains("travel"))
            return R.drawable.ic_travel;

        if (name.contains("work"))
            return R.drawable.ic_work;

        if (name.contains("fun"))
            return R.drawable.ic_fun;

        if (name.contains("food"))
            return R.drawable.ic_food;

        return R.drawable.ic_default;
    }

    private void applyDiscoverTheme() {

        if (isDark) {

            rootDiscover.setBackgroundColor(
                    Color.parseColor("#0D0F1A")
            );

            btnThemeDiscover.setText("☀");

            btnThemeDiscover.setTextColor(Color.WHITE);

            btnThemeDiscover.setBackground(
                    round("#000000")
            );

            btnBackDiscover.setTextColor(Color.WHITE);

            btnBackDiscover.setBackground(
                    round("#000000")
            );

            tvRecommended.setTextColor(
                    Color.parseColor("#A0A3BD")
            );

            txtGreeting.setTextColor(
                    Color.parseColor("#CCCCCC")
            );

            txtUserName.setTextColor(Color.WHITE);

        } else {

            rootDiscover.setBackgroundColor(Color.WHITE);

            btnThemeDiscover.setText("☾");

            btnThemeDiscover.setTextColor(Color.BLACK);

            btnThemeDiscover.setBackground(
                    round("#FFFFFF")
            );

            btnBackDiscover.setTextColor(Color.BLACK);

            btnBackDiscover.setBackground(
                    round("#FFFFFF")
            );

            tvRecommended.setTextColor(
                    Color.parseColor("#666666")
            );

            txtGreeting.setTextColor(
                    Color.parseColor("#666666")
            );

            txtUserName.setTextColor(
                    Color.parseColor("#111111")
            );
        }

        profileCircle.setTextColor(Color.WHITE);
    }

    private GradientDrawable round(String color) {

        GradientDrawable d =
                new GradientDrawable();

        d.setColor(Color.parseColor(color));

        d.setCornerRadius(
                18 * getResources()
                        .getDisplayMetrics()
                        .density
        );

        return d;
    }

    private int dp(int value) {

        return (int)
                (value *
                        getResources()
                                .getDisplayMetrics()
                                .density);
    }
}