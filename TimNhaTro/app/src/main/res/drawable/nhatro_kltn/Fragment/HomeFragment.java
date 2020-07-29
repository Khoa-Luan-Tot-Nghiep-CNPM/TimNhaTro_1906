package com.doan.nhatro_kltn.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.doan.nhatro_kltn.Adapter.MotelRoomAdapter;
import com.doan.nhatro_kltn.Model.MotelRoom;
import com.doan.nhatro_kltn.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class HomeFragment extends Fragment {


    //private View container;
    private RecyclerView recRoomsFeatured;
    private MotelRoomAdapter motelRoomAdapter;
    private ArrayList<MotelRoom> arrayMotelRoom = new ArrayList<>();
    private ArrayList<MotelRoom> arrayMotelRoomSave = new ArrayList<>();
    public HomeFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    ViewFlipper viewFlipper;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_home, container, false);

        initView(v);
        SliderShow(v);


        return v;
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
                .child("MotelRoom")
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
        recRoomsFeatured.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL,true));
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
