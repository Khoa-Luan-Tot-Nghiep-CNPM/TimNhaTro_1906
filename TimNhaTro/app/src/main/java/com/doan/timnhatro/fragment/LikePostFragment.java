package com.doan.timnhatro.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.doan.timnhatro.R;
import com.doan.timnhatro.adapter.MotelRoomAdapter;
import com.doan.timnhatro.base.Constants;
import com.doan.timnhatro.model.MotelRoom;
import com.doan.timnhatro.utils.AccountUtils;
import com.doan.timnhatro.utils.LocationUtils;
import com.doan.timnhatro.view.CreatePostsActivity;
import com.doan.timnhatro.view.LoginActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LikePostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LikePostFragment extends Fragment {
    //private View container;
    private RecyclerView recRoomsFeatured;
    private MotelRoomAdapter motelRoomAdapter;
    private Button SearchNearBtn, AddPostBtn;
    private ArrayList<MotelRoom> arrayMotelRoom = new ArrayList<>();
    private ArrayList<MotelRoom> arrayMotelRoomSave = new ArrayList<>();
    public LikePostFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static HouseFragment newInstance(String param1, String param2) {
        HouseFragment fragment = new HouseFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    ViewFlipper viewFlipper;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_house, container, false);

        initView(v);
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
        return v;
    }

    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.REQUEST_PERMISSION);
            return false;
        }
        if (LocationUtils.getInstance().isGPSEnable()) {
            loadFragment(new MapsFragment());
            return true;
        }

        new AlertDialog.Builder(getActivity().getApplicationContext())
                .setTitle("GPS Chưa Được Kích Hoạt")
                .setMessage("Vui lòng kích hoạt GPS và thử lại sau")
                .setPositiveButton("Trở lại",null)
                .show();

        return false;
    }

    private void loadFragment(MapsFragment mapsFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, mapsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createList();
        getArrayMotelRoom();
        //addEvents();
    }

    /*private void addEvents() {

    }*/

    private void getArrayMotelRoom() {
        FirebaseDatabase.getInstance().getReference()
                .child("LikePost")
                .child(AccountUtils.getInstance().getAccount().getPhoneNumber())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                arrayMotelRoom.add(snapshot.getValue(MotelRoom.class));
                                arrayMotelRoomSave.add(snapshot.getValue(MotelRoom.class));
                            }
                            motelRoomAdapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void createList() {
        recRoomsFeatured.setHasFixedSize(true);
        recRoomsFeatured.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,true));
        motelRoomAdapter = new MotelRoomAdapter(arrayMotelRoom);
        recRoomsFeatured.setAdapter(motelRoomAdapter);
    }

    private void SliderShow(View v) {
        int image[]= {R.drawable.slide1,R.drawable.slide2,R.drawable.slide3};
        viewFlipper=(ViewFlipper)v.findViewById(R.id.vf_image_slider);

        for (int i=0;i<image.length;i++) {
            flipperImage(image[i]);
        }
    }

    private void initView(View v) {
        recRoomsFeatured = v.findViewById(R.id.recRoomsFeatured);
        SearchNearBtn = v.findViewById(R.id.btn_findNear);
        AddPostBtn = v.findViewById(R.id.btn_addPost);
    }

    public void flipperImage(int image){
        ImageView imageView=new ImageView(getActivity());
        imageView.setBackgroundResource(image);
        viewFlipper.addView(imageView);
        viewFlipper.setFlipInterval(5000);
        viewFlipper.setAutoStart(true);

        //animation

        viewFlipper.setInAnimation(getActivity(),android.R.anim.slide_in_left);
        viewFlipper.setOutAnimation(getActivity(),android.R.anim.slide_out_right);

    }
}
