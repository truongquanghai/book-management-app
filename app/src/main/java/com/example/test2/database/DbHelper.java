package com.example.test2.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "PNLib";
    private static final int DB_VERSION = 11; // Tăng lên 11 để đảm bảo database được chép lại từ assets
    private final Context mContext;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
        // Kiểm tra và copy database ngay khi khởi tạo
        checkAndCopyDatabase();
    }

    private void checkAndCopyDatabase() {
        File dbFile = mContext.getDatabasePath(DB_NAME);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("DATABASE_VERSION", Context.MODE_PRIVATE);
        int lastVersion = sharedPreferences.getInt("db_version", 0);

        if (!dbFile.exists() || DB_VERSION > lastVersion) {
            // Xóa database cũ nếu tồn tại để tránh xung đột khi ghi đè (SQLITE_READONLY_DBMOVED)
            if (dbFile.exists()) {
                mContext.deleteDatabase(DB_NAME);
            }
            copyAssetsToSystem(dbFile);
            sharedPreferences.edit().putInt("db_version", DB_VERSION).apply();
        }
    }

    private void copyAssetsToSystem(File dbFile) {
        try {
            // Đảm bảo thư mục databases tồn tại
            File parentFile = dbFile.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                parentFile.mkdirs();
            }

            // Mở file từ assets (phải đúng tên PNLib.db)
            InputStream is = mContext.getAssets().open(DB_NAME + ".db");
            OutputStream os = new FileOutputStream(dbFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
            os.close();
            is.close();
            Log.d("DbHelper", "Đã copy thành công database từ assets vào: " + dbFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e("DbHelper", "Lỗi copy database: " + e.getMessage());
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Không làm gì vì đã dùng database có sẵn
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            Log.d("DbHelper", "Nâng cấp database từ " + oldVersion + " lên " + newVersion);
            // File assets mới đã có sẵn các cột chuẩn (email, avatar), 
            // nên không cần chạy ALTER TABLE ở đây nữa.
        }
    }
}
