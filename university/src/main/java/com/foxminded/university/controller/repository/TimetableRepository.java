package com.foxminded.university.controller.repository;

import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.foxminded.university.model.Classroom;
import com.foxminded.university.model.Course;
import com.foxminded.university.model.DateInterval;
import com.foxminded.university.model.Lesson;
import com.foxminded.university.model.Student;
import com.foxminded.university.model.Teacher;
import com.foxminded.university.model.Timeslot;
import com.foxminded.university.model.Timetable;

@Repository
public class TimetableRepository {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public TimetableRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public Timetable getByStudent(Student student, DateInterval dateInterval) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get timetable by student ({}, {}, {})", 
                     student.getId(), dateInterval.getStartDate(), dateInterval.getEndDate());
        }
        
        Timetable timetable = new Timetable();
        timetable.setDateInterval(dateInterval);
        timetable.setLessons(getLessonsByStudent(student, dateInterval));
        return timetable;
    }
    
    public Timetable getByTeacher(Teacher teacher, DateInterval dateInterval) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get timetable by teacher ({}, {}, {})", 
                     teacher.getId(), dateInterval.getStartDate(), dateInterval.getEndDate());
        }
        
        Timetable timetable = new Timetable();
        timetable.setDateInterval(dateInterval);
        timetable.setLessons(getLessonsByTeacher(teacher, dateInterval));
        return timetable;
    }
    
    private List<Lesson> getLessonsByStudent(Student student, DateInterval dateInterval) {
        String query = "SELECT  lessons.lesson_id, lessons.lesson_date, " + 
                       "lessons.timeslot_id, timeslots.timeslot_description, " + 
                       "lessons.classroom_id, classrooms.classroom_number, " + 
                       "lessons.course_id, courses.course_name " + 
                       "FROM lessons " + 
                       "JOIN timeslots ON lessons.timeslot_id = timeslots.timeslot_id " + 
                       "JOIN classrooms ON lessons.classroom_id = classrooms.classroom_id " + 
                       "JOIN courses ON lessons.course_id = courses.course_id " + 
                       "WHERE lesson_date BETWEEN ? AND ? " + 
                       "AND lessons.course_id IN (" +
                       "    SELECT course_id FROM groups_courses WHERE group_id = (" + 
                       "         SELECT group_id FROM students WHERE student_id = ? ))" + 
                       "ORDER BY lessons.lesson_date, lessons.timeslot_id;";
        return jdbcTemplate.query(query, 
                                  new Object[] {dateInterval.getStartDate(), 
                                                dateInterval.getEndDate(), 
                                                student.getId()}, 
                                  this::mapRow);
    }
    
    private List<Lesson> getLessonsByTeacher(Teacher teacher, DateInterval dateInterval) {
        String query = "SELECT  lessons.lesson_id, lessons.lesson_date, " + 
                "lessons.timeslot_id, timeslots.timeslot_description, " + 
                "lessons.classroom_id, classrooms.classroom_number, " + 
                "lessons.course_id, courses.course_name " + 
                "FROM lessons " + 
                "JOIN timeslots ON lessons.timeslot_id = timeslots.timeslot_id " + 
                "JOIN classrooms ON lessons.classroom_id = classrooms.classroom_id " + 
                "JOIN courses ON lessons.course_id = courses.course_id " + 
                "WHERE lesson_date BETWEEN ? AND ? " + 
                "AND lessons.course_id IN (SELECT course_id FROM courses where teacher_id = ?) " + 
                "ORDER BY lessons.lesson_date, lessons.timeslot_id";
        return jdbcTemplate.query(query, 
                                  new Object[] {dateInterval.getStartDate(), 
                                                dateInterval.getEndDate(), 
                                                teacher.getId()}, 
                                  this::mapRow);
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
