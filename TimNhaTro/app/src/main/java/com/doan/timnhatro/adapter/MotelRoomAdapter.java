package com.doan.timnhatro.adapter;

import android.content.Intent;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.doan.timnhatro.R;
import com.doan.timnhatro.base.Constants;
import com.doan.timnhatro.model.MotelRoom;
import com.doan.timnhatro.utils.AccountUtils;
import com.doan.timnhatro.utils.DateUtils;
import com.doan.timnhatro.view.DetailMotelRoomActivity;
import com.doan.timnhatro.view.LoginActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.graphics.Typeface.BOLD;

public class MotelRoomAdapter extends RecyclerView.Adapter<MotelRoomAdapter.ViewHolder> {

    private ArrayList<MotelRoom> arrayMotelRoom;
    private DecimalFormat formatMoney = new DecimalFormat("###,###");
    private Boolean CheckedlikePost = false;

    public MotelRoomAdapter(ArrayList<MotelRoom> arrayMotelRoom) {
        this.arrayMotelRoom = arrayMotelRoom;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_motel_room,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Glide.with(holder.imgAvatar.getContext())
                .load(arrayMotelRoom.get(holder.getAdapterPosition()).getAccount().getAvatar())
                .into(holder.imgAvatar);
        holder.txtName.setText(arrayMotelRoom.get(holder.getAdapterPosition()).getAccount().getName());
        holder.txtTime.setText(DateUtils.getTimeCount(arrayMotelRoom.get(holder.getAdapterPosition()).getId()));
        if (AccountUtils.getInstance().getAccount()!=null){
            holder.setLikePostStatus(arrayMotelRoom.get(holder.getAdapterPosition()).getId(), AccountUtils.getInstance().getAccount().getPhoneNumber());
        }
        holder.imgLikepost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AccountUtils.getInstance().getAccount()!=null){
                    CheckedlikePost = true;
                    FirebaseDatabase.getInstance().getReference()
                            .child("LikePost")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (CheckedlikePost.equals(true)){
                                        if (dataSnapshot.child(AccountUtils.getInstance().getAccount().getPhoneNumber())
                                                .hasChild(arrayMotelRoom.get(holder.getAdapterPosition()).getId())){
                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("LikePost")
                                                    .child(AccountUtils.getInstance().getAccount().getPhoneNumber())
                                                    .child(arrayMotelRoom.get(holder.getAdapterPosition()).getId()).removeValue();
                                            CheckedlikePost=false;
                                        }
                                        else {
                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("LikePost")
                                                    .child(AccountUtils.getInstance().getAccount().getPhoneNumber())
                                                    .child(arrayMotelRoom.get(holder.getAdapterPosition()).getId())
                                                    .setValue(arrayMotelRoom.get(holder.getAdapterPosition()));
                                            CheckedlikePost=false;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
                else {
                    Intent intent = new Intent(view.getContext(), LoginActivity.class);
                    view.getContext().startActivity(intent);
                }
            }
        });
        Glide.with(holder.imgPicture.getContext())
                .load(arrayMotelRoom.get(holder.getAdapterPosition()).getArrayPicture().get(0))
                .centerCrop()
                .into(holder.imgPicture);

        SpannableString formatPrice = new SpannableString("Giá phòng: " + formatMoney.format(arrayMotelRoom.get(holder.getAdapterPosition()).getPrice()).replace(",",".") + " VND");
        formatPrice.setSpan(new StyleSpan(BOLD),0,10,0);
        holder.txtPrice.setText(formatPrice);

        SpannableString formatStreet = new SpannableString("Đường: " + arrayMotelRoom.get(holder.getAdapterPosition()).getStreet() + ", " + arrayMotelRoom.get(holder.getAdapterPosition()).getDistrict() + ", "  + arrayMotelRoom.get(holder.getAdapterPosition()).getCity());
        formatStreet.setSpan(new StyleSpan(BOLD),0,6,0);
        holder.txtStreet.setText(formatStreet);

        holder.txtViewDetail.setPaintFlags(holder.txtViewDetail.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    @Override
    public int getItemCount() {
        return arrayMotelRoom.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView imgAvatar;
        TextView txtName,txtTime,txtPrice,txtStreet,txtViewDetail;
        ImageView imgPicture, imgLikepost;
        DatabaseReference LikePostRef;
        ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtName = itemView.findViewById(R.id.txtName);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtStreet = itemView.findViewById(R.id.txtStreet);
            imgPicture = itemView.findViewById(R.id.imgPicture);
            txtViewDetail = itemView.findViewById(R.id.txtViewDetail);
            imgLikepost = itemView.findViewById(R.id.likepost);
            LikePostRef = FirebaseDatabase.getInstance().getReference().child("LikePost");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DetailMotelRoomActivity.class);
                    intent.putExtra(Constants.MOTEL_ROOM, arrayMotelRoom.get(getAdapterPosition()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(intent);
                }
            });
        }
        public void setLikePostStatus(final String PostKey, final String IdUser){
            LikePostRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(IdUser).hasChild(PostKey)){
                        imgLikepost.setImageResource(R.drawable.ic_likepost_circle);
                    }
                    else {
                        imgLikepost.setImageResource(R.drawable.ic_dislike_circle);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
