package com.foxminded.university.controller.web;

import java.sql.SQLException;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.foxminded.university.controller.service.StudentsService;
import com.foxminded.university.model.Group;
import com.foxminded.university.model.Student;

@Controller
public class StudentsController {
    private StudentsService studentsService;

    @Autowired
    public StudentsController(StudentsService studentsService) {
        this.studentsService = studentsService;
    }
    
    @RequestMapping("/student")
    public String studentsInfo(Model model) throws NamingException, SQLException {        
        List<Group> groups = studentsService.getAllGroups();
        model.addAttribute("groups", groups);
        model.addAttribute("groupsCount", groups.size());
        model.addAttribute("student", new Student());
        
        int[] studentsCount = {0};
        groups.forEach(group -> studentsCount[0] += group.getStudents().size());
        model.addAttribute("studentsCount", studentsCount[0]);
        
        return "student";
    }
    
    @PostMapping("/addstudent")
    public String addStudent(@ModelAttribute Student student, Model model) {
        studentsService.addStudent(student.getFirstName(), student.getLastName(), student.getGroup().getId());
        return "redirect:/student";
    }
    
    @GetMapping("/deletestudent")
    public String deleteStudent(@RequestParam String id, Model model) {
        try {
            int studentId = Integer.parseInt(id);
            studentsService.deleteStudent(studentId);
        } catch (NumberFormatException ex) {
            //log error
        }
        return "redirect:/student";
    }
    
    @GetMapping("/student/editstudent")
    public String getStudent(@RequestParam String id, Model model)  {
        try {
            int studentId = Integer.parseInt(id);
            model.addAttribute("student", studentsService.getStudent(studentId));
            model.addAttribute("groups", studentsService.getAllGroups());
        } catch (NumberFormatException ex) {
            //log error
        }
        return "student/editstudent";
    }
    
    @PostMapping("/student/editstudent")
    public String updateStudent(@ModelAttribute Student student, Model model) {
        studentsService.update(student);
        return "redirect:/student";
    }
}
