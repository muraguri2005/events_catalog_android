package android.freelessons.org.sampleandroidappusingfirebase;

import android.app.DialogFragment;
import android.content.Intent;
import android.freelessons.org.sampleandroidappusingfirebase.domain.Event;
import android.freelessons.org.sampleandroidappusingfirebase.ui.EventActivity;
import android.freelessons.org.sampleandroidappusingfirebase.ui.SignInUI;
import android.freelessons.org.sampleandroidappusingfirebase.ui.SignUpUI;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActionBarActivity {

    private static final String EVENTS_CHILD = "events";
    @InjectView(R.id.eventRecyclerView) RecyclerView mEventRecyclerView;
    @InjectView(R.id.addEvent)
    FloatingActionButton addEvent;
    @InjectView(R.id.signIn)
    Button signIn;
    @InjectView(R.id.signUp) Button signUp;
    @InjectView(R.id.signOut) Button signOut;
    DatabaseReference databaseReference;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    FirebaseRecyclerAdapter<Event,EventViewHolder> firebaseRecyclerAdapter;
    LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference=FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                updateViews();
            }
        };
        firebaseAuth.addAuthStateListener(firebaseAuthStateListener);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mEventRecyclerView.setLayoutManager(mLinearLayoutManager);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(Event.class,R.layout.event_item,EventViewHolder.class,databaseReference.child(EVENTS_CHILD)) {
            @Override
            protected void populateViewHolder(EventViewHolder viewHolder, Event event, int position) {
                viewHolder.eventDate.setText(simpleDateFormat.format(event.getStartDate()));
                viewHolder.eventDescription.setText(event.getDescription());
                viewHolder.eventLocation.setText(event.getLocation());
                viewHolder.eventName.setText(event.getName());
            }

            @Override
            protected Event parseSnapshot(DataSnapshot snapshot) {
                Event event = new Event();
                if(snapshot.child(Event.NAME_PROPERTY).getValue() != null)
                    event.setName(snapshot.child(Event.NAME_PROPERTY).getValue().toString());
                if(snapshot.child(Event.DESCRIPTION_PROPERTY).getValue() != null)
                    event.setDescription(snapshot.child(Event.DESCRIPTION_PROPERTY).getValue().toString());
                if(snapshot.child(Event.LOCATION_PROPERTY).getValue() != null)
                    event.setLocation(snapshot.child(Event.LOCATION_PROPERTY).getValue().toString());
                if(snapshot.child(Event.START_DATE_PROPERTY).getValue() != null)
                    event.setStartDate(new Date(Long.parseLong(snapshot.child(Event.START_DATE_PROPERTY).getValue().toString())));
                return event;
            }
        };

        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int eventsCount = firebaseRecyclerAdapter.getItemCount();
                int lastVisiblePostion = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if ( lastVisiblePostion == -1 || (positionStart >= (eventsCount -1) && lastVisiblePostion == (positionStart -1 ))) {
                    mEventRecyclerView.scrollToPosition(positionStart);
                }
            }
        });
        mEventRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void addEvent(){
        if(firebaseAuth.getCurrentUser()!=null){
            Intent intent=new Intent(getApplicationContext(),EventActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(MainActivity.this, "Please sign in to create an event", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEvent();
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
        updateViews();
    }
    private void updateViews(){
        if(firebaseAuth.getCurrentUser()!=null){
            signIn.setVisibility(View.GONE);
            signOut.setVisibility(View.VISIBLE);
            signUp.setVisibility(View.GONE);
        }else{
            signIn.setVisibility(View.VISIBLE);
            signOut.setVisibility(View.GONE);
            signUp.setVisibility(View.VISIBLE);
        }
    }
    private void signIn(){
        DialogFragment dialog=SignInUI.newInstance();
        dialog.show(getFragmentManager(),"SignInUI");
    }
    private void signUp(){
        DialogFragment dialog=SignUpUI.newInstance();
        dialog.show(getFragmentManager(),"SignUpUI");
    }
    private void signOut(){
        firebaseAuth.signOut();
    }
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName;
        TextView eventDate;
        TextView eventLocation;
        TextView eventDescription;
        public EventViewHolder(View v){
            super(v);
            eventName = (TextView)itemView.findViewById(R.id.eventName);
            eventDate = (TextView)itemView.findViewById(R.id.eventDate);
            eventLocation = (TextView)itemView.findViewById(R.id.eventLocation);
            eventDescription = (TextView)itemView.findViewById(R.id.eventDescription);
        }
    }
}
