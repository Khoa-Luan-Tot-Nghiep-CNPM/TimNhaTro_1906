package com.doan.timnhatro.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.doan.timnhatro.model.MotelRoom;

import com.doan.timnhatro.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class StatisticFragment extends Fragment {

    private BarChart mChart;
    private Toolbar mToolbar;

    private ArrayList<MotelRoom> motelRooms = new ArrayList<>() ;
    private DatabaseReference MotelRef;

    public StatisticFragment() {
        // Required empty public constructor
    }

    public static StatisticFragment newInstance(String param1, String param2) {
        StatisticFragment fragment = new StatisticFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_statistic, container, false);

        mToolbar = v.findViewById(R.id.statistic_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        //setSupportActionBar(mToolbar);
        //((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  //      ((AppCompatActivity)getActivity()). getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Thống kê");

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == android.R.id.home){
                    loadFragment(new AccountFragment());
                }
                return false;
            }
        });
        MotelRef = FirebaseDatabase.getInstance().getReference().child("MotelRoom");
        mChart = v.findViewById(R.id.BarChart);
        SetBarChart();

        // Inflate the layout for this fragment
        return v;
    }

    private void SetBarChart() {
        MotelRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    MotelRoom motelRoom = dataSnapshot.getValue(MotelRoom.class);

                    motelRooms.add(motelRoom);
                    int nhaNguyenCan=0, nhaTro=0, chungCu=0, KTX=0, phongOGhep=0;
                    for (int i=0; i<motelRooms.size(); i++){
                        if (motelRooms.get(i).getNameMotelRoom().toLowerCase().contains("nhà nguyên căn")){
                            nhaNguyenCan++;
                        }
                        if (motelRooms.get(i).getNameMotelRoom().toLowerCase().contains("nhà trọ")){
                            nhaTro++;
                        }
                        if (motelRooms.get(i).getNameMotelRoom().toLowerCase().contains("chung cư")){
                            chungCu++;
                        }
                        if (motelRooms.get(i).getNameMotelRoom().toLowerCase().contains("ký túc xá")){
                            KTX++;
                        }
                        if (motelRooms.get(i).getNameMotelRoom().toLowerCase().contains("phòng ở ghép")){
                            phongOGhep++;
                        }
                    }

//                    int a[] = {nhaNguyenCan,chungCu,KTX,nhaTro,phongOGhep};
//                    int max = Max(a);
                    String[] typeRooms = {"Nhà nguyên căn", "Nhà trọ", "Chung cư", "Ký túc xá", "Phòng ở ghép"};

                    mChart.setDrawBarShadow(false);
                    mChart.setDrawValueAboveBar(true);
                    mChart.getDescription().setEnabled(false);
                    mChart.setDrawGridBackground(false);

                    YAxis yAxis = mChart.getAxisRight();
                    yAxis.setDrawGridLines(false);
                    yAxis.setDrawAxisLine(false);
                    mChart.getAxisRight().setEnabled(false);
                    mChart.getAxisLeft().setDrawGridLines(false);

                    XAxis xaxis = mChart.getXAxis();
                    xaxis.setDrawGridLines(false);
                    xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xaxis.setGranularity(1f);
                    xaxis.setDrawLabels(true);
                    xaxis.setDrawAxisLine(true);
                    xaxis.setValueFormatter(new IndexAxisValueFormatter(typeRooms));

                    Legend legend = mChart.getLegend();
                    legend.setEnabled(false);

                    ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();

                    barEntries.add(new BarEntry(0,(int) nhaNguyenCan));
                    barEntries.add(new BarEntry(1,(int) nhaTro));
                    barEntries.add(new BarEntry(2,(int) chungCu));
                    barEntries.add(new BarEntry(3,(int) KTX));
                    barEntries.add(new BarEntry(4,(int) phongOGhep));

                    List<IBarDataSet> dataSets = new ArrayList<>();
                    BarDataSet barDataSet = new BarDataSet(barEntries, " ");
                    barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    barDataSet.setDrawValues(true);
                    barDataSet.setValueTextSize(15);
                    dataSets.add(barDataSet);

                    BarData data = new BarData(dataSets);
                    mChart.setData(data);
                    mChart.invalidate();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}