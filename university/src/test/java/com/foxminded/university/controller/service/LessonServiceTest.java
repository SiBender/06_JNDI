package com.foxminded.university.controller.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.foxminded.university.controller.repository.ClassroomRepository;
import com.foxminded.university.controller.repository.CourseRepository;
import com.foxminded.university.controller.repository.LessonRepository;
import com.foxminded.university.controller.repository.TimeslotRepository;
import com.foxminded.university.model.Teacher;
import com.foxminded.university.model.Lesson;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LessonServiceTest {
    @Mock
    LessonRepository lessonRepository;
    @Mock
    TimeslotRepository timeslotRepository;
    @Mock
    ClassroomRepository classroomRepository;
    @Mock
    CourseRepository courseRepository;
    
    @InjectMocks
    LessonService lessonService;
    
    @BeforeAll
    void init() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    void getAllClassroomsShouldCallClassroomRepositoryTest() {
        lessonService.getAllClassrooms();
        verify(classroomRepository).getAll();
    }

    @Test
    void getAllTimeslotsShouldCallTimeslotRepositoryTest() {
        lessonService.getAllTimeslots();
        verify(timeslotRepository).getAll();
    }

    @Test
    void getCoursesByTeacherShouldCallCourseRepositoryTest() {
        lessonService.getCoursesByTeacher(123);
        verify(courseRepository).getByTeacher(any(Teacher.class));
    }

    @Test
    void createLessonShouldCallLessonRepositoryTest() {
        lessonService.createLesson("2020-01-31", 333, 444, 555);
        verify(lessonRepository).add(any(Lesson.class));
    }

}
