package com.example.marat.recyclerviewtutorial;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private ArrayList<ArrayList<String>> data;
    private Context context;

    public RecyclerViewAdapter(ArrayList<ArrayList<String>> input, Context c) {
        data = input;
        context = c;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public MyOnItemClickListener listener;
        public TextView mTextView;
        public Context cont;
        int pos;

        public MyViewHolder(View v, Context c, MyOnItemClickListener l) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.info_text);
            listener = l;
            cont = c;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.startActivity(cont, pos);
        }

        public interface MyOnItemClickListener {
            void startActivity(Context c, int p);
        }
    }

    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v, context, new MyViewHolder.MyOnItemClickListener() {
            @Override
            public void startActivity(Context c, int position) {
                Intent intent = new Intent(c, ItemInformationActivity.class);
                intent.putExtra("Text", data.get(position).get(2));
                intent.putExtra("Position", data.get(position).get(0));
                c.startActivity(intent);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.mTextView.setText(data.get(position).get(2));
        holder.pos = position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateData(ArrayList<ArrayList<String>> newData) {
        data = newData;
        this.notifyDataSetChanged();
    }
}
