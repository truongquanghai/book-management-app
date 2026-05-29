package com.example.test2.Top10;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.test2.Adapter.TopAdapter;
import com.example.test2.DAO.PhieuMuonDAO;
import com.example.test2.R;
import com.example.test2.model.Top;

import java.util.ArrayList;

public class Top10Fragment extends Fragment {
    ListView lvTop;
    TextView tvEmpty;
    ArrayList<Top> list;
    TopAdapter adapter;
    PhieuMuonDAO dao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_top10, container, false);
        lvTop = v.findViewById(R.id.lvTop);
        tvEmpty = v.findViewById(R.id.tvEmpty);
        dao = new PhieuMuonDAO(getActivity());
        list = (ArrayList<Top>) dao.getTop();
        
        if (list.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            lvTop.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            lvTop.setVisibility(View.VISIBLE);
        }

        adapter = new TopAdapter(getActivity(), list);
        lvTop.setAdapter(adapter);
        return v;
    }
}
