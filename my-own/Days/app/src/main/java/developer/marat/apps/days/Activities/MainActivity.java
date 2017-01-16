package developer.marat.apps.days.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import developer.marat.apps.days.MainTabs.AllTabView;
import developer.marat.apps.days.MainTabs.SinceTabView;
import developer.marat.apps.days.MainTabs.UntilTabView;
import developer.marat.apps.days.R;

public class MainActivity extends AppCompatActivity {

    FragmentTabHost mFragmentTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mFragmentTabHost.setup(MainActivity.this, getSupportFragmentManager(), android.R.id.tabcontent);

        /*
        mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("all").setIndicator("All"), AllTabView.class, null);
        mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("since").setIndicator("Since"), SinceTabView.class, null);
        mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("until").setIndicator("Until"), UntilTabView.class, null);
        */

        /*
        mFragmentTabHost.setOnTabChangedListener(new FragmentTabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                for (int i=0; i<mFragmentTabHost.getTabWidget().getTabCount(); i++) {
                    mFragmentTabHost.getTabWidget().getChildTabViewAt(i).setBackgroundColor(Color.parseColor("#7F7F7F"));
                }
                mFragmentTabHost.getTabWidget().getChildTabViewAt(mFragmentTabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#424242"));
            }
        });
        */



        String tag1 = "all";
        String tag2 = "since";
        String tag3 = "until";

        final View tabView1 = createTabView(this, "All");
        final View tabView2 = createTabView(this, "Since");
        final View tabView3 = createTabView(this, "Until");

        mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec(tag1).setIndicator(tabView1).setContent(new FragmentTabHost.TabContentFactory() {
            public View createTabContent(String tag1) {
                return tabView1;
            }
        }), AllTabView.class, null);
        mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec(tag2).setIndicator(tabView2).setContent(new FragmentTabHost.TabContentFactory() {
            public View createTabContent(String tag2) {
                return tabView2;
            }
        }), SinceTabView.class, null);
        mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec(tag3).setIndicator(tabView3).setContent(new FragmentTabHost.TabContentFactory() {
            public View createTabContent(String tag1) {
                return tabView3;
            }
        }), UntilTabView.class, null);
    }

    private static View createTabView(final Context context, final String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
        TextView tv = (TextView) view.findViewById(R.id.tabsText);
        tv.setText(text);
        return view;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.one_time) {
            Intent i = new Intent(this, QuickCalculationActivity.class);
            startActivity(i);
            return true;
        } else if (id == R.id.add_new) {
            Intent intent = new Intent(this, AddNewEventActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}