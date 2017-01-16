package developer.marat.apps.days.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import developer.marat.apps.days.R;
import developer.marat.apps.days.Tabs.DateTabView;
import developer.marat.apps.days.Tabs.DaysTabView;

public class QuickCalculationActivity extends AppCompatActivity  {

    FragmentTabHost mFragmentTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_calculation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mFragmentTabHost.setup(QuickCalculationActivity.this, getSupportFragmentManager(), android.R.id.tabcontent);

        /*
        mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("days").setIndicator("DAYS"), DaysTabView.class, null);
        mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("date").setIndicator("DATE"), DateTabView.class, null);
        */

        String tag1 = "days";
        String tag2 = "date";

        final View tabView1 = createTabView(this, "Days");
        final View tabView2 = createTabView(this, "Date");

        mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec(tag1).setIndicator(tabView1).setContent(new FragmentTabHost.TabContentFactory() {
            public View createTabContent(String tag1) {
                return tabView1;
            }
        }), DaysTabView.class, null);
        mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec(tag2).setIndicator(tabView2).setContent(new FragmentTabHost.TabContentFactory() {
            public View createTabContent(String tag2) {
                return tabView2;
            }
        }), DateTabView.class, null);
    }

    private static View createTabView(final Context context, final String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
        TextView tv = (TextView) view.findViewById(R.id.tabsText);
        tv.setText(text);
        return view;
    }
}