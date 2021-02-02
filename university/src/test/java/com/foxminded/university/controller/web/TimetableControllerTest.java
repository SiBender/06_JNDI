package com.foxminded.university.controller.web;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.util.Arrays;

import javax.servlet.ServletContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.context.WebApplicationContext;

import com.foxminded.university.config.WebConfig;
import com.foxminded.university.controller.service.LessonService;
import com.foxminded.university.controller.service.TimetableService;
import com.foxminded.university.controller.util.TimetableFormatter;
import com.foxminded.university.model.Classroom;
import com.foxminded.university.model.Course;
import com.foxminded.university.model.DateInterval;
import com.foxminded.university.model.Lesson;
import com.foxminded.university.model.Timeslot;
import com.foxminded.university.model.Timetable;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WebConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = "classpath:testDatabase.sql")
@WebAppConfiguration
class TimetableControllerTest {
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    private WebApplicationContext wac;
    
    private MockMvc mockMvc;
    
    
    
    @Mock
    Model model;
    
    @Mock
    TimetableService timetableService;

    @Mock
    LessonService lessonService;

    @Mock
    TimetableFormatter timetableFormatter;
    
    
    TimetableController timetableController;
    
    @BeforeAll
    void init() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        this.timetableController = new TimetableController(timetableService,
                                            lessonService, timetableFormatter);
    }
    
    @Order(1)
    @Test
    void givenWac_whenServletContext_thenItProvidesTimetableControllerTest() {
        ServletContext servletContext = wac.getServletContext();
        
        assertNotNull(servletContext);        
        assertTrue(servletContext instanceof MockServletContext);
        assertNotNull(wac.getBean("timetableController"));
    }
    
    @Order(2)
    @Test
    void timetableInfoTest() throws Exception {
        this.mockMvc.perform(get("/timetable"))
                    .andExpect(view().name("timetable"))
                    .andExpect(status().isOk())
                    .andExpect(model().size(4))
                    .andExpect(model().attributeExists("studentId"))
                    .andExpect(model().attributeExists("timetable"))
                    .andExpect(model().attributeExists("timeslots"))
                    .andExpect(model().attributeExists("timemap"));
        
        Mockito.when(timetableService.getStudentTimetable(anyString(), anyString(), anyInt()))
               .thenReturn(new Timetable());
        Mockito.when(lessonService.getAllTimeslots()).thenReturn(Arrays.asList(new Timeslot()));
        
        timetableController.timetableInfo(model);
        verify(timetableService).getStudentTimetable(anyString(), anyString(), anyInt());
        verify(lessonService).getAllTimeslots();
        verify(timetableFormatter).generateFormattedTable(any(Timetable.class), anyList());
    }

    @Order(3)
    @Test
    void getTimetableByTeacherTest() throws Exception {
        this.mockMvc.perform(get("/teacherstimetable"))
                    .andExpect(view().name("teacherstimetable"))
                    .andExpect(status().isOk())
                    .andExpect(model().size(4))
                    .andExpect(model().attributeExists("teacherId"))
                    .andExpect(model().attributeExists("dateInterval"))
                    .andExpect(model().attributeExists("timeslots"))
                    .andExpect(model().attributeExists("timemap"));
        
        
        DateInterval dateInterval = new DateInterval(LocalDate.now(), LocalDate.now());
        Timetable timetable = new Timetable();
        timetable.setDateInterval(dateInterval);
        
        Mockito.when(timetableService.getTeacherTimetable(anyString(), anyString(), anyInt()))
               .thenReturn(timetable);
        Mockito.when(lessonService.getAllTimeslots()).thenReturn(Arrays.asList(new Timeslot()));
    
        timetableController.getTimetableByTeacher(model);
        verify(timetableService).getTeacherTimetable(anyString(), anyString(), anyInt());
        verify(lessonService, times(2)).getAllTimeslots();
        verify(timetableFormatter, times(2)).generateFormattedTable(any(Timetable.class), anyList());
    }

    @Order(4)
    @Test
    void createNewLessonTest() throws Exception {
        this.mockMvc.perform(get("/timetable/addlesson")
                .param("date", "2020-06-18")
                .param("tid", "1")
                .param("timeslot", "1"))
                .andExpect(view().name("timetable/addlesson"))
                .andExpect(status().isOk())
                .andExpect(model().size(5))
                .andExpect(model().attribute("timeslotId", Matchers.equalTo(1)))
                .andExpect(model().attribute("classrooms", Matchers.iterableWithSize(2)))
                .andExpect(model().attribute("timeslots", Matchers.iterableWithSize(6)))
                .andExpect(model().attribute("courses", Matchers.iterableWithSize(2)));
        
        timetableController.createNewLesson("2020-06-19", 1, 1, model);
        verify(lessonService).getAllClassrooms();
        verify(lessonService, times(3)).getAllTimeslots();
        verify(lessonService).getCoursesByTeacher(1);
    }

    @Order(5)
    @Test
    void saveNewLessonTest() throws Exception {
        this.mockMvc.perform(post("/timetable/addlesson")
                .param("date", "2020-06-20")
                .param("time.id", "1")
                .param("course.id", "1")
                .param("classroom.id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacherstimetable"));
        
        Timeslot timeslot = new Timeslot();
        timeslot.setId(1);
        Classroom classroom = new Classroom();
        classroom.setId(1);
        Course course = new Course();
        course.setId(1);
        
        Lesson lesson = new Lesson(); 
        lesson.setDate(LocalDate.now());
        lesson.setClassroom(classroom);
        lesson.setCourse(course);
        lesson.setTime(timeslot);
        
        timetableController.saveNewLesson(lesson, model);
        verify(lessonService).createLesson(anyString(), anyInt(), anyInt(), anyInt());
    }

    @Order(6)
    @Test
    void getLessonInfoTest() throws Exception {
        this.mockMvc.perform(get("/timetable/editlesson")
                .param("id", "1")
                .param("tid", "1"))
                .andExpect(view().name("timetable/editlesson"))
                .andExpect(status().isOk())
                .andExpect(model().size(4))
                .andExpect(model().attribute("classrooms", Matchers.iterableWithSize(2)))
                .andExpect(model().attribute("classrooms", Matchers.iterableWithSize(2)))
                .andExpect(model().attribute("courses", Matchers.iterableWithSize(2)))
                .andExpect(model().attribute("lesson", 
                        Matchers.hasProperty("id", Matchers.equalTo(1))));
        
        timetableController.getLessonInfo(1, 1, model);
        verify(lessonService).getLessonById(1);
        verify(lessonService, times(2)).getAllClassrooms();
        verify(lessonService, times(4)).getAllTimeslots();
        verify(lessonService, times(2)).getCoursesByTeacher(1);
    }
    
    @Order(7)
    @Test
    void updateLessonTest() throws Exception {
        this.mockMvc.perform(post("/timetable/editlesson")
                    .param("id", "1")
                    .param("date", "2020-06-20")
                    .param("time.id", "3")
                    .param("course.id", "1")
                    .param("classroom.id", "1"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/teacherstimetable"));
        
        timetableController.updateLesson(new Lesson(), model);
        verify(lessonService).updateLesson(any(Lesson.class));
    }

    @Order(8)
    @Test
    void deleteLessonTest() throws Exception {
        this.mockMvc.perform(get("/deletelesson")
                .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacherstimetable"));
        
        timetableController.deleteLesson(10);
        verify(lessonService).deleteLesson(10);
    }
}
