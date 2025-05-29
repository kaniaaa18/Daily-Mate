package com.example.dailymate.Presentation.ui;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.dailymate.Data.database.DBHelper;
import com.example.dailymate.Presentation.ui.ReminderReceiver;
import com.example.dailymate.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddEditActivity extends AppCompatActivity {
    EditText editTitle, editDescription, editDate, editTime;
    Button btnSave;
    Button btnCamera, btnGallery;
    ImageView imageView;
    DBHelper dbHelper;
    long activityId = -1;
    Calendar selectedCalendar; // Simpan tanggal dan waktu yang dipilih

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        checkAndRequestPermissions();

        editTitle = findViewById(R.id.title);
        editDescription = findViewById(R.id.description);
        editDate = findViewById(R.id.date);
        editTime = findViewById(R.id.time);

        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);
        imageView = findViewById(R.id.imageView);

        btnSave = findViewById(R.id.save);

        selectedCalendar = Calendar.getInstance();

        editDate.setOnClickListener(v -> showDatePicker());
        editTime.setOnClickListener(v -> showTimePicker());

        dbHelper = new DBHelper(this);

        // MODE EDIT
        Intent intent = getIntent();
        activityId = intent.getIntExtra("activityId", -1);
        if (activityId != -1) {
            // Kalau activityId ada, berarti mode Edit
            editTitle.setText(intent.getStringExtra("title"));
            editDescription.setText(intent.getStringExtra("description"));

            long timeInMillis = intent.getLongExtra("time", System.currentTimeMillis());
            selectedCalendar.setTimeInMillis(timeInMillis);

            // Tampilkan tanggal dan jam yang lama di EditText
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
            editDate.setText(sdfDate.format(selectedCalendar.getTime()));
            editTime.setText(sdfTime.format(selectedCalendar.getTime()));

            // Tampilkan gambar
            String imageUriString = intent.getStringExtra("imageUri");
            if (imageUriString != null) {
                Uri imageUri = Uri.parse(imageUriString);
                imageView.setImageURI(imageUri);
            }
        }

        btnSave.setOnClickListener(view -> saveData());

        btnCamera.setOnClickListener(v -> {
            Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(camIntent, 100);
        });


        btnGallery.setOnClickListener(v -> {
            Intent galIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galIntent, 101);
        });
    }

    private void checkAndRequestPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.READ_MEDIA_IMAGES);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[0]), 1);
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
        }, 1);
    }

    private void showDatePicker() {
        new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedCalendar.set(Calendar.YEAR, year);
                    selectedCalendar.set(Calendar.MONTH, month);
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    editDate.setText(sdf.format(selectedCalendar.getTime()));
                },
                selectedCalendar.get(Calendar.YEAR),
                selectedCalendar.get(Calendar.MONTH),
                selectedCalendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void showTimePicker() {
        new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedCalendar.set(Calendar.MINUTE, minute);

                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    editTime.setText(sdf.format(selectedCalendar.getTime()));
                },
                selectedCalendar.get(Calendar.HOUR_OF_DAY),
                selectedCalendar.get(Calendar.MINUTE),
                true
        ).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 100) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(photo);
            } else if (requestCode == 101) {
                imageView.setImageURI(data.getData());
            }
        }
    }

    private void saveData() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String date = editDate.getText().toString().trim();
        String time = editTime.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Lengkapi semua data!", Toast.LENGTH_SHORT).show();
            return;
        }

        long timeInMillis = selectedCalendar.getTimeInMillis();

        if (activityId == -1) {
            dbHelper.insertActivity(title, description, timeInMillis);
            Toast.makeText(this, "Aktivitas berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.updateActivity(activityId, title, description, timeInMillis);
            Toast.makeText(this, "Aktivitas berhasil diperbarui!", Toast.LENGTH_SHORT).show();
        }

        setReminder(title, timeInMillis);
        finish();
    }

    private void setReminder(String title, long timeInMillis) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    startActivity(intent);
                    return;
                }
            }

            Intent intent = new Intent(this, ReminderReceiver.class);
            intent.putExtra("title", title);

            int requestCode = (int) System.currentTimeMillis(); // Unik dan positif

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
            );
        }
    }

    public boolean isInputValid(String title, String description, String date, String time) {
        return !title.trim().isEmpty() && !description.trim().isEmpty()
                && !date.trim().isEmpty() && !time.trim().isEmpty();
    }
}