package android.freelessons.org.sampleandroidappusingfirebase;

import android.content.Intent;
import android.freelessons.org.sampleandroidappusingfirebase.domain.Event;
import android.freelessons.org.sampleandroidappusingfirebase.ui.EventActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import roboguice.activity.RoboActionBarActivity;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActionBarActivity {

    static String TAG="SAPWF";
    List<Event> events=new ArrayList<>();
    @InjectView(R.id.eventsList) ListView listView;
    @InjectView(R.id.addEvent)
    FloatingActionButton addEvent;
    EventListAdapter eventListAdapter=new EventListAdapter();
    DatabaseReference databaseReference;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM, yyyy");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference=FirebaseDatabase.getInstance().getReference();

    }

    private void addEvent(){
        Intent intent=new Intent(getApplicationContext(),EventActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onResume() {
        events=new ArrayList<>();
        super.onResume();

        DatabaseReference eventsReference=databaseReference.child("events");
        eventsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Event event=new Event();
                event.setName(dataSnapshot.child(Event.NAME_PROPERTY).getValue().toString());
                event.setDescription(dataSnapshot.child(Event.DESCRIPTION_PROPERTY).getValue().toString());
                event.setLocation(dataSnapshot.child(Event.LOCATION_PROPERTY).getValue().toString());
                event.setEventId(dataSnapshot.child(Event.EVENT_ID_PROPERTY).getValue().toString());
                if(dataSnapshot.child(Event.START_DATE_PROPERTY).getValue()!=null){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(Long.parseLong(dataSnapshot.child(Event.START_DATE_PROPERTY).getValue().toString()));
                    event.setStartDate(calendar.getTime());
                }

                events.add(event);
                eventListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        listView.setAdapter(eventListAdapter);
        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEvent();
            }
        });
    }

    class EventListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return events.size();
        }

        @Override
        public Object getItem(int i) {
            return events.get(i);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View eventRow = layoutInflater.inflate(R.layout.event_row,null);
            TextView eventName = (TextView)eventRow.findViewById(R.id.eventName);
            Event event = (Event) getItem(i);
            eventName.setText(event.getName());
            TextView eventDate = (TextView)eventRow.findViewById(R.id.eventDate);
            eventDate.setText(simpleDateFormat.format(event.getStartDate()));
            TextView eventLocation = (TextView)eventRow.findViewById(R.id.eventLocation);
            eventLocation.setText(event.getLocation());
            TextView eventDescription = (TextView)eventRow.findViewById(R.id.eventDescription);
            eventDescription.setText(event.getDescription());
            return eventRow;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
    }
}
