package com.example.test2.DoanhThu;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.test2.DAO.PhieuMuonDAO;
import com.example.test2.R;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Locale;

public class DoanhThuFragment extends Fragment {
    Button btnDoanhThu;
    ImageButton btnTuNgay, btnDenNgay;
    EditText edTu_Day, edTu_Month, edTu_Year;
    EditText edDen_Day, edDen_Month, edDen_Year;
    TextView tvDoanhThu;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_doanh_thu, container, false);
        edTu_Day = v.findViewById(R.id.edTu_Day);
        edTu_Month = v.findViewById(R.id.edTu_Month);
        edTu_Year = v.findViewById(R.id.edTu_Year);

        edDen_Day = v.findViewById(R.id.edDen_Day);
        edDen_Month = v.findViewById(R.id.edDen_Month);
        edDen_Year = v.findViewById(R.id.edDen_Year);

        tvDoanhThu = v.findViewById(R.id.tvDoanhThu);
        btnTuNgay = v.findViewById(R.id.btnTuNgay);
        btnDenNgay = v.findViewById(R.id.btnDenNgay);
        btnDoanhThu = v.findViewById(R.id.btnDoanhThu);

        setupAutoMove(edTu_Day, edTu_Month);
        setupAutoMove(edTu_Month, edTu_Year);
        setupAutoMove(edDen_Day, edDen_Month);
        setupAutoMove(edDen_Month, edDen_Year);

        btnTuNgay.setOnClickListener(view -> showDatePicker(edTu_Day, edTu_Month, edTu_Year));

        btnDenNgay.setOnClickListener(view -> showDatePicker(edDen_Day, edDen_Month, edDen_Year));

        btnDoanhThu.setOnClickListener(view -> {
            String tuNgay = getFormattedDate(edTu_Day, edTu_Month, edTu_Year);
            String denNgay = getFormattedDate(edDen_Day, edDen_Month, edDen_Year);

            if (tuNgay == null || denNgay == null) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Vui lòng nhập ngày hợp lệ", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            if (tuNgay.compareTo(denNgay) > 0) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc", Toast.LENGTH_SHORT).show();
                }
                edTu_Day.requestFocus();
                return;
            }

            PhieuMuonDAO dao = new PhieuMuonDAO(requireContext());
            int doanhThu = dao.getDoanhThu(tuNgay, denNgay);
            
            DecimalFormat formatter = new DecimalFormat("#,###");
            String formattedDoanhThu = formatter.format(doanhThu).replace(",", ".");
            tvDoanhThu.setText(String.format("Doanh thu: %s VNĐ", formattedDoanhThu));

            if (getActivity() != null) {
                if (doanhThu == 0) {
                    Toast.makeText(getActivity(), "Không có doanh thu trong khoảng này", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Đã thống kê doanh thu", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

    private void setupAutoMove(final EditText current, final EditText next) {
        current.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ((current.getId() != R.id.edTu_Year && current.getId() != R.id.edDen_Year)) {
                    if (s.length() == 2) {
                        next.requestFocus();
                    }
                }
                else if (s.length() == 4) {
                    if (next != null) next.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private String getFormattedDate(EditText edDay, EditText edMonth, EditText edYear) {
        String d = edDay.getText().toString();
        String m = edMonth.getText().toString();
        String y = edYear.getText().toString();

        if (d.isEmpty() || m.isEmpty() || y.length() < 4) return null;

        try {
            int day = Integer.parseInt(d);
            int month = Integer.parseInt(m);
            int year = Integer.parseInt(y);

            if (month < 1 || month > 12) {
                if (getActivity() != null) Toast.makeText(getActivity(), "Tháng không hợp lệ (1-12)", Toast.LENGTH_SHORT).show();
                edMonth.requestFocus();
                return null;
            }

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month - 1);
            int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            if (day < 1 || day > maxDay) {
                if (getActivity() != null) Toast.makeText(getActivity(), String.format(Locale.getDefault(), "Tháng %d chỉ có tối đa %d ngày", month, maxDay), Toast.LENGTH_SHORT).show();
                edDay.requestFocus();
                return null;
            }

            if (year < 2000 || year > 2100) {
                if (getActivity() != null) Toast.makeText(getActivity(), "Năm không hợp lệ (2000-2100)", Toast.LENGTH_SHORT).show();
                edYear.requestFocus();
                return null;
            }

            return String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
        } catch (Exception e) {
            return null;
        }
    }

    private void showDatePicker(final EditText edDay, final EditText edMonth, final EditText edYear) {
        Calendar c = Calendar.getInstance();
        DatePickerDialog d = new DatePickerDialog(requireContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    edDay.setText(String.format(Locale.getDefault(), "%02d", dayOfMonth));
                    edMonth.setText(String.format(Locale.getDefault(), "%02d", monthOfYear + 1));
                    edYear.setText(String.valueOf(year));
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        d.show();
    }
}
