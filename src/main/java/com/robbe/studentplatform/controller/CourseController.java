package com.robbe.studentplatform.controller;

import com.robbe.studentplatform.model.Course;
import com.robbe.studentplatform.model.Student;
import com.robbe.studentplatform.repository.CourseRepository;
import com.robbe.studentplatform.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/courses")
public class CourseController {
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    StudentRepository studentRepository;

    @GetMapping
    public Iterable<Course> getAllCourses(){
        return courseRepository.findAll();
    }

    @GetMapping("/{id}")
    public EntityModel<Course> getCourse(@PathVariable("id") Integer id) {
        Course c = courseRepository.findById(id).orElseThrow(); //todo exceptions
        return EntityModel.of(c,
                linkTo(methodOn(CourseController.class).getCourse(id)).withSelfRel(),
                linkTo(methodOn(CourseController.class).getCourseStudentsById(id)).withRel("students"),
                linkTo(methodOn(CourseController.class).getAllCourses()).withRel("courses"));
    }
    @GetMapping("/{id}/students")
    public Iterable<Student> getCourseStudentsById(@PathVariable("id") Integer id){
        Optional<Course> c =courseRepository.findById(id);
        return c.<Iterable<Student>>map(Course::getStudents).orElse(null); // todo exceptions
    }

    @PostMapping("")
    public Course makeCourse(@RequestBody @Valid Course c){
       return courseRepository.save(c);
    }

    @PutMapping("/{courseId}/students/{studentId}")
    public String addStudent(@PathVariable("courseId") Integer courseiId, @PathVariable("studentId") Integer studentId){
        Optional<Course> courseOptional = courseRepository.findById(courseiId);
        if(courseOptional.isPresent()){
            Optional<Student> s = studentRepository.findById(studentId);
            if(s.isPresent()){
                Course course = courseOptional.get();
                Student student = s.get();
                course.addStudent(student);
                courseRepository.save(course);
                return "Student "+ s.get().getFirstname()+ " "+s.get().getLastname()+ " added.";
            }
            return "No student with this id."; // todo exceptions
        }
        return "No course found with this id."; // todo exceptions
    }
    @DeleteMapping("/{id}")
    public String deleteCourseById(@PathVariable("id") Integer id){
       courseRepository.deleteById(id);
        return "Course deleted";
    }

}
