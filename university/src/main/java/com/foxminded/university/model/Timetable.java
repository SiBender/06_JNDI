package com.foxminded.university.model;

import java.util.List;

public class Timetable {
    private List<Lesson> lessons;
    private DateInterval dateInterval;

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public DateInterval getDateInterval() {
        return dateInterval;
    }

    public void setDateInterval(DateInterval dateInterval) {
        this.dateInterval = dateInterval;
    }
    
    public String toString() {
        StringBuilder output = new StringBuilder();
        for(Lesson lesson : lessons) {
            output.append(lesson.getDate()).append(" | ");
            output.append(lesson.getTime().getDescription()).append(" | ");
            output.append(lesson.getCourse().getName()).append(" | ");
            output.append(lesson.getClassroom().getNumber()).append("\n");
        }
        return output.toString(); 
    }
}
