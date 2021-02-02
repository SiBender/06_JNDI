package com.foxminded.university.controller.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.foxminded.university.model.Timeslot;

@Repository
public class TimeslotRepository {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TimeslotRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public List<Timeslot> getAll() {
        if (logger.isDebugEnabled()) {
            logger.debug("Get all timeslots");
        }
        
        String query = "SELECT * FROM timeslots ORDER BY timeslot_description";
        return jdbcTemplate.query(query, new TimeslotRowMapper());
    }
    
    public void add(Timeslot timeslot) {
        if (logger.isDebugEnabled()) {
            logger.debug("Add timeslot - {}", timeslot.getDescription());
        }
        
        String query = "INSERT INTO timeslots (timeslot_description) VALUES (?)";
        jdbcTemplate.update(query, timeslot.getDescription());
    }
    
    public void delete(Timeslot timeslot) {
        if (logger.isDebugEnabled()) {
            logger.debug("Delete timeslot with ID = {}", timeslot.getId());
        }
        
        String query = "DELETE FROM timeslots WHERE timeslot_id = ?";
        jdbcTemplate.update(query, timeslot.getId());
    }
    
    public void update(Timeslot timeslot) {
        if (logger.isDebugEnabled()) {
            logger.debug("Update timeslot with ID = {}", timeslot.getId());
        }
        
        String query = "UPDATE timeslots SET timeslot_description = ? "
                     + "WHERE timeslot_id = ?";
        jdbcTemplate.update(query, timeslot.getDescription(), timeslot.getId());
    }
    
    public Timeslot getById(int id) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get timeslot by id ({})", id);
        }
        
        String query = "SELECT * FROM timeslots WHERE timeslot_id = ?";
        return jdbcTemplate.queryForObject(query, new Object[] {id} ,new TimeslotRowMapper());
    }
    
    private class TimeslotRowMapper implements RowMapper<Timeslot> {
        @Override
        public Timeslot mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            Timeslot timeslot = new Timeslot();
            timeslot.setId(resultSet.getInt("timeslot_id"));
            timeslot.setDescription(resultSet.getString("timeslot_description"));
            return timeslot;
        }
    }
}
