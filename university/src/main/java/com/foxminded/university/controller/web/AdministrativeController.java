package com.foxminded.university.controller.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.foxminded.university.controller.service.AdministrativeService;
import com.foxminded.university.model.Faculty;
import com.foxminded.university.model.Group;
import com.foxminded.university.model.Teacher;

@Controller
public class AdministrativeController {
    private AdministrativeService administrativeService;
    
    @Autowired 
    public AdministrativeController(AdministrativeService administrativeService) {
        this.administrativeService = administrativeService;
    }
    
    @RequestMapping("/admin")
    public String getInfo(Model model) {
        model.addAttribute("faculty", new  Faculty());
        model.addAttribute("teacher", new Teacher());
        model.addAttribute("group", new Group());
        
        List<Faculty> faculties = administrativeService.getAllFaculties();
        
        List<Group> groups = new ArrayList<>();
        faculties.forEach(f -> groups.addAll(administrativeService.getGroupsByFaculty(f.getId())));

        List<Teacher> teachers = new ArrayList<>();
        faculties.forEach(f -> teachers.addAll(administrativeService.getTeachersByFaculty(f.getId())));
        
        model.addAttribute("faculties", faculties);
        model.addAttribute("groups", groups);
        model.addAttribute("teachers", teachers);
        return "admin";
    }
    
    @PostMapping("/addfaculty")
    public String addFaculty(@ModelAttribute Faculty faculty, Model model) {
        administrativeService.createFaculty(faculty.getShortName(), faculty.getFullName());
        return "redirect:/admin";
    }
    
    @GetMapping("/deletefaculty")
    public String deleteFaculty(@RequestParam String id, Model model) {
        try {
            int facultyId = Integer.parseInt(id);
            administrativeService.deleteFaculty(facultyId);
        } catch (NumberFormatException ex) {
            //log error
        }
        return "redirect:/admin";
    }
    
    @GetMapping("/admin/editfaculty")
    public String getFacultyById(@RequestParam String id, Model model) {
        try {
            int facultyId = Integer.parseInt(id);
            Faculty faculty = administrativeService.getFacultyById(facultyId);
            model.addAttribute("faculty", faculty);
        } catch (NumberFormatException ex) {
            //log error
            return "admin";
        }
        
        return "admin/editfaculty";
    }
    
    @PostMapping("/admin/editfaculty")
    public String updateFaculty(@ModelAttribute Faculty faculty, Model model) {
        administrativeService.updateFaculty(faculty);
        return "redirect:/admin";
    }
    
    @PostMapping("/addteacher")
    public String addTeacher(@ModelAttribute Teacher teacher, Model model) {
        administrativeService.createTeacher(teacher.getFirstName(), teacher.getLastName(), 
                                            teacher.getFaculty().getId());
        return "redirect:/admin";
    }
    
    @GetMapping("/deleteteacher")
    public String deleteTeacher(@RequestParam String id, Model model) {
        try {
            int teacherId = Integer.parseInt(id);
            administrativeService.deleteTeacher(teacherId);
        } catch (NumberFormatException ex) {
            //log error
        }
        return "redirect:/admin";
    }
    
    @GetMapping("/admin/editteacher")
    public String getTeacherById(@RequestParam String id, Model model) {
        try {
            int teacherId = Integer.parseInt(id);
            Teacher teacher = administrativeService.getTeacherById(teacherId);
            List<Faculty> faculties = administrativeService.getAllFaculties();
            
            model.addAttribute("teacher", teacher);
            model.addAttribute("faculties", faculties);
        } catch (NumberFormatException ex) {
            //log error
            return "admin";
        }
        
        return "admin/editteacher";
    }
    
    @PostMapping("/admin/editteacher")
    public String updateTeacher(@ModelAttribute Teacher teacher, Model model) {
        administrativeService.updateTeacher(teacher);
        return "redirect:/admin";
    }
    
    @PostMapping("/addgroup")
    public String addGroup(@ModelAttribute Group group, Model model) {
        administrativeService.createGroup(group.getGroupName(), group.getFaculty().getId());
        return "redirect:/admin";
    }
    
    @GetMapping("/deletegroup")
    public String deleteGroup(@RequestParam String id, Model model) {
        try {
            int groupId = Integer.parseInt(id);
            administrativeService.deleteGroupById(groupId);
        } catch (NumberFormatException ex) {
            //log error
        }
        return "redirect:/admin";
    }
    
    @GetMapping("/admin/editgroup")
    public String getGroupById(@RequestParam String id, Model model) {
        try {
            int groupId = Integer.parseInt(id);
            Group group = administrativeService.getGroupById(groupId);
            List<Faculty> faculties = administrativeService.getAllFaculties();
            
            model.addAttribute("group", group);
            model.addAttribute("faculties", faculties);
        } catch (NumberFormatException ex) {
            //log error
            return "admin";
        }
        
        return "admin/editgroup";
    }
    
    @PostMapping("/admin/editgroup")
    public String updateGroup(@ModelAttribute Group group, Model model) {
        administrativeService.updateGroup(group);
        return "redirect:/admin";
    }
}
