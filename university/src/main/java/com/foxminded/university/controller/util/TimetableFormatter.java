package com.foxminded.university.controller.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.foxminded.university.model.DateInterval;
import com.foxminded.university.model.Lesson;
import com.foxminded.university.model.Timeslot;
import com.foxminded.university.model.Timetable;

@Component
public class TimetableFormatter {   
    public Map<LocalDate, Lesson[]> generateFormattedTable(Timetable timeTable, List<Timeslot> timeslots) {        
        List<LocalDate> dates = generateDatesList(timeTable.getDateInterval());
        Map<LocalDate, Lesson[]> output = new LinkedHashMap<>();
        for(LocalDate date : dates) {
            output.put(date, new Lesson[timeslots.size()]);
        }
        
        for(Lesson lesson : timeTable.getLessons()) {
            Lesson[] lessonsOfDay = output.get(lesson.getDate());
            lessonsOfDay[findTimeslotIndex(timeslots, lesson.getTime())] = lesson;
        }
        
        return output;
    }
    
    private List<LocalDate> generateDatesList(DateInterval dateInterval) {
        List<LocalDate> output = new ArrayList<>();
        LocalDate currentDate = dateInterval.getStartDate(); 
        while(currentDate.isAfter(dateInterval.getEndDate()) == false) {
            output.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }
        return output;
    }
    
    private int findTimeslotIndex(List<Timeslot> timeslots, Timeslot timeslot) {
        int index = -1;
        for (int i = 0 ; i < timeslots.size(); i++) {
            if (timeslots.get(i).getId() == timeslot.getId()) {
                index = i;
                break;
            }
        }
        return index;
    }
}
