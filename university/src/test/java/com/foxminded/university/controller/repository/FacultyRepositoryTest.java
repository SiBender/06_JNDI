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
import com.foxminded.university.model.Faculty;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WebConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebAppConfiguration
class FacultyRepositoryTest {
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    FacultyRepository facultyRepository;
    
    @Order(1)
    @Test
    void addShouldCreateNewRowInFacultiesTableTest() {
        Faculty faculty = new Faculty();
        faculty.setShortName("GF");
        faculty.setFullName("Griffindor");
        facultyRepository.add(faculty);
        
        int actual = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM faculties", Integer.class);
        assertEquals(3, actual);
    }

    @Order(2)
    @Test
    void getAllShouldReturnListOfFacultiesTest() {
        List<Faculty> actulaFaculties = facultyRepository.getAll();
        assertEquals(3, actulaFaculties.size());
        assertEquals("CS", actulaFaculties.get(0).getShortName());
    }

    @Order(3)
    @Test
    void getByIdShouldThrouwExceptionForNonExistingDataTest() {
        Throwable thrown = assertThrows(EmptyResultDataAccessException.class, () -> {
            facultyRepository.getById(100);
        });  
    }
    
    @Order(4)
    @ParameterizedTest
    @CsvSource({"1, 'CS', 'Computer Science'",
                "2, 'BA', 'Ballet Art'",
                "3, 'GF', 'Griffindor'"})
    void getByIdShouldReturnFacultyObjectTest(int id, String shortName, String fullname) {
        Faculty faculty = facultyRepository.getById(id);
        assertEquals(shortName, faculty.getShortName());
        assertEquals(fullname, faculty.getFullName());
    }

    @AfterAll
    @Test
    void restoreDatabaseState() {
        jdbcTemplate.update("DELETE FROM faculties WHERE faculty_id NOT IN (1, 2)");
        assertEquals(2,jdbcTemplate.queryForObject("SELECT COUNT(*) FROM faculties", Integer.class));
    }
}
