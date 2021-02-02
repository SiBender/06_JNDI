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

import com.foxminded.university.controller.service.LessonService;
import com.foxminded.university.model.Classroom;
import com.foxminded.university.model.Timeslot;

@Controller
public class LessonController {
    private LessonService lessonService;
    
    @Autowired
    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }
    
    @RequestMapping("/lesson")
    public String lessonsInfo(Model model) {
        List<Classroom> classrooms = lessonService.getAllClassrooms();
        List<Timeslot> timeslots = lessonService.getAllTimeslots();
        
        model.addAttribute("classrooms", classrooms);
        model.addAttribute("timeslots", timeslots);
        model.addAttribute("classroom", new Classroom());
        model.addAttribute("timeslot", new Timeslot());
        return "lesson";
    }
    
    @PostMapping("/addclassroom")
    public String addClassroom(@ModelAttribute Classroom classroom, Model model) {
        lessonService.addClassroom(classroom);
        return "redirect:/lesson";
    }
    
    @GetMapping("/deleteclassroom")
    public String deleteClassroom(@RequestParam String id, Model model) {
        try {
            int classroomId = Integer.parseInt(id);
            lessonService.deleteClassroom(classroomId);
        } catch (NumberFormatException ex) {
            //log error
        }
        return "redirect:/lesson";
    }
    
    @GetMapping("/lesson/editclassroom")
    public String getClassroom(@RequestParam String id, Model model) {
        try {
            int classroomId = Integer.parseInt(id);
            model.addAttribute("classroom", lessonService.getClassroomById(classroomId));
        } catch (NumberFormatException ex) {
            // log error
        }
        return "lesson/editclassroom";
    }
    
    @PostMapping("/lesson/editclassroom")
    public String updateClassroom(@ModelAttribute Classroom classroom, Model model) {
        lessonService.updateClassroom(classroom);
        return "redirect:/lesson";
    }
    
    @PostMapping("/addtimeslot")
    public String addTimeslot(@ModelAttribute Timeslot timeslot, Model model) {
        lessonService.addTimeslot(timeslot);
        return "redirect:/lesson";
    }
    
    @GetMapping("/lesson/edittimeslot")
    public String getTimeslot(@RequestParam String id, Model model) {
        try {
            int timeslotId = Integer.parseInt(id);
            model.addAttribute("timeslot", lessonService.getTimeslotById(timeslotId));
        } catch (NumberFormatException ex) {
            // log error
        }
        return "lesson/edittimeslot";
    }
    
    @PostMapping("/lesson/edittimeslot")
    public String updateTimeslot(@ModelAttribute Timeslot timeslot, Model model) {
        lessonService.updateTimeslot(timeslot);
        return "redirect:/lesson";
    }
    
    @GetMapping("/deletetimeslot")
    public String deleteTimeslot(@RequestParam String id, Model model) {
        try {
            int timeslotId = Integer.parseInt(id);
            lessonService.deleteTimeslot(timeslotId);
        } catch (NumberFormatException ex) {
            //log error
        }
        return "redirect:/lesson";
    }
}
