package android.freelessons.org.sampleandroidappusingfirebase.util;

import android.freelessons.org.sampleandroidappusingfirebase.domain.Event;

import com.google.firebase.database.DataSnapshot;

import java.util.Date;


public class EventUtil {
    public static Event parseSnaphot(DataSnapshot snapshot) {
        Event event = new Event();
        if(snapshot.child(Event.NAME_PROPERTY).getValue() != null)
            event.setName(snapshot.child(Event.NAME_PROPERTY).getValue().toString());
        if(snapshot.child(Event.DESCRIPTION_PROPERTY).getValue() != null)
            event.setDescription(snapshot.child(Event.DESCRIPTION_PROPERTY).getValue().toString());
        if(snapshot.child(Event.LOCATION_PROPERTY).getValue() != null)
            event.setLocation(snapshot.child(Event.LOCATION_PROPERTY).getValue().toString());
        if(snapshot.child(Event.START_DATE_PROPERTY).getValue() != null)
            event.setStartDate(new Date((long) Double.parseDouble(snapshot.child(Event.START_DATE_PROPERTY).getValue().toString())));
        event.setEventId(snapshot.getKey());
        return event;
    }
}
