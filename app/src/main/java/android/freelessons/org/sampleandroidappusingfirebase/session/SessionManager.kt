package android.freelessons.org.sampleandroidappusingfirebase.session

import android.content.Context
import android.content.SharedPreferences
import android.freelessons.org.sampleandroidappusingfirebase.domain.Event
import java.util.*

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences
    fun saveEvent(event: Event) {
        val editor = sharedPreferences.edit()
        editor.putString(Event.DESCRIPTION_PROPERTY, event.description)
        event.duration?.toFloat()?.let { editor.putFloat(Event.DURATION_PROPERTY, it) }
        editor.putString(Event.DURATION_TYPE_PROPERTY, event.durationType)
        editor.putString(Event.EVENT_ID_PROPERTY, event.eventId)
        editor.putString(Event.LOCATION_PROPERTY, event.location)
        editor.putString(Event.NAME_PROPERTY, event.name)
        event.startDate?.let { editor.putLong(Event.START_DATE_PROPERTY, it.time) }
        editor.putString(Event.USER_ID_PROPERTY, event.userId)
        editor.apply()
    }

    val event: Event
        get() {
            val event = Event()
            event.description = sharedPreferences.getString(Event.DESCRIPTION_PROPERTY, "")
            event.duration = sharedPreferences.getFloat(Event.DURATION_PROPERTY, 0f).toDouble()
            event.durationType = sharedPreferences.getString(Event.DURATION_TYPE_PROPERTY, "")
            event.eventId = sharedPreferences.getString(Event.EVENT_ID_PROPERTY, null)
            event.location = sharedPreferences.getString(Event.LOCATION_PROPERTY, "")
            event.name = sharedPreferences.getString(Event.NAME_PROPERTY, "")
            event.startDate = Date(sharedPreferences.getLong(Event.START_DATE_PROPERTY, Calendar.getInstance().timeInMillis))
            event.userId = sharedPreferences.getString(Event.USER_ID_PROPERTY, "")
            return event
        }

    init {
        sharedPreferences = context.getSharedPreferences("sapuf", Context.MODE_PRIVATE)
    }
}