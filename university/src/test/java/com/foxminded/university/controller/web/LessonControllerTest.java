package com.foxminded.university.controller.web;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import javax.servlet.ServletContext;

import static org.mockito.ArgumentMatchers.any;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.foxminded.university.model.Classroom;
import com.foxminded.university.model.Timeslot;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WebConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = "classpath:testDatabase.sql")
@WebAppConfiguration
class LessonControllerTest {
    @Autowired
    private WebApplicationContext wac;
    
    private MockMvc mockMvc;
    
    @Mock
    Model model;
    
    @Mock
    LessonService lessonService;
    
    LessonController lessonController;
    
    
    @BeforeAll
    void init() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        this.lessonController = new LessonController(lessonService);
    }
    
    @Order(1)
    @Test
    void givenWac_whenServletContext_thenItProvidesLessonControllerTest() {
        ServletContext servletContext = wac.getServletContext();
        
        assertNotNull(servletContext);        
        assertTrue(servletContext instanceof MockServletContext);
        assertNotNull(wac.getBean("lessonController"));
    }
    
    @Test
    void lessonsInfoTest() throws Exception {
        this.mockMvc.perform(get("/lesson"))
                    .andExpect(view().name("lesson"))
                    .andExpect(status().isOk())
                    .andExpect(model().size(4))
                    .andExpect(model().attributeExists("classrooms"))
                    .andExpect(model().attributeExists("classroom"))
                    .andExpect(model().attributeExists("timeslots"))
                    .andExpect(model().attributeExists("timeslot"));
        
        lessonController.lessonsInfo(model);
        verify(lessonService).getAllClassrooms();
        verify(lessonService).getAllTimeslots();
    }

    @Order(3)
    @Test
    void addClassroomTest() throws Exception {
        this.mockMvc.perform(post("/addclassroom")
                    .param("number", "555AAA")
                    .param("capacity", "1000"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/lesson"));
        
        lessonController.addClassroom(new Classroom(), model);
        verify(lessonService).addClassroom(any(Classroom.class));
    }

    @Order(4)
    @Test
    void deleteClassroomTest() throws Exception {
        this.mockMvc.perform(get("/deleteclassroom")
                    .param("id", "3"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/lesson"))
                    .andExpect(model().size(0));
        
        lessonController.deleteClassroom("3", model);
        verify(lessonService).deleteClassroom(3);
    }

    @Order(2)
    @Test
    void getClassroomTest() throws Exception {
        this.mockMvc.perform(get("/lesson/editclassroom")
                    .param("id", "1"))
                    .andExpect(view().name("lesson/editclassroom"))
                    .andExpect(status().isOk())
                    .andExpect(model().size(1))
                    .andExpect(model().attribute("classroom", 
                            Matchers.hasProperty("id", Matchers.equalTo(1))))
                    .andExpect(model().attribute("classroom", 
                            Matchers.hasProperty("number", Matchers.equalTo("101A"))));
        
        lessonController.getClassroom("1", model);
        verify(lessonService).getClassroomById(1);
    }

    @Test
    void updateClassroomTest() throws Exception {
        this.mockMvc.perform(post("/lesson/editclassroom")
                    .param("id", "1")
                    .param("number", "555AAA")
                    .param("capacity", "1000"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/lesson"));
        
        lessonController.updateClassroom(new Classroom(), model);
        verify(lessonService).updateClassroom(any(Classroom.class));
    }

    @Test
    void addTimeslotTest() throws Exception {
        this.mockMvc.perform(post("/addtimeslot")
                    .param("description", "01:00 - 02:00"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/lesson"));
        
        lessonController.addTimeslot(new Timeslot(), model);
        verify(lessonService).addTimeslot(any(Timeslot.class));
    }

    @Test
    void getTimeslotTest() throws Exception {
        this.mockMvc.perform(get("/lesson/edittimeslot")
                    .param("id", "1"))
                    .andExpect(view().name("lesson/edittimeslot"))
                    .andExpect(status().isOk())
                    .andExpect(model().size(1))
                    .andExpect(model().attribute("timeslot", 
                            Matchers.hasProperty("id", Matchers.equalTo(1))));
        
        lessonController.getTimeslot("1", model);
        verify(lessonService).getTimeslotById(1);
    }

    @Test
    void updateTimeslotTest() throws Exception {
        this.mockMvc.perform(post("/lesson/editclassroom")
                    .param("id", "1")
                    .param("description", "01:00 - 02:00"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/lesson"));
        
        lessonController.updateTimeslot(new Timeslot(), model);
        verify(lessonService).updateTimeslot(any(Timeslot.class));
    }

    @Test
    void deleteTimeslotTest() throws Exception {
        this.mockMvc.perform(get("/deletetimeslot")
                    .param("id", "10"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/lesson"))
                    .andExpect(model().size(0));
        
        lessonController.deleteTimeslot("10", model);
        verify(lessonService).deleteTimeslot(10);
    }
}
