package com.foxminded.university.controller.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.foxminded.university.controller.service.TeachersService;
import com.foxminded.university.model.Course;
import com.foxminded.university.model.Group;
import com.foxminded.university.model.Teacher;

@Controller
public class TeachersController {
    private TeachersService teachersService;
    
    @Autowired
    public TeachersController(TeachersService teachersService) {
        this.teachersService = teachersService;
    }
    
    @RequestMapping("/teacher")
    public String teachersInfo(Model model) {
        List<Teacher> teachers = teachersService.getAll();
        model.addAttribute("teachers", teachers);
        model.addAttribute("course", new Course());
        
        List<Group> groups = teachersService.getAllGroups();
        model.addAttribute("groups", groups);
        return "teacher";
    }
    
    @PostMapping("/addcourse")
    public String addCourse(@ModelAttribute Course course, Model mode) {
        teachersService.createCourse(course.getName(), course.getDescription(), course.getTeacher().getId());
        return "redirect:/teacher";
    }
    
    @GetMapping("/deletecourse")
    public String deleteCourse(@RequestParam String id, Model model) {
        try {
            int courseId = Integer.parseInt(id);
            teachersService.deleteCourse(courseId);
        } catch (NumberFormatException ex) {
            //log error
            return "teacher";
        }
        return "redirect:/teacher";
    }
    
    @GetMapping("/teacher/editcourse")
    public String getCourse(@RequestParam String id, Model model) {
        try {
            int courseId = Integer.parseInt(id);
            model.addAttribute("teachers", teachersService.getAll());
            model.addAttribute("course", teachersService.getCourse(courseId));
        } catch (NumberFormatException ex) {
            //log error
            return "teacher";
        }
        return "teacher/editcourse";
    }
    
    @PostMapping("/teacher/editcourse")
    public String updateCourse(@ModelAttribute Course course, Model model) {
        teachersService.updateCourse(course);
        return "redirect:/teacher";
    }
    
    @GetMapping("/deletegroupscourse")
    public String deleteGroupsCourse(@RequestParam int cid, @RequestParam int gid,  Model model) {
        teachersService.deleteGroupsCourse(gid, cid);
        return "redirect:/teacher";
    }
    
    @GetMapping("/teacher/assigncourse")
    public String getGroupsCoursesInfo(@RequestParam int gid,  Model model) {
        Group group = teachersService.getGroupById(gid);
        model.addAttribute("group", group);
        model.addAttribute("courses", teachersService.getFreeCourses(group));
        return "teacher/assigncourse";
    }
    
    @PostMapping("/teacher/assigncourse")
    public String assignGroupsCourse(@ModelAttribute Group group, Model model) {
        teachersService.assignGroupsCourse(group.getId(), group.getCourses().get(0).getId());
        return "redirect:/teacher";
    }
}
