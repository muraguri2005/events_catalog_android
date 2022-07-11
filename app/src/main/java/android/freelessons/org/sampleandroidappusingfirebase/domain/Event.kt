package android.freelessons.org.sampleandroidappusingfirebase.domain

import java.util.*

class Event(
        var eventId: String? = null,
        var name: String? = null,
        var description: String? = null,
        var location: String? = null,
        var startDate: Date? = Date(),
        var duration: Double? = 0.0,
        var durationType: String? = null,
        var userId: String? = null,
        var posterPath: String? = null
) {
    fun toMap(): Map<String, Any?> {
        val eventMap: MutableMap<String, Any?> = HashMap()
        eventMap[EVENT_ID_PROPERTY] = eventId
        eventMap[NAME_PROPERTY] = name
        eventMap[DESCRIPTION_PROPERTY] = description
        eventMap[LOCATION_PROPERTY] = location
        eventMap[START_DATE_PROPERTY] = startDate?.time
        eventMap[DURATION_PROPERTY] = duration
        eventMap[DURATION_TYPE_PROPERTY] = durationType
        eventMap[USER_ID_PROPERTY] = userId
        eventMap[POSTER_PATH] = posterPath
        return eventMap
    }

    companion object {
        @JvmField
        var EVENT_ID_PROPERTY = "eventid"

        @JvmField
        var NAME_PROPERTY = "name"

        @JvmField
        var DESCRIPTION_PROPERTY = "description"

        @JvmField
        var LOCATION_PROPERTY = "location"

        @JvmField
        var START_DATE_PROPERTY = "startdate"

        @JvmField
        var DURATION_PROPERTY = "duration"

        @JvmField
        var DURATION_TYPE_PROPERTY = "durationtype"

        @JvmField
        var USER_ID_PROPERTY = "userid"

        @JvmField
        var POSTER_PATH = "poster_path"
    }
}