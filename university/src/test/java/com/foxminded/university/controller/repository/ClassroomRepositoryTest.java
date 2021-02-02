package com.foxminded.university.controller.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import com.foxminded.university.config.WebConfig;
import com.foxminded.university.model.Classroom;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WebConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebAppConfiguration
class ClassroomRepositoryTest {
    @Autowired
    ClassroomRepository classroomRepository;
    
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Order(1)
    @Test
    void addShouldAddOneRowInClassroomTableTest() {
        Classroom classroom = new Classroom();
        classroom.setNumber("555");
        classroom.setCapacity(123);
        
        classroomRepository.add(classroom);
        
        int expected = 3;
        int actual = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM CLASSROOMS", Integer.class);
        assertEquals(expected, actual);
    }
    
    
    @Order(2)
    @Test
    void getAllShouldReturnArrayListOfClassroomsTest() {
        List<Classroom> actual = classroomRepository.getAll();
        assertEquals(3, actual.size());
        assertEquals(1, actual.get(0).getId());
    }
    
    
    @Order(3)
    @ParameterizedTest
    @CsvSource({"555, 3, 123",
                "101A, 1, 30",
                "102A, 2, 300"})
    void getBynameShouldReturnClassroomObjectTest(String number, int expectedId, int expectedCapacity) {
        Classroom current = classroomRepository.getByNumber(number);
        assertEquals(expectedId, current.getId());
        assertEquals(expectedCapacity, current.getCapacity());
    }
    
    

    @Order(4)
    @ParameterizedTest
    @CsvSource({"1, 101A",
                "2, 102A",
                "3, 555"})
    void getByIdShouldReturnClassroomObjectTest(int id, String expectedNumber) {
        assertEquals(expectedNumber, classroomRepository.getById(id).getNumber());
    }
    
    @Order(5)
    @Test
    void getByNumberShouldThrouwExceptionForNonExistingDataTest() {
        Throwable thrown = assertThrows(EmptyResultDataAccessException.class, () -> {
            classroomRepository.getByNumber("zzzzz");
        });
    }
    
    @Order(6)
    @Test
    void getByIdShouldThrouwExceptionForNonExistingDataTest() {
        Throwable thrown = assertThrows(EmptyResultDataAccessException.class, () -> {
            classroomRepository.getById(123);
        });
    }
    
    
    @AfterAll
    @Test
    void restoreDatabaseState() {
        jdbcTemplate.update("DELETE FROM classrooms WHERE classroom_number NOT IN ('101A', '102A')");
    }
}
