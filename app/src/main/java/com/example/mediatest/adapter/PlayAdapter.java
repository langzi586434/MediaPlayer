package com.example.mediatest.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mediatest.R;
import com.example.mediatest.commit.Comment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlayAdapter extends BaseAdapter {
    private static final String TAG = "PlayAdapter";
    private Context context;
    private ArrayList<File> files;
    private onAdapterClickInterface onAdapterClickInterface;
    private String type;

    public void setOnAdapterClickInterface(PlayAdapter.onAdapterClickInterface onAdapterClickInterface) {
        this.onAdapterClickInterface = onAdapterClickInterface;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setFiles(ArrayList<File> files, String type) {
        if (this.files != null) this.files.clear();
        this.files = files;
        this.type = type;
        notifyDataSetChanged();
    }

    public ArrayList<File> getFiles() {
        return files;
    }

    public void addDataAll(List<File> list,String type) {
        this.type = type;
        List<File> data = getFiles();
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        Log.d(TAG, "getCount: " + files.size());
        return files.size();
    }

    @Override
    public Object getItem(int i) {
        return files.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_play_item_layout, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.play_image);
            holder.textView = (TextView) convertView.findViewById(R.id.play_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (type.equals(Comment.VIDEO))
            holder.imageView.setImageBitmap(getVideoThumb(files.get(i).getPath()));
//            Glide.with(context)
//                    .load(files.get(i).getPath())
//                    .into(holder.imageView);
        else
            holder.imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.mus));
//            Glide.with(context)
//                    .load(R.drawable.mus)
//                    .into(holder.imageView);

        holder.textView.setText(files.get(i).getName());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAdapterClickInterface.onclick(i);
            }
        });
        return convertView;
    }

    public static Bitmap getVideoThumb(String path) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(path);
        return media.getFrameAtTime();
    }


    static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }

    public interface onAdapterClickInterface {
        void onclick(int position);
    }
}
