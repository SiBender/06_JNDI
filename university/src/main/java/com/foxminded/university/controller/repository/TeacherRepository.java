package com.foxminded.university.controller.repository;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.foxminded.university.model.Course;
import com.foxminded.university.model.Faculty;
import com.foxminded.university.model.Teacher;

@Repository
public class TeacherRepository {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TeacherRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public void add(Teacher teacher) {
        String query = "INSERT INTO teachers (first_name, last_name, faculty_id) "
                     + "VALUES (?, ?, ?)";
        try {
            jdbcTemplate.update(query, teacher.getFirstName(), teacher.getLastName(), teacher.getFaculty().getId());
            if (logger.isDebugEnabled()) {
                logger.debug("Insert new teacher ({}, {}, {})", 
                                       teacher.getFirstName(), teacher.getLastName(), teacher.getFaculty().getId());
            }
        } catch (DataAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Error while creating new teacher", ex);
            }
        }
    }
    
    public List<Teacher> getByFaculty(Faculty faculty) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get teacher by faculty (id = {})", faculty.getId());
        }
        
        String query = "SELECT teacher_id, first_name, last_name, faculty_id FROM teachers "
                     + "WHERE faculty_id = ?";
        return jdbcTemplate.query(query, new Object[] {faculty.getId()}, (resultSet, rowNum) -> {
            Teacher teacher = new Teacher();
            teacher.setId(resultSet.getInt("teacher_id"));
            teacher.setFirstName(resultSet.getString("first_name"));
            teacher.setLastName(resultSet.getString("last_name"));
            teacher.setFaculty(faculty);
            return teacher;
        });
    }
    
    public List<Teacher> getAll() {
        if (logger.isDebugEnabled()) {
            logger.debug("Get all teachers list");
        }
        
        String query = "SELECT teacher_id, first_name, last_name, faculty_id FROM teachers";
        return jdbcTemplate.query(query, (resultSet, rowNum) -> {
            Teacher teacher = new Teacher();
            teacher.setId(resultSet.getInt("teacher_id"));
            teacher.setFirstName(resultSet.getString("first_name"));
            teacher.setLastName(resultSet.getString("last_name"));
            
            Faculty faculty = new Faculty();
            faculty.setId(resultSet.getInt("faculty_id"));
            teacher.setFaculty(faculty);
            return teacher;
        });
    }
    
    public Teacher getById(int id) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get teacher by id ({})", id);
        }
        String query = "SELECT first_name, last_name, teachers.faculty_id, faculty_short_name, faculty_full_name " + 
                       "FROM teachers " + 
                       "JOIN faculties ON teachers.faculty_id = faculties.faculty_id " + 
                       "WHERE teacher_id = ?";
        Teacher output = jdbcTemplate.queryForObject(query, new Object[] {id}, (resultSet, rowNum) -> {
            Teacher teacher = new Teacher();
            teacher.setId(id);
            teacher.setFirstName(resultSet.getString("first_name"));
            teacher.setLastName(resultSet.getString("last_name"));
            
            Faculty faculty = new Faculty();
            faculty.setId(resultSet.getInt("faculty_id"));
            faculty.setShortName(resultSet.getString("faculty_short_name"));
            faculty.setFullName(resultSet.getString("faculty_full_name"));
            
            teacher.setFaculty(faculty);
            return teacher;
        });
        
        output.setCourses(getCoursesByTeacher(output));
        return output;
    }
    
    private List<Course> getCoursesByTeacher(Teacher teacher) {
        String query = "SELECT * FROM courses WHERE teacher_id = ?";
        return jdbcTemplate.query(query, new Object[] {teacher.getId()}, (resultSet, rowNum) -> {
            Course course = new Course();
            course.setId(resultSet.getInt("course_id"));
            course.setName(resultSet.getString("course_name"));
            course.setDescription(resultSet.getString("course_description"));
            return course;
        });
    }  
    
    public void delete(Teacher teacher) {
        if (logger.isDebugEnabled()) {
            logger.debug("Delete teacher with id ({})", teacher.getId());
        }
        
        String query = "DELETE FROM teachers WHERE teacher_id = ?";
        jdbcTemplate.update(query, teacher.getId());
    }
    
    public void update(Teacher teacher) {
        if (logger.isDebugEnabled()) {
            logger.debug("Update teacher with id ({})", teacher.getId());
        }
        
        String query = "UPDATE teachers SET "
                     + "first_name = ?, "
                     + "last_name = ?, "
                     + "faculty_id = ?"
                     + "WHERE teacher_id = ?";
        jdbcTemplate.update(query, teacher.getFirstName(), teacher.getLastName(),
                                   teacher.getFaculty().getId(), teacher.getId());
    }
}
