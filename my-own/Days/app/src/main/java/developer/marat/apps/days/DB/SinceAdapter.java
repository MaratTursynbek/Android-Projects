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

public class SinceAdapter extends RecyclerView.Adapter<SinceAdapter.SinceViewHolder> {

    private ArrayList<ArrayList<String>> mDays;
    private Context context;

    public SinceAdapter(Context c, ArrayList<ArrayList<String>> input) {
        context = c;
        mDays = input;
    }

    public static class SinceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Context context;
        MyClickListener listener;

        TextView numberOfDays;
        TextView sinceOrUntil;
        TextView eventTitle;
        TextView daysText;
        TextView eventDate;

        public SinceViewHolder(View itemView, Context c, MyClickListener listen) {
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
    public SinceAdapter.SinceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.days_list_item, parent, false);
        SinceViewHolder holder = new SinceViewHolder(v, context, new SinceViewHolder.MyClickListener() {
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
    public void onBindViewHolder(SinceAdapter.SinceViewHolder holder, int position) {
        long result = 0;
        ArrayList<String> event = mDays.get(position);
        String type = event.get(1);
        String fromDate = event.get(2);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
        Calendar current, mDate1;

        current = Calendar.getInstance(TimeZone.getDefault());
        current.set(Calendar.HOUR_OF_DAY, 0);
        current.set(Calendar.MINUTE, 0);
        current.set(Calendar.SECOND, 0);
        current.set(Calendar.MILLISECOND, 0);

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
