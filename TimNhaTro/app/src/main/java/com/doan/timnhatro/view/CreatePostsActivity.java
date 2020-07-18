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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.doan.timnhatro.model.Notification;
import com.doan.timnhatro.model.Position;
import com.doan.timnhatro.utils.AccountUtils;
import com.doan.timnhatro.utils.LocationUtils;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private final ArrayList<String> arrayTypeRoom = new ArrayList<>();
    private final ArrayList<String> arrayCity = new ArrayList<>();
    private final ArrayList<String> arrayDistrict = new ArrayList<>();
    private PictureIntroduceAdapter pictureIntroduceAdapter;
    private Position position;
    private EditText edtDescribe, edtPrice, edtStreet;
    private TextView txtLatitude, txtLongitude;
    private Spinner edtName, edtCity, edtDistrict;
    private ProgressDialog progressDialog;
    private DatabaseReference TypeMotelRef, CityNameRef, DistrictRef;

    String[] name = new String[2];
    String[] city = new String[2];
    String[] district = new String[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_posts);

        TypeMotelRef = FirebaseDatabase.getInstance().getReference().child("typemotel");
        CityNameRef = FirebaseDatabase.getInstance().getReference().child("CiTy");
        DistrictRef = FirebaseDatabase.getInstance().getReference().child("District");

        initView();
       /* edtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TypeMotelRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            int i=0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                                //arrayTypeRoom.add(snapshot.getValue().toString());
                                arrayTypeRoom.add(snapshot.getValue().toString());
                                AlertDialog.Builder builder =new AlertDialog.Builder(CreatePostsActivity.this);

                                if (arrayTypeRoom.size() == dataSnapshot.getChildrenCount()){
                                    builder.setTitle("Loại phòng").setItems(arrayTypeRoom.toArray(new String[0]), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            switch (i){
                                                case 0:
                                                    edtName.setText(arrayTypeRoom.get(0));
                                                    break;
                                                case 1:
                                                    edtName.setText(arrayTypeRoom.get(1));
                                                    break;
                                                case 2:
                                                    edtName.setText(arrayTypeRoom.get(2));
                                                    break;
                                                case 3:
                                                    edtName.setText(arrayTypeRoom.get(3));
                                                    break;
                                                case 4:
                                                    edtName.setText(arrayTypeRoom.get(4));
                                                    break;
                                            }
                                        }
                                    }).show();
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });*/
        //selectInfo();
        showTypeRoomSpiner();
        showCityNameSpiner();


        edtCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                city[1] = arrayCity.get(i);
                showDistrictNameSpiner();
                edtDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        district[0] = arrayDistrict.get(i);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        createPictureSelect();
        addEvents();
    }

    private void showDistrictNameSpiner() {
        if (!TextUtils.isEmpty(city[1])) {
            DistrictRef.child(city[1]).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        arrayDistrict.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            arrayDistrict.add(snapshot.getValue().toString());
                        }

                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(CreatePostsActivity.this, R.layout.support_simple_spinner_dropdown_item, arrayDistrict);
                        edtDistrict.setAdapter(arrayAdapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void showCityNameSpiner() {
        CityNameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    arrayCity.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        arrayCity.add(snapshot.getValue().toString());
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(CreatePostsActivity.this, R.layout.support_simple_spinner_dropdown_item, arrayCity);
                    edtCity.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void showTypeRoomSpiner() {
        TypeMotelRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    arrayTypeRoom.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        arrayTypeRoom.add(snapshot.getValue().toString());
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(CreatePostsActivity.this, R.layout.support_simple_spinner_dropdown_item, arrayTypeRoom);
                    edtName.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addEvents() {
        pictureIntroduceAdapter.setOnPickPictureListener(new OnPickPictureListener() {
            @Override
            public void onPicker() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_PERMISSION);
                        return;
                    }
                }
                Intent intent = new Intent(getApplicationContext(), PicturePickerActivity.class);
                intent.putExtra(Constants.TYPE_PICKER, Constants.TYPE_PICK_MULTIPLE);
                startActivityForResult(intent, Constants.REQUEST_CODE_PICTURE);
            }
        });
    }

    private void createPictureSelect() {
        recPicture.setHasFixedSize(true);
        recPicture.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        pictureIntroduceAdapter = new PictureIntroduceAdapter(arrayPicture);
        recPicture.setAdapter(pictureIntroduceAdapter);
    }

    public void onClickCreatePosts(View view) {
        //String name         = edtName.getText().toString();
        String describe = edtDescribe.getText().toString();
        String price = edtPrice.getText().toString();
        String street = edtStreet.getText().toString();
        //String district     = edtDistrict.getText().toString();
        // String city         = edtCity.getText().toString();


        edtName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                name[0] = arrayTypeRoom.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (arrayPicture.size() == 0) {
            Toast.makeText(this, "Vui lòng chọn hình ảnh phòng trọ !", Toast.LENGTH_SHORT).show();
            return;
        }


        if (TextUtils.isEmpty(describe)) {
            Toast.makeText(this, "Mô tả không được để trống !", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(price)) {
            Toast.makeText(this, "Giá tiền không được để trống !", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(street)) {
            Toast.makeText(this, "Tên đường không được để trống !", Toast.LENGTH_SHORT).show();
            return;
        }

        if (position == null) {
            Toast.makeText(this, "Tọa độ chưa được chọn !", Toast.LENGTH_SHORT).show();
            return;
        }

        arrayPicture.remove("Demo");

        final String id = System.currentTimeMillis() + "";

        final MotelRoom motelRoom = new MotelRoom();
        motelRoom.setId(id);
        motelRoom.setNameMotelRoom(name[0]);
        motelRoom.setDescribe(describe);
        motelRoom.setPrice(Long.valueOf(price));
        motelRoom.setStreet(street);
        motelRoom.setDistrict(district[0]);
        motelRoom.setCity(city[0]);
        motelRoom.setPosition(position);
        motelRoom.setAccount(AccountUtils.getInstance().getAccount());

        progressDialog.show();
        uploadPicture(motelRoom);


    }

    private void uploadPicture(final MotelRoom motelRoom) {

        final ArrayList<String> realArrayPicture = new ArrayList<>();

        for (int i = 0; i < arrayPicture.size(); i++) {

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

                            if (realArrayPicture.size() == arrayPicture.size()) {
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
                if (error == null) {
                    progressDialog.setMessage("Đang tải lên bài viết...");
                    FirebaseDatabase.getInstance().getReference()
                            .child("MotelRoomQueue")
                            .child(motelRoom.getId())
                            .setValue(motelRoom, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        progressDialog.cancel();
                                        Toast.makeText(getApplicationContext(), "Tạo phòng trọ thành công", Toast.LENGTH_SHORT).show();

                                        // Insert notification into Admin
                                        Notification notification = new Notification();
                                        notification.setId(System.currentTimeMillis() + "");
                                        notification.setUserName(AccountUtils.getInstance().getAccount().getUserName());

                                        FirebaseDatabase.getInstance().getReference().child("NotificationAdmin").child(notification.getId()).setValue(notification);

                                        finish();
                                    } else {
                                        progressDialog.cancel();
                                        geoFire.removeLocation(motelRoom.getId());
                                        Toast.makeText(getApplicationContext(), "Tạo phòng trọ thất bại !", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    progressDialog.cancel();
                    Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initView() {
        recPicture = findViewById(R.id.recPicture);
        edtName = findViewById(R.id.edtName);
        edtDescribe = findViewById(R.id.edtDescribe);
        edtPrice = findViewById(R.id.edtPrice);
        edtStreet = findViewById(R.id.edtStreet);
        edtDistrict = findViewById(R.id.edtDistrict);
        edtCity = findViewById(R.id.edtCity);
        txtLatitude = findViewById(R.id.txtLatitude);
        txtLongitude = findViewById(R.id.txtLongitude);


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

        if (requestCode == Constants.REQUEST_CODE_PICTURE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> arrayPictureResult = data.getStringArrayListExtra(Constants.PICTURE);

            if (arrayPictureResult == null) {
                return;
            }
            arrayPicture.addAll(arrayPictureResult);
            pictureIntroduceAdapter.notifyDataSetChanged();
        } else {

            if (data == null) {
                return;
            }

            position = data.getParcelableExtra(Constants.POSITION);


            if (position == null) {
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
                intent.putExtra(Constants.TYPE_PICKER, Constants.TYPE_PICK_MULTIPLE);
                startActivityForResult(intent, Constants.REQUEST_CODE_PICTURE);
            }
        }
    }

    public void onClickSelectPosition(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.REQUEST_PERMISSION);
                return;
            }
        }

        if (LocationUtils.getInstance().isGPSEnable()) {
            startActivityForResult(new Intent(this, SelectLocationActivity.class), 241);
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("GPS Chưa Được Kích Hoạt")
                .setMessage("Vui lòng kích hoạt GPS và thử lại sau")
                .setPositiveButton("Trở lại", null)
                .show();
    }
}
