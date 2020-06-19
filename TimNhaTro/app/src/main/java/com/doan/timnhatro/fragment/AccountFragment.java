package com.doan.timnhatro.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.doan.timnhatro.R;
import com.doan.timnhatro.adapter.MotelRoomAdapter;
import com.doan.timnhatro.base.Constants;
import com.doan.timnhatro.model.Account;
import com.doan.timnhatro.model.MotelRoom;
import com.doan.timnhatro.utils.AccountUtils;
import com.doan.timnhatro.view.HouseActivity;
import com.doan.timnhatro.view.PostsManagerActivity;
import com.doan.timnhatro.view.UpdateNameActivity;
import com.doan.timnhatro.view.UpdatePasswordActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment {

    private View container;
    private Button btnOptions,btnLogout;
    private TextView txtName,txtPhoneNumber;
    private CircleImageView imgAvatar;
    private MotelRoomAdapter motelRoomAdapter;
    private RecyclerView recRoomsFeatured;
    private ArrayList<MotelRoom> arrayMotelRoom = new ArrayList<>();

    public AccountFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {
        container = inflater.inflate(R.layout.fragment_account, viewGroup, false);
        initView();
        return container;
    }

    private void initView() {
        imgAvatar = container.findViewById(R.id.imgAvatar);
        txtName = container.findViewById(R.id.txtName);
        txtPhoneNumber = container.findViewById(R.id.txtPhoneNumber);
        btnOptions = container.findViewById(R.id.btnOptions);
        btnLogout = container.findViewById(R.id.btnLogout);
        recRoomsFeatured = container.findViewById(R.id.recRoomsFeatured);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        loadUI();
        createList();
        getArrayMotelRoom();
        addEvents();
    }

    private void getArrayMotelRoom() {
        FirebaseDatabase.getInstance().getReference()
                .child("MotelRoom")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){

                            MotelRoom motelRoom;
                            String userName = AccountUtils.getInstance().getAccount().getUserName();

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                                motelRoom = snapshot.getValue(MotelRoom.class);

                                if (motelRoom.getAccount().getUserName().equals(userName)) {
                                    arrayMotelRoom.add(motelRoom);
                                }
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

    private void addEvents() {
        btnOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (AccountUtils.getInstance().getAccount().getPhoneNumber().equals(Constants.PHONE_NUMBER_ADMIN)){
                    startActivity(new Intent(getActivity(), PostsManagerActivity.class));
                    return;
                }

                String[] items = {"Đổi mật khẩu","Đổi tên"};

                new AlertDialog.Builder(v.getContext())
                        .setTitle("Cập Nhật Thông Tin")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        startActivity(new Intent(v.getContext(), UpdatePasswordActivity.class));
                                        break;
                                    case 1:
                                        startActivity(new Intent(v.getContext(), UpdateNameActivity.class));
                                        break;
                                }
                            }
                        })
                        .show();
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Đăng Xuất")
                        .setMessage("Bạn có muốn đăng xuất tài khoản hiện tại ra khỏi ứng dụng này ?")
                        .setNegativeButton("Đăng xuất", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AccountUtils.getInstance().logOut();

                                Intent intent = new Intent(v.getContext(), HouseActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        })
                        .setPositiveButton("Trở lại",null)
                        .show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        loadUI();
    }

    private void loadUI() {
        Account account = AccountUtils.getInstance().getAccount();

        if (account.getPhoneNumber().equals(Constants.PHONE_NUMBER_ADMIN)){
            btnOptions.setText("Quản Trị");
        }

        Glide.with(Objects.requireNonNull(getActivity()))
                .load(account.getAvatar())
                .into(imgAvatar);

        txtName.setText(account.getName());
        txtPhoneNumber.setText(account.getPhoneNumber());
    }
}
