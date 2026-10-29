package com.example.silentvoice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silentvoice.api.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteModeActivity extends AppCompatActivity {

    ImageView deleteBackground;
    TextView btnThemeToggle;
    EditText editModeName;
    Button btnDeleteMode;

    TextView profileCircle, txtGreeting, txtUserName;
    TextView btnBackDelete;

    boolean isDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_mode);

        isDarkMode = ThemeManager.isDarkMode(this);

        deleteBackground = findViewById(R.id.deleteBackground);
        btnThemeToggle = findViewById(R.id.btnThemeToggle);
        editModeName = findViewById(R.id.editModeName);
        btnDeleteMode = findViewById(R.id.btnDeleteMode);

        profileCircle = findViewById(R.id.profileCircle);
        txtGreeting = findViewById(R.id.txtGreeting);
        txtUserName = findViewById(R.id.txtUserName);

        btnBackDelete = findViewById(R.id.btnBackDelete);

        setupUserProfile();
        applyTheme();

        profileCircle.setOnClickListener(v -> {
            Intent intent = new Intent(DeleteModeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        btnBackDelete.setOnClickListener(v -> finish());

        btnThemeToggle.setOnClickListener(v -> {
            isDarkMode = !isDarkMode;

            ThemeManager.saveTheme(
                    DeleteModeActivity.this,
                    isDarkMode
            );

            applyTheme();
        });

        btnDeleteMode.setOnClickListener(v -> deleteMode());
    }

    @Override
    protected void onResume() {
        super.onResume();

        setupUserProfile();

        isDarkMode = ThemeManager.isDarkMode(this);

        applyTheme();
    }

    private void setupUserProfile() {

        SharedPreferences profilePrefs =
                getSharedPreferences("USER_PROFILE", MODE_PRIVATE);

        SharedPreferences userPrefs =
                getSharedPreferences("USER_DATA", MODE_PRIVATE);

        String savedName = profilePrefs.getString(
                "name",
                userPrefs.getString("username", "Guest")
        );

        String savedEmail = profilePrefs.getString(
                "email",
                userPrefs.getString("email", "")
        );

        txtGreeting.setText("Good Afternoon");

        if (savedEmail != null && !savedEmail.trim().isEmpty()) {

            txtUserName.setText(savedEmail);

            if (savedName != null
                    && !savedName.trim().isEmpty()
                    && !savedName.equals("Guest")) {

                profileCircle.setText(savedName.substring(0, 1).toUpperCase());

            } else {

                profileCircle.setText(savedEmail.substring(0, 1).toUpperCase());
            }

        } else if (savedName != null
                && !savedName.trim().isEmpty()
                && !savedName.equals("Guest")) {

            txtUserName.setText(savedName);
            profileCircle.setText(savedName.substring(0, 1).toUpperCase());

        } else {

            txtUserName.setText("Guest");
            profileCircle.setText("👤");
        }
    }

    private void deleteMode() {

        String modeName = editModeName.getText().toString().trim();

        if (modeName.isEmpty()) {
            Toast.makeText(
                    this,
                    "Enter mode name first",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        SharedPreferences userPrefs =
                getSharedPreferences("USER_DATA", MODE_PRIVATE);

        int userId = userPrefs.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(
                    this,
                    "User not found",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        ApiService apiService =
                RetrofitClientSpring
                        .getClient()
                        .create(ApiService.class);

        apiService.deleteMode(userId, modeName)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(
                            Call<Void> call,
                            Response<Void> response
                    ) {

                        if (response.isSuccessful()) {

                            Toast.makeText(
                                    DeleteModeActivity.this,
                                    "Mode deleted successfully",
                                    Toast.LENGTH_SHORT
                            ).show();

                            editModeName.setText("");

                            setResult(RESULT_OK);
                            finish();

                        } else {

                            Toast.makeText(
                                    DeleteModeActivity.this,
                                    "Mode not found",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<Void> call,
                            Throwable t
                    ) {

                        Toast.makeText(
                                DeleteModeActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void applyTheme() {

        if (isDarkMode) {

            deleteBackground.setImageResource(R.drawable.delete_mode_dark);

            btnThemeToggle.setText("☀");
            btnThemeToggle.setTextColor(Color.WHITE);
            btnThemeToggle.setBackgroundColor(Color.BLACK);

            txtGreeting.setTextColor(Color.parseColor("#CCCCCC"));
            txtUserName.setTextColor(Color.WHITE);
            profileCircle.setTextColor(Color.WHITE);

            btnBackDelete.setTextColor(Color.WHITE);
            btnBackDelete.setBackgroundColor(Color.BLACK);

        } else {

            deleteBackground.setImageResource(R.drawable.delete_mode_light);

            btnThemeToggle.setText("☾");
            btnThemeToggle.setTextColor(Color.BLACK);
            btnThemeToggle.setBackgroundColor(Color.WHITE);

            txtGreeting.setTextColor(Color.parseColor("#666666"));
            txtUserName.setTextColor(Color.parseColor("#111111"));
            profileCircle.setTextColor(Color.WHITE);

            btnBackDelete.setTextColor(Color.BLACK);
            btnBackDelete.setBackgroundColor(Color.WHITE);
        }
    }
}