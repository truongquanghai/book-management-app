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
        values.put("soLuong", obj.getSoLuong());
        return db.insert("Sach", null, values);
    }

    public int update(Sach obj) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tenSach", obj.getTenSach());
        values.put("giaThue", obj.getGiaThue());
        values.put("maLoai", obj.getMaLoai());
        values.put("soLuong", obj.getSoLuong());
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

    public List<Sach> search(String query, int status) {
        // status: 0-Tất cả, 1-Hết sách, 2-Sắp hết (<=5)
        StringBuilder sql = new StringBuilder("SELECT Sach.* FROM Sach " +
                     "INNER JOIN LoaiSach ON Sach.maLoai = LoaiSach.maLoai " +
                     "WHERE (Sach.tenSach LIKE ? OR LoaiSach.tenLoai LIKE ?)");
        
        List<String> args = new ArrayList<>();
        String searchParam = "%" + query + "%";
        args.add(searchParam);
        args.add(searchParam);

        if (status == 1) { // Hết sách
            sql.append(" AND Sach.soLuong = 0");
        } else if (status == 2) { // Sắp hết
            sql.append(" AND Sach.soLuong > 0 AND Sach.soLuong <= 5");
        }

        return getData(sql.toString(), args.toArray(new String[0]));
    }

    public Sach getID(String id) {
        String sql = "SELECT * FROM Sach WHERE maSach=?";
        List<Sach> list = getData(sql, id);
        return list.size() > 0 ? list.get(0) : null;
    }

    @android.annotation.SuppressLint("Range")
    private List<Sach> getData(String sql, String... selectionArgs) {
        List<Sach> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            Sach obj = new Sach();
            obj.setMaSach(cursor.getInt(cursor.getColumnIndex("maSach")));
            obj.setTenSach(cursor.getString(cursor.getColumnIndex("tenSach")));
            obj.setGiaThue(cursor.getInt(cursor.getColumnIndex("giaThue")));
            obj.setMaLoai(cursor.getInt(cursor.getColumnIndex("maLoai")));
            
            int colSoLuong = cursor.getColumnIndex("soLuong");
            if (colSoLuong != -1) obj.setSoLuong(cursor.getInt(colSoLuong));

            list.add(obj);
        }
        cursor.close();
        return list;
    }
}
