package com.foxminded.university.controller.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.foxminded.university.model.Faculty;

@Repository
public class FacultyRepository {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public FacultyRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public void add(Faculty faculty) {        
        String query = "INSERT INTO faculties (faculty_short_name, faculty_full_name) "
                     + "VALUES (?, ?)";
        try {
            jdbcTemplate.update(query, faculty.getShortName(), faculty.getFullName());
            if (logger.isDebugEnabled()) {
                logger.debug("Insert new faculty ({}, {})", faculty.getShortName(), faculty.getFullName());
            }
        } catch (DataAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Error while creating new faculty", ex);
            }
        }
    }
    
    public List<Faculty> getAll() {
        if (logger.isDebugEnabled()) { 
            logger.debug("Get all faculties");
        }
        String query = "SELECT * FROM faculties";
        return jdbcTemplate.query(query, this::mapRow);
    }
    
    public Faculty getById(int id) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get faculty by id ({})", id);
        }
        String query = "SELECT * FROM faculties WHERE faculty_id = ?";
        return jdbcTemplate.queryForObject(query, new Object[] { id }, this::mapRow);
    }

    private Faculty mapRow(ResultSet resiltSet, int rowNum) throws SQLException {
        Faculty faculty = new Faculty();
        faculty.setId(resiltSet.getInt("faculty_id"));
        faculty.setShortName(resiltSet.getString("faculty_short_name"));
        faculty.setFullName(resiltSet.getString("faculty_full_name"));
        return faculty;
    }
    
    public void delete(Faculty faculty) {
        if (logger.isDebugEnabled()) {
            logger.debug("Delete faculty, id = {}", faculty.getId());
        }
        
        String query = "DELETE FROM faculties WHERE faculty_id = ?";
        jdbcTemplate.update(query, faculty.getId());
    }
    
    public void update(Faculty faculty) {
        if (logger.isDebugEnabled()) {
            logger.debug("Update faculty with id = {}", faculty.getId());
        } 
        String query = "UPDATE faculties SET "
                + "faculty_short_name = ?, "
                + "faculty_full_name = ? "
                + "WHERE faculty_id = ?";
        jdbcTemplate.update(query, faculty.getShortName(), faculty.getFullName(), faculty.getId());
    }
}
