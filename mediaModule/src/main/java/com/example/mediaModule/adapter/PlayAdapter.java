package com.example.mediaModule.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.mylibrary.baseAdapter.RBaseAdapter;
import com.example.mediaModule.R;

import java.io.File;

public class PlayAdapter extends RBaseAdapter<File, PlayAdapter.VH> {

    public PlayAdapter(Context context, int layoutId) {
        super(context, layoutId);
    }

    @Override
    public void changeView(int index) {
        Glide.with(context)
                .load(files.get(index).getPath())
                .into(vh.imageView);
        vh.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "onClick: "+index);
                onAdapterItemListener.onItemClick(index, v);
            }
        });
    }

    @Override
    public VH initView(View view) {
        vh = new VH();
        vh.imageView = view.findViewById(R.id.play_image);
        return vh;
    }


    public static class VH extends RBaseAdapter.RBaseViewHolder {
        ImageView imageView;

        @Override
        protected void initView() {
            super.initView();
        }
    }

}
