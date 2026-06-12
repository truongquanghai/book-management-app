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
import android.widget.AutoCompleteTextView;
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
    EditText edSearch, edSoLuongMuon, edGhiChu;
    AutoCompleteTextView acFilterStatus;
    int selectedStatus = 0;
    TextView tvTienThue, tvMaPM, tvThanhVienLabel, tvSachLabel, tvEmpty;
    TextView tvNgayMuon, tvHanTra, tvNgayTra;
    Button btnSave, btnCancel, btnXacNhanTraSach, btnPlus, btnMinus;
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
        acFilterStatus = v.findViewById(R.id.acFilterStatus);
        tvEmpty = v.findViewById(R.id.tvEmpty);
        fab = v.findViewById(R.id.fab);
        dao = new PhieuMuonDAO(getActivity());

        // Setup Dropdown
        String[] statuses = {"Tất cả", "Trả đúng hạn", "Chưa trả", "Trả muộn", "Quá hạn"};
        android.widget.ArrayAdapter<String> statusAdapter = new android.widget.ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, statuses);
        acFilterStatus.setAdapter(statusAdapter);
        acFilterStatus.setText(statuses[0], false);

        acFilterStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedStatus = position;
                performFilter();
            }
        });

        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performFilter();
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
        performFilter();
    }

    private void performFilter() {
        String query = edSearch.getText().toString();
        int status = selectedStatus;
        
        ArrayList<PhieuMuon> newList = (ArrayList<PhieuMuon>) dao.search(query, status);
        
        if (list == null) {
            list = newList;
            adapter = new PhieuMuonAdapter(getActivity(), this, list);
            lvPhieuMuon.setAdapter(adapter);
        } else {
            list.clear();
            list.addAll(newList);
            adapter.notifyDataSetChanged();
        }

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
        btnPlus = dialog.findViewById(R.id.btnPlus);
        btnMinus = dialog.findViewById(R.id.btnMinus);
        tvThanhVienLabel = dialog.findViewById(R.id.tvThanhVienLabel);
        tvSachLabel = dialog.findViewById(R.id.tvSachLabel);
        layout_spTV = dialog.findViewById(R.id.layout_spThanhVien);
        layout_spSach = dialog.findViewById(R.id.layout_spSach);
        edSoLuongMuon = dialog.findViewById(R.id.edSoLuongMuon);
        edGhiChu = dialog.findViewById(R.id.edGhiChu);

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
                
                // Cập nhật hiển thị tiền thuê dựa trên số lượng hiện tại
                updateTienThueDisplay();
                
                tvSachLabel.setText("Sách: " + s.getTenSach());
                popupSach.dismiss();
            }
        });

        edSoLuongMuon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateTienThueDisplay();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        layout_spSach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupSach.show();
            }
        });

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sl = 1;
                try {
                    sl = Integer.parseInt(edSoLuongMuon.getText().toString());
                } catch (Exception e) {}
                sl++;
                edSoLuongMuon.setText(String.valueOf(sl));
            }
        });

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sl = 1;
                try {
                    sl = Integer.parseInt(edSoLuongMuon.getText().toString());
                } catch (Exception e) {}
                if (sl > 1) {
                    sl--;
                    edSoLuongMuon.setText(String.valueOf(sl));
                }
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
            edSoLuongMuon.setText("1");
            btnPlus.setVisibility(View.VISIBLE);
            btnMinus.setVisibility(View.VISIBLE);
            edSoLuongMuon.setEnabled(true);
            edGhiChu.setText("");
            btnXacNhanTraSach.setVisibility(View.GONE);
            btnSave.setVisibility(View.VISIBLE); // Hiện nút Lưu khi thêm mới
        } else { // Xem chi tiết
            final PhieuMuon item = obj[0];
            tvMaPM.setVisibility(View.VISIBLE);
            tvMaPM.setText("Mã phiếu mượn: " + item.getMaPM());
            edSoLuongMuon.setText(String.valueOf(item.getSoLuongMuon()));
            edSoLuongMuon.setEnabled(false); // Không cho sửa số lượng khi xem chi tiết
            btnPlus.setVisibility(View.GONE);
            btnMinus.setVisibility(View.GONE);
            
            edGhiChu.setText(item.getGhiChu());
            
            maThanhVien = item.getMaTV();
            ThanhVien tv = thanhVienDAO.getID(String.valueOf(maThanhVien));
            if (tv != null) tvThanhVienLabel.setText("Thành viên: " + tv.getHoTen());
            
            maSach = item.getMaSach();
            Sach s = sachDAO.getID(String.valueOf(maSach));
            if (s != null) {
                tvSachLabel.setText("Sách: " + s.getTenSach());
                int giaGoc = s.getGiaThue() * item.getSoLuongMuon();
                if (item.getTienPhat() > 0) {
                    tvTienThue.setText("Tổng tiền: " + item.getTienThue() + " (Phạt: " + item.getTienPhat() + ")");
                    tvTienThue.setTextColor(Color.RED);
                } else {
                    tvTienThue.setText("Tổng tiền: " + item.getTienThue());
                    tvTienThue.setTextColor(Color.BLACK);
                }
            }
            
            tvNgayMuon.setText(item.getNgay());
            tvHanTra.setText(item.getHanTra());
            
            btnSave.setVisibility(View.VISIBLE); // Cho phép cập nhật ghi chú
            btnSave.setText("Cập nhật ghi chú");

            if (item.getNgayTra() != null && !item.getNgayTra().isEmpty()) {
                tvNgayTra.setText(item.getNgayTra());
                btnXacNhanTraSach.setVisibility(View.GONE);
                edGhiChu.setEnabled(false);
                btnSave.setVisibility(View.GONE);
            } else {
                tvNgayTra.setText("Chưa trả");
                btnXacNhanTraSach.setVisibility(View.VISIBLE);
            }

            btnXacNhanTraSach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Lấy ngày hiện tại làm ngày trả
                    String ngayTra = sdf.format(new Date());
                    item.setNgayTra(ngayTra);
                    
                    try {
                        Date dHanTra = sdf.parse(item.getHanTra());
                        Date dNgayTra = sdf.parse(ngayTra);
                        
                        // Nếu ngày trả sau hạn trả -> Phạt 5000đ
                        if (dNgayTra != null && dHanTra != null && dNgayTra.after(dHanTra)) {
                            int phuPhi = 5000;
                            item.setTienPhat(phuPhi);
                            item.setTienThue(item.getTienThue() + phuPhi);
                            Toast.makeText(context, "Trả muộn! Đã cộng phí phạt 5.000 VNĐ", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (dao.update(item) > 0) {
                        // Hoàn kho: Cộng lại số lượng sách đã mượn vào kho
                        Sach s = sachDAO.getID(String.valueOf(item.getMaSach()));
                        if (s != null) {
                            s.setSoLuong(s.getSoLuong() + item.getSoLuongMuon());
                            sachDAO.update(s);
                        }
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
                String sSoLuong = edSoLuongMuon.getText().toString();
                String ghiChu = edGhiChu.getText().toString();
                
                if (maThanhVien == 0 || maSach == 0 || sSoLuong.isEmpty()) {
                    Toast.makeText(context, "Vui lòng chọn đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                int soLuongMuon = Integer.parseInt(sSoLuong);
                if (soLuongMuon <= 0) {
                    Toast.makeText(context, "Số lượng mượn phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                PhieuMuon item = (type == 0) ? new PhieuMuon() : obj[0];
                String user = getActivity().getIntent().getStringExtra("user");
                item.setMaTT(user != null ? user : "admin");
                item.setMaTV(maThanhVien);
                item.setMaSach(maSach);
                item.setSoLuongMuon(soLuongMuon);
                item.setGhiChu(ghiChu);

                if (type == 0) {
                    item.setTienThue(tienThue * soLuongMuon); // Tiền thuê = giá * số lượng
                    item.setTienPhat(0);
                }
                // Nếu là edit (type != 0), chúng ta giữ nguyên tiền thuế đã tính trước đó (có thể bao gồm phạt)
                // Hoặc nếu muốn cập nhật lại tiền thuê gốc nếu chưa trả:
                else if (item.getNgayTra() == null || item.getNgayTra().isEmpty()) {
                     item.setTienThue(tienThue * soLuongMuon);
                }

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
                    item.setNgayTra(""); // Trạng thái chưa trả

                    // Kiểm tra kho trước khi cho mượn
                    Sach currentSach = sachDAO.getID(String.valueOf(maSach));
                    if (currentSach == null || currentSach.getSoLuong() < soLuongMuon) {
                        Toast.makeText(context, "Không đủ sách trong kho! Hiện có: " + (currentSach != null ? currentSach.getSoLuong() : 0), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (dao.insert(item) > 0) {
                        // Trừ số lượng sách trong kho
                        currentSach.setSoLuong(currentSach.getSoLuong() - soLuongMuon);
                        sachDAO.update(currentSach);
                        
                        Toast.makeText(context, "Thêm phiếu mượn thành công", Toast.LENGTH_SHORT).show();
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
                // Lấy thông tin phiếu mượn trước khi xóa để hoàn kho
                PhieuMuon pm = dao.getID(Id);
                if (pm != null && (pm.getNgayTra() == null || pm.getNgayTra().isEmpty())) {
                    // Nếu phiếu chưa trả mà bị xóa, hoàn lại số lượng sách vào kho
                    Sach s = sachDAO.getID(String.valueOf(pm.getMaSach()));
                    if (s != null) {
                        s.setSoLuong(s.getSoLuong() + pm.getSoLuongMuon());
                        sachDAO.update(s);
                    }
                }
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

    private void updateTienThueDisplay() {
        if (maSach != 0) {
            String sSoLuong = edSoLuongMuon.getText().toString();
            int sl = 1;
            if (!sSoLuong.isEmpty()) {
                try {
                    sl = Integer.parseInt(sSoLuong);
                } catch (NumberFormatException e) {
                    sl = 1;
                }
            }
            tvTienThue.setText("Tiền thuê: " + (tienThue * sl));
        }
    }
}
