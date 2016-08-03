package android.freelessons.org.sampleandroidappusingfirebase.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Event {
    String eventId;
    String name;
    String description;
    String location;
    Date startDate=new Date();
    double duration;
    String durationType;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getDurationType() {
        return durationType;
    }

    public void setDurationType(String durationType) {
        this.durationType = durationType;
    }

    public static String EVENT_ID_PROPERTY="eventid";
    public static String NAME_PROPERTY="name";
    public static String DESCRIPTION_PROPERTY="description";
    public static String LOCATION_PROPERTY="location";
    public static String START_DATE_PROPERTY="startdate";
    public static String  DURATION_PROPERTY="duration";
    public static String  DURATION_TYPE_PROPERTY="durationtype";
    public Map<String,Object> toMap(){
        Map<String,Object> eventMap=new HashMap<>();
        eventMap.put(EVENT_ID_PROPERTY,eventId);
        eventMap.put(NAME_PROPERTY,name);
        eventMap.put(DESCRIPTION_PROPERTY,description);
        eventMap.put(LOCATION_PROPERTY,location);
        eventMap.put(START_DATE_PROPERTY,startDate.getTime());
        eventMap.put(DURATION_PROPERTY,duration);
        eventMap.put(DURATION_TYPE_PROPERTY,durationType);
        return eventMap;
    }
}
