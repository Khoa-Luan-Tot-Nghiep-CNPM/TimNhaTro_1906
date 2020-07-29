package com.doan.nhatro_kltn.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class PictureArrayAdapter extends PagerAdapter {
    private ArrayList<String> arrayPicture;

    public PictureArrayAdapter(ArrayList<String> arrayPicture) {
        this.arrayPicture = arrayPicture;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return arrayPicture.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        ImageView imgPicture = new ImageView(container.getContext());
        imgPicture.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        Glide.with(container.getContext())
                .load(arrayPicture.get(position))
                .centerCrop()
                .into(imgPicture);

        container.addView(imgPicture);

        return imgPicture;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }
}
