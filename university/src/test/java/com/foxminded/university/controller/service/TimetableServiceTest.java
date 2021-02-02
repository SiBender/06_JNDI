package com.foxminded.university.controller.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;

import com.foxminded.university.controller.repository.TimetableRepository;
import com.foxminded.university.controller.util.DateIntervalGenerator;
import com.foxminded.university.model.Teacher;
import com.foxminded.university.model.Student;
import com.foxminded.university.model.DateInterval;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TimetableServiceTest {
    @Mock
    TimetableRepository timetableRepository;
    @Mock
    DateIntervalGenerator dateIntervalGenerator;
    
    @InjectMocks
    TimetableService timetableService;
    
    @BeforeAll
    void init() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    void getTeacherTimetableShouldCallTimetableRepositoryAndDateIntervalGeneratorTest() {
        String startDate = "2020-01-01";
        String endDate = "2021-01-01";
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        Mockito.when(dateIntervalGenerator.getFromString(startDate, endDate))
               .thenReturn(new DateInterval(start, end));
        
        timetableService.getTeacherTimetable(startDate, endDate, 111);
        
        verify(dateIntervalGenerator).getFromString(startDate, endDate);
        verify(timetableRepository).getByTeacher(any(Teacher.class), any(DateInterval.class));
    }

    @Test
    void getStudentTimetableShouldCallTimetableRepositoryAndDateIntervalGeneratorTest() {
        String startDate = "2000-01-01";
        String endDate = "2001-01-01";
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        Mockito.when(dateIntervalGenerator.getFromString(startDate, endDate))
               .thenReturn(new DateInterval(start, end));
        
        timetableService.getStudentTimetable(startDate, endDate, 222);
        verify(dateIntervalGenerator).getFromString(startDate, endDate);
        verify(timetableRepository).getByStudent(any(Student.class), any(DateInterval.class));
    }

}
