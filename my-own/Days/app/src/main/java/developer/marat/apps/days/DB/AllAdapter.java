package developer.marat.apps.days.DB;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import developer.marat.apps.days.Activities.EventInformationActivity;
import developer.marat.apps.days.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class AllAdapter extends RecyclerView.Adapter<AllAdapter.AllViewHolder> {

    private ArrayList<ArrayList<String>> mDays;
    private Context context;

    public AllAdapter(Context c, ArrayList<ArrayList<String>> input) {
        mDays = input;
        context = c;
    }

    public static class AllViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Context context;
        MyClickListener listener;

        TextView numberOfDays;
        TextView sinceOrUntil;
        TextView eventTitle;
        TextView daysText;
        TextView eventDate;

        public AllViewHolder(View itemView, Context c, MyClickListener listen) {
            super(itemView);
            context = c;
            listener = listen;

            numberOfDays = (TextView) itemView.findViewById(R.id.eventDays);
            sinceOrUntil = (TextView) itemView.findViewById(R.id.eventType);
            eventTitle = (TextView) itemView.findViewById(R.id.eventTitle);
            daysText = (TextView) itemView.findViewById(R.id.daysText);
            eventDate = (TextView) itemView.findViewById(R.id.eventDate);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.startEventDetailsActivity(context, this.getLayoutPosition());
        }

        public interface MyClickListener {
            void startEventDetailsActivity(Context c, int p);
        }
    }

    @Override
    public AllAdapter.AllViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.days_list_item, parent, false);
        AllViewHolder holder = new AllViewHolder(v, context, new AllViewHolder.MyClickListener() {
            @Override
            public void startEventDetailsActivity(Context c, int p) {
                Intent i = new Intent(c.getApplicationContext(), EventInformationActivity.class);
                long pos = Long.parseLong(mDays.get(p).get(0));
                i.putExtra("EventPosition", pos);
                c.startActivity(i);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(AllAdapter.AllViewHolder holder, int position) {
        long result = 0;
        ArrayList<String> event = mDays.get(position);
        String type = event.get(1);
        String fromDate = event.get(2);
        String untilDate = event.get(3);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
        Calendar current, mDate1, mDate2;

        current = Calendar.getInstance(TimeZone.getDefault());
        current.set(Calendar.HOUR_OF_DAY, 0);
        current.set(Calendar.MINUTE, 0);
        current.set(Calendar.SECOND, 0);
        current.set(Calendar.MILLISECOND, 0);

        if ("since".equals(type)) {

            Date from = null;
            try {
                from = format.parse(fromDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mDate1 = Calendar.getInstance(TimeZone.getDefault());
            mDate1.setTime(from);

            result = (current.getTime().getTime() - mDate1.getTime().getTime()) / (1000 * 60 * 60 * 24);
            holder.eventDate.setText(df.format(from));
        } else if ("until".equals(type)) {

            Date until = null;
            try {
                until = format.parse(untilDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mDate2 = Calendar.getInstance(TimeZone.getDefault());
            mDate2.setTime(until);

            result = (mDate2.getTime().getTime() - current.getTime().getTime()) / (1000 * 60 * 60 * 24);
            holder.eventDate.setText(df.format(until));

            if (result < 0) {
                result = (current.getTime().getTime() - mDate2.getTime().getTime()) / (1000 * 60 * 60 * 24);
                DaysDatabase entry = new DaysDatabase(context);
                type = "since";
                entry.open();
                ArrayList<String> orders = entry.getLastOrders();
                int since = Integer.parseInt(orders.get(1));
                since++;
                entry.updateEntry(Long.parseLong(event.get(0)), type, untilDate, fromDate, event.get(4), event.get(5), Integer.parseInt(event.get(6)), since, Boolean.parseBoolean(event.get(8)), Boolean.parseBoolean(event.get(9)));
                entry.close();
            }
        }

        holder.numberOfDays.setText("" + result);
        holder.sinceOrUntil.setText(type);
        holder.eventTitle.setText(event.get(4));

        if (result == 1){
            holder.daysText.setText("day");
        }
    }

    @Override
    public int getItemCount() {
        return mDays.size();
    }

    public void updateData(ArrayList<ArrayList<String>> newData) {
        mDays = newData;
        this.notifyDataSetChanged();
    }
}
