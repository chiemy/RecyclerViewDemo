package com.chiemy.recyclerviewdemo.adapter;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created: chiemy
 * Date: 16/6/28
 * Description:
 */
public abstract class AnimatorAdapter<T, VH extends ViewHolder> extends RecyclerView.Adapter <VH> {
    private List<T> datas = new ArrayList<>();
    private long interval;
    private long firstItemAnimOffset;
    private Handler handler = new Handler(Looper.myLooper());

    public void addAll(final List<T> list){
        if (list == null) {
            return;
        }
        datas.clear();
        int size = list.size();
        for (int i = 0 ; i < size ; i++) {
            final int index = i;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    datas.add(list.get(index));
                    notifyItemInserted(index);
                }
            }, i * interval + firstItemAnimOffset);
        }
    }

    public void removeAll(){
        final int size = datas.size();
        for (int i = 0 ; i < size ; i++) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    removeLastItem();
                }
            }, i * interval + firstItemAnimOffset);
        }
    }

    public void setFirstItemAnimOffset(long offset) {
        this.firstItemAnimOffset = offset;
    }

    public void setItemAnimInterval(long interval) {
        this.interval = interval;
    }

    public T getItem(int position) {
        return datas.get(position);
    }

    public void addItem(T t){
        datas.add(t);
        notifyItemInserted(datas.size() - 1);
    }

    public void removeItem(int position){
        if(datas.isEmpty()){
            return;
        }
        datas.remove(position);
        notifyItemRemoved(position);
    }

    public void removeLastItem() {
        removeItem(getItemCount() - 1);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }
}
