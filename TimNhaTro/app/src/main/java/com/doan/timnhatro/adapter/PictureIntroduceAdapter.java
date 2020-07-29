package com.doan.timnhatro.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.doan.timnhatro.R;
import com.doan.timnhatro.callback.OnPickPictureListener;

import java.util.ArrayList;

public class PictureIntroduceAdapter extends RecyclerView.Adapter<PictureIntroduceAdapter.ViewHolder>{

    private final int TYPE_PREVIEW_IMAGE        = 1;
    private final int TYPE_PREVIEW_ADD_IMAGE    = 2;

    private ArrayList<String> arrayPicture;
    private OnPickPictureListener onPickPictureListener;

    public PictureIntroduceAdapter(ArrayList<String> arrayPicture) {
        this.arrayPicture = arrayPicture;
        this.arrayPicture.add(0,"Demo");
    }

    public void setOnPickPictureListener(OnPickPictureListener onPickPictureListener) {
        this.onPickPictureListener = onPickPictureListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (arrayPicture.get(position).equals("Demo")) {
            return TYPE_PREVIEW_ADD_IMAGE;
        }else {
            return TYPE_PREVIEW_IMAGE;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = null;
        switch (i){
            case TYPE_PREVIEW_IMAGE:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_picture_introduce,viewGroup,false);
                break;
            case TYPE_PREVIEW_ADD_IMAGE:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_add_picture_introduce,viewGroup,false);
                break;
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {
        switch (holder.getItemViewType()){
            case TYPE_PREVIEW_IMAGE:
                Glide.with(holder.imgPicture.getContext())
                        .load(arrayPicture.get(i))
                        .override(400)
                        .centerCrop()
                        .into(holder.imgPicture);
                holder.imgDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        arrayPicture.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                    }
                });
                break;
            case TYPE_PREVIEW_ADD_IMAGE:
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onPickPictureListener.onPicker();
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return arrayPicture.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgPicture,imgDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPicture  = itemView.findViewById(R.id.imgPicture);
            imgDelete   = itemView.findViewById(R.id.imgDelete);
        }
    }
}
