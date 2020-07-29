package com.doan.timnhatro.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.doan.timnhatro.R;
import com.doan.timnhatro.base.Constants;
import com.doan.timnhatro.callback.OnNumberPickedListener;
import com.doan.timnhatro.callback.OnSinglePickPictureListener;
import com.doan.timnhatro.model.PicturePicker;
import com.doan.timnhatro.utils.ScreenUtils;

import java.util.ArrayList;

public class PicturePickerAdapter extends RecyclerView.Adapter<PicturePickerAdapter.ViewHolder>  {

    private ArrayList<PicturePicker> arrayImages;
    private String TYPE_PICKER;
    private OnNumberPickedListener onNumberPickedListener;
    private OnSinglePickPictureListener onSinglePickPictureListener;

    public PicturePickerAdapter(ArrayList<PicturePicker> arrayImages,String TYPE_PICKER) {
        this.arrayImages = arrayImages;
        this.TYPE_PICKER = TYPE_PICKER;
    }

    public void setOnNumberPickedListener(OnNumberPickedListener onNumberPickedListener) {
        this.onNumberPickedListener = onNumberPickedListener;
    }

    public void setOnSinglePickPictureListener(OnSinglePickPictureListener onSinglePickPictureListener) {
        this.onSinglePickPictureListener = onSinglePickPictureListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_picture_picker,null));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {
        Glide.with(holder.imgPicture.getContext())
                .load(arrayImages.get(i).getPathPicture())
                .centerCrop()
                .into(holder.imgPicture);

        if (TYPE_PICKER.equals(Constants.TYPE_PICK_MULTIPLE)) {
            holder.cbSelect.setVisibility(View.VISIBLE);
            holder.cbSelect.setChecked(arrayImages.get(holder.getAdapterPosition()).isChecked());
            holder.cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isCheckedButton) {
                    arrayImages.get(holder.getAdapterPosition()).setChecked(isCheckedButton);
                    onNumberPickedListener.onSelect();
                }
            });
        }else {
            holder.cbSelect.setVisibility(View.GONE);
            holder.imgPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSinglePickPictureListener.onSelector(arrayImages.get(holder.getAdapterPosition()).getPathPicture());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return arrayImages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView   imgPicture;
        CheckBox    cbSelect;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPicture  = itemView.findViewById(R.id.imgPicture);
            cbSelect    = itemView.findViewById(R.id.cbSelect);

            imgPicture.setLayoutParams(new FrameLayout.LayoutParams(ScreenUtils.getInstance().getWidth()/3 - 5,ScreenUtils.getInstance().getWidth()/3 - 5));
        }
    }
}