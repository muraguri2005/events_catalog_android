package android.freelessons.org.sampleandroidappusingfirebase.ui

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.freelessons.org.sampleandroidappusingfirebase.R
import android.freelessons.org.sampleandroidappusingfirebase.domain.Event
import android.freelessons.org.sampleandroidappusingfirebase.session.SessionManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class EventActivity : AppCompatActivity() {
    private var databaseReference: DatabaseReference? = null
    private var nameEditText: EditText? = null
    private var descriptionEditText: EditText? = null
    private var saveEvent: Button? = null
    var locationEditText: EditText? = null
    private var startDateEditText: EditText? = null
    private var locationView: AutocompleteSupportFragment? = null
    private var dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
    private var myCalendar: Calendar = Calendar.getInstance()
    private var event = Event()
    private var sessionManager: SessionManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event)
        startDateEditText = findViewById(R.id.startDateEditText)
        locationEditText = findViewById(R.id.locationEditText)
        saveEvent = findViewById(R.id.saveEvent)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        nameEditText = findViewById(R.id.nameEditText)
        sessionManager = SessionManager(this)
        event = sessionManager!!.event
        event.startDate?.let {
            myCalendar.time = it
        }
        databaseReference = FirebaseDatabase.getInstance().reference
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "YOUR_API_KEY")
        }
        locationView = supportFragmentManager.findFragmentById(R.id.locationView) as AutocompleteSupportFragment?
        if (locationView != null) {
            locationView!!.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))
            locationView!!.setOnPlaceSelectedListener(object : PlaceSelectionListener {
                override fun onPlaceSelected(place: Place) {
                    Log.d(TAG, "Place:" + place.name)
                    if (place.latLng != null) {
                        Log.d(TAG, "Latitude: ${place.latLng?.latitude}")
                        Log.d(TAG, "Longitude: ${place.latLng?.longitude}")
                    }
                    locationEditText?.setText(place.name)
                }

                override fun onError(status: Status) {
                    Log.e(TAG, "Error: $status")
                }
            })
        }
    }

    override fun onStart() {
        super.onStart()
        updateViews()
    }

    //update views
    private fun updateViews() {
        nameEditText!!.setText(event.name)
        descriptionEditText!!.setText(event.description)
        locationEditText!!.setText(event.location)
        event.startDate?.let {
            startDateEditText!!.setText(dateFormat.format(it))
        }

        saveEvent!!.setOnClickListener {
            if (isValid) {
                saveEvent()
            } else {
                Toast.makeText(applicationContext, "Please make sure all fields are filled", Toast.LENGTH_LONG).show()
            }
        }
        val date = OnDateSetListener { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            myCalendar[Calendar.YEAR] = year
            myCalendar[Calendar.MONTH] = monthOfYear
            myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            updateLabel()
        }
        startDateEditText!!.onFocusChangeListener = OnFocusChangeListener { _: View?, hasFocus: Boolean ->
            if (hasFocus) {
                DatePickerDialog(this@EventActivity, date, myCalendar[Calendar.YEAR], myCalendar[Calendar.MONTH],
                        myCalendar[Calendar.DAY_OF_MONTH]).show()
            }
        }
    }

    private val isValid: Boolean
        get() = nameEditText!!.text.toString().isNotEmpty() && descriptionEditText!!.text.toString()
            .isNotEmpty()

    private fun saveEvent() {
        val eventReference: DatabaseReference
        if (event.eventId == null) {
            eventReference = databaseReference!!.child("events").push()
            event.eventId = eventReference.key
        } else {
            eventReference = databaseReference!!.child("events").child(event.eventId!!)
        }
        event.name = nameEditText!!.text.toString()
        event.description = descriptionEditText!!.text.toString()
        event.location = locationEditText!!.text.toString()
        if (FirebaseAuth.getInstance().currentUser != null) {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser != null) {
                event.userId = firebaseUser.uid
            }
        }
        try {
            event.startDate = dateFormat.parse(startDateEditText?.text.toString())
        } catch (e: ParseException) {
            //TODO: do something serious
            //e.printStackTrace();
        }
        eventReference.updateChildren(event.toMap()).addOnCompleteListener { task: Task<Void?> ->
            if (task.isSuccessful) {
                Log.d(TAG, "Success")
                Toast.makeText(this@EventActivity, "Event saved successfully", Toast.LENGTH_SHORT).show()
            } else {
                Log.e(TAG, "erro")
                var errorMessage: String? = null
                val exception = task.exception
                if (exception != null) {
                    errorMessage = exception.message
                }
                Toast.makeText(this@EventActivity, "Error saving Event $errorMessage", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateLabel() {
        startDateEditText!!.setText(dateFormat.format(myCalendar.time))
    }

    companion object {
        private const val TAG = "SAPWF"
    }
}