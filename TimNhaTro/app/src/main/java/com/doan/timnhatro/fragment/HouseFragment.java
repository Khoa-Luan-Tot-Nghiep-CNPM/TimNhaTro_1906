package com.doan.timnhatro.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doan.timnhatro.R;
import com.doan.timnhatro.adapter.MotelRoomAdapter;
import com.doan.timnhatro.base.Constants;
import com.doan.timnhatro.model.MotelRoom;
import com.doan.timnhatro.utils.AccountUtils;
import com.doan.timnhatro.utils.LocationUtils;
import com.doan.timnhatro.view.CreatePostsActivity;
import com.doan.timnhatro.view.LoginActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.rangeseekbar.RangeSeekBar;

public class HouseFragment extends Fragment {


    //private View container;
    private RecyclerView recRoomsFeatured;
    private MotelRoomAdapter motelRoomAdapter;
    private Button SearchNearBtn, AddPostBtn, btnChonKhoangGia, btnLoaiPhong;
    private ArrayList<MotelRoom> arrayMotelRoom = new ArrayList<>();
    //private ArrayList<MotelRoom> arrayMotelRoomSave = new ArrayList<>();
    private Spinner spinner;
    private LinearLayout linearLayoutTypeRoom;
    private RelativeLayout relativeLayoutPriceRange;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehaviorTypeRoom;
    private BottomSheetBehavior<RelativeLayout> bottomSheetBehaviorPriceRange;
    private ListView listView;
    private TextView txtDisplay;
    private RangeSeekBar rangeSeekBar;
    private Button btnUse;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference dataRef;

    boolean isClickSpinner = false;
    long number1, number2;

    public HouseFragment() {
        // Required empty public constructor
    }

