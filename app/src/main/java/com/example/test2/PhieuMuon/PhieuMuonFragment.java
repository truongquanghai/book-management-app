package com.example.test2.PhieuMuon;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.ListPopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.test2.Adapter.PhieuMuonAdapter;
import com.example.test2.Adapter.SachSpinnerAdapter;
import com.example.test2.Adapter.ThanhVienSpinnerAdapter;
import com.example.test2.DAO.PhieuMuonDAO;
import com.example.test2.DAO.SachDAO;
import com.example.test2.DAO.ThanhVienDAO;
import com.example.test2.R;
import com.example.test2.model.PhieuMuon;
import com.example.test2.model.Sach;
import com.example.test2.model.ThanhVien;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PhieuMuonFragment extends Fragment {
    ListView lvPhieuMuon;
    ArrayList<PhieuMuon> list;
    PhieuMuonDAO dao;
    PhieuMuonAdapter adapter;
    FloatingActionButton fab;
    Dialog dialog;
    EditText edSearch;
    TextView tvTienThue, tvMaPM, tvThanhVienLabel, tvSachLabel, tvEmpty;
    TextView tvNgayMuon, tvHanTra, tvNgayTra;
    Button btnSave, btnCancel, btnXacNhanTraSach;
    LinearLayout layout_spTV, layout_spSach;

    ThanhVienDAO thanhVienDAO;
    ArrayList<ThanhVien> listThanhVien;
    ThanhVienSpinnerAdapter thanhVienSpinnerAdapter;
    int maThanhVien;

    SachDAO sachDAO;
    ArrayList<Sach> listSach;
    SachSpinnerAdapter sachSpinnerAdapter;
    int maSach, tienThue;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_phieu_muon, container, false);
        lvPhieuMuon = v.findViewById(R.id.lvPhieuMuon);
        edSearch = v.findViewById(R.id.edSearch);
        tvEmpty = v.findViewById(R.id.tvEmpty);
        fab = v.findViewById(R.id.fab);
        dao = new PhieuMuonDAO(getActivity());

        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                list.clear();
                list.addAll(dao.search(s.toString()));
                adapter.notifyDataSetChanged();
                tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(getActivity(), 0);
            }
        });

        capNhatLv();
        return v;
    }

    void capNhatLv() {
        list = (ArrayList<PhieuMuon>) dao.getAll();
        adapter = new PhieuMuonAdapter(getActivity(), this, list);
        lvPhieuMuon.setAdapter(adapter);
        tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
    }

    public void openDialog(final Context context, final int type, final PhieuMuon... obj) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_phieu_muon);
        tvMaPM = dialog.findViewById(R.id.tvMaPM);
        tvTienThue = dialog.findViewById(R.id.tvTienThue);
        tvNgayMuon = dialog.findViewById(R.id.tvNgayMuon);
        tvHanTra = dialog.findViewById(R.id.tvHanTra);
        tvNgayTra = dialog.findViewById(R.id.tvNgayTra);
        btnCancel = dialog.findViewById(R.id.btnCancel);
        btnSave = dialog.findViewById(R.id.btnSave);
        btnXacNhanTraSach = dialog.findViewById(R.id.btnXacNhanTraSach);
        tvThanhVienLabel = dialog.findViewById(R.id.tvThanhVienLabel);
        tvSachLabel = dialog.findViewById(R.id.tvSachLabel);
        layout_spTV = dialog.findViewById(R.id.layout_spThanhVien);
        layout_spSach = dialog.findViewById(R.id.layout_spSach);

        // Xử lý Thành viên
        thanhVienDAO = new ThanhVienDAO(context);
        listThanhVien = (ArrayList<ThanhVien>) thanhVienDAO.getAll();
        thanhVienSpinnerAdapter = new ThanhVienSpinnerAdapter(context, listThanhVien);
        
        final ListPopupWindow popupThanhVien = new ListPopupWindow(context);
        popupThanhVien.setAdapter(thanhVienSpinnerAdapter);
        popupThanhVien.setAnchorView(layout_spTV);
        popupThanhVien.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ThanhVien tv = listThanhVien.get(position);
                maThanhVien = tv.getMaTV();
                tvThanhVienLabel.setText("Thành viên: " + tv.getHoTen());
                popupThanhVien.dismiss();
            }
        });

        layout_spTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupThanhVien.show();
            }
        });

        // Xử lý Sách
        sachDAO = new SachDAO(context);
        listSach = (ArrayList<Sach>) sachDAO.getAll();
        sachSpinnerAdapter = new SachSpinnerAdapter(context, listSach);
        
        final ListPopupWindow popupSach = new ListPopupWindow(context);
        popupSach.setAdapter(sachSpinnerAdapter);
        popupSach.setAnchorView(layout_spSach);
        popupSach.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Sach s = listSach.get(position);
                maSach = s.getMaSach();
                tienThue = s.getGiaThue();
                tvTienThue.setText("Tiền thuê: " + tienThue);
                tvSachLabel.setText("Sách: " + s.getTenSach());
                popupSach.dismiss();
            }
        });

        layout_spSach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupSach.show();
            }
        });

        tvNgayMuon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(context, tvNgayMuon, 0);
            }
        });

        tvHanTra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(context, tvHanTra, 1);
            }
        });

        tvNgayTra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(context, tvNgayTra, 2);
            }
        });

        if (type == 0) { // Thêm mới
            tvMaPM.setVisibility(View.GONE);
            Calendar cal = Calendar.getInstance();
            String ngayMuon = sdf.format(cal.getTime());
            tvNgayMuon.setText(ngayMuon);
            
            cal.add(Calendar.DATE, 7); // Mặc định hạn trả là 7 ngày sau
            String hanTra = sdf.format(cal.getTime());
            tvHanTra.setText(hanTra);
            
            tvNgayTra.setText("Chưa trả");
            btnXacNhanTraSach.setVisibility(View.GONE);
            btnSave.setVisibility(View.VISIBLE); // Hiện nút Lưu khi thêm mới
        } else { // Xem chi tiết
            final PhieuMuon item = obj[0];
            tvMaPM.setVisibility(View.VISIBLE);
            tvMaPM.setText("Mã phiếu mượn: " + item.getMaPM());
            
            maThanhVien = item.getMaTV();
            ThanhVien tv = thanhVienDAO.getID(String.valueOf(maThanhVien));
            if (tv != null) tvThanhVienLabel.setText("Thành viên: " + tv.getHoTen());
            
            maSach = item.getMaSach();
            Sach s = sachDAO.getID(String.valueOf(maSach));
            if (s != null) {
                tvSachLabel.setText("Sách: " + s.getTenSach());
                int giaGoc = s.getGiaThue();
                if (item.getTienThue() > giaGoc) {
                    tvTienThue.setText("Tiền thuê: " + item.getTienThue() + " (Gồm " + (item.getTienThue() - giaGoc) + " phí phạt)");
                    tvTienThue.setTextColor(Color.RED);
                } else {
                    tvTienThue.setText("Tiền thuê: " + item.getTienThue());
                    tvTienThue.setTextColor(Color.BLACK);
                }
            }
            
            tvNgayMuon.setText(item.getNgay());
            tvHanTra.setText(item.getHanTra());
            
            btnSave.setVisibility(View.GONE); // Ẩn nút Cập nhật

            if (item.getNgayTra() != null && !item.getNgayTra().isEmpty()) {
                tvNgayTra.setText(item.getNgayTra());
                btnXacNhanTraSach.setVisibility(View.GONE);
            } else {
                tvNgayTra.setText("Chưa trả");
                btnXacNhanTraSach.setVisibility(View.VISIBLE);
            }

            btnXacNhanTraSach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Nếu người dùng chưa chọn ngày trả thủ công, lấy ngày hiện tại
                    String ngayTra = tvNgayTra.getText().toString();
                    if (ngayTra.equals("Chưa trả")) {
                        ngayTra = sdf.format(new Date());
                    }

                    item.setNgayTra(ngayTra);
                    
                    // Lấy hạn trả từ TextView để đảm bảo tính đúng nếu vừa chỉnh sửa trên UI
                    String hanTraStr = tvHanTra.getText().toString();
                    item.setHanTra(hanTraStr);

                    try {
                        Date dHanTra = sdf.parse(hanTraStr);
                        Date dNgayTra = sdf.parse(ngayTra);
                        if (dNgayTra.after(dHanTra)) {
                            int phuPhi = 5000;
                            item.setTienThue(item.getTienThue() + phuPhi);
                            Toast.makeText(context, "Quá hạn! Đã cộng thêm phí phạt 5.000 VNĐ", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (dao.update(item) > 0) {
                        Toast.makeText(context, "Xác nhận trả sách thành công", Toast.LENGTH_SHORT).show();
                        capNhatLv();
                        dialog.dismiss();
                    }
                }
            });
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (maThanhVien == 0 || maSach == 0) {
                    Toast.makeText(context, "Vui lòng chọn đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                PhieuMuon item = (type == 0) ? new PhieuMuon() : obj[0];
                String user = getActivity().getIntent().getStringExtra("user");
                item.setMaTT(user != null ? user : "admin");
                item.setMaTV(maThanhVien);
                item.setMaSach(maSach);
                item.setTienThue(tienThue);

                String ngayMuon = tvNgayMuon.getText().toString();
                String hanTra = tvHanTra.getText().toString();

                try {
                    Date dNgayMuon = sdf.parse(ngayMuon);
                    Date dHanTra = sdf.parse(hanTra);
                    if (dNgayMuon != null && dHanTra != null && dNgayMuon.after(dHanTra)) {
                        Toast.makeText(context, "Ngày mượn không thể sau hạn trả", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                item.setNgay(ngayMuon);
                item.setHanTra(hanTra);

                if (type == 0) {
                    item.setNgayTra(""); // Chưa trả

                    if (dao.insert(item) > 0) {
                        Toast.makeText(context, "Thêm thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (dao.update(item) > 0) {
                        Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
                capNhatLv();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    
    public void xoa(final String Id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Xóa");
        builder.setMessage("Bạn có muốn xóa không?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dao.delete(Id);
                capNhatLv();
            }
        });
        builder.setNegativeButton("Không", null);
        builder.show();
    }

    private void showDatePickerDialog(Context context, final TextView textView, final int type) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(year, month, dayOfMonth);
                        String dateStr = sdf.format(cal.getTime());
                        
                        try {
                            if (type == 0) { // Đang chọn Ngày mượn
                                String currentHanTra = tvHanTra.getText().toString();
                                if (!currentHanTra.isEmpty() && !currentHanTra.equals("Hạn trả: ")) {
                                    Date dNewMuon = cal.getTime();
                                    Date dHanTra = sdf.parse(currentHanTra);
                                    if (dNewMuon.after(dHanTra)) {
                                        Toast.makeText(context, "Lỗi: Ngày mượn đang sau hạn trả", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            } else if (type == 1) { // Đang chọn Hạn trả
                                String currentNgayMuon = tvNgayMuon.getText().toString();
                                if (!currentNgayMuon.isEmpty() && !currentNgayMuon.equals("Ngày mượn: ")) {
                                    Date dNewHanTra = cal.getTime();
                                    Date dNgayMuon = sdf.parse(currentNgayMuon);
                                    if (dNewHanTra.before(dNgayMuon)) {
                                        Toast.makeText(context, "Lỗi: Hạn trả đang trước ngày mượn", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        
                        textView.setText(dateStr);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
}
