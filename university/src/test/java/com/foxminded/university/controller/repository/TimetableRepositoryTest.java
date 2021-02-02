package com.foxminded.university.controller.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import com.foxminded.university.config.WebConfig;
import com.foxminded.university.model.DateInterval;
import com.foxminded.university.model.Student;
import com.foxminded.university.model.Teacher;
import com.foxminded.university.model.Timetable;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WebConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebAppConfiguration
class TimetableRepositoryTest {
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    TimetableRepository TimetableRepository;
    
    @Test
    void testGetByStudentShouldReturnTimetableObjectTest() {
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2021,1,1);
        DateInterval dateInterval = new DateInterval(startDate, endDate);
        
        Student student = new Student();
        student.setId(1);
        
        Timetable timetable = TimetableRepository.getByStudent(student, dateInterval);
        assertEquals(startDate, timetable.getDateInterval().getStartDate());
        assertEquals(endDate, timetable.getDateInterval().getEndDate());
        assertTrue(timetable.getLessons().size() > 0);
    }

    @ParameterizedTest
    @CsvSource({"100", "200", "-1000"})
    void testGetByStudentShouldReturnTimetableWithEmptyListOfLessonsForNonExistingStudentTest(int id) {
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2021,1,1);
        DateInterval dateInterval = new DateInterval(startDate, endDate);
        
        Student student = new Student();
        student.setId(id);
        
        Timetable timetable = TimetableRepository.getByStudent(student, dateInterval);
        assertTrue(timetable.getLessons().isEmpty());
    }
    
    @Test
    void testGetByTeacherShouldReturnTimetableObjectTest() {
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2021,1,1);
        DateInterval dateInterval = new DateInterval(startDate, endDate);
        
        Teacher teacher = new Teacher();
        teacher.setId(1);
        
        Timetable timetable = TimetableRepository.getByTeacher(teacher, dateInterval);
        assertEquals(startDate, timetable.getDateInterval().getStartDate());
        assertEquals(endDate, timetable.getDateInterval().getEndDate());
        assertTrue(timetable.getLessons().size() > 0);
    }

    @ParameterizedTest
    @CsvSource({"100", "200", "-1000"})
    void testGetByTeacherShouldReturnTimetableWithEmptyListOfLessonsForNonExistingTeacherTest(int id) {
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2021,1,1);
        DateInterval dateInterval = new DateInterval(startDate, endDate);
        
        Teacher teacher = new Teacher();
        teacher.setId(id);
        
        Timetable timetable = TimetableRepository.getByTeacher(teacher, dateInterval);
        assertTrue(timetable.getLessons().isEmpty());
    }
    
    @AfterAll
    @Test
    void restoreDatabaseState() {
        assertEquals(2,jdbcTemplate.queryForObject("SELECT COUNT(*) FROM faculties", Integer.class));
    }
}
