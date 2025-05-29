package com.example.dailymate.Data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.dailymate.Data.entity.Note;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "activities.db";
    private static final int DB_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE activities (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT, time LONG)");
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email TEXT, photo TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Tambahkan kolom photo jika belum ada
            db.execSQL("ALTER TABLE users ADD COLUMN photo TEXT");
        }
        // Jika nanti ada versi upgrade lebih tinggi, bisa ditambah di sini
    }

    // Simpan pengguna
    public long insertUser(String name, String email) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        return db.insert("users", null, values);
    }

    // Ambil pengguna pertama
    public String getUserName() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM users LIMIT 1", null);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(0);
            cursor.close();
            return name;
        }
        return "Nama Pengguna";
    }

    public void updateUserName(String name) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users LIMIT 1", null);
        if (cursor.moveToFirst()) {
            // Update jika sudah ada pengguna
            ContentValues values = new ContentValues();
            values.put("name", name);
            int id = cursor.getInt(0);
            db.update("users", values, "id = ?", new String[]{String.valueOf(id)});
        } else {
            // Insert jika belum ada pengguna
            insertUser(name, "default@email.com");
        }
        cursor.close();
    }

    public void updateUserPhoto(String photoUri) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("photo", photoUri);
        db.update("users", values, null, null);
    }

    public String getUserPhoto() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT photo FROM users LIMIT 1", null);
        if (cursor.moveToFirst()) {
            String photo = cursor.getString(0);
            cursor.close();
            return photo;
        }
        return null;
    }

    // Menambahkan aktivitas baru
    public long insertActivity(String title, String description, long time) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("time", time);
        return db.insert("activities", null, values);
    }

    // Mendapatkan semua aktivitas
    public List<Note> getAllActivities() {
        List<Note> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM activities ORDER BY time ASC", null);
        while (cursor.moveToNext()) {
            list.add(new Note(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getLong(3)
            ));
        }
        cursor.close();
        return list;
    }

    // Mendapatkan aktivitas berdasarkan ID
    public Note getActivityById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM activities WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor != null && cursor.moveToFirst()) {
            Note activity = new Note(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getLong(3)
            );
            cursor.close();
            return activity;
        }
        return null;
    }

    // Memperbarui aktivitas berdasarkan ID
    public void updateActivity(long id, String title, String description, long time) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("time", time);

        db.update("activities", values, "id = ?", new String[]{String.valueOf(id)});
    }

    // Hapus aktivitas berdasarkan ID
    public void deleteActivity(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("activities", "id = ?", new String[]{String.valueOf(id)});
    }
}