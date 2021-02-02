package com.foxminded.university.controller.web;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;

import javax.servlet.ServletContext;

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
import com.foxminded.university.controller.service.AdministrativeService;
import com.foxminded.university.model.Faculty;
import com.foxminded.university.model.Teacher;
import com.foxminded.university.model.Group;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WebConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = "classpath:testDatabase.sql")
@WebAppConfiguration
class AdministrativeControllerTest {   
    @Autowired
    private WebApplicationContext wac;
    
    private MockMvc mockMvc;
    
    @Mock
    AdministrativeService administrativeService;
    
    @Mock
    Model model;
    
    AdministrativeController administrativeController;

    @BeforeAll
    void init() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        administrativeController = new AdministrativeController(administrativeService);   
    }
     
    @Order(1)
    @Test
    void givenWac_whenServletContext_thenItProvidesAdministrativeControllerTest() {
        ServletContext servletContext = wac.getServletContext();
        
        assertNotNull(servletContext);        
        assertTrue(servletContext instanceof MockServletContext);
        assertNotNull(wac.getBean("administrativeController"));   
    }
    
    @Order(2)
    @Test
    void getInfoShouldCallAdministrativeServiceAndReturnCorrectViewNameTest() throws Exception {
        this.mockMvc.perform(get("/admin"))
                    .andExpect(view().name("admin"))
                    .andExpect(status().isOk())
                    .andExpect(model().size(6))
                    .andExpect(model().attributeExists("faculty"))
                    .andExpect(model().attributeExists("group"))
                    .andExpect(model().attributeExists("teacher"))
                    .andExpect(model().attributeExists("faculties"))
                    .andExpect(model().attributeExists("groups"))
                    .andExpect(model().attributeExists("teachers"));

        Faculty faculty = new Faculty();
        faculty.setId(1);
        Mockito.when(administrativeService.getAllFaculties()).thenReturn(Arrays.asList(faculty));
        
        administrativeController.getInfo(model);

        verify(model).addAttribute(anyString(), any(Faculty.class));
        verify(model).addAttribute(anyString(), any(Teacher.class));
        verify(model).addAttribute(anyString(), any(Group.class));
        verify(administrativeService).getAllFaculties();
        verify(administrativeService).getGroupsByFaculty(anyInt());
        verify(administrativeService).getTeachersByFaculty(anyInt());    
    }

    @Order(3)
    @Test
    void addFacultyTest() throws Exception {
        this.mockMvc.perform(post("/addfaculty")
                    .param("shortName", "testName")
                    .param("fullName", "testFullName"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin"))
                    .andExpect(model().size(1))
                    .andExpect(model().attributeExists("faculty"));
        
        
        String facultyShortName = "ShortName";
        String facultyFullName = "FullName";
        Faculty faculty = new Faculty();
        faculty.setShortName(facultyShortName);
        faculty.setFullName(facultyFullName);
        
        administrativeController.addFaculty(faculty, model);
        verify(administrativeService).createFaculty(facultyShortName, facultyFullName);
    }

    @Order(4)
    @Test
    void deleteFacultyTest() throws Exception {
        this.mockMvc.perform(get("/deletefaculty")
                    .param("id", "3"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin"))
                    .andExpect(model().size(0));
        
        administrativeController.deleteFaculty("3", model);
        verify(administrativeService).deleteFaculty(3); 
    }

    @Order(5)
    @Test
    void getFacultyByIdTest() throws Exception {
        this.mockMvc.perform(get("/admin/editfaculty")
                .param("id", "1"))
                .andExpect(view().name("admin/editfaculty"))
                .andExpect(status().isOk())
                .andExpect(model().size(1))
                .andExpect(model().attribute("faculty", 
                        Matchers.hasProperty("id", Matchers.equalTo(1))));
        
        administrativeController.getFacultyById("1", model);
        verify(administrativeService).getFacultyById(1);     
    }

    @Order(6)
    @Test
    void updateFacultyTest() throws Exception {
        this.mockMvc.perform(post("/admin/editfaculty")
                .param("id", "1")
                .param("shortName", "CS")
                .param("fullName", "Computer science"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
        
        administrativeController.updateFaculty(new Faculty(), model);
        verify(administrativeService).updateFaculty(any(Faculty.class));   
    }
    
    @Order(9)
    @Test
    void addTeacherTest() throws Exception {
        this.mockMvc.perform(post("/addteacher")
                .param("firstName", "Alan")
                .param("lastName", "Turing")
                .param("faculty.id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
        
        Teacher teacher = new Teacher();
        teacher.setFirstName("test");
        teacher.setLastName("test");
        Faculty faculty = new Faculty();
        faculty.setId(1);
        teacher.setFaculty(faculty);
        administrativeController.addTeacher(teacher, model);
        verify(administrativeService).createTeacher(anyString(), anyString(), anyInt());   
    }
    
    @Order(8)
    @Test
    void deleteTeacherTest() throws Exception {
        this.mockMvc.perform(get("/deleteteacher")
                .param("id", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
        
        administrativeController.deleteTeacher("3", model);
        verify(administrativeService).deleteTeacher(anyInt());    
    }

    @Order(7)
    @Test
    void getTeacherByIdTest() throws Exception {
        this.mockMvc.perform(get("/admin/editteacher")
                .param("id", "1"))
                .andExpect(view().name("admin/editteacher"))
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(model().attribute("teacher", 
                        Matchers.hasProperty("id", Matchers.equalTo(1))))
                .andExpect(model().attribute("faculties", Matchers.iterableWithSize(2)));
        
        administrativeController.getTeacherById("1", model);
        verify(administrativeService).getTeacherById(anyInt());
        verify(administrativeService, times(2)).getAllFaculties();    
    }

    @Order(10)
    @Test
    void updateTeacherTest() throws Exception {
        this.mockMvc.perform(post("/admin/editteacher")
                .param("firstName", "Alan")
                .param("lastName", "Turing")
                .param("faculty.id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
        
        administrativeController.updateTeacher(new Teacher(), model);
        verify(administrativeService).updateTeacher(any(Teacher.class));    
    }

    @Order(11)
    @Test
    void addGroupTest() throws Exception {
        this.mockMvc.perform(post("/addgroup")
                .param("groupName", "Alan")
                .param("faculty.id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
        
        Group group = new Group();
        group.setGroupName("testName");
        Faculty faculty = new Faculty();
        faculty.setId(1);
        group.setFaculty(faculty);
        
        administrativeController.addGroup(group, model);
        verify(administrativeService).createGroup(anyString(), anyInt());    
    }
    
    @Order(12)
    @Test
    void deleteGroupTest() throws Exception {
        this.mockMvc.perform(get("/deletegroup")
                .param("id", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
        
        administrativeController.deleteGroup("10", model);
        verify(administrativeService).deleteGroupById(anyInt());   
    }

    @Order(13)
    @Test
    void getGroupByIdTest() throws Exception {
        this.mockMvc.perform(get("/admin/editgroup")
                .param("id", "1"))
                .andExpect(view().name("admin/editgroup"))
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(model().attribute("group", 
                        Matchers.hasProperty("id", Matchers.equalTo(1))))
                .andExpect(model().attribute("faculties", Matchers.iterableWithSize(2)));
        
        administrativeController.getGroupById("1", model);
        verify(administrativeService).getGroupById(anyInt());
        verify(administrativeService, times(3)).getAllFaculties();   
    }

    @Order(14)
    @Test
    void updateGroupTest() throws Exception {
        this.mockMvc.perform(post("/admin/editgroup")
                .param("id", "1")
                .param("groupName", "cs-22")
                .param("faculty.id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
        
        administrativeController.updateGroup(new Group(), model);
        verify(administrativeService).updateGroup(any(Group.class));    
    }
}
