package com.foxminded.university.controller.web;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;

import javax.servlet.ServletContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;

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
import com.foxminded.university.controller.service.TeachersService;
import com.foxminded.university.model.Course;
import com.foxminded.university.model.Group;
import com.foxminded.university.model.Teacher;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WebConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = "classpath:testDatabase.sql")
@WebAppConfiguration
class TeachersControllerTest {
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    private WebApplicationContext wac;
    
    private MockMvc mockMvc;
    
    
    
    @Mock
    Model model;
    
    @Mock
    TeachersService teachersService;
    
    TeachersController teachersController;

    @BeforeAll
    void init() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        this.teachersController = new TeachersController(teachersService);
    } 

    @Order(1)
    @Test
    void givenWac_whenServletContext_thenItProvidesTeachersControllerTest() {
        ServletContext servletContext = wac.getServletContext();
        
        assertNotNull(servletContext);        
        assertTrue(servletContext instanceof MockServletContext);
        assertNotNull(wac.getBean("teachersController"));
    }
    
    @Order(2)
    @Test
    void teachersInfoTest() throws Exception {
        this.mockMvc.perform(get("/teacher"))
                    .andExpect(view().name("teacher"))
                    .andExpect(status().isOk())
                    .andExpect(model().size(3))
                    .andExpect(model().attributeExists("groups"))
                    .andExpect(model().attributeExists("teachers"))
                    .andExpect(model().attributeExists("course"));
        
        teachersController.teachersInfo(model);
        verify(teachersService).getAllGroups();
        verify(teachersService).getAll();
    }

    @Order(3)
    @Test
    void addCourseTest() throws Exception {
        this.mockMvc.perform(post("/addcourse")
                    .param("name", "TC")
                    .param("description", "Test course")
                    .param("teacher.id", "1"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/teacher"));
        
        Course course = new Course();
        course.setName("TC");
        course.setDescription("Test course");
        Teacher teacher = new Teacher();
        teacher.setId(1);
        course.setTeacher(teacher);
        teachersController.addCourse(course, model);
        verify(teachersService).createCourse(anyString(), anyString(), anyInt());
    }

    @Order(4)
    @Test
    void deleteCourseTest() throws Exception {
        this.mockMvc.perform(get("/deletecourse")
                    .param("id", "3"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/teacher"));
        
        teachersController.deleteCourse("3", model);
        verify(teachersService).deleteCourse(3);
    }

    @Order(5)
    @Test
    void getCourseTest() throws Exception {
        this.mockMvc.perform(get("/teacher/editcourse")
                    .param("id", "1"))
                    .andExpect(view().name("teacher/editcourse"))
                    .andExpect(status().isOk())
                    .andExpect(model().size(2))
                    .andExpect(model().attribute("teachers", Matchers.iterableWithSize(1)))
                    .andExpect(model().attribute("course", 
                            Matchers.hasProperty("id", Matchers.equalTo(1))))
                    .andExpect(model().attribute("course", 
                            Matchers.hasProperty("name", Matchers.equalTo("Turing machine"))));
        
        teachersController.getCourse("1", model);
        verify(teachersService).getCourse(1);
        verify(teachersService, times(2)).getAll();
    }

    @Order(6)
    @Test
    void updateCourseTest() throws Exception {
        this.mockMvc.perform(post("/teacher/editcourse")
                    .param("id", "1")
                    .param("name", "TC")
                    .param("descripton", "Test course")
                    .param("teacher.id", "1"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/teacher"));
        
        teachersController.updateCourse(new Course(), model);
        verify(teachersService).updateCourse(any(Course.class));
    }

    @Order(7)
    @Test
    void deleteGroupsCourseTest() throws Exception {
        this.mockMvc.perform(get("/deletegroupscourse")
                    .param("cid", "1")
                    .param("gid", "1"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/teacher"))
                    .andExpect(model().size(0));
        
        teachersController.deleteGroupsCourse(10, 10, model);
        verify(teachersService).deleteGroupsCourse(10, 10);
    }

    @Order(8)
    @Test
    void getGroupsCoursesInfoTest() throws Exception {
        this.mockMvc.perform(get("/teacher/assigncourse")
                .param("gid", "1"))
                .andExpect(view().name("teacher/assigncourse"))
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(model().attribute("courses", Matchers.iterableWithSize(0)))
                .andExpect(model().attribute("group", 
                        Matchers.hasProperty("id", Matchers.equalTo(1))))
                .andExpect(model().attribute("group", 
                        Matchers.hasProperty("groupName", Matchers.equalTo("cs-20"))));
        
        Mockito.when(teachersService.getGroupById(anyInt())).thenReturn(new Group());
        teachersController.getGroupsCoursesInfo(1, model);
        verify(teachersService).getGroupById(1);
        verify(teachersService).getFreeCourses(any(Group.class));
    }

    @Order(9)
    @Test
    void assignGroupsCourseTest() throws Exception {
        this.mockMvc.perform(post("/teacher/assigncourse")
                .param("id", "1")
                .param("groupName", "name")
                .param("courses[0].id", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacher"));
        
        Group group = new Group();
        group.setId(1);
        Course course = new Course();
        course.setId(1);
        group.setCourses(Arrays.asList(course));
        teachersController.assignGroupsCourse(group, model);
        verify(teachersService).assignGroupsCourse(anyInt(), anyInt());
    }
}
