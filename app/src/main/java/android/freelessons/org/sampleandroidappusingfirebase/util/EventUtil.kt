package android.freelessons.org.sampleandroidappusingfirebase.util

import android.freelessons.org.sampleandroidappusingfirebase.domain.Event
import com.google.firebase.database.DataSnapshot
import java.util.*

object EventUtil {
    @JvmStatic
    fun parseSnaphot(snapshot: DataSnapshot): Event {
        val event = Event()
        if (snapshot.child(Event.NAME_PROPERTY).value != null) event.name = snapshot.child(Event.NAME_PROPERTY).value.toString()
        if (snapshot.child(Event.DESCRIPTION_PROPERTY).value != null) event.description = snapshot.child(Event.DESCRIPTION_PROPERTY).value.toString()
        if (snapshot.child(Event.LOCATION_PROPERTY).value != null) event.location = snapshot.child(Event.LOCATION_PROPERTY).value.toString()
        if (snapshot.child(Event.START_DATE_PROPERTY).value != null) event.startDate = Date(snapshot.child(Event.START_DATE_PROPERTY).value.toString().toDouble().toLong())
        event.eventId = snapshot.key
        if (snapshot.child(Event.POSTER_PATH).value != null) event.posterPath = snapshot.child(Event.POSTER_PATH).value.toString()
        return event
    }
}