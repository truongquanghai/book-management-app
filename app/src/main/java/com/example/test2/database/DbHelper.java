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
    private static final int DB_VERSION = 14; // Nâng lên 14 để thêm tienPhat và ghiChu vào PhieuMuon
    private final Context mContext;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
        // Kiểm tra và copy database ngay khi khởi tạo
        checkAndCopyDatabase();
    }

    private void checkAndCopyDatabase() {
        File dbFile = mContext.getDatabasePath(DB_NAME);
        if (!dbFile.exists()) {
            copyAssetsToSystem(dbFile);
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
            
            // Cập nhật version 12 -> 13: Thêm soLuong vào Sach và PhieuMuon
            if (oldVersion < 13) {
                try {
                    db.execSQL("ALTER TABLE Sach ADD COLUMN soLuong INTEGER DEFAULT 0");
                } catch (Exception e) {
                    Log.e("DbHelper", "Cột soLuong trong Sach đã tồn tại: " + e.getMessage());
                }
                try {
                    db.execSQL("ALTER TABLE PhieuMuon ADD COLUMN soLuongMuon INTEGER DEFAULT 1");
                } catch (Exception e) {
                    Log.e("DbHelper", "Cột soLuongMuon trong PhieuMuon đã tồn tại: " + e.getMessage());
                }
            }
            
            // Cập nhật version 13 -> 14: Thêm tienPhat và ghiChu vào PhieuMuon
            if (oldVersion < 14) {
                try {
                    db.execSQL("ALTER TABLE PhieuMuon ADD COLUMN tienPhat INTEGER DEFAULT 0");
                    db.execSQL("ALTER TABLE PhieuMuon ADD COLUMN ghiChu TEXT");
                } catch (Exception e) {
                    Log.e("DbHelper", "Cột tienPhat/ghiChu đã tồn tại: " + e.getMessage());
                }
            }
        }
    }
}
