package com.example.test2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.test2.R;
import com.example.test2.model.Top;

import java.util.ArrayList;

public class TopAdapter extends ArrayAdapter<Top> {
    private final Context context;
    private final ArrayList<Top> list;
    TextView tvTenSach, tvSoLuong;

    public TopAdapter(@NonNull Context context, ArrayList<Top> list) {
        super(context, 0, list);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_top10, null);
        }
        final Top item = list.get(position);
        if (item != null) {
            tvTenSach = v.findViewById(R.id.tvTenSach);
            tvTenSach.setText(item.getTenSach());

            tvSoLuong = v.findViewById(R.id.tvSoLuong);
            tvSoLuong.setText("Số lượng mượn: " + item.getSoLuong());
        }
        return v;
    }
}
