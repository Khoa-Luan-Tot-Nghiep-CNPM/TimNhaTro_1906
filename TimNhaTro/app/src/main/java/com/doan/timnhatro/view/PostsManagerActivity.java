package com.doan.timnhatro.view;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doan.timnhatro.R;
import com.doan.timnhatro.adapter.MotelRoomManagerAdapter;
import com.doan.timnhatro.model.MotelRoom;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PostsManagerActivity extends AppCompatActivity {

    private MotelRoomManagerAdapter motelRoomManagerAdapter;
    private RecyclerView recRoomsFeatured;
    private ArrayList<MotelRoom> arrayMotelRoom = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_manager);

        initView();
        createToolbar();
        createList();
        getArrayMotelRoom();
    }

    private void initView() {
        recRoomsFeatured = findViewById(R.id.recRoomsFeatured);
    }

    private void getArrayMotelRoom() {
        FirebaseDatabase.getInstance().getReference()
                .child("MotelRoomQueue")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                arrayMotelRoom.add(snapshot.getValue(MotelRoom.class));
                            }
                            motelRoomManagerAdapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void createList() {
        recRoomsFeatured.setHasFixedSize(true);
        recRoomsFeatured.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,true));
        motelRoomManagerAdapter = new MotelRoomManagerAdapter(arrayMotelRoom);
        recRoomsFeatured.setAdapter(motelRoomManagerAdapter);
    }

    private void createToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white_32dp);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }
}
