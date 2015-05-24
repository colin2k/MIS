package de.fh_aachen.mis.mis_project.model;

import java.sql.Time;
import java.util.Date;

/**
 * Created by batman on 5/25/15.
 */
public class Note {
    private long id;
    private String note;
    private Date remind_date;
    private Time remind_time;

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

    @Override
    public String toString() {
        return note;
    }
}
