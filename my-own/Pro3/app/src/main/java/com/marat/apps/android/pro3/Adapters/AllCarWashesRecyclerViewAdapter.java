package com.marat.apps.android.pro3.Adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.marat.apps.android.pro3.Activities.CarWashDetailsActivity;
import com.marat.apps.android.pro3.Databases.CarWashesDatabase;
import com.marat.apps.android.pro3.R;

public class AllCarWashesRecyclerViewAdapter extends RecyclerView.Adapter<AllCarWashesRecyclerViewAdapter.ViewHolder> {

    private Cursor cursor;
    private Context context;
    private CarWashesDatabase db;

    public AllCarWashesRecyclerViewAdapter(Cursor data, Context c, CarWashesDatabase database) {
        cursor = data;
        context = c;
        db = database;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView carWashPhoto;
        private TextView carWashName;
        private TextView carWashAddress;
        private TextView carWashPrice;
        private TextView carWashDistance;
        private ItemClickListener listener;

        ViewHolder(View itemView, ItemClickListener listener) {
            super(itemView);
            carWashName = (TextView) itemView.findViewById(R.id.cwcNameTextView);
            carWashAddress = (TextView) itemView.findViewById(R.id.cwcAddressTextView);
            carWashPrice = (TextView) itemView.findViewById(R.id.cwcPriceTextView);
            carWashDistance = (TextView) itemView.findViewById(R.id.cwcDistanceToTextView);
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
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_car_wash_card, parent, false);
        return new ViewHolder(v, new ViewHolder.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                db.open();
                cursor.moveToPosition(position);
                Intent intent = new Intent(context, CarWashDetailsActivity.class);
                intent.putExtra("car_wash_id", cursor.getInt(cursor.getColumnIndex(CarWashesDatabase.KEY_CAR_WASH_ID)));
                intent.putExtra("car_wash_name", cursor.getString(cursor.getColumnIndex(CarWashesDatabase.KEY_CAR_WASH_NAME)));
                db.close();
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        db.open();
        cursor.moveToPosition(position);
        holder.carWashName.setText(cursor.getString(cursor.getColumnIndex(CarWashesDatabase.KEY_CAR_WASH_NAME)));
        holder.carWashAddress.setText(cursor.getString(cursor.getColumnIndex(CarWashesDatabase.KEY_CAR_WASH_ADDRESS)));
        holder.carWashPrice.setText("Кузов + Салон от " + cursor.getInt(cursor.getColumnIndex(CarWashesDatabase.KEY_CAR_WASH_EXAMPLE_PRICE)) + " тг.");
        db.close();
    }

    @Override
    public int getItemCount() {
        db.open();
        int count = cursor.getCount();
        db.close();
        return count;
    }

    public void updateCursor(Cursor newCursor) {
        cursor = newCursor;
        notifyDataSetChanged();
    }
}
