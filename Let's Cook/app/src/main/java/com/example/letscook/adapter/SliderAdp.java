package com.example.letscook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.letscook.R;
import com.example.letscook.controller.slider.SliderActivity;
import com.smarteist.autoimageslider.SliderViewAdapter;

public class SliderAdp extends SliderViewAdapter<SliderAdp.Holder> {
    //Initialize variables
    int[] images;
    Context context;

    public SliderAdp(int[] images, Context context) {
        this.images = images;
        this.context = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {
        //Initialize view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slider, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {
        //Set image on image view
        Glide.with(context).load(images[position]).diskCacheStrategy(DiskCacheStrategy.ALL).into(viewHolder.imageView);
    }

    @Override
    public int getCount() {
        return images.length;
    }

    public static class Holder extends SliderViewAdapter.ViewHolder {
        ImageView imageView;

        public Holder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById((R.id.image_view));
        }
    }
}
