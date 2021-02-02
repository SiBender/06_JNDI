package com.foxminded.university.controller.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.foxminded.university.model.DateInterval;

@Component
public class DateIntervalGenerator {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final int NUM_DAYS_TO_END_OF_WEEK = 6;

    public DateInterval getMonthByDate(LocalDate date) {
        LocalDate startDate = getFirstDayOfMonth(date);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        return new DateInterval(startDate, endDate);
    }
    
    public DateInterval getWeekByDate(LocalDate date) {
        LocalDate startDate = getFirstDayOfWeek(date);
        LocalDate endDate = startDate.plusDays(NUM_DAYS_TO_END_OF_WEEK);
        return new DateInterval(startDate, endDate);
    }
    
    public DateInterval getCurrentMonth() {
        return getMonthByDate(LocalDate.now());
    }
    
    public DateInterval getCurrentWeek() {        
        return getWeekByDate(LocalDate.now());
    }

    private LocalDate getFirstDayOfMonth(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return LocalDate.of(date.getYear(), date.getMonth() , 1);
    }
    
    private LocalDate getFirstDayOfWeek(LocalDate currentDate) {
        if (currentDate == null) {
            currentDate = LocalDate.now();
        }
        int dayNumber = 0;
        switch (currentDate.getDayOfWeek()) {
            case MONDAY : dayNumber = 0; break;
            case TUESDAY : dayNumber = 1; break;
            case WEDNESDAY : dayNumber = 2; break;
            case THURSDAY : dayNumber = 3; break;
            case FRIDAY : dayNumber = 4; break;
            case SATURDAY : dayNumber = 5; break;
            case SUNDAY : dayNumber = 6; break;
        }
        return currentDate.minusDays(dayNumber);
    }
    
    public DateInterval getFromString(String startDate, String endDate) {
        DateInterval output = null;
        try {
            output = new DateInterval(LocalDate.parse(startDate), LocalDate.parse(endDate));
        } catch (DateTimeParseException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Error parsing dateInterval from string (?, ?)", startDate, endDate);
            }
        } 
        return output;
    } 
}
