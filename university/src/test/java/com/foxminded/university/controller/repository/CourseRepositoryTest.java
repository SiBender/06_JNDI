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
import com.foxminded.university.model.Course;
import com.foxminded.university.model.Teacher;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WebConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebAppConfiguration
class CourseRepositoryTest {
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    CourseRepository courseRepository;
    
    
    @Order(1)
    @Test
    void addShouldCreateNewRowInCoursesTableTest() {
        Course course = new Course();
        course.setName("testCourse");
        course.setDescription("testDescr");
        Teacher teacher = new Teacher();
        teacher.setId(1);
        course.setTeacher(teacher);
        
        courseRepository.add(course);
        int expected = 3;
        int current = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM COURSES", Integer.class);
        assertEquals(expected, current);
    }
    
    @Order(2)
    @Test
    void getByTeacherShouldReturnListOfCoursesTest() {
        Teacher teacher = new Teacher();
        teacher.setId(1);
        List<Course> actual = courseRepository.getByTeacher(teacher);
        assertEquals(3, actual.size());
        assertEquals("Turing machine", actual.get(0).getName());
    }
    
    @Order(3)
    @Test
    void getByTeacherShouldReturnEmptyListForNonExistingDataTest() {
        Teacher teacher = new Teacher();
        teacher.setId(100);
        List<Course> actual = courseRepository.getByTeacher(teacher);
        assertTrue(actual.isEmpty());
    }

    @Order(4)
    @ParameterizedTest
    @CsvSource({"1, 'Turing machine', Alan",
                "2, 'Turing-complete languages', Alan",
                "3, testCourse, Alan"})
    void getByIdShouldReturnCourseObjectTest(int id, String courseName, String teacherName) {
        Course current = courseRepository.getById(id);
        assertEquals(courseName, current.getName());
        assertEquals(teacherName, current.getTeacher().getFirstName());
    }
    
    @Order(5)
    @Test
    void getByIdShouldThrouwExceptionForNonExistingDataTest() {
        Throwable thrown = assertThrows(EmptyResultDataAccessException.class, () -> {
            courseRepository.getById(100);
        });  
    }
    
    @AfterAll
    @Test
    void restoreDatabaseState() {
        jdbcTemplate.update("DELETE FROM courses WHERE course_id > 2");
        assertEquals(2,jdbcTemplate.queryForObject("SELECT COUNT(*) FROM faculties", Integer.class));
        assertEquals(2,jdbcTemplate.queryForObject("SELECT COUNT(*) FROM courses", Integer.class));
    }
}
