package com.example.test2.DoanhThu;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DoanhThuFragment extends Fragment {
    Button btnDoanhThu;
    ImageButton btnTuNgay, btnDenNgay;
    EditText edTu_Day, edTu_Month, edTu_Year;
    EditText edDen_Day, edDen_Month, edDen_Year;
    TextView tvDoanhThu;
    SimpleDateFormat sdfDb = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

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

        btnTuNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(edTu_Day, edTu_Month, edTu_Year);
            }
        });

        btnDenNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(edDen_Day, edDen_Month, edDen_Year);
            }
        });

        btnDoanhThu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tuNgay = getFormattedDate(edTu_Day, edTu_Month, edTu_Year);
                String denNgay = getFormattedDate(edDen_Day, edDen_Month, edDen_Year);

                if (tuNgay == null || denNgay == null) {
                    Toast.makeText(getActivity(), "Vui lòng nhập ngày hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                PhieuMuonDAO dao = new PhieuMuonDAO(getActivity());
                DecimalFormat formatter = new DecimalFormat("#,###");
                String formattedDoanhThu = formatter.format(dao.getDoanhThu(tuNgay, denNgay)).replace(",", ".");
                tvDoanhThu.setText("Doanh thu: " + formattedDoanhThu + " VNĐ");
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
                // Ô Ngày và Tháng có tối đa 2 ký tự
                if ((current.getId() != R.id.edTu_Year && current.getId() != R.id.edDen_Year)) {
                    if (s.length() == 2) {
                        next.requestFocus();
                    }
                }
                // Ô Năm có tối đa 4 ký tự
                else if (s.length() == 4) {
                    // Nếu là ô năm thì không cần nhảy đi đâu nữa hoặc nhảy đến nút tính toán
                    // next ở đây có thể là null hoặc view tiếp theo
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

            // Kiểm tra tháng hợp lệ (1-12)
            if (month < 1 || month > 12) {
                Toast.makeText(getActivity(), "Tháng không hợp lệ (1-12)", Toast.LENGTH_SHORT).show();
                edMonth.requestFocus();
                return null;
            }

            // Kiểm tra ngày hợp lệ dựa trên tháng và năm
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month - 1); // Tháng trong Calendar bắt đầu từ 0
            int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            if (day < 1 || day > maxDay) {
                Toast.makeText(getActivity(), "Tháng " + month + " chỉ có tối đa " + maxDay + " ngày", Toast.LENGTH_SHORT).show();
                edDay.requestFocus();
                return null;
            }

            // Kiểm tra năm hợp lệ (giới hạn thực tế từ 2000 đến 2100)
            if (year < 2000 || year > 2100) {
                Toast.makeText(getActivity(), "Năm không hợp lệ (2000-2100)", Toast.LENGTH_SHORT).show();
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
        DatePickerDialog d = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        edDay.setText(String.format("%02d", dayOfMonth));
                        edMonth.setText(String.format("%02d", monthOfYear + 1));
                        edYear.setText(String.valueOf(year));
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        d.show();
    }
}
