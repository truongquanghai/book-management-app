package com.example.test2.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.test2.database.DbHelper;
import com.example.test2.model.Sach;

import java.util.ArrayList;
import java.util.List;

public class SachDAO {
    private final DbHelper dbHelper;

    public SachDAO(Context context) {
        dbHelper = new DbHelper(context);
    }

    public long insert(Sach obj) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tenSach", obj.getTenSach());
        values.put("giaThue", obj.getGiaThue());
        values.put("maLoai", obj.getMaLoai());
        return db.insert("Sach", null, values);
    }

    public int update(Sach obj) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tenSach", obj.getTenSach());
        values.put("giaThue", obj.getGiaThue());
        values.put("maLoai", obj.getMaLoai());
        return db.update("Sach", values, "maSach=?", new String[]{String.valueOf(obj.getMaSach())});
    }

    public int delete(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("Sach", "maSach=?", new String[]{id});
    }

    public List<Sach> getAll() {
        String sql = "SELECT * FROM Sach";
        return getData(sql);
    }

    public List<Sach> search(String query) {
        // Tìm theo tên sách hoặc tên loại sách (thông qua bảng LoaiSach)
        String sql = "SELECT Sach.* FROM Sach " +
                     "INNER JOIN LoaiSach ON Sach.maLoai = LoaiSach.maLoai " +
                     "WHERE Sach.tenSach LIKE ? OR LoaiSach.tenLoai LIKE ?";
        String searchParam = "%" + query + "%";
        return getData(sql, searchParam, searchParam);
    }

    public Sach getID(String id) {
        String sql = "SELECT * FROM Sach WHERE maSach=?";
        List<Sach> list = getData(sql, id);
        return list.size() > 0 ? list.get(0) : null;
    }

    private List<Sach> getData(String sql, String... selectionArgs) {
        List<Sach> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            Sach obj = new Sach();
            obj.setMaSach(cursor.getInt(0));
            obj.setTenSach(cursor.getString(1));
            obj.setGiaThue(cursor.getInt(2));
            obj.setMaLoai(cursor.getInt(3));
            list.add(obj);
        }
        cursor.close();
        return list;
    }
}
