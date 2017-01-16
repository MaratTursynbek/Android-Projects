package com.example.marat.recyclerviewtutorial;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "myTag";

    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    long movedItem, draggedItem;

    ArrayList<ArrayList<String>> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ViewDbActivity.class);
                startActivity(i);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        ItemTouchHelper item = new ItemTouchHelper(new ItemTouchHelper.Callback() {

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.RIGHT;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Collections.swap(data, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
                movedItem = Long.parseLong(data.get(fromPos).get(0));
                draggedItem = Long.parseLong(data.get(toPos).get(0));

                Database db = new Database(MainActivity.this);
                db.open();
                db.updateOrder(draggedItem, toPos);
                db.updateOrder(movedItem, fromPos);
                db.close();
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                data.remove(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        });

        item.attachToRecyclerView(recyclerView);
        recyclerView.setItemAnimator(itemAnimator);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialize();

        if(adapter == null) {
            Log.v(TAG, "Adapter = NULL. Initialized");
            setAdapter();
        }
        else {
            Log.v(TAG, "notify is called");
            adapter.updateData(data);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void initialize() {
        Database db = new Database(this);
        db.open();
        data = db.getData();
        db.close();

        Collections.sort(data, new Comparator<ArrayList<String>>() {
            @Override
            public int compare(ArrayList<String> row1, ArrayList<String> row2) {
                return row1.get(1).compareTo(row2.get(1));
            }
        });
    }

    public void setAdapter() {
        adapter = new RecyclerViewAdapter(data, this);
        recyclerView.setAdapter(adapter);
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
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, AddNewItemActivity.class);
            if (data.size() == 0) {
                i.putExtra("max", "0");
            }
            else {
                i.putExtra("max", data.get(data.size()-1).get(1));
            }
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
