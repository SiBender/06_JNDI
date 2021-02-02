package com.foxminded.university.controller.repository;

import static org.junit.jupiter.api.Assertions.*;

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
import com.foxminded.university.model.Group;
import com.foxminded.university.model.Student;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WebConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebAppConfiguration
class StudentRepositoryTest {
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    StudentRepository studentRepository;
    
    @Order(1)
    @Test
    void addShoulCreateNewRowInStudentsTableTest() {
        Group group = new Group();
        group.setId(1);
        
        Student student = new Student();
        student.setFirstName("testFirstName");
        student.setLastName("testLastname");
        student.setGroup(group);
        studentRepository.add(student);
        
        int actual = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM students", Integer.class);
        assertEquals(3, actual);
    }

    @Order(2)
    @ParameterizedTest
    @CsvSource({"100", "200", "-1000"})
    void getByIdShouldThrouwExceptionForNonExistingDataTest(int id) {
        Throwable thrown = assertThrows(EmptyResultDataAccessException.class, () -> {
            studentRepository.getById(id);
        });  
    }
    
    @Order(3)
    @Test
    void getByIdShouldReturnStudentObjectTest() {
        Student actual = studentRepository.getById(1);
        assertEquals("John", actual.getFirstName());
        assertEquals("Smith", actual.getLastName());
        assertEquals("cs-20", actual.getGroup().getGroupName());
    }
    
    @AfterAll
    @Test
    void restoreDatabaseState() {
        assertEquals(2,jdbcTemplate.queryForObject("SELECT COUNT(*) FROM faculties", Integer.class));
    }
}
