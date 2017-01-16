package developer.marat.apps.days.MainTabs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import developer.marat.apps.days.DB.DaysDatabase;
import developer.marat.apps.days.DB.SinceAdapter;
import developer.marat.apps.days.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SinceTabView extends Fragment {

    Context ourContext;

    RecyclerView mList;
    SinceAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    TextView mEmptyText;

    long movedItem, draggedItem;

    ArrayList<ArrayList<String>> mDays;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v2 = inflater.inflate(R.layout.list_view_content, container, false);

        mList = (RecyclerView) v2.findViewById(R.id.recycler_view);
        mEmptyText = (TextView) v2.findViewById(R.id.empty_text);
        ourContext = getActivity();

        layoutManager = new LinearLayoutManager(ourContext);
        mList.setLayoutManager(layoutManager);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        mList.setItemAnimator(itemAnimator);

        ItemTouchHelper item = new ItemTouchHelper(new ItemTouchHelper.Callback() {

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.RIGHT;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Collections.swap(mDays, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
                movedItem = Long.parseLong(mDays.get(fromPos).get(0));
                draggedItem = Long.parseLong(mDays.get(toPos).get(0));

                DaysDatabase db = new DaysDatabase(ourContext);
                db.open();
                db.updateSinceOrder(draggedItem, toPos);
                db.updateSinceOrder(movedItem, fromPos);
                db.close();
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {}
        });

        item.attachToRecyclerView(mList);

        return v2;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDays();

        if (mDays.size() == 0) {
            mList.setVisibility(View.GONE);
            mEmptyText.setVisibility(View.VISIBLE);
        } else {
            mList.setVisibility(View.VISIBLE);
            mEmptyText.setVisibility(View.GONE);
        }

        if(adapter == null) {
            adapter = new SinceAdapter(ourContext, mDays);
        }
        else {
            adapter.updateData(mDays);
        }

        mList.setAdapter(adapter);
    }

    public void getDays() {
        DaysDatabase info = new DaysDatabase(ourContext);
        info.open();
        mDays = info.getSince();
        info.close();

        Collections.sort(mDays, new Comparator<ArrayList<String>>() {
            @Override
            public int compare(ArrayList<String> row1, ArrayList<String> row2) {
                return row1.get(7).compareTo(row2.get(7));
            }
        });
    }
}