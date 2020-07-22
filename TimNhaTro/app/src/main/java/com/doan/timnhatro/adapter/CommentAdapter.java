package com.doan.timnhatro.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.doan.timnhatro.R;
import com.doan.timnhatro.model.Comments;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    private ArrayList<Comments> arrayComments;

    public CommentAdapter(ArrayList<Comments> arrayComments) {
        this.arrayComments = arrayComments;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_commets,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentAdapter.ViewHolder holder, int position) {
        Glide.with(holder.imgAvatar.getContext())
                .load(arrayComments.get(holder.getAdapterPosition()).getAvatar())
                .into(holder.imgAvatar);

        holder.txtUser.setText(arrayComments.get(holder.getAdapterPosition()).getUsername());
        holder.txtTime.setText(arrayComments.get(holder.getAdapterPosition()).getTime());
        holder.txtDate.setText(arrayComments.get(holder.getAdapterPosition()).getDate());
        holder.txtComment.setText(arrayComments.get(holder.getAdapterPosition()).getComment());
    }

    @Override
    public int getItemCount() {
        return arrayComments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView imgAvatar;
        TextView txtUser,txtTime,txtDate,txtComment;
        ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAvatar = itemView.findViewById(R.id.imgAvatarCmt);
            txtUser = itemView.findViewById(R.id.comment_username);
            txtTime = itemView.findViewById(R.id.comment_time);
            txtDate = itemView.findViewById(R.id.comment_date);
            txtComment = itemView.findViewById(R.id.comment_text);

        }

    }


}
