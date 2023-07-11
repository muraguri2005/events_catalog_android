package android.freelessons.org.sampleandroidappusingfirebase

import android.content.Intent
import android.freelessons.org.sampleandroidappusingfirebase.domain.Event
import android.freelessons.org.sampleandroidappusingfirebase.session.SessionManager
import android.freelessons.org.sampleandroidappusingfirebase.ui.EventActivity
import android.freelessons.org.sampleandroidappusingfirebase.ui.SignInUI
import android.freelessons.org.sampleandroidappusingfirebase.ui.SignUpUI
import android.freelessons.org.sampleandroidappusingfirebase.util.EventUtil
import android.freelessons.org.sampleandroidappusingfirebase.util.ImageRequester.Companion.getInstance
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.android.volley.toolbox.NetworkImageView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    var mEventRecyclerView: RecyclerView? = null
    private var addEvent: FloatingActionButton? = null
    private var signIn: Button? = null
    private var signUp: Button? = null
    private var signOut: Button? = null
    private var databaseReference: DatabaseReference? = null
    var simpleDateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseAuthStateListener: AuthStateListener? = null
    private var adapter: FirebaseRecyclerAdapter<*, *>? = null
    var mLinearLayoutManager: LinearLayoutManager? = null
    private var sessionManager: SessionManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sessionManager = SessionManager(this)
        databaseReference = FirebaseDatabase.getInstance().reference
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuthStateListener = AuthStateListener { updateViews() }
        firebaseAuth!!.addAuthStateListener(firebaseAuthStateListener!!)
        mEventRecyclerView = findViewById(R.id.eventRecyclerView)
        addEvent = findViewById(R.id.addEvent)
        signIn = findViewById(R.id.signIn)
        signOut = findViewById(R.id.signOut)
        signUp = findViewById(R.id.signUp)
        mLinearLayoutManager = LinearLayoutManager(this)
        mEventRecyclerView?.layoutManager = mLinearLayoutManager
        val options = FirebaseRecyclerOptions.Builder<Event>().setQuery(databaseReference!!.child(EVENTS_CHILD)) { snapshot: DataSnapshot -> EventUtil.parseSnaphot(snapshot) }.setLifecycleOwner(this).build()
        this.adapter = object : FirebaseRecyclerAdapter<Event, EventViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.event_item, parent, false)
                return EventViewHolder(view)
            }

            override fun onBindViewHolder(viewHolder: EventViewHolder, position: Int, event: Event) {
                viewHolder.eventTitle.text = getString(R.string.event_title, event.name, event.startDate?.let { simpleDateFormat.format(it) }, event.location)
                viewHolder.eventDescription.text = event.description
                val imageRequester = getInstance(baseContext)
                imageRequester!!.setImageFromUrl(viewHolder.poster, event.posterPath)
                viewHolder.itemView.setOnClickListener { editEvent(event) }
            }

        }
        adapter?.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                val eventsCount = adapter?.itemCount
                val lastVisiblePosition = mLinearLayoutManager!!.findLastCompletelyVisibleItemPosition()
                if (lastVisiblePosition == -1 || positionStart >= eventsCount!! -1 && lastVisiblePosition == positionStart - 1) {
                    mEventRecyclerView?.scrollToPosition(positionStart)
                }
            }
        })
        mEventRecyclerView?.adapter = adapter
    }

    private fun addEvent(event: Event) {
        sessionManager!!.saveEvent(event)
        if (firebaseAuth!!.currentUser != null) {
            val intent = Intent(applicationContext, EventActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this@MainActivity, "Please sign in to create an event", Toast.LENGTH_SHORT).show()
        }
    }

    private fun editEvent(event: Event) {
        sessionManager!!.saveEvent(event)
        if (firebaseAuth!!.currentUser != null) {
            val intent = Intent(applicationContext, EventActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this@MainActivity, "Please sign in to edit the event", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        addEvent!!.setOnClickListener { addEvent(Event()) }
        signIn!!.setOnClickListener { signIn() }
        signUp!!.setOnClickListener { signUp() }
        signOut!!.setOnClickListener { signOut() }
        updateViews()
    }

    private fun updateViews() {
        if (firebaseAuth!!.currentUser != null) {
            signIn!!.visibility = View.GONE
            signOut!!.visibility = View.VISIBLE
            signUp!!.visibility = View.GONE
        } else {
            signIn!!.visibility = View.VISIBLE
            signOut!!.visibility = View.GONE
            signUp!!.visibility = View.VISIBLE
        }
    }

    private fun signIn() {
        val dialog: DialogFragment = SignInUI.newInstance()
        dialog.show(supportFragmentManager, "SignInUI")
    }

    private fun signUp() {
        val dialog: DialogFragment = SignUpUI.newInstance()
        dialog.show(supportFragmentManager, "SignUpUI")
    }

    private fun signOut() {
        firebaseAuth!!.signOut()
    }

    class EventViewHolder internal constructor(v: View?) : RecyclerView.ViewHolder(v!!) {
        var eventTitle: TextView = itemView.findViewById(R.id.eventTitle)
        var eventDescription: TextView = itemView.findViewById(R.id.eventDescription)
        var poster: NetworkImageView = itemView.findViewById(R.id.imageView)

    }

    override fun onStart() {
        super.onStart()
        adapter!!.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter!!.stopListening()
    }

    companion object {
        private const val EVENTS_CHILD = "events"
    }
}