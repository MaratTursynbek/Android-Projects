package com.marat.apps.android.pro3.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.marat.apps.android.pro3.Interfaces.ServiceCarTypeChosenListener;
import com.marat.apps.android.pro3.Models.CarType;
import com.marat.apps.android.pro3.R;

import java.util.ArrayList;

public class CarTypesRecyclerViewAdapter extends RecyclerView.Adapter<CarTypesRecyclerViewAdapter.ViewHolder> {

    private Context context;

    private ServiceCarTypeChosenListener delegate;

    private ArrayList<CarType> carTypes;

    public int selectedCar = 0;

    public CarTypesRecyclerViewAdapter(Context c, ServiceCarTypeChosenListener listener, ArrayList<CarType> data, int userCarTypeId) {
        context = c;
        delegate = listener;
        carTypes = data;
        selectedCar = userCarTypeId;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView icon;
        private TextView name;
        private ItemClickListener listener;

        ViewHolder(View itemView, ItemClickListener listener) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.carTypeImageView);
            name = (TextView) itemView.findViewById(R.id.createAccountCarTypeTextView);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(getAdapterPosition());
        }

        interface ItemClickListener {
            void onItemClick(int position);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_car_types_card, parent, false);
        return new ViewHolder(v, new ViewHolder.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                selectedCar = carTypes.get(position).getCarTypeID();
                delegate.onCarTypeChosen();
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.d("logtag", "onBindViewHolder" + position);
        holder.icon.setImageResource(carTypes.get(position).getCarTypeIconId());
        holder.name.setText(carTypes.get(position).getCarTypeName());
        if (selectedCar == carTypes.get(position).getCarTypeID()) {
            holder.name.setBackgroundResource(R.drawable.bg_chosen_car_type_text);
            holder.name.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        } else {
            holder.name.setBackgroundResource(0);
            holder.name.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return carTypes.size();
    }
}
