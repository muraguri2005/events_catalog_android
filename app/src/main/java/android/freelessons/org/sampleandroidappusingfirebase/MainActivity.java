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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Locale;


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
        firebaseAuthStateListener= firebaseAuth -> updateViews();
        firebaseAuth.addAuthStateListener(firebaseAuthStateListener);

        mEventRecyclerView = findViewById(R.id.eventRecyclerView);
        addEvent = findViewById(R.id.addEvent);
        signIn = findViewById(R.id.signIn);
        signOut = findViewById(R.id.signOut);
        signUp = findViewById(R.id.signUp);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mEventRecyclerView.setLayoutManager(mLinearLayoutManager);
        FirebaseRecyclerOptions<Event> options = new FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(databaseReference.child(EVENTS_CHILD), EventUtil::parseSnaphot).build();

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
                viewHolder.itemView.setOnClickListener(view -> editEvent(event));
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
        addEvent.setOnClickListener(view -> addEvent(new Event()));
        signIn.setOnClickListener(view -> signIn());
        signUp.setOnClickListener(view -> signUp());
        signOut.setOnClickListener(view -> signOut());
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
