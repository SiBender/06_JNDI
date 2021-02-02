package com.foxminded.university.controller.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.foxminded.university.model.Classroom;
import com.foxminded.university.model.Course;
import com.foxminded.university.model.Lesson;
import com.foxminded.university.model.Timeslot;

@Repository
public class LessonRepository {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LessonRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public void add(Lesson lesson) {
        String query = "INSERT INTO lessons (lesson_date, timeslot_id, course_id, classroom_id) "
                     + "VALUES (?, ?, ?, ?)";
        try {
            jdbcTemplate.update(query, lesson.getDate(), lesson.getTime().getId(),
                                lesson.getCourse().getId(), lesson.getClassroom().getId());
            if (logger.isDebugEnabled()) {
                logger.debug("Insert new lesson({}, {}, {}, {})", lesson.getDate(), lesson.getTime().getId(),
                            lesson.getCourse().getId(), lesson.getClassroom().getId());
            }
        } catch (DataAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Error while creating new lesson", ex);
            }
            throw ex;
        }                    
    } 
    
    public Lesson getById(int id) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get lesson by id ({})", id);
        }
        
        String query = "SELECT lessons.lesson_id, lessons.lesson_date,"
                     + "timeslots.timeslot_id, timeslots.timeslot_description, "
                     + "courses.course_id, courses.course_name, courses.course_description,"
                     + "classrooms.classroom_id, classrooms.classroom_number "
                     + "FROM lessons "
                     + "JOIN timeslots ON lessons.timeslot_id=timeslots.timeslot_id "
                     + "JOIN courses ON lessons.course_id = courses.course_id "
                     + "JOIN classrooms ON lessons.classroom_id=classrooms.classroom_id "
                     + "WHERE lesson_id = ?";
        return jdbcTemplate.queryForObject(query, new Object[] {id}, this::mapRow);
    }
    
    public void update(Lesson lesson) {
        if (logger.isDebugEnabled()) {
            logger.debug("Update lesson with id = {}", lesson.getId());
        }
        String query = "UPDATE lessons SET "
                     + "lesson_date = ?, "
                     + "course_id = ?, "
                     + "timeslot_id = ?, "
                     + "classroom_id = ? "
                     + "WHERE lesson_id = ?";
        jdbcTemplate.update(query, lesson.getDate(), lesson.getCourse().getId(),
                                   lesson.getTime().getId(), lesson.getClassroom().getId(),
                                   lesson.getId());
    }
    
    public void delete(Lesson lesson) {
        if (logger.isDebugEnabled()) {
            logger.debug("Update lesson with id = {}", lesson.getId());
        }
        String query = "DELETE FROM lessons WHERE lesson_id = ?";
        jdbcTemplate.update(query, lesson.getId());
    }
    
    private Lesson mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Lesson lesson = new Lesson();
        Timeslot timeslot = new Timeslot();
        Course course = new Course();
        Classroom classroom = new Classroom();
        
        lesson.setId(resultSet.getInt("lesson_id"));
        lesson.setDate(resultSet.getDate("lesson_date").toLocalDate());
        
        timeslot.setId(resultSet.getInt("timeslot_id"));
        timeslot.setDescription(resultSet.getString("timeslot_description"));
        lesson.setTime(timeslot);
        
        course.setId(resultSet.getInt("course_id"));
        course.setName(resultSet.getString("course_name"));
        lesson.setCourse(course);
        
        classroom.setId(resultSet.getInt("classroom_id"));
        classroom.setNumber(resultSet.getString("classroom_number"));
        lesson.setClassroom(classroom);
        return lesson;
    }
}
