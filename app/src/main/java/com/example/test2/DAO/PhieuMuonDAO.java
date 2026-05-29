package com.example.test2.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.test2.database.DbHelper;
import com.example.test2.model.PhieuMuon;
import com.example.test2.model.Top;

import java.util.ArrayList;
import java.util.List;

public class PhieuMuonDAO {
    private final DbHelper dbHelper;

    public PhieuMuonDAO(Context context) {
        dbHelper = new DbHelper(context);
    }

    public long insert(PhieuMuon obj) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("maTT", obj.getMaTT());
        values.put("maTV", obj.getMaTV());
        values.put("maSach", obj.getMaSach());
        values.put("ngay", obj.getNgay());
        values.put("hanTra", obj.getHanTra());
        values.put("ngayTra", obj.getNgayTra());
        values.put("tienThue", obj.getTienThue());
        return db.insert("PhieuMuon", null, values);
    }

    public int update(PhieuMuon obj) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("maTT", obj.getMaTT());
        values.put("maTV", obj.getMaTV());
        values.put("maSach", obj.getMaSach());
        values.put("ngay", obj.getNgay());
        values.put("hanTra", obj.getHanTra());
        values.put("ngayTra", obj.getNgayTra());
        values.put("tienThue", obj.getTienThue());
        return db.update("PhieuMuon", values, "maPM=?", new String[]{String.valueOf(obj.getMaPM())});
    }

    public int delete(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("PhieuMuon", "maPM=?", new String[]{id});
    }

    public List<PhieuMuon> getAll() {
        String sql = "SELECT * FROM PhieuMuon";
        return getData(sql);
    }

    public List<PhieuMuon> search(String query) {
        // Tìm theo mã phiếu mượn hoặc tên thành viên (thông qua bảng ThanhVien)
        String sql = "SELECT PhieuMuon.* FROM PhieuMuon " +
                     "INNER JOIN ThanhVien ON PhieuMuon.maTV = ThanhVien.maTV " +
                     "WHERE PhieuMuon.maPM LIKE ? OR ThanhVien.hoTen LIKE ?";
        String searchParam = "%" + query + "%";
        return getData(sql, searchParam, searchParam);
    }

    private List<PhieuMuon> getData(String sql, String... selectionArgs) {
        List<PhieuMuon> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            PhieuMuon obj = new PhieuMuon();
            obj.setMaPM(cursor.getInt(0));
            obj.setMaTT(cursor.getString(1));
            obj.setMaTV(cursor.getInt(2));
            obj.setMaSach(cursor.getInt(3));
            obj.setNgay(cursor.getString(4));
            obj.setHanTra(cursor.getString(5));
            obj.setNgayTra(cursor.getString(6));
            obj.setTienThue(cursor.getInt(7));
            list.add(obj);
        }
        cursor.close();
        return list;
    }

    public List<Top> getTop() {
        String sql = "SELECT tenSach, count(PhieuMuon.maSach) as soLuong FROM PhieuMuon INNER JOIN Sach ON PhieuMuon.maSach = Sach.maSach GROUP BY PhieuMuon.maSach ORDER BY soLuong DESC LIMIT 10";
        List<Top> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            Top top = new Top();
            top.setTenSach(cursor.getString(0));
            top.setSoLuong(cursor.getInt(1));
            list.add(top);
        }
        cursor.close();
        return list;
    }

    public int getDoanhThu(String tuNgay, String denNgay) {
        // Tính tổng tiền từ những phiếu đã trả (ngayTra không trống) trong khoảng từ ngày đến ngày
        String sqlDoanhThu = "SELECT SUM(tienThue) as doanhThu FROM PhieuMuon WHERE ngayTra BETWEEN ? AND ?";
        List<Integer> list = new ArrayList<Integer>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlDoanhThu, new String[]{tuNgay, denNgay});
        while (cursor.moveToNext()) {
            try {
                list.add(cursor.getInt(0));
            } catch (Exception e) {
                list.add(0);
            }
        }
        cursor.close();
        return list.get(0);
    }
}
