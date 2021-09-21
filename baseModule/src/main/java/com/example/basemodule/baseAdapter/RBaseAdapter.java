package com.example.basemodule.baseAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;


public abstract class RBaseAdapter<T, V> extends BaseAdapter {
    protected final Context context;
    protected ArrayList<T> files;
    private final int layoutId;
    protected V vh;
    protected OnAdapterItemListener onAdapterItemListener;
    protected OnAdapterItemLongListener onAdapterItemLongListener;
    protected OnAdapterItemChildListener onAdapterItemChildListener;

    public void setOnAdapterItemChildListener(OnAdapterItemChildListener onAdapterItemChildListener) {
        this.onAdapterItemChildListener = onAdapterItemChildListener;
    }

    public void setOnAdapterItemLongListener(OnAdapterItemLongListener onAdapterItemLongListener) {
        this.onAdapterItemLongListener = onAdapterItemLongListener;
    }

    public void setOnAdapterItemListener(OnAdapterItemListener onAdapterItemListener) {
        this.onAdapterItemListener = onAdapterItemListener;
    }

    public RBaseAdapter(Context context, int layoutId) {
        this.context = context;
        this.layoutId = layoutId;
    }

    public ArrayList<T> getData() {
        if (files == null) files = new ArrayList<>();
        return files;
    }

    public T getData(int index) {
        if (files != null)
            return files.get(index);
        return null;
    }

    public void changeData(List<T> list) {
        ArrayList<T> data = getData();
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }

    public void addData(List<T> list) {
        ArrayList<T> data = getData();
        data.addAll(list);
        notifyDataSetChanged();
    }

    public void addItemData(T t, int index) {
        ArrayList<T> data = getData();
        data.add(index, t);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return getData().size();
    }

    @Override
    public Object getItem(int i) {
        return getData().get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        V viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(layoutId, null);
            viewHolder = initView(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (V) convertView.getTag();
        }
        changeView(i);
        return convertView;
    }

  public   interface OnAdapterItemListener {
         void onItemClick(int i ,View view);
    }

    public interface OnAdapterItemLongListener {
        void onLongClick(int i, View view);
    }

    public interface OnAdapterItemChildListener {
        void onChildClick(int i, View view);
    }

    public abstract void changeView(int index);

    public abstract V initView(View view);

    public static class RBaseViewHolder {
        protected void initView() {

        }
    }
}
