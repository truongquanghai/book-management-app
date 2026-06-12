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
        values.put("soLuongMuon", obj.getSoLuongMuon());
        values.put("tienPhat", obj.getTienPhat());
        values.put("ghiChu", obj.getGhiChu());
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
        values.put("soLuongMuon", obj.getSoLuongMuon());
        values.put("tienPhat", obj.getTienPhat());
        values.put("ghiChu", obj.getGhiChu());
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

    public PhieuMuon getID(String id) {
        String sql = "SELECT * FROM PhieuMuon WHERE maPM=?";
        List<PhieuMuon> list = getData(sql, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<PhieuMuon> search(String query, int status) {
        StringBuilder sql = new StringBuilder("SELECT PhieuMuon.* FROM PhieuMuon " +
                "INNER JOIN ThanhVien ON PhieuMuon.maTV = ThanhVien.maTV " +
                "WHERE (PhieuMuon.maPM LIKE ? OR ThanhVien.hoTen LIKE ?)");
        
        List<String> args = new ArrayList<>();
        String searchParam = "%" + query + "%";
        args.add(searchParam);
        args.add(searchParam);

        if (status == 1) { // Trả đúng hạn
            sql.append(" AND PhieuMuon.ngayTra IS NOT NULL AND PhieuMuon.ngayTra != '' AND PhieuMuon.ngayTra <= PhieuMuon.hanTra");
        } else if (status == 2) { // Chưa trả (Trong hạn)
            sql.append(" AND (PhieuMuon.ngayTra IS NULL OR PhieuMuon.ngayTra = '') AND date('now') <= date(PhieuMuon.hanTra)");
        } else if (status == 3) { // Trả muộn
            sql.append(" AND PhieuMuon.ngayTra IS NOT NULL AND PhieuMuon.ngayTra != '' AND PhieuMuon.ngayTra > PhieuMuon.hanTra");
        } else if (status == 4) { // Quá hạn (Chưa trả)
            sql.append(" AND (PhieuMuon.ngayTra IS NULL OR PhieuMuon.ngayTra = '') AND date('now') > date(PhieuMuon.hanTra)");
        }

        return getData(sql.toString(), args.toArray(new String[0]));
    }

    @android.annotation.SuppressLint("Range")
    private List<PhieuMuon> getData(String sql, String... selectionArgs) {
        List<PhieuMuon> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            PhieuMuon obj = new PhieuMuon();
            obj.setMaPM(cursor.getInt(cursor.getColumnIndex("maPM")));
            obj.setMaTT(cursor.getString(cursor.getColumnIndex("maTT")));
            obj.setMaTV(cursor.getInt(cursor.getColumnIndex("maTV")));
            obj.setMaSach(cursor.getInt(cursor.getColumnIndex("maSach")));
            obj.setNgay(cursor.getString(cursor.getColumnIndex("ngay")));
            obj.setHanTra(cursor.getString(cursor.getColumnIndex("hanTra")));
            obj.setNgayTra(cursor.getString(cursor.getColumnIndex("ngayTra")));
            obj.setTienThue(cursor.getInt(cursor.getColumnIndex("tienThue")));
            
            // Kiểm tra cột tồn tại an toàn
            int colSoLuong = cursor.getColumnIndex("soLuongMuon");
            if (colSoLuong != -1) obj.setSoLuongMuon(cursor.getInt(colSoLuong));
            
            int colTienPhat = cursor.getColumnIndex("tienPhat");
            if (colTienPhat != -1) obj.setTienPhat(cursor.getInt(colTienPhat));
            
            int colGhiChu = cursor.getColumnIndex("ghiChu");
            if (colGhiChu != -1) obj.setGhiChu(cursor.getString(colGhiChu));

            list.add(obj);
        }
        cursor.close();
        return list;
    }

    public List<Top> getTop() {
        String sql = "SELECT tenSach, SUM(PhieuMuon.soLuongMuon) as tongSoLuong FROM PhieuMuon INNER JOIN Sach ON PhieuMuon.maSach = Sach.maSach GROUP BY PhieuMuon.maSach ORDER BY tongSoLuong DESC LIMIT 10";
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
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT SUM(tienThue) FROM PhieuMuon WHERE ngayTra BETWEEN ? AND ?";
        Cursor cursor = db.rawQuery(sql, new String[]{tuNgay, denNgay});
        int doanhThu = 0;
        if (cursor.moveToFirst()) {
            doanhThu = cursor.getInt(0);
        }
        cursor.close();
        return doanhThu;
    }
}
