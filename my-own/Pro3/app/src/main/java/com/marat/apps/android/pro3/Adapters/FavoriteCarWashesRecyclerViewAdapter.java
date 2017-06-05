package com.marat.apps.android.pro3.Adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.marat.apps.android.pro3.Activities.CarWashDetailsActivity;
import com.marat.apps.android.pro3.Databases.CarWashesDatabase;
import com.marat.apps.android.pro3.R;

public class FavoriteCarWashesRecyclerViewAdapter extends RecyclerView.Adapter<FavoriteCarWashesRecyclerViewAdapter.ViewHolder> {

    private Cursor cursor;
    private Context context;
    private CarWashesDatabase db;
    private int currentCityId = -1;

    public FavoriteCarWashesRecyclerViewAdapter(Cursor data, Context c, CarWashesDatabase database) {
        cursor = data;
        context = c;
        db = database;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView carWashPhoto;
        private TextView carWashName;
        private TextView carWashPrice;
        private TextView carWashDistance;
        private ItemClickListener listener;
        private RelativeLayout parentLayout;
        private CardView cardView;
        private View headerLayout;
        private TextView header;

        ViewHolder(View itemView, ItemClickListener listener) {
            super(itemView);
            carWashName = (TextView) itemView.findViewById(R.id.cwcNameTextView);
            carWashPrice = (TextView) itemView.findViewById(R.id.cwcPriceTextView);
            carWashDistance = (TextView) itemView.findViewById(R.id.cwcDistanceToTextView);
            parentLayout = (RelativeLayout) itemView.findViewById(R.id.listItemCarWashParentLayout);
            cardView = (CardView) itemView.findViewById(R.id.listItemCarWashCardLayout);
            headerLayout = itemView.findViewById(R.id.listItemCarWashHeaderLayout);
            header = (TextView) itemView.findViewById(R.id.listItemCarWashHeaderTextView);
            this.listener = listener;
            cardView.setOnClickListener(this);
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
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_car_wash_card_with_header, parent, false);
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
        holder.carWashPrice.setText("Кузов + Салон от " + cursor.getInt(cursor.getColumnIndex(CarWashesDatabase.KEY_CAR_WASH_EXAMPLE_PRICE)) + " тг.");

        if ((position == 0) || (currentCityId != cursor.getInt(cursor.getColumnIndex(CarWashesDatabase.KEY_CAR_WASH_CITY_ID)))) {
            holder.header.setText(cursor.getString(cursor.getColumnIndex(CarWashesDatabase.KEY_CAR_WASH_CITY_NAME)));
            currentCityId = cursor.getInt(cursor.getColumnIndex(CarWashesDatabase.KEY_CAR_WASH_CITY_ID));
        } else {
            holder.parentLayout.removeView(holder.headerLayout);
        }

        db.close();
    }

    @Override
    public int getItemCount() {
        db.open();
        int count = cursor.getCount();
        db.close();
        return count;
    }
}
