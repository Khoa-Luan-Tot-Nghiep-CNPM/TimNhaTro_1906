package com.doan.timnhatro.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doan.timnhatro.R;
import com.doan.timnhatro.adapter.PictureIntroduceAdapter;
import com.doan.timnhatro.base.Constants;
import com.doan.timnhatro.callback.OnPickPictureListener;
import com.doan.timnhatro.model.MotelRoom;
import com.doan.timnhatro.model.Position;
import com.doan.timnhatro.utils.AccountUtils;
import com.doan.timnhatro.utils.LocationUtils;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

public class CreatePostsActivity extends AppCompatActivity {

    private RecyclerView recPicture;
    private ArrayList<String> arrayPicture = new ArrayList<>();
    private PictureIntroduceAdapter pictureIntroduceAdapter;
    private Position position;
    private EditText edtName,edtDescribe,edtPrice,edtStreet,edtDistrict,edtCity;
    private TextView txtLatitude,txtLongitude;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_posts);

        initView();
        createPictureSelect();
        addEvents();
    }

    private void addEvents() {
        pictureIntroduceAdapter.setOnPickPictureListener(new OnPickPictureListener() {
            @Override
            public void onPicker() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_PERMISSION);
                        return;
                    }
                }
                Intent intent = new Intent(getApplicationContext(), PicturePickerActivity.class);
                intent.putExtra(Constants.TYPE_PICKER,Constants.TYPE_PICK_MULTIPLE);
                startActivityForResult(intent, Constants.REQUEST_CODE_PICTURE);
            }
        });
    }

    private void createPictureSelect() {
        recPicture.setHasFixedSize(true);
        recPicture.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        pictureIntroduceAdapter = new PictureIntroduceAdapter(arrayPicture);
        recPicture.setAdapter(pictureIntroduceAdapter);
    }

    public void onClickCreatePosts(View view) {
        String name         = edtName.getText().toString();
        String describe     = edtDescribe.getText().toString();
        String price        = edtPrice.getText().toString();
        String street       = edtStreet.getText().toString();
        String district     = edtDistrict.getText().toString();
        String city         = edtCity.getText().toString();

        if (arrayPicture.size() == 0){
            Toast.makeText(this, "Vui lòng chọn hình ảnh phòng trọ !", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(name)){
            Toast.makeText(this, "Tên phòng trọ không được để trống !", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(describe)){
            Toast.makeText(this, "Mô tả không được để trống !", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(price)){
            Toast.makeText(this, "Giá tiền không được để trống !", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(street)){
            Toast.makeText(this, "Tên đường không được để trống !", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(district)){
            Toast.makeText(this, "Quận/Huyện không được để trống !", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(city)){
            Toast.makeText(this, "Tên thành phố không được để trống !", Toast.LENGTH_SHORT).show();
            return;
        }

        if (position == null){
            Toast.makeText(this, "Tọa độ chưa được chọn !", Toast.LENGTH_SHORT).show();
            return;
        }

        arrayPicture.remove("Demo");

        final String id = System.currentTimeMillis() + "";

        final MotelRoom motelRoom = new MotelRoom();
        motelRoom.setId(id);
        motelRoom.setNameMotelRoom(name);
        motelRoom.setDescribe(describe);
        motelRoom.setPrice(Long.valueOf(price));
        motelRoom.setStreet(street);
        motelRoom.setDistrict(district);
        motelRoom.setCity(city);
        motelRoom.setPosition(position);
        motelRoom.setAccount(AccountUtils.getInstance().getAccount());

        progressDialog.show();
        uploadPicture(motelRoom);
    }

    private void uploadPicture(final MotelRoom motelRoom) {

        final ArrayList<String> realArrayPicture = new ArrayList<>();

        for (int i=0; i < arrayPicture.size(); i++){

            final StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference().child(System.currentTimeMillis() + i + "");

            try {
                InputStream inputStream = new FileInputStream(arrayPicture.get(i));

                UploadTask uploadTask = firebaseStorage.putStream(inputStream);

                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }
                        return firebaseStorage.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();

                            realArrayPicture.add(downloadUri.toString());

                            if (realArrayPicture.size() == arrayPicture.size()){
                                motelRoom.setArrayPicture(realArrayPicture);

                                insertMotelRoom(motelRoom);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
                progressDialog.cancel();
            }
        }
    }

    private void insertMotelRoom(final MotelRoom motelRoom) {
        progressDialog.setMessage("Đang tải lên vị trí...");
        DatabaseReference mapReference = FirebaseDatabase.getInstance().getReference().child("Maps");
        final GeoFire geoFire = new GeoFire(mapReference);

        geoFire.setLocation(motelRoom.getId(), new GeoLocation(position.getLatitude(), position.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error == null){
                    progressDialog.setMessage("Đang tải lên bài viết...");
                    FirebaseDatabase.getInstance().getReference()
                            .child("MotelRoomQueue")
                            .child(motelRoom.getId())
                            .setValue(motelRoom, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (databaseError == null){
                                        progressDialog.cancel();
                                        Toast.makeText(getApplicationContext(), "Tạo phòng trọ thành công", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }else {
                                        progressDialog.cancel();
                                        geoFire.removeLocation(motelRoom.getId());
                                        Toast.makeText(getApplicationContext(), "Tạo phòng trọ thất bại !", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else {
                    progressDialog.cancel();
                    Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initView() {
        recPicture      = findViewById(R.id.recPicture);
        edtName         = findViewById(R.id.edtName);
        edtDescribe     = findViewById(R.id.edtDescribe);
        edtPrice        = findViewById(R.id.edtPrice);
        edtStreet       = findViewById(R.id.edtStreet);
        edtDistrict     = findViewById(R.id.edtDistrict);
        edtCity         = findViewById(R.id.edtCity);
        txtLatitude     = findViewById(R.id.txtLatitude);
        txtLongitude    = findViewById(R.id.txtLongitude);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang tải lên hình ảnh");
        progressDialog.setCancelable(false);
    }

    public void cnClickCancel(View view) {
        finish();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CODE_PICTURE && resultCode == RESULT_OK && data != null){
            ArrayList<String> arrayPictureResult = data.getStringArrayListExtra(Constants.PICTURE);

            if (arrayPictureResult == null){
                return;
            }
            arrayPicture.addAll(arrayPictureResult);
            pictureIntroduceAdapter.notifyDataSetChanged();
        }else {

            if (data == null){
                return;
            }

            position = data.getParcelableExtra(Constants.POSITION);

            if (position == null){
                return;
            }

            txtLatitude.setText(position.getLatitude() + "");
            txtLongitude.setText(position.getLongitude() + "");
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(getApplicationContext(), PicturePickerActivity.class);
                intent.putExtra(Constants.TYPE_PICKER,Constants.TYPE_PICK_MULTIPLE);
                startActivityForResult(intent, Constants.REQUEST_CODE_PICTURE);
            }
        }
    }

    public void onClickSelectPosition(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.REQUEST_PERMISSION);
                return;
            }
        }

        if (LocationUtils.getInstance().isGPSEnable()) {
            startActivityForResult(new Intent(this,SelectLocationActivity.class),241);
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("GPS Chưa Được Kích Hoạt")
                .setMessage("Vui lòng kích hoạt GPS và thử lại sau")
                .setPositiveButton("Trở lại",null)
                .show();
    }
}
