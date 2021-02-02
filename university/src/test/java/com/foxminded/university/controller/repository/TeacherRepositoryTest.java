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
import com.foxminded.university.model.Teacher;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WebConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebAppConfiguration
class TeacherRepositoryTest {
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    TeacherRepository teacherRepository;
    
    @Order(1)
    @Test
    void addShoulCreateNewRowInTeachersTableTest() {
        Teacher teacher = new Teacher();
        teacher.setFirstName("testTeacher");
        teacher.setLastName("testTeacher2");
        
        Faculty faculty = new Faculty();
        faculty.setId(1);
        
        teacher.setFaculty(faculty);
        teacherRepository.add(teacher);
        
        int actual = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM teachers", Integer.class);
        assertEquals(2, actual);
    }

    @Order(2)
    @ParameterizedTest
    @CsvSource({"1, 2", 
                "100, 0", 
                "5, 0"})
    void getByFacultyShouldReturnListOfTeachersTest(int id, int expectedSize) {
        Faculty faculty = new Faculty();
        faculty.setId(id);
        List<Teacher> actual = teacherRepository.getByFaculty(faculty);
        assertEquals(expectedSize, actual.size());
    }

    @Order(3)
    @Test
    void getByIdShouldReturnTeacherObjectTest() {
        Teacher teacher = teacherRepository.getById(1);
        assertEquals("Alan", teacher.getFirstName());
        assertEquals("Turing", teacher.getLastName());
        assertEquals("Turing machine", teacher.getCourses().get(0).getName());
        assertEquals("CS", teacher.getFaculty().getShortName());
    }
    
    @Order(4)
    @ParameterizedTest
    @CsvSource({"100", "200", "-1000"})
    void getByIdShouldThrouwExceptionForNonExistingDataTest(int id) {
        Throwable thrown = assertThrows(EmptyResultDataAccessException.class, () -> {
            teacherRepository.getById(id);
        });  
    }

    @AfterAll
    @Test
    void restoreDatabaseState() {
        jdbcTemplate.update("DELETE FROM teachers WHERE teacher_id > 1");
        assertEquals(1,jdbcTemplate.queryForObject("SELECT COUNT(*) FROM teachers", Integer.class));
        assertEquals(2,jdbcTemplate.queryForObject("SELECT COUNT(*) FROM faculties", Integer.class));
    }
}
