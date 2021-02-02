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
import com.foxminded.university.model.Faculty;
import com.foxminded.university.model.Group;
import com.foxminded.university.model.Student;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WebConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebAppConfiguration
class GroupRepositoryTest {
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    GroupRepository groupRepository;
    
    @Order(1)  
    @Test
    void addShouldCreateNewRowInGroupsTableTest() {
        Group group = new Group();
        group.setGroupName("testGroup");
        Faculty faculty = new Faculty();
        faculty.setId(1);
        group.setFaculty(faculty);
        
        groupRepository.add(group);
        
        int actual = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM groups", Integer.class);
        assertEquals(2, actual);
    }

    @Order(2)
    @Test
    void getAllShouldReturnListOfGroups() {
        List<Group> actualGroups = groupRepository.getAll();
        assertEquals(2, actualGroups.size());
        assertEquals("cs-20", actualGroups.get(0).getGroupName());
    }

    @Order(3)
    @Test
    void getByFacultyShouldReturnEmtyListForNonExistingDataTest() {
        Faculty faculty = new Faculty();
        faculty.setId(100);
        List<Group> actual = groupRepository.getByFaculty(faculty);
        assertTrue(actual.isEmpty());
    }

    @Order(4)
    @Test
    void getByFacultyShouldReturnListOfGroupsTest() {
        Faculty faculty = new Faculty();
        faculty.setId(1);
        List<Group> actual = groupRepository.getByFaculty(faculty);
        assertEquals(2, actual.size());
        assertEquals("cs-20", actual.get(0).getGroupName());
    }
    
    @Order(5)
    @Test
    void assignCourseToGroupShouldCreateNewRowInGroupsCoursesTableTest() {
        Course course = new Course();
        course.setId(1);
        Group group = new Group();
        group.setId(2);
        
        groupRepository.assignCourseToGroup(group, course);
        int actual = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM groups_courses", Integer.class);
        assertEquals(3, actual);
    }
    
    @Order(6)
    @ParameterizedTest
    @CsvSource({"10, 2",
                "1, 10",
                "100, 100"})
    void assignCourseToGroupShouldSkipInsertionIntoTableForNonExistingDataTest(int courseId, int groupId) {
        Course course = new Course();
        course.setId(courseId);
        Group group = new Group();
        group.setId(groupId);
        
        groupRepository.assignCourseToGroup(group, course);
        
        int actual = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM groups_courses", Integer.class);
        assertEquals(3, actual);
    }

    @Order(7)
    @Test
    void assignCourseToGroupShouldSkipInsertionRepeatingDataTest() {
        Course course = new Course();
        course.setId(1);
        Group group = new Group();
        group.setId(2);
        
        groupRepository.assignCourseToGroup(group, course);
        int actual = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM groups_courses", Integer.class);
        assertEquals(3, actual);
    }
    
    @Order(8)
    @Test
    void getByIdShouldReturnGroupObjectTest() {
        Group actual = groupRepository.getById(1);
        assertNotNull(actual);
        assertNotNull(actual.getCourses());
        assertNotNull(actual.getStudents());
    }
    
    @Order(9)
    @ParameterizedTest
    @CsvSource({"100", "200", "-1000"})
    void getByIdShouldThrouwExceptionForNonExistingDataTest(int id) {
        Throwable thrown = assertThrows(EmptyResultDataAccessException.class, () -> {
            groupRepository.getById(id);
        });
    }

    @Order(10)
    @Test
    void getByStudentShouldReturnGroupObjectTest() {
        Student student = new Student();
        student.setId(1);
        Group actual = groupRepository.getByStudent(student);
        assertEquals(1, actual.getId());
        assertEquals("cs-20", actual.getGroupName());
        assertEquals("CS", actual.getFaculty().getShortName());
    }

    @Order(11)
    @ParameterizedTest
    @CsvSource({"1000", "555500", "-1000"})
    void getByStudentThrouwExceptionForNonExistingDataTest(int id) {
        Student student = new Student();
        student.setId(id);
        Throwable thrown = assertThrows(EmptyResultDataAccessException.class, () -> {
            groupRepository.getByStudent(student);
        });
    }
    
    @AfterAll
    @Test
    void restoreDatabaseState() {
        jdbcTemplate.update("DELETE FROM groups_courses WHERE group_id > 1");
        jdbcTemplate.update("DELETE FROM groups WHERE group_id > 1");
        assertEquals(2,jdbcTemplate.queryForObject("SELECT COUNT(*) FROM faculties", Integer.class));
    }  
}
