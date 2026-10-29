package com.example.silentvoice;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class SOSMonitorService extends Service {

    Handler handler = new Handler();
    boolean sosOpened = false;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            checkSOS();

            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        handler.removeCallbacks(runnable);

        boolean enabled = intent != null &&
                intent.getBooleanExtra("enable_sos_monitor", false);

        if (enabled) {
            handler.post(runnable);
        } else {
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    private void checkSOS() {

        new Thread(() -> {

            try {

                java.net.URL url =
                        new java.net.URL("http://10.0.2.2:8180/data");

                java.net.HttpURLConnection connection =
                        (java.net.HttpURLConnection) url.openConnection();

                java.io.BufferedReader reader =
                        new java.io.BufferedReader(
                                new java.io.InputStreamReader(
                                        connection.getInputStream()
                                )
                        );

                String line = reader.readLine();

                reader.close();

                if (line != null) {

                    String[] v = line.split(",");

                    int sos =
                            v.length > 11
                                    ? Integer.parseInt(v[11].trim())
                                    : 0;

                    if (sos == 1 && !sosOpened) {

                        sosOpened = true;

                        Intent intent =
                                new Intent(
                                        SOSMonitorService.this,
                                        SOSActivity.class
                                );

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(intent);
                    }

                    if (sos == 0) {
                        sosOpened = false;
                    }
                }

            } catch (Exception e) {

                // Wokwi may be off.
                // Ignore silently so manual mode stays clean.
            }

        }).start();
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}