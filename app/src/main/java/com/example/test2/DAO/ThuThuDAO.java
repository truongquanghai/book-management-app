package com.example.test2.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.test2.database.DbHelper;
import com.example.test2.model.ThuThu;

import java.util.ArrayList;
import java.util.List;

public class ThuThuDAO {
    private final DbHelper dbHelper;

    public ThuThuDAO(Context context) {
        dbHelper = new DbHelper(context);
    }

    public boolean checkLogin(String user, String matKhau) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Cho phép đăng nhập bằng maTT hoặc hoTen
        Cursor cursor = db.rawQuery("select * from ThuThu where (maTT = ? or hoTen = ?) and matKhau = ?", new String[]{user, user, matKhau});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public long insert(ThuThu obj) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("maTT", obj.getMaTT());
        values.put("hoTen", obj.getHoTen());
        values.put("matKhau", obj.getMatKhau());
        values.put("email", obj.getEmail());
        values.put("avatar", obj.getAvatar()); // Thêm avatar
        return db.insert("ThuThu", null, values);
    }

    public int update(ThuThu obj) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("hoTen", obj.getHoTen());
        values.put("matKhau", obj.getMatKhau());
        values.put("email", obj.getEmail());
        values.put("avatar", obj.getAvatar());
        return db.update("ThuThu", values, "maTT=?", new String[]{obj.getMaTT()});
    }

    public int updatePass(ThuThu obj) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("matKhau", obj.getMatKhau());
        return db.update("ThuThu", values, "maTT=?", new String[]{obj.getMaTT()});
    }

    public int delete(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("ThuThu", "maTT=?", new String[]{id});
    }

    public List<ThuThu> getAll() {
        String sql = "SELECT * FROM ThuThu WHERE maTT != 'admin'";
        return getData(sql);
    }

    public ThuThu getID(String id) {
        String sql = "SELECT * FROM ThuThu WHERE maTT=?";
        List<ThuThu> list = getData(sql, id);
        return list.size() > 0 ? list.get(0) : null;
    }

    public ThuThu getByID_Or_Name(String user) {
        String sql = "SELECT * FROM ThuThu WHERE maTT=? OR hoTen=?";
        List<ThuThu> list = getData(sql, user, user);
        return list.size() > 0 ? list.get(0) : null;
    }

    private List<ThuThu> getData(String sql, String... selectionArgs) {
        List<ThuThu> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            ThuThu obj = new ThuThu();
            
            int maTTIdx = cursor.getColumnIndex("maTT");
            int hoTenIdx = cursor.getColumnIndex("hoTen");
            int matKhauIdx = cursor.getColumnIndex("matKhau");
            int emailIdx = cursor.getColumnIndex("email");
            int avatarIdx = cursor.getColumnIndex("avatar");

            if (maTTIdx != -1) obj.setMaTT(cursor.getString(maTTIdx));
            if (hoTenIdx != -1) obj.setHoTen(cursor.getString(hoTenIdx));
            if (matKhauIdx != -1) obj.setMatKhau(cursor.getString(matKhauIdx));
            
            if (emailIdx != -1) {
                obj.setEmail(cursor.getString(emailIdx));
            } else {
                obj.setEmail(obj.getMaTT() + "@gmail.com");
            }

            if (avatarIdx != -1) {
                obj.setAvatar(cursor.getString(avatarIdx));
            }

            list.add(obj);
        }
        cursor.close();
        return list;
    }
}
