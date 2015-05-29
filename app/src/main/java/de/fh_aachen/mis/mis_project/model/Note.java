package de.fh_aachen.mis.mis_project.model;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by batman on 5/25/15.
 */
public class Note {

    private long id;
    private String note;
    private boolean has_reminder;
    private Date remind_date;
    private Time remind_time;
    private String datetime_str;
    private String reminder_email;

    private Double location_lat;
    private Double location_lng;
    private int priority;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNoteText() {
        return note;
    }

    public void setNoteText(String text) {
        this.note = text;
    }

    public Date getDate() {
        return remind_date;
    }

    public void setDate(Date date) {
        this.remind_date = date;
    }

    public Time getTime() {
        return remind_time;
    }

    public void setDate(Time time) {
        this.remind_time = time;
    }

    public boolean getHasReminder() {
        return has_reminder;
    }

    public void setHasReminder(boolean has_reminder) {
        this.has_reminder = has_reminder;
    }

    public void setDatetimeStr(String datetime){
        this.datetime_str = datetime;
    }

    public String getDatetimeStr(){
        return this.datetime_str;
    }

    public String getReminder_email() {
        return reminder_email;
    }

    public void setReminder_email(String reminder_email) {
        this.reminder_email = reminder_email;
    }

    public void setLocationLat(Double location_lat) {
        this.location_lat = location_lat;
    }

    public void setLocationLng(Double location_lng) {
        this.location_lng = location_lng;
    }

    public Double getLocationLat() {
        return this.location_lat;
    }

    public Double getLocationLng() {
        return this.location_lng;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy H:m:s");
        SimpleDateFormat myformat;
        myformat = new SimpleDateFormat("dd-MM-yyyy H:m");
        String formatted_date = null;
        try {
            formatted_date = myformat.format(sdf.parse(datetime_str));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return note + (has_reminder ? ("\nWhen: " + formatted_date) : "");
    }
}
