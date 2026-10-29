package com.example.silentvoice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.silentvoice.api.ApiService;
import com.example.silentvoice.api.LoginRequest;
import com.example.silentvoice.api.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    ImageView btnEye, bgLogin, iconEmail, iconLock;
    Button btnLogin;
    TextView btnThemeLogin, btnSignup, tvLoginTitle, tvLoginSubtitle, tvNoAccount;
    LinearLayout cardLogin, inputEmailBox, inputPasswordBox;
    RelativeLayout root;

    boolean isPasswordVisible = false;
    boolean isDark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        isDark = ThemeManager.isDarkMode(this);

        root = findViewById(R.id.rootLogin);
        bgLogin = findViewById(R.id.bgLogin);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnEye = findViewById(R.id.btnEye);
        btnLogin = findViewById(R.id.btnLogin);
        btnThemeLogin = findViewById(R.id.btnThemeLogin);
        btnSignup = findViewById(R.id.btnSignup);

        cardLogin = findViewById(R.id.cardLogin);
        inputEmailBox = findViewById(R.id.inputEmailBox);
        inputPasswordBox = findViewById(R.id.inputPasswordBox);

        iconEmail = findViewById(R.id.iconEmail);
        iconLock = findViewById(R.id.iconLock);

        tvLoginTitle = findViewById(R.id.tvLoginTitle);
        tvLoginSubtitle = findViewById(R.id.tvLoginSubtitle);
        tvNoAccount = findViewById(R.id.tvNoAccount);

        applyTheme();

        btnSignup.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, SignInActivity.class))
        );

        btnEye.setOnClickListener(v -> togglePassword());

        btnThemeLogin.setOnClickListener(v -> {

            isDark = !isDark;

            ThemeManager.saveTheme(
                    LoginActivity.this,
                    isDark
            );

            applyTheme();
        });
        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void togglePassword() {
        if (isPasswordVisible) {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            isPasswordVisible = false;
        } else {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            isPasswordVisible = true;
        }

        etPassword.setSelection(etPassword.getText().length());
    }

    private void loginUser() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fill all fields ⚠️", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("PLEASE WAIT...");

        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        LoginRequest request = new LoginRequest(email, password);

        api.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                btnLogin.setEnabled(true);
                btnLogin.setText("LOGIN");

                Log.d("LOGIN_DEBUG", "HTTP CODE: " + response.code());

                if (response.isSuccessful() && response.body() != null) {

                    LoginResponse loginResponse = response.body();

                    Log.d("LOGIN_DEBUG", "BODY: " + loginResponse.toString());

                    if ("SUCCESS".equalsIgnoreCase(loginResponse.getStatus())) {

                        String savedEmail = loginResponse.getEmail();
                        if (savedEmail == null || savedEmail.trim().isEmpty()) {
                            savedEmail = email;
                        }

                        String savedName = loginResponse.getFullName();
                        if (savedName == null || savedName.trim().isEmpty()) {
                            savedName = savedEmail.split("@")[0];
                        }

                        String savedPhone = loginResponse.getPhone();
                        if (savedPhone == null) {
                            savedPhone = "";
                        }

                        SharedPreferences profilePrefs =
                                getSharedPreferences("USER_PROFILE", MODE_PRIVATE);

                        profilePrefs.edit()
                                .putString("name", savedName)
                                .putString("email", savedEmail)
                                .putString("phone", savedPhone)
                                .apply();

                        SharedPreferences userPrefs =
                                getSharedPreferences("USER_DATA", MODE_PRIVATE);

                        userPrefs.edit()
                                .putString("username", savedName)
                                .putString("email", savedEmail)
                                .putInt("userId", loginResponse.getUserId())
                                .apply();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("name", savedName);
                        intent.putExtra("email", savedEmail);
                        intent.putExtra("userId", loginResponse.getUserId());
                        startActivity(intent);
                        finish();

                    } else {
                        String msg = loginResponse.getMessage();
                        if (msg == null || msg.trim().isEmpty()) {
                            msg = "Wrong email or password";
                        }

                        Toast.makeText(
                                LoginActivity.this,
                                msg,
                                Toast.LENGTH_LONG
                        ).show();
                    }

                } else {
                    try {
                        String error = response.errorBody() != null
                                ? response.errorBody().string()
                                : "Unknown error";

                        Log.e("LOGIN_DEBUG", "ERROR BODY: " + error);

                        Toast.makeText(
                                LoginActivity.this,
                                "Login failed: " + response.code() + "\n" + error,
                                Toast.LENGTH_LONG
                        ).show();

                    } catch (Exception e) {
                        Toast.makeText(
                                LoginActivity.this,
                                "Login failed",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

                btnLogin.setEnabled(true);
                btnLogin.setText("LOGIN");

                Log.e("LOGIN_DEBUG", "FAILURE", t);

                Toast.makeText(
                        LoginActivity.this,
                        "Connection Failed: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void applyTheme() {

        if (isDark) {

            bgLogin.setImageResource(R.drawable.login_bg_dark);
            root.setBackgroundColor(Color.parseColor("#050B16"));

            btnThemeLogin.setText("☀");
            btnThemeLogin.setTextColor(Color.WHITE);
            btnThemeLogin.setBackgroundResource(R.drawable.bg_theme_toggle);

            cardLogin.setBackground(makeRound("#E61B1930", "#443C78", 24));

            tvLoginTitle.setTextColor(Color.parseColor("#B8A7FF"));
            tvLoginSubtitle.setTextColor(Color.parseColor("#D8D5E8"));
            tvNoAccount.setTextColor(Color.parseColor("#A8A3B8"));
            btnSignup.setTextColor(Color.parseColor("#B8A7FF"));

            iconEmail.setColorFilter(Color.parseColor("#B8A7FF"));
            iconLock.setColorFilter(Color.parseColor("#B8A7FF"));
            btnEye.setColorFilter(Color.parseColor("#C6C0D8"));

            styleInput(etEmail, inputEmailBox, "#251F3D", "#4E4380", "#FFFFFF", "#B7B2C8");
            styleInput(etPassword, inputPasswordBox, "#251F3D", "#4E4380", "#FFFFFF", "#B7B2C8");

        } else {

            bgLogin.setImageResource(R.drawable.login_bg_light);
            root.setBackgroundColor(Color.WHITE);

            btnThemeLogin.setText("☾");
            btnThemeLogin.setTextColor(Color.parseColor("#111827"));
            btnThemeLogin.setBackgroundResource(R.drawable.bg_theme_toggle_light);

            cardLogin.setBackground(makeRound("#EEFFFFFF", "#E6D8FF", 24));

            tvLoginTitle.setTextColor(Color.parseColor("#7C5CFF"));
            tvLoginSubtitle.setTextColor(Color.parseColor("#7A7288"));
            tvNoAccount.setTextColor(Color.parseColor("#9CA3AF"));
            btnSignup.setTextColor(Color.parseColor("#7C5CFF"));

            iconEmail.setColorFilter(Color.parseColor("#7C5CFF"));
            iconLock.setColorFilter(Color.parseColor("#7C5CFF"));
            btnEye.setColorFilter(Color.parseColor("#999999"));

            styleInput(etEmail, inputEmailBox, "#F8F4FF", "#E6D8FF", "#111827", "#9CA3AF");
            styleInput(etPassword, inputPasswordBox, "#F8F4FF", "#E6D8FF", "#111827", "#9CA3AF");
        }
    }

    private void styleInput(EditText editText,
                            LinearLayout box,
                            String bgColor,
                            String strokeColor,
                            String textColor,
                            String hintColor) {

        box.setBackground(makeRound(bgColor, strokeColor, 18));
        editText.setTextColor(Color.parseColor(textColor));
        editText.setHintTextColor(Color.parseColor(hintColor));
    }

    private GradientDrawable makeRound(String color, String strokeColor, int radiusDp) {

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor(color));
        drawable.setCornerRadius(radiusDp * getResources().getDisplayMetrics().density);
        drawable.setStroke(1, Color.parseColor(strokeColor));

        return drawable;
    }
}