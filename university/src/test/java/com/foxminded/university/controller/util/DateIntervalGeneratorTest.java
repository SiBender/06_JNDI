package com.foxminded.university.controller.util;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.foxminded.university.model.DateInterval;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DateIntervalGeneratorTest {
    DateIntervalGenerator dateIntervalGenerator;
    
    @BeforeAll
    void init() {
        dateIntervalGenerator = new DateIntervalGenerator();
    }
    
    @Test
    void getMonthByDateShouldReturnFirstAndLastDaysOfCurrentMonthIfInputIsNullTest() {
        DateInterval actual = dateIntervalGenerator.getMonthByDate(null);
        
        LocalDate expectedStartDate = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1);
        LocalDate expectedEndDate = expectedStartDate.plusMonths(1).minusDays(1);
        assertEquals(expectedStartDate, actual.getStartDate());
        assertEquals(expectedEndDate, actual.getEndDate());
    }
    
    @ParameterizedTest
    @CsvSource({"1970, 12, 1, '1970-12-01 - 1970-12-31'",
                "2020, 7, 14, '2020-07-01 - 2020-07-31'",
                "2020, 2, 11, '2020-02-01 - 2020-02-29'",
                "1986, 8, 15, '1986-08-01 - 1986-08-31'",
                "2000, 1, 11, '2000-01-01 - 2000-01-31'",
                "2000, 2, 22, '2000-02-01 - 2000-02-29'",
                "3000, 5, 10, '3000-05-01 - 3000-05-31'",})
    void  getMonthByDateShouldReturnCorrestIntervalTest(int year, int month, int day, String expected) {
        LocalDate currentDate = LocalDate.of(year, month, day);
        DateInterval actualDateInterval = dateIntervalGenerator.getMonthByDate(currentDate);
        assertEquals(expected, actualDateInterval.getStartDate().toString() + " - " 
                             + actualDateInterval.getEndDate().toString());
    }
    
    @Test
    void getCurrentMonthShouldReturnDateIntervalWithCurrentMonthTest() {
        LocalDate starlOfCurrentMonth = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1);
        LocalDate endOfCurrentMonth = starlOfCurrentMonth.plusMonths(1).minusDays(1);
        DateInterval actual = dateIntervalGenerator.getCurrentMonth();
        assertEquals(starlOfCurrentMonth, actual.getStartDate());
        assertEquals(endOfCurrentMonth, actual.getEndDate());
    }
    
    @ParameterizedTest
    @CsvSource({"1970, 12, 1, '1970-11-30 - 1970-12-06'",
                "2020, 7, 14, '2020-07-13 - 2020-07-19'",
                "2020, 2, 11, '2020-02-10 - 2020-02-16'",
                "1986, 8, 15, '1986-08-11 - 1986-08-17'",
                "2000, 1, 11, '2000-01-10 - 2000-01-16'",
                "2000, 2, 22, '2000-02-21 - 2000-02-27'",
                "3000, 5, 10, '3000-05-05 - 3000-05-11'",})
    void getWeekByDateShouldReturnDateIntervalWithCurrentWeekTest(int year, int month, int day, String expected) {
        LocalDate testDate = LocalDate.of(year, month, day);
        DateInterval actualDateInterval = dateIntervalGenerator.getWeekByDate(testDate);
        assertEquals(expected, actualDateInterval.getStartDate().toString() + " - " 
                               + actualDateInterval.getEndDate().toString());
    }
    
    @ParameterizedTest
    @CsvSource({"'1970-11-30', '1970-12-06'",
                "'2020-07-13', '2020-07-19'",
                "'2020-02-10', '2020-02-16'",
                "'1986-08-11', '1986-08-17'",
                "'2000-01-10', '2020-07-19'",
                "'2000-02-21', '2000-02-27'",
                "'3000-05-05', '3000-05-11'"})
    void getFromStringShouldReturnNullIfInputIsNotAValidDateTest(String startDate, String endDate) {
        DateInterval actual = dateIntervalGenerator.getFromString(startDate, endDate);
        assertNotNull(actual);
        int expectedYear = Integer.parseInt(startDate.substring(0,4));
        int expectedMonth = Integer.parseInt(startDate.substring(5,7));
        int expectedDay = Integer.parseInt(startDate.substring(8));
        LocalDate expectedStart = LocalDate.of(expectedYear, expectedMonth, expectedDay);
        assertEquals(expectedStart, actual.getStartDate());
    }
    
    @ParameterizedTest
    @CsvSource({"'2020-07-', '2020-07-19'",
                "'2020-07-', '2020-07-19'",
                "'2020-07-', '2020-07-19'",
                "'2020-07-', '2020-07-19'",
                "'2020-07-', '2020-07-19'",
                "'2020-07-', '2020-07-19'",
                "'2020-07-', '2020-07-19'",
                "'2020-07-', '2020-07-19'"})
    void getFromStringShouldReturnCorrectDateintervalTest(String startDate, String endDate) {
        
    }
}
