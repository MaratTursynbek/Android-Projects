package com.marat.apps.android.pro3.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.marat.apps.android.pro3.R;

import java.util.List;

public class CarTypesRecyclerViewAdapter extends RecyclerView.Adapter<CarTypesRecyclerViewAdapter.ViewHolder> {

    private int[] carIcons = new int[]{R.drawable.ic_cars_small, R.drawable.ic_cars_sedan, R.drawable.ic_cars_limo, R.drawable.ic_cars_suv, R.drawable.ic_cars_minivan};
    private String[] carNames = new String[]{"Малолитражка", "Седан", "Премиум", "Внедорожник", "Минивэн"};

    private Context context;

    public CarTypesRecyclerViewAdapter(Context c) {
        context = c;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView name;
        private View item;

        ViewHolder(View itemView) {
            super(itemView);
            item = itemView;
            icon = (ImageView) itemView.findViewById(R.id.carTypeImageView);
            name = (TextView) itemView.findViewById(R.id.carTypeTextView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_car_types_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int pos = position;
        holder.icon.setImageResource(carIcons[pos]);
        holder.name.setText(carNames[pos]);
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("tag", "item " + pos + " being clicked");
            }
        });
    }

    @Override
    public int getItemCount() {
        return carNames.length;
    }
}
