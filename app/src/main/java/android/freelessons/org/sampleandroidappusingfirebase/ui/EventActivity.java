package android.freelessons.org.sampleandroidappusingfirebase.ui;

import android.app.DatePickerDialog;
import android.freelessons.org.sampleandroidappusingfirebase.R;
import android.freelessons.org.sampleandroidappusingfirebase.domain.Event;
import android.freelessons.org.sampleandroidappusingfirebase.session.SessionManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;



public class EventActivity extends AppCompatActivity {
    private static String TAG="SAPWF";
    DatabaseReference databaseReference;
    EditText nameEditText;
    EditText descriptionEditText;
    Button saveEvent;
    EditText locationEditText;
    EditText startDateEditText;
    PlaceAutocompleteFragment locationView;


    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());
    Calendar myCalendar = Calendar.getInstance();
    Event event = new Event();
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event);
        startDateEditText = findViewById(R.id.startDateEditText);
        locationEditText = findViewById(R.id.locationEditText);
        saveEvent = findViewById(R.id.saveEvent);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        nameEditText = findViewById(R.id.nameEditText);
        sessionManager = new SessionManager(this);
        event = sessionManager.getEvent();
        myCalendar.setTime(event.getStartDate());
        databaseReference= FirebaseDatabase.getInstance().getReference();
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        locationView = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.locationView);
        locationView.setText("Search Location");
        locationView.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.d(TAG,"Place:"+place.getName());
                Log.d(TAG,"Latitude:"+place.getLatLng().latitude);
                Log.d(TAG,"Longitude:"+place.getLatLng().longitude);
                locationEditText.setText(place.getName());
            }

            @Override
            public void onError(Status status) {
                Log.e(TAG,"Error: "+status);
            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
        updateViews();
    }

    //update views
    private void updateViews() {
        nameEditText.setText(event.getName());
        descriptionEditText.setText(event.getDescription());
        locationEditText.setText(event.getLocation());
        startDateEditText.setText(dateFormat.format(event.getStartDate()));
        saveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid()) {
                    saveEvent();
                }else{
                    Toast.makeText(getApplicationContext(),"Please make sure all fields are filled",Toast.LENGTH_LONG).show();
                }
            }
        });
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        startDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    new DatePickerDialog(EventActivity.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });



    }
    private boolean isValid(){
        return !nameEditText.getText().toString().isEmpty() && !descriptionEditText.getText().toString().isEmpty();
    }
    private void saveEvent(){
        DatabaseReference eventReference;
        if(event.getEventId()==null) {
            eventReference = databaseReference.child("events").push();
            event.setEventId(eventReference.getKey());
        }else{
            eventReference=databaseReference.child("events").child(event.getEventId());
        }
        event.setName(nameEditText.getText().toString());
        event.setDescription(descriptionEditText.getText().toString());
        event.setLocation(locationEditText.getText().toString());
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser!=null) {
                event.setUserId(firebaseUser.getUid());
            }
        }
        try {
            event.setStartDate(dateFormat.parse(startDateEditText.getText().toString()));
        } catch (ParseException e) {
            //TODO: do something serious
            //e.printStackTrace();
        }

        eventReference.updateChildren(event.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG,"Success");
                    Toast.makeText(EventActivity.this, "Event saved successfully", Toast.LENGTH_SHORT).show();
                }else{
                    Log.e(TAG,"erro");
                    String errorMessage = null;
                    Exception exception = task.getException();
                    if (exception!=null) {
                        errorMessage = exception.getMessage();
                    }
                    Toast.makeText(EventActivity.this, "Error saving Event "+errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void updateLabel() {

        startDateEditText.setText(dateFormat.format(myCalendar.getTime()));
    }
}
