package com.foxminded.university.controller.repository;

import static org.junit.jupiter.api.Assertions.*;

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
import com.foxminded.university.model.Timeslot;

@ExtendWith(SpringExtension.class) 
@ContextConfiguration(classes = {WebConfig.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebAppConfiguration
class TimeslotRepositoryTest {
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    TimeslotRepository timeslotRepository;
    
    @Order(1)
    @Test
    void getAllShouldReturnListOfTimeslotsTest() {
        List<Timeslot> actual = timeslotRepository.getAll();
        assertEquals(6, actual.size());
    }

    @Order(2)
    @ParameterizedTest
    @CsvSource({"1, '09:00 - 10:30'", 
                "2, '10:30 - 12:00'", 
                "3, '12:30 - 14:00'"})
    void getByIdShouldReturnTimeslotObjectTest(int id, String expectedDescription) {
        Timeslot timeslot = timeslotRepository.getById(id);
        assertEquals(expectedDescription, timeslot.getDescription());
    }

    @Order(3)
    @ParameterizedTest
    @CsvSource({"100", "200", "-1000"})
    void getByIdShouldThrouwExceptionForNonExistingDataTest(int id) {
        Throwable thrown = assertThrows(EmptyResultDataAccessException.class, () -> {
            timeslotRepository.getById(id);
        });  
    }
    
    @AfterAll
    @Test
    void restoreDatabaseState() {
        assertEquals(2,jdbcTemplate.queryForObject("SELECT COUNT(*) FROM faculties", Integer.class));
    }
}
