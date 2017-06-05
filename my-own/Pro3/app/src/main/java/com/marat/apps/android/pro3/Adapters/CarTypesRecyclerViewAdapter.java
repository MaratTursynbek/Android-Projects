package com.marat.apps.android.pro3.Adapters;

import android.app.Activity;
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
import com.marat.apps.android.pro3.Models.CarTypeWithServices;
import com.marat.apps.android.pro3.R;

import java.util.ArrayList;

public class CarTypesRecyclerViewAdapter extends RecyclerView.Adapter<CarTypesRecyclerViewAdapter.ViewHolder> {

    private Context context;

    private ServiceCarTypeChosenListener delegate;

    private ArrayList<CarTypeWithServices> arrayCarTypesWithServices;

    private int selectedCarID = -1;

    public CarTypesRecyclerViewAdapter(Activity a, ArrayList<CarTypeWithServices> data) {
        context = a;
        delegate = (ServiceCarTypeChosenListener) a;
        arrayCarTypesWithServices = data;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView icon;
        private TextView name;
        private ItemClickListener listener;

        ViewHolder(View itemView, ItemClickListener l) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.carTypeImageView);
            name = (TextView) itemView.findViewById(R.id.createAccountCarTypeTextView);
            listener = l;
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
                selectedCarID = arrayCarTypesWithServices.get(position).getCarTypeID();
                delegate.onCarTypeChosen(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.d("logtag", "onBindViewHolder" + position);
        holder.icon.setImageResource(arrayCarTypesWithServices.get(position).getCarTypeIconID());
        holder.name.setText(arrayCarTypesWithServices.get(position).getCarTypeName());
        if (selectedCarID == arrayCarTypesWithServices.get(position).getCarTypeID()) {
            holder.name.setBackgroundResource(R.drawable.bg_chosen_car_type_text);
            holder.name.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        } else {
            holder.name.setBackgroundResource(0);
            holder.name.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return arrayCarTypesWithServices.size();
    }
}
