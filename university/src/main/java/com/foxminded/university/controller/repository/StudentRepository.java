package com.foxminded.university.controller.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.foxminded.university.model.Group;
import com.foxminded.university.model.Student;

@Repository
public class StudentRepository {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public StudentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int getCount() {
        if (logger.isDebugEnabled()) {
            logger.debug("Get count of students");
        }
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM students", Integer.class);
    }
    
    public void add(Student student) {
        String query = "INSERT INTO students (first_name, last_name, group_id) "
                     + "VALUES (?, ?, ?)";
        try {
            jdbcTemplate.update(query, student.getFirstName(), student.getLastName(), student.getGroup().getId());
            if (logger.isDebugEnabled()) {
                logger.debug("Insert new student ({}, {}, {})", student.getFirstName(), student.getLastName(), student.getGroup().getId());
            }
        } catch (DataAccessException ex) {
            logger.error("Error while creating new student", ex);
        }
    }
    
    public Student getById(int id) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get student by id ({})", id);
        }
        String query = "SELECT student_id, first_name, last_name, groups.group_id, groups.group_name " + 
                       "FROM students " + 
                       "JOIN groups ON students.group_id = groups.group_id " + 
                       "WHERE student_id = ?";
        return jdbcTemplate.queryForObject(query, new Object[] {id}, (resultSet, rowNum) ->{
            Student student = new Student();
            student.setId(resultSet.getInt("student_id"));
            student.setFirstName(resultSet.getString("first_name"));
            student.setLastName(resultSet.getString("last_name"));
            
            Group group = new Group();
            group.setId(resultSet.getInt("group_id"));
            group.setGroupName(resultSet.getString("group_name"));
            
            student.setGroup(group);
            return student;
        });
    }
    
    public void delete(Student student) {
        if (logger.isDebugEnabled()) {
            logger.debug("Delete student with id = {}", student.getId());
        }
        String query = "DELETE FROM students WHERE student_id = ?";
        jdbcTemplate.update(query, student.getId());
    }
    
    public void update(Student student) {
        if (logger.isDebugEnabled()) {
            logger.debug("Update student with id = {}", student.getId());
        }
        String query = "UPDATE students SET "
                     + "first_name = ?, "
                     + "last_name = ?, "
                     + "group_id = ? "
                     + "WHERE student_id = ?";
        jdbcTemplate.update(query, student.getFirstName(), student.getLastName(), 
                                   student.getGroup().getId(), student.getId());
    }
}
