package com.doan.timnhatro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.doan.timnhatro.R;

import java.util.ArrayList;

public class ResultNullAdapter extends RecyclerView.Adapter<ResultNullAdapter.ViewHolder> {


    private ArrayList<String> arrayList = new ArrayList<>();
    public ResultNullAdapter(ArrayList<String> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ResultNullAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Nạp layout cho View biểu diễn phần tử sinh viên
        View nullView =
                inflater.inflate(R.layout.row_result_null, parent, false);

        ViewHolder viewHolder = new ViewHolder(nullView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ResultNullAdapter.ViewHolder holder, int position) {

        holder.txtResultnull.setText(arrayList.get(0));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtResultnull;
        ViewHolder(@NonNull View itemView) {
            super(itemView);


            txtResultnull = itemView.findViewById(R.id.textNull);
        }
    }
}
