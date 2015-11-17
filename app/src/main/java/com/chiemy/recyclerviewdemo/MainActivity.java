package com.chiemy.recyclerviewdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static List<String> DATAS;
    private static final int DATASET_COUNT = 7;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initDataset();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new MyAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, R.drawable.list_divider, false, false));
        // recyclerView.addItemDecoration(new SpaceItemDecoration(this, R.dimen.activity_vertical_margin));

    }

    private void initDataset() {
        DATAS = new ArrayList<>(DATASET_COUNT);
        for (int i = 0; i < DATASET_COUNT; i++) {
            DATAS.add("This is element #" + i);
        }
    }

    private static class MyAdapter extends RecyclerView.Adapter<ItemViewHolder> {
        private LayoutInflater inflater;

        public MyAdapter(Context context) {
            inflater = LayoutInflater.from(context);

        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemViewHolder(inflater.inflate(R.layout.item_list_test, parent, false));
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            holder.tv.setText(DATAS.get(position));
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            return DATAS.size();
        }

    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tv;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv_text);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            addItem(false);
            return true;
        } else if (id == R.id.action_remove) {
            removeItem(false);
            return true;
        } else if (id == R.id.action_add_with_anim) {
            addItem(true);
            return true;
        } else if (id == R.id.action_remove_with_anim) {
            removeItem(true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addItem(boolean anim){
        int index = DATAS.size() - DATASET_COUNT;
        String text;
        if (index < 0) {
            index = Math.abs(index) - 1;
            text = "This is element #" + index;
        } else {
            text = "This is element #new " + index;
        }
        DATAS.add(0, text);
        if (anim){
            adapter.notifyItemInserted(0);
        }else{
            adapter.notifyDataSetChanged();
        }
    }

    private void removeItem(boolean anim){
        DATAS.remove(0);
        if (anim){
            adapter.notifyItemRemoved(0);
        } else {
            adapter.notifyDataSetChanged();
        }
    }
}