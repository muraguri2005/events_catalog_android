package android.freelessons.org.sampleandroidappusingfirebase.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.freelessons.org.sampleandroidappusingfirebase.domain.Event;

import java.util.Calendar;
import java.util.Date;


public class SessionManager {
    private final SharedPreferences sharedPreferences;
    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences("sapuf",Context.MODE_PRIVATE);
    }
    public void saveEvent(Event event) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Event.DESCRIPTION_PROPERTY,event.getDescription());
        editor.putFloat(Event.DURATION_PROPERTY, (float) event.getDuration());
        editor.putString(Event.DURATION_TYPE_PROPERTY,event.getDurationType());
        editor.putString(Event.EVENT_ID_PROPERTY,event.getEventId());
        editor.putString(Event.LOCATION_PROPERTY,event.getLocation());
        editor.putString(Event.NAME_PROPERTY,event.getName());
        editor.putLong(Event.START_DATE_PROPERTY,event.getStartDate().getTime());
        editor.putString(Event.USER_ID_PROPERTY,event.getUserId());
        editor.apply();
    }
    public Event getEvent() {
        Event event = new Event();
        event.setDescription(sharedPreferences.getString(Event.DESCRIPTION_PROPERTY,""));
        event.setDuration(sharedPreferences.getFloat(Event.DURATION_PROPERTY, 0));
        event.setDurationType(sharedPreferences.getString(Event.DURATION_TYPE_PROPERTY,""));
        event.setEventId(sharedPreferences.getString(Event.EVENT_ID_PROPERTY,null));
        event.setLocation(sharedPreferences.getString(Event.LOCATION_PROPERTY,""));
        event.setName(sharedPreferences.getString(Event.NAME_PROPERTY,""));
        event.setStartDate(new Date(sharedPreferences.getLong(Event.START_DATE_PROPERTY, Calendar.getInstance().getTimeInMillis())));
        event.setUserId(sharedPreferences.getString(Event.USER_ID_PROPERTY,""));
        return event;
    }
}
