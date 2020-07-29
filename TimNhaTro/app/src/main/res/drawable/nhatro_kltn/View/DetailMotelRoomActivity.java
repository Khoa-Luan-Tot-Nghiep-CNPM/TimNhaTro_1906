package com.doan.nhatro_kltn.View;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.doan.nhatro_kltn.Adapter.PictureArrayAdapter;
import com.doan.nhatro_kltn.Base.Constant;
import com.doan.nhatro_kltn.Model.MotelRoom;
import com.doan.nhatro_kltn.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.graphics.Typeface.BOLD;

public class DetailMotelRoomActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MotelRoom motelRoom;
    private CircleImageView imgAvatar;
    private TextView txtNameMotelRoom,txtTime,txtPrice,txtStreet,edtDescribe,txtName,txtPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_motel_room);

        initView();
        getIntentData();
        createToolbar();
        createArrayPicture();
        loadUI();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    private void loadUI() {
        txtNameMotelRoom.setText(motelRoom.getNameMotelRoom());

        @SuppressLint("SimpleDateFormat")
        SpannableString formatTime = new SpannableString("Ngày đăng:   " + new SimpleDateFormat("HH:mm:ss   dd/MM/yyyy").format(Long.valueOf(motelRoom.getId())));
        formatTime.setSpan(new StyleSpan(BOLD),0,10,0);
        formatTime.setSpan(new ForegroundColorSpan(Color.BLACK),0,10,0);
        txtTime.setText(formatTime);

        SpannableString formatPrice = new SpannableString("Giá phòng:   " + new DecimalFormat("###,###").format(motelRoom.getPrice()).replace(",",".") + " VND");
        formatPrice.setSpan(new StyleSpan(BOLD),0,10,0);
        txtPrice.setText(formatPrice);

        SpannableString formatStreet = new SpannableString("Đường:   " + motelRoom.getStreet() + ",  " + motelRoom.getDistrict() + ",  " + motelRoom.getCity());
        formatStreet.setSpan(new StyleSpan(BOLD),0,6,0);
        txtStreet.setText(formatStreet);

        edtDescribe.setText(motelRoom.getDescribe());

        Glide.with(this)
                .load(motelRoom.getAccount().getAvatar())
                .into(imgAvatar);

        txtName.setText(motelRoom.getAccount().getName());

        SpannableString formatPhoneNumber = new SpannableString("Số điện thoại:   " + motelRoom.getAccount().getPhoneNumber());
        formatPhoneNumber.setSpan(new StyleSpan(BOLD),0,14,0);
        formatTime.setSpan(new ForegroundColorSpan(Color.BLACK),0,14,0);
        txtPhoneNumber.setText(formatPhoneNumber);
    }

    private void createArrayPicture() {
        PictureArrayAdapter pictureArrayAdapter = new PictureArrayAdapter(motelRoom.getArrayPicture());
        viewPager.setAdapter(pictureArrayAdapter);
    }

    private void createToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white_32dp);
    }

    private void getIntentData() {
        motelRoom = getIntent().getParcelableExtra(Constant.MOTEL_ROOM);
    }

    private void initView() {
        viewPager = findViewById(R.id.viewPager);
        txtNameMotelRoom = findViewById(R.id.txtNameMotelRoom);
        txtTime = findViewById(R.id.txtTime);
        txtPrice = findViewById(R.id.txtPrice);
        txtStreet = findViewById(R.id.txtStreet);
        edtDescribe = findViewById(R.id.edtDescribe);
        imgAvatar = findViewById(R.id.imgAvatar);
        txtName = findViewById(R.id.txtName);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
    }
}
