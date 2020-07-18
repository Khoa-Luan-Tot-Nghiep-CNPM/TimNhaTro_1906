package com.doan.timnhatro.adapter;

import android.content.Intent;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.doan.timnhatro.R;
import com.doan.timnhatro.base.Constants;
import com.doan.timnhatro.model.MotelRoom;
import com.doan.timnhatro.utils.DateUtils;
import com.doan.timnhatro.view.DetailMotelRoomActivity;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.graphics.Typeface.BOLD;

public class MotelRoomManagerAdapter extends RecyclerView.Adapter<MotelRoomManagerAdapter.ViewHolder> {

    private ArrayList<MotelRoom> arrayMotelRoom;
    private DecimalFormat formatMoney = new DecimalFormat("###,###");

    public MotelRoomManagerAdapter(ArrayList<MotelRoom> arrayMotelRoom) {
        this.arrayMotelRoom = arrayMotelRoom;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_motel_room_manager,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(holder.imgAvatar.getContext())
                .load(arrayMotelRoom.get(holder.getAdapterPosition()).getAccount().getAvatar())
                .into(holder.imgAvatar);
        holder.txtName.setText(arrayMotelRoom.get(holder.getAdapterPosition()).getAccount().getName());
        holder.txtTime.setText(DateUtils.getTimeCount(arrayMotelRoom.get(holder.getAdapterPosition()).getId()));
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
    }

    @Override
    public int getItemCount() {
        return arrayMotelRoom.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView imgAvatar;
        TextView txtName,txtTime,txtPrice,txtStreet;
        ImageView imgPicture;
        LinearLayout llAgree,llRefuse;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtName = itemView.findViewById(R.id.txtName);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtStreet = itemView.findViewById(R.id.txtStreet);
            imgPicture = itemView.findViewById(R.id.imgPicture);
            llAgree = itemView.findViewById(R.id.llAgree);
            llRefuse = itemView.findViewById(R.id.llRefuse);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DetailMotelRoomActivity.class);
                    intent.putExtra(Constants.MOTEL_ROOM, arrayMotelRoom.get(getAdapterPosition()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(intent);
                }
            });

            llAgree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("MotelRoom")
                            .child(arrayMotelRoom.get(getAdapterPosition()).getId())
                            .setValue(arrayMotelRoom.get(getAdapterPosition()),
                                    new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                            if (databaseError == null){
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child("MotelRoomQueue")
                                                        .child(arrayMotelRoom.get(getAdapterPosition()).getId())
                                                        .setValue(null);
                                                arrayMotelRoom.remove(getAdapterPosition());
                                                notifyItemRemoved(getAdapterPosition());

                                                Toast.makeText(v.getContext(), "Phê duyệt thành công", Toast.LENGTH_SHORT).show();
                                            }else {
                                                Toast.makeText(v.getContext(), "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                }
            });

            llRefuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("MotelRoomQueue")
                            .child(arrayMotelRoom.get(getAdapterPosition()).getId())
                            .setValue(null);
                    arrayMotelRoom.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());

                    Toast.makeText(v.getContext(), "Xóa bài thành công", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
