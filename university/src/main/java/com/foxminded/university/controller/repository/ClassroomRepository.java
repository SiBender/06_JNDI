package com.foxminded.university.controller.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.foxminded.university.model.Classroom;


@Repository
public class ClassroomRepository {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ClassroomRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public void add(Classroom classroom) {        
        String query = "INSERT INTO classrooms (classroom_number, capacity) "
                     + "VALUES (?, ?)";
        try {
            jdbcTemplate.update(query, classroom.getNumber(), classroom.getCapacity());
            if (logger.isDebugEnabled()) {
                logger.debug("Create new classroom ({}, {})", classroom.getNumber(), classroom.getCapacity());
            }
        } catch (DataAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Error while creating classroom", ex);
            }
        }
    }
    
    public void delete(Classroom classroom) {
        String query = "DELETE FROM classrooms WHERE classroom_id = ?";
        try {
            jdbcTemplate.update(query, classroom.getId());
            if (logger.isDebugEnabled()) {
                logger.debug("Delete classroom with ID = {}", classroom.getId());
            }
        } catch (DataAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Error while deleting classroom", ex);
            }
        }
    }
    
    public void update(Classroom classroom) {
        String query = "UPDATE classrooms SET "
                     + "classroom_number = ?, "
                     + "capacity = ? "
                     + "WHERE classroom_id = ?";
        try {
            jdbcTemplate.update(query, classroom.getNumber(), classroom.getCapacity(), classroom.getId());
            if (logger.isDebugEnabled()) {
                logger.debug("Update classroom with ID = {}", classroom.getId());
            }
        } catch (DataAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Error while updating classroom", ex);
            }
        }
    }
    
    public List<Classroom> getAll() {
        if (logger.isDebugEnabled()) { logger.debug("Get all classrooms"); }
        
        String query = "SELECT * FROM classrooms";
        return jdbcTemplate.query(query, this::mapRow);
    }
    
    public Classroom getById(int id) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get classroom by id ({})", id);
        }
        
        String query = "SELECT * FROM classrooms WHERE classroom_id = ?";
        return jdbcTemplate.queryForObject(query, new Object[] {id}, this::mapRow);
    }
    
    public Classroom getByNumber(String number) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get classroom by number ({})", number);
        }
        
        String query = "SELECT * FROM classrooms WHERE classroom_number = ?";
        return jdbcTemplate.queryForObject(query, new Object[] { number }, this::mapRow);
    }

    private Classroom mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Classroom classroom = new Classroom();
        classroom.setId(resultSet.getInt("classroom_id"));
        classroom.setNumber(resultSet.getString("classroom_number"));
        classroom.setCapacity(resultSet.getInt("capacity"));
        return classroom;
    }
}