    ViewFlipper viewFlipper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_house, container, false);

        initView(v);

        setUpRecyclerView();

        SearchNearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });
        AddPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AccountUtils.getInstance().getAccount() != null) {
                    startActivity(new Intent(getActivity().getApplicationContext(), CreatePostsActivity.class));
                    return;
                }
                startActivity(new Intent(getActivity().getApplicationContext(), LoginActivity.class));
            }
        });
        SliderShow(v);

        getDataCityFromFirebase();

        btnLoaiPhong.setOnClickListener(listener);
        btnChonKhoangGia.setOnClickListener(listener);

        bottomSheetBehaviorTypeRoom.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehaviorPriceRange.setState(BottomSheetBehavior.STATE_HIDDEN);

        return v;
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_choose_range_price:
                    showBottomSheetPriceRange();
                    break;
                case R.id.button_type_room:
                    showBottomSheetTypeRoom();
                    break;
            }
        }
    };


    private void getDataCityFromFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        dataRef = firebaseDatabase.getReference().child("CiTy");
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> arrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String nameCity = dataSnapshot.getValue(String.class);
                    arrayList.add(nameCity);
                }

                setUpSpinner(arrayList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setUpSpinner(final List<String> list) {
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isClickSpinner = true;
                if (isClickSpinner) {
                    if (position == 0) {
                        arrayMotelRoom.clear();
                        setUpRecyclerView();
                    } else {
                        arrayMotelRoom.clear();
                        setUpRecyclerViewWithNameCity(list.get(position));
                    }

                } else {
                    arrayMotelRoom.clear();
                    setUpRecyclerView();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.REQUEST_PERMISSION);
            return false;
        }
        if (LocationUtils.getInstance().isGPSEnable()) {
            loadFragment(new MapsFragment());
            return true;
        }

        new AlertDialog.Builder(getActivity().getApplicationContext())
                .setTitle("GPS Chưa Được Kích Hoạt")
                .setMessage("Vui lòng kích hoạt GPS và thử lại sau")
                .setPositiveButton("Trở lại", null)
                .show();

        return false;
    }

    private void loadFragment(MapsFragment mapsFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, mapsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setUpRecyclerView() {
        recRoomsFeatured.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recRoomsFeatured.setLayoutManager(layoutManager);

        FirebaseDatabase.getInstance().getReference().child("MotelRoom")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            MotelRoom motelRoom = dataSnapshot.getValue(MotelRoom.class);
                            arrayMotelRoom.add(motelRoom);
                        }

                        motelRoomAdapter = new MotelRoomAdapter(arrayMotelRoom);
                        recRoomsFeatured.setAdapter(motelRoomAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    //Loc data theo ten thanh pho
    private void setUpRecyclerViewWithNameCity(final String nameCity) {
        recRoomsFeatured.setHasFixedSize(true);
        recRoomsFeatured.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        FirebaseDatabase.getInstance().getReference()
                .child("MotelRoom")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            MotelRoom motelRoom = dataSnapshot.getValue(MotelRoom.class);
                            if (motelRoom.getCity().equals(nameCity)) {
                                arrayMotelRoom.add(motelRoom);
                            }
                        }

                        motelRoomAdapter = new MotelRoomAdapter(arrayMotelRoom);
                        recRoomsFeatured.setAdapter(motelRoomAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    //Loc data theo ten loai phong nhu: Nha nguyen can, chung cu, nha tro,....
    private void setUpRecyclerViewWithTypeRoom(final String nameTypeRoom) {
        recRoomsFeatured.setHasFixedSize(true);
        recRoomsFeatured.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        FirebaseDatabase.getInstance().getReference()
                .child("MotelRoom")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            MotelRoom motelRoom = dataSnapshot.getValue(MotelRoom.class);
                            if (motelRoom != null) {
                                if (motelRoom.getNameMotelRoom().equals(nameTypeRoom)) {
                                    arrayMotelRoom.add(motelRoom);
                                }
                            }
                        }

                        motelRoomAdapter = new MotelRoomAdapter(arrayMotelRoom);
                        recRoomsFeatured.setAdapter(motelRoomAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setUpRecyclerViewWithRangePrice(final long a, final long b) {
        recRoomsFeatured.setHasFixedSize(true);
        recRoomsFeatured.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        FirebaseDatabase.getInstance().getReference()
                .child("MotelRoom")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            MotelRoom motelRoom = dataSnapshot.getValue(MotelRoom.class);
                            if (motelRoom != null) {
                                if ((motelRoom.getPrice() >= a) && (motelRoom.getPrice() <= b)) {
                                    arrayMotelRoom.add(motelRoom);
                                }
                            }
                        }

                        motelRoomAdapter = new MotelRoomAdapter(arrayMotelRoom);
                        recRoomsFeatured.setAdapter(motelRoomAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void SliderShow(View v) {
        int image[] = {R.drawable.slide1, R.drawable.slide2, R.drawable.slide3};
        viewFlipper = v.findViewById(R.id.vf_image_slider);

        for (int i = 0; i < image.length; i++) {
            flipperImage(image[i]);
        }
    }

    private void initView(View v) {
        linearLayoutTypeRoom = v.findViewById(R.id.layout_type_room);
        relativeLayoutPriceRange = v.findViewById(R.id.layout_price_range);
        bottomSheetBehaviorTypeRoom = BottomSheetBehavior.from(linearLayoutTypeRoom);
        bottomSheetBehaviorPriceRange = BottomSheetBehavior.from(relativeLayoutPriceRange);

        rangeSeekBar = v.findViewById(R.id.range_seekBar);
        txtDisplay = v.findViewById(R.id.textview_price_range);

        recRoomsFeatured = v.findViewById(R.id.recRoomsFeatured);
        SearchNearBtn = v.findViewById(R.id.btn_findNear);
        AddPostBtn = v.findViewById(R.id.btn_addPost);
        spinner = v.findViewById(R.id.spinner_khuvuc);
        btnChonKhoangGia = v.findViewById(R.id.button_choose_range_price);
        btnLoaiPhong = v.findViewById(R.id.button_type_room);
        listView = v.findViewById(R.id.listview_type_room);
        btnUse = v.findViewById(R.id.button_used);
    }

    private void setUpListViewTypeRoom() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        dataRef = firebaseDatabase.getReference().child("typemotel");
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final ArrayList<String> arrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String typeRoom = dataSnapshot.getValue(String.class);
                    arrayList.add(typeRoom);
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, arrayList);
                listView.setAdapter(arrayAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String nameTypeRoom = arrayList.get(position);
                        btnLoaiPhong.setText(nameTypeRoom);

                        arrayMotelRoom.clear();
                        setUpRecyclerViewWithTypeRoom(nameTypeRoom);

                        bottomSheetBehaviorTypeRoom.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void flipperImage(int image) {
        ImageView imageView = new ImageView(getActivity());
        imageView.setBackgroundResource(image);
        viewFlipper.addView(imageView);
        viewFlipper.setFlipInterval(5000);
        viewFlipper.setAutoStart(true);

        //animation

        viewFlipper.setInAnimation(getActivity(), android.R.anim.slide_in_left);
        viewFlipper.setOutAnimation(getActivity(), android.R.anim.slide_out_right);
    }

    private void showBottomSheetTypeRoom() {
        bottomSheetBehaviorTypeRoom.setState(BottomSheetBehavior.STATE_COLLAPSED);
        setUpListViewTypeRoom();
    }

    private void showBottomSheetPriceRange() {
        bottomSheetBehaviorPriceRange.setState(BottomSheetBehavior.STATE_COLLAPSED);
        txtDisplay.setText("Giá từ 0đ đến 100000000đ");
        rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onProgressChanged(RangeSeekBar rangeSeekBar, int i, int i1, boolean b) {
                number1 = Long.parseLong(String.valueOf(i)) * 1000000;
                number2 = Long.parseLong(String.valueOf(i1)) * 1000000;
                txtDisplay.setText("Giá từ " + number1 + "đ đến " + number2 + "đ");
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar rangeSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar rangeSeekBar) {

            }
        });

        btnUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayMotelRoom.clear();
                setUpRecyclerViewWithRangePrice(number1, number2);
                bottomSheetBehaviorPriceRange.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }
}
