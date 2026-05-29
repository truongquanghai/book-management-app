package com.example.test2.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.test2.database.DbHelper;
import com.example.test2.model.ThanhVien;

import java.util.ArrayList;
import java.util.List;

public class ThanhVienDAO {
    private final DbHelper dbHelper;

    public ThanhVienDAO(Context context) {
        dbHelper = new DbHelper(context);
    }

    public long insert(ThanhVien obj) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("hoTen", obj.getHoTen());
        values.put("namSinh", obj.getNamSinh());
        values.put("sdt", obj.getSdt());
        values.put("diaChi", obj.getDiaChi());
        return db.insert("ThanhVien", null, values);
    }

    public int update(ThanhVien obj) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("hoTen", obj.getHoTen());
        values.put("namSinh", obj.getNamSinh());
        values.put("sdt", obj.getSdt());
        values.put("diaChi", obj.getDiaChi());
        return db.update("ThanhVien", values, "maTV=?", new String[]{String.valueOf(obj.getMaTV())});
    }

    public int delete(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("ThanhVien", "maTV=?", new String[]{id});
    }

    public List<ThanhVien> getAll() {
        String sql = "SELECT * FROM ThanhVien";
        return getData(sql);
    }

    public List<ThanhVien> search(String query) {
        String sql = "SELECT * FROM ThanhVien WHERE hoTen LIKE ? OR sdt LIKE ?";
        String searchParam = "%" + query + "%";
        return getData(sql, searchParam, searchParam);
    }

    public ThanhVien getID(String id) {
        String sql = "SELECT * FROM ThanhVien WHERE maTV=?";
        List<ThanhVien> list = getData(sql, id);
        return list.size() > 0 ? list.get(0) : null;
    }

    private List<ThanhVien> getData(String sql, String... selectionArgs) {
        List<ThanhVien> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            ThanhVien obj = new ThanhVien();
            obj.setMaTV(cursor.getInt(0));
            obj.setHoTen(cursor.getString(1));
            obj.setNamSinh(cursor.getString(2));
            obj.setSdt(cursor.getString(3));
            obj.setDiaChi(cursor.getString(4));
            list.add(obj);
        }
        cursor.close();
        return list;
    }
}
