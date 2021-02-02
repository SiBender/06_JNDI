package com.foxminded.university.controller.repository;

import com.foxminded.university.model.Course;
import com.foxminded.university.model.Faculty;
import com.foxminded.university.model.Group;
import com.foxminded.university.model.Student;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class GroupRepository {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GroupRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public void add(Group group) {
        String query = "INSERT INTO groups (group_name, faculty_id) "
                     + "VALUES (?, ?)";
        try {
            jdbcTemplate.update(query, group.getGroupName(), group.getFaculty().getId());
            if (logger.isDebugEnabled()) {
                logger.debug("Insert new group ({}, {})", group.getGroupName(), group.getFaculty().getId());
            }
        } catch (DataAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Error while creating new group", ex);
            }
        }
    }
    
    public List<Group> getAll() {
        logger.debug("Get all groups");
        
        String query = "SELECT * FROM groups";
        return jdbcTemplate.query(query, new LazyLoadGroupRowMapper());
    }
    
    public List<Group> getByFaculty(Faculty faculty) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get froup by faculty (id = {})", faculty.getId());
        }
        
        String query = "SELECT * FROM groups WHERE faculty_id = ?";
        return jdbcTemplate.query(query, new Object[] {faculty.getId()} ,new LazyLoadGroupRowMapper());
    }
    
    public void assignCourseToGroup(Group group, Course course) {
        String query = "INSERT INTO groups_courses (group_id, course_id) "
                     + "VALUES (?, ?)";
        try {
            jdbcTemplate.update(query, group.getId(), course.getId());  
            if (logger.isDebugEnabled()) {
                logger.debug("Assign course (id = {}) to group (id = {})", course.getId(), group.getId());
            }
        } catch (DataAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Error assigning course to group", ex);
            }
        }
    }
    
    public Group getById(int id) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get group by id ({})", id);
        }
        
        String query = "SELECT group_name, groups.faculty_id, faculty_short_name, faculty_full_name " + 
                       "FROM groups " + 
                       "JOIN faculties ON groups.faculty_id = faculties.faculty_id " + 
                       "WHERE group_id = ?";
        Group output = jdbcTemplate.queryForObject(query,  new Object[] {id}, (resultSet, rowNum) -> {
            Group group = new Group();
            group.setId(id);
            group.setGroupName(resultSet.getString("group_name"));

            Faculty faculty = new Faculty();
            faculty.setId(resultSet.getInt("faculty_id"));
            faculty.setShortName(resultSet.getString("faculty_short_name"));
            faculty.setFullName(resultSet.getString("faculty_short_name"));
            group.setFaculty(faculty);

            return group;
        });
        
        output.setCourses(getCoursesByGroup(output));
        output.setStudents(getStudentsByGroup(output));
        return output;
    }
    
    public Group getByStudent(Student student) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get group by student (id = {})", student.getId());
        }
        
        String query = "SELECT group_id FROM students WHERE student_id = ?";
        int facultyId = jdbcTemplate.queryForObject(query, new Object[] {student.getId()}, Integer.class);
        return getById(facultyId);
    }
    
    public void delete(Group group) {
        if (logger.isDebugEnabled()) {
            logger.debug("Delete group with id {}", group.getId());
        }
        
        String query = "DELETE FROM groups WHERE group_id = ?";
        jdbcTemplate.update(query, group.getId());
    }
    
    public void update(Group group) {
        if (logger.isDebugEnabled()) {
            logger.debug("Update group with id {}", group.getId());
        }
        
        String query = "UPDATE groups SET "
                     + "group_name = ?, "
                     + "faculty_id = ? "
                     + "WHERE group_id = ?";
        jdbcTemplate.update(query, group.getGroupName(), group.getFaculty().getId(), group.getId());
    }
    
    private List<Course> getCoursesByGroup(Group group) {
        String query = "SELECT * FROM courses "
                     + "WHERE course_id IN "
                     + "(SELECT course_id FROM groups_courses WHERE group_id = ?)";
        return jdbcTemplate.query(query, new Object[] {group.getId()}, (resultSet, rowNum) -> {
            Course course = new Course();
            course.setId(resultSet.getInt("course_id"));
            course.setName(resultSet.getString("course_name"));
            course.setDescription(resultSet.getString("course_description"));
            return course;
        }); 
    }
    
    public void deleteGroupsCourse(int groupId, int courseId) {
        if (logger.isDebugEnabled()) {
            logger.debug("Delete groups course: groupId = {}, courseId = {}", groupId, courseId);
        }
        
        String query = "DELETE FROM groups_courses WHERE "
                     + "group_id = ? AND course_id = ?";
        jdbcTemplate.update(query, groupId, courseId);
    }
    
    private List<Student> getStudentsByGroup(Group group) {
        String query = "SELECT * FROM students WHERE group_id = ?";
        return jdbcTemplate.query(query, new Object[] {group.getId()}, (resultSet, rowNum) ->{
            Student student = new Student();
            student.setId(resultSet.getInt("student_id"));
            student.setFirstName(resultSet.getString("first_name"));
            student.setLastName(resultSet.getString("last_name"));
            return student;
        });
    }
    
    private class LazyLoadGroupRowMapper implements RowMapper<Group> {
        @Override
        public Group mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            Group group = new Group();
            group.setId(resultSet.getInt("group_id"));
            group.setGroupName(resultSet.getString("group_name"));

            Faculty faculty = new Faculty();
            faculty.setId(resultSet.getInt("faculty_id"));
            group.setFaculty(faculty);
            
            return group;
        }
    }
}
