package com.example.silentvoice;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silentvoice.api.ApiService;
import com.example.silentvoice.api.FeedbackRequest;
import com.example.silentvoice.api.FeedbackResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackActivity extends AppCompatActivity {

    private ScrollView feedbackRoot;

    private TextView txtQuestion;
    private TextView txtFace;
    private TextView txtMood;

    private TextView btnThemeFeedback;
    private TextView btnBackFeedback;

    private Button btnBad;
    private Button btnUgh;
    private Button btnOk;
    private Button btnGood;
    private Button btnSubmit;

    private String selectedMood = "GOOD";

    private boolean isDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        isDarkMode = ThemeManager.isDarkMode(this);

        feedbackRoot = findViewById(R.id.feedbackRoot);

        txtQuestion = findViewById(R.id.txtQuestion);
        txtFace = findViewById(R.id.txtFace);
        txtMood = findViewById(R.id.txtMood);

        btnThemeFeedback = findViewById(R.id.btnThemeFeedback);
        btnBackFeedback = findViewById(R.id.btnBackFeedback);

        btnBad = findViewById(R.id.btnBad);
        btnUgh = findViewById(R.id.btnUgh);
        btnOk = findViewById(R.id.btnOk);
        btnGood = findViewById(R.id.btnGood);

        btnSubmit = findViewById(R.id.btnSubmit);

        applyTheme();

        selectMood("GOOD");

        btnBad.setOnClickListener(v -> selectMood("BAD"));

        btnUgh.setOnClickListener(v -> selectMood("UGH"));

        btnOk.setOnClickListener(v -> selectMood("OK"));

        btnGood.setOnClickListener(v -> selectMood("GOOD"));

        btnSubmit.setOnClickListener(v -> submitFeedback());

        btnBackFeedback.setOnClickListener(v -> finish());

        btnThemeFeedback.setOnClickListener(v -> {

            isDarkMode = !isDarkMode;

            ThemeManager.saveTheme(
                    FeedbackActivity.this,
                    isDarkMode
            );

            applyTheme();
        });
    }

    private void applyTheme() {

        if (isDarkMode) {

            feedbackRoot.setBackgroundColor(0xFF070B1A);

            txtQuestion.setTextColor(0xFFFFFFFF);

            btnThemeFeedback.setText("☀");
            btnThemeFeedback.setTextColor(0xFFFFFFFF);

            btnThemeFeedback.setBackgroundResource(
                    R.drawable.bg_theme_toggle
            );

            btnBackFeedback.setTextColor(0xFFFFFFFF);

            btnBackFeedback.setBackgroundResource(
                    R.drawable.bg_theme_toggle
            );

        } else {

            feedbackRoot.setBackgroundColor(0xFFFFFFFF);

            txtQuestion.setTextColor(0xFF222222);

            btnThemeFeedback.setText("☾");

            btnThemeFeedback.setTextColor(0xFF111827);

            btnThemeFeedback.setBackgroundResource(
                    R.drawable.bg_theme_toggle_light
            );

            btnBackFeedback.setTextColor(0xFF111827);

            btnBackFeedback.setBackgroundResource(
                    R.drawable.bg_theme_toggle_light
            );
        }
    }

    private void selectMood(String mood) {

        selectedMood = mood;

        int color;

        String face;

        switch (mood) {

            case "BAD":

                color = 0xFFFF6B6B;

                face = "😞";

                break;

            case "UGH":

                color = 0xFFFFD966;

                face = "😕";

                break;

            case "OK":

                color = 0xFF11C5D9;

                face = "😐";

                break;

            default:

                color = 0xFF14A7A4;

                face = "😊";

                break;
        }

        txtMood.setText(mood);

        txtMood.setTextColor(color);

        txtFace.setText(face);

        txtFace.setTextColor(color);

        btnSubmit.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(color)
        );
    }

    private void submitFeedback() {

        SharedPreferences userPrefs =
                getSharedPreferences("USER_DATA", MODE_PRIVATE);

        int userId = userPrefs.getInt("userId", -1);

        if (userId == -1) {

            Toast.makeText(
                    FeedbackActivity.this,
                    "User not found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        FeedbackRequest request =
                new FeedbackRequest(
                        userId,
                        selectedMood
                );

        ApiService api =
                RetrofitClientSpring
                        .getClient()
                        .create(ApiService.class);

        api.submitFeedback(request)
                .enqueue(new Callback<FeedbackResponse>() {

                    @Override
                    public void onResponse(
                            Call<FeedbackResponse> call,
                            Response<FeedbackResponse> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            Toast.makeText(
                                    FeedbackActivity.this,
                                    response.body().getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();

                            finish();

                        } else {

                            Toast.makeText(
                                    FeedbackActivity.this,
                                    "Feedback failed",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<FeedbackResponse> call,
                            Throwable t
                    ) {

                        Toast.makeText(
                                FeedbackActivity.this,
                                "Connection Failed: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        isDarkMode = ThemeManager.isDarkMode(this);

        applyTheme();
    }
}