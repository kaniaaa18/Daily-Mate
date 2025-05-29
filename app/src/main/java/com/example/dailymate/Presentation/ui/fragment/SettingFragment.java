package com.example.dailymate.Presentation.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.dailymate.R;

public class SettingFragment extends Fragment {
    private Switch switchNotifications;
    private LinearLayout layoutTheme, layoutLogout;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_DARK_MODE = "dark_mode";

    public SettingFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        switchNotifications = view.findViewById(R.id.switch_notifications);
        layoutTheme = view.findViewById(R.id.setting_theme);
        layoutLogout = view.findViewById(R.id.setting_logout);

        prefs = requireContext().getSharedPreferences(PREFS_NAME, 0);

        // --- Set status switch notifikasi dari status aplikasi ---
        boolean notificationsEnabled = NotificationManagerCompat.from(requireContext()).areNotificationsEnabled();
        switchNotifications.setChecked(notificationsEnabled);

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Arahkan pengguna ke setting notifikasi aplikasi
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().getPackageName());
            } else {
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + requireContext().getPackageName()));
            }
            startActivity(intent);
            Toast.makeText(requireContext(), "Silakan ubah pengaturan notifikasi di sini", Toast.LENGTH_SHORT).show();

            // Reset switch ke status asli
            switchNotifications.setChecked(NotificationManagerCompat.from(requireContext()).areNotificationsEnabled());
        });

        // --- Tema ---
        boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false);
        layoutTheme.setOnClickListener(v -> {
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                prefs.edit().putBoolean(KEY_DARK_MODE, false).apply();
                Toast.makeText(requireContext(), "Tema Light diaktifkan", Toast.LENGTH_SHORT).show();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                prefs.edit().putBoolean(KEY_DARK_MODE, true).apply();
                Toast.makeText(requireContext(), "Tema Dark diaktifkan", Toast.LENGTH_SHORT).show();
            }
        });

        // --- Logout ---
        layoutLogout.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            Toast.makeText(requireContext(), "Berhasil keluar", Toast.LENGTH_SHORT).show();

            // Intent ke LoginActivity
            Intent intent = new Intent(requireContext(), com.example.dailymate.Presentation.ui.LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}