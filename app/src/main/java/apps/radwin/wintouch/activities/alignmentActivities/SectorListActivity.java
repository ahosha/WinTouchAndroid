package apps.radwin.wintouch.activities.alignmentActivities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.adapters.aligmentAdapters.SectorListAdapter;
import apps.radwin.wintouch.models.HBSModel;

public class SectorListActivity extends AppCompatActivity {

    ArrayList<HBSModel> arraySectors;
    SectorListAdapter adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sector_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        arraySectors = new ArrayList<HBSModel>();
//        adapter = new SectorListAdapter(this, arraySectors);
        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);

        for (Integer i=0; i<10; i++) {
//            adapter.add(createHBS(i));
        }

        adapter.notifyDataSetChanged();
    }

    public HBSModel createHBS(Integer i)
    {

        HBSModel hbs = new HBSModel("Habarzel st. " + i.toString(), "RW12", "-56bDm", "2km", 1);

        if (i==0)
            hbs.type=0;
        else if (i<4)
            hbs.type=1;
        else if (i==4)
            hbs.type = 3;
        else
            hbs.type=2;

        return hbs;
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
}
