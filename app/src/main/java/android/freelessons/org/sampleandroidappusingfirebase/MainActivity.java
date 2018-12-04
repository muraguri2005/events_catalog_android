package android.freelessons.org.sampleandroidappusingfirebase;

import android.app.DialogFragment;
import android.content.Intent;
import android.freelessons.org.sampleandroidappusingfirebase.domain.Event;
import android.freelessons.org.sampleandroidappusingfirebase.session.SessionManager;
import android.freelessons.org.sampleandroidappusingfirebase.ui.EventActivity;
import android.freelessons.org.sampleandroidappusingfirebase.ui.SignInUI;
import android.freelessons.org.sampleandroidappusingfirebase.ui.SignUpUI;
import android.freelessons.org.sampleandroidappusingfirebase.util.EventUtil;
import android.freelessons.org.sampleandroidappusingfirebase.util.ImageRequester;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Locale;

//import roboguice.inject.ContentView;
//import roboguice.inject.InjectView;

public class MainActivity extends AppCompatActivity {

    private static final String EVENTS_CHILD = "events";
    RecyclerView mEventRecyclerView;
    FloatingActionButton addEvent;
    Button signIn;
    Button signUp;
    Button signOut;
    DatabaseReference databaseReference;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    FirebaseRecyclerAdapter<Event,EventViewHolder> firebaseRecyclerAdapter;
    LinearLayoutManager mLinearLayoutManager;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(this);
        databaseReference=FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                updateViews();
            }
        };
        firebaseAuth.addAuthStateListener(firebaseAuthStateListener);

        mEventRecyclerView = findViewById(R.id.eventRecyclerView);
        addEvent = findViewById(R.id.addEvent);
        signIn = findViewById(R.id.signIn);
        signOut = findViewById(R.id.signOut);
        signUp = findViewById(R.id.signUp);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mEventRecyclerView.setLayoutManager(mLinearLayoutManager);
        FirebaseRecyclerOptions<Event> options = new FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(databaseReference.child(EVENTS_CHILD), new SnapshotParser<Event>() {
                    @NonNull
                    @Override
                    public Event parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return EventUtil.parseSnaphot(snapshot);
                    }
                }).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(options) {
            @NonNull
            @Override
            public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.event_item, parent, false);
                return new EventViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull EventViewHolder viewHolder, int position, @NonNull final Event event) {
                viewHolder.eventTitle.setText(getString(R.string.event_title,event.getName(),simpleDateFormat.format(event.getStartDate()),event.getLocation()));
                viewHolder.eventDescription.setText(event.getDescription());
                ImageRequester imageRequester = ImageRequester.getInstance(getBaseContext());
                imageRequester.setImageFromUrl(viewHolder.poster,event.getPosterPath());
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editEvent(event);
                    }
                });
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

    private void addEvent(Event event){
        sessionManager.saveEvent(event);
        if(firebaseAuth.getCurrentUser()!=null){
            Intent intent=new Intent(getApplicationContext(),EventActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(MainActivity.this, "Please sign in to create an event", Toast.LENGTH_SHORT).show();
        }
    }
    private void editEvent(Event event){
        sessionManager.saveEvent(event);
        if(firebaseAuth.getCurrentUser()!=null){
            Intent intent=new Intent(getApplicationContext(),EventActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(MainActivity.this, "Please sign in to edit the event", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEvent(new Event());
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
    private static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitle;
        TextView eventDescription;
        NetworkImageView poster;
        public EventViewHolder(View v){
            super(v);
            eventTitle = itemView.findViewById(R.id.eventTitle);
            eventDescription = itemView.findViewById(R.id.eventDescription);
            poster =  itemView.findViewById(R.id.imageView);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseRecyclerAdapter.stopListening();
    }
}
