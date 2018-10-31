package view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.vsolv.bigflow.R;

import java.util.ArrayList;

import models.TimeLineAdapter;
import models.Variables;

public class StatusActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Variables.Timeline> timelines;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_timeLine1);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
        setDataListItems();
        TimeLineAdapter mTimeLineAdapter = new TimeLineAdapter(getApplication(), timelines);
        recyclerView.setAdapter(mTimeLineAdapter);

    }
    private void setDataListItems() {
        timelines = new ArrayList<>();
        Variables.Timeline timeline = new Variables.Timeline();
        timeline.title = "Item successfully delivereddsfgsdfsdfsd edgdsgf sd";
        timeline.subtitle = "sub delivered";
        timeline.status = Variables.Timeline.Status.ACTIVE;
        timelines.add(timeline);
        timeline = new Variables.Timeline();
        timeline.title = "Item successfully delivered";
        timeline.subtitle = "sub delivered";
        timeline.status = Variables.Timeline.Status.INACTIVE;
        timelines.add(timeline);
        timeline = new Variables.Timeline();
        timeline.title = "Item successfully delivered";
        timeline.subtitle = "sub delivered";
        timeline.status = Variables.Timeline.Status.REJECTED;
        timelines.add(timeline);
        timeline = new Variables.Timeline();
        timeline.title = "Item successfully delivered";
        timeline.subtitle = "sub delivered";
        timeline.status = Variables.Timeline.Status.COMPLETED;
        timelines.add(timeline);
        timeline = new Variables.Timeline();
        timeline.title = "Item successfully delivered";
        timeline.subtitle = "sub delivered";
        timeline.status = Variables.Timeline.Status.ACTIVE;
        timelines.add(timeline);
    }
}
