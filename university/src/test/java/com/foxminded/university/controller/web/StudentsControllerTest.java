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
import com.foxminded.university.controller.service.StudentsService;
import com.foxminded.university.model.Group;
import com.foxminded.university.model.Student;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WebConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = "classpath:testDatabase.sql")
@WebAppConfiguration
class StudentsControllerTest {

    
    @Autowired
    private WebApplicationContext wac;
    
    private MockMvc mockMvc;
    
    @Mock
    Model model;
    
    @Mock
    StudentsService studentsService;
    
    StudentsController studentsController;

    @BeforeAll
    void init() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        this.studentsController = new StudentsController(studentsService);
    }    
      
    @Order(1)
    @Test
    void givenWac_whenServletContext_thenItProvidesStudentsControllerTest() {
        ServletContext servletContext = wac.getServletContext();
        
        assertNotNull(servletContext);        
        assertTrue(servletContext instanceof MockServletContext);
        assertNotNull(wac.getBean("studentsController"));
    }
    
    @Order(0)
    @Test
    void studentsInfoTest() throws Exception {
        this.mockMvc.perform(get("/student"))
                    .andExpect(view().name("student"))
                    .andExpect(status().isOk())
                    .andExpect(model().size(4))
                    .andExpect(model().attributeExists("groups"))
                    .andExpect(model().attributeExists("student"))
                    .andExpect(model().attributeExists("groupsCount"))
                    .andExpect(model().attributeExists("studentsCount"));
        
        studentsController.studentsInfo(model);
        verify(studentsService).getAllGroups();
    }

    @Order(3)
    @Test
    void addStudentTest() throws Exception {
        this.mockMvc.perform(post("/addstudent")
                    .param("firsName", "John")
                    .param("lastName", "Smith")
                    .param("group.id", "1"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/student"));
        
        Student student = new Student();
        student.setFirstName("");
        student.setLastName("");
        Group group = new Group();
        group.setId(1);
        student.setGroup(group);
        studentsController.addStudent(student, model);
        verify(studentsService).addStudent(anyString(), anyString(), anyInt());
    }

    @Order(0)
    @Test
    void getStudentTest() throws Exception {
        this.mockMvc.perform(get("/student/editstudent")
                    .param("id", "1"))
                    .andExpect(view().name("student/editstudent"))
                    .andExpect(status().isOk())
                    .andExpect(model().size(2))
                    .andExpect(model().attribute("groups", Matchers.iterableWithSize(1)))
                    .andExpect(model().attribute("student", 
                            Matchers.hasProperty("id", Matchers.equalTo(1))))
                    .andExpect(model().attribute("student", 
                            Matchers.hasProperty("firstName", Matchers.equalTo("John"))));
        
        studentsController.getStudent("1", model);
        verify(studentsService).getStudent(1);
    }

    @Order(5)
    @Test
    void updateStudentTest() throws Exception {
        this.mockMvc.perform(post("/student/editstudent")
                    .param("id", "1")
                    .param("firsName", "New-John")
                    .param("lastName", "New-Smith")
                    .param("group.id", "1"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/student"));
        
        studentsController.updateStudent(new Student(), model);
        verify(studentsService).update(any(Student.class));
    }

    @Order(6)
    @Test
    void deleteStudentTest() throws Exception {
        this.mockMvc.perform(get("/deletestudent")
                    .param("id", "10"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/student"))
                    .andExpect(model().size(0));
        
        studentsController.deleteStudent("10", model);
        verify(studentsService).deleteStudent(10);
    }
}
